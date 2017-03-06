/*
 * Copyright (c) 2015 Ondrej Kuzelka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package lrnn.ruleLearner;

import ida.ilp.logic.Clause;
import ida.ilp.logic.Constant;
import ida.ilp.logic.Literal;
import ida.ilp.logic.io.PseudoPrologParser;
import ida.utils.Sugar;
import ida.utils.collections.ValueToIndex;
import ida.utils.tuples.Pair;
import ida.utils.tuples.Quadruple;
import ida.utils.tuples.Triple;
import lrnn.construction.example.Example;
import lrnn.crossvalidation.Crossvalidation;
import lrnn.crossvalidation.NeuralCrossvalidation;
import lrnn.crossvalidation.SampleSplitter;
import lrnn.global.Global;
import lrnn.global.Glogger;
import lrnn.global.Settings;
import lrnn.learning.LearningStep;
import lrnn.learning.Result;
import lrnn.learning.Results;
import lrnn.learning.Sample;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by gusta on 31.1.17.
 */
public class SoftClusteringSPI {

    Crossvalidation crossValidation;
    int folds = 5;

    private boolean parallelCrossval = false;   //not working yet! (but with parallel grounding and learning within LRNNs turned on it should run just as fast)

    private int searchBeamSize = 20;
    private int searchMaxSize = 6;

    private int autoencodingSteps = 1000;
    private int trainingSteps = 2000;

    private boolean reinitializeAllWeightsWithinSPIcycle = false;
    private boolean reinitializeFinalWeightsWithinSPIcycle = true;

    private int atomClusters = 3;
    private int bondClusters = 3;

    private int maxSpiCycles = 10;
    private double minMissedExamples4ruleLearning = 1;

    private boolean alternatingClasses = true;
    //----------------------------

    private String atomClusterName = "atc";
    private String bondClusterName = "bc";
    private String tmpConstant = "a";
    private double weightMultiplier = 1;

    private int bondId = 0; //this needs to be global i.e. unique id for each bond within the whole dataset!
    ValueToIndex<Pair<String, String>> bondIDs = new ValueToIndex();
    private double subsampleTripleRules = 1;

    public static void main(String[] args) throws IOException {

        Global.setSeed(1);
        Settings.setDataset(args[0]);
        //create logger for all messages within the program
        Glogger.init();

        SoftClusteringSPI lc = new SoftClusteringSPI();

        File datasetPath = new File(args[0]);

        lc.crossvalidate(datasetPath);
    }

    private Crossvalidation crossvalidate(File datasetPath) throws IOException {
        List<Sample> samples = Files.lines(datasetPath.toPath()).map(line ->
                new Sample(new Example(line.substring(line.indexOf(" ") + 1) + "."), line.substring(0, line.indexOf(" ")).equals("+") ? 1.0 : 0.0))
                .collect(Collectors.toList());

        //stratified(!) crossvalidation split
        SampleSplitter ss = new SampleSplitter(folds, samples);
        List<String> foldPaths = ss.outputSplits(datasetPath.getParent());
        crossValidation = new NeuralCrossvalidation(ss);

        Stream<String> foldStream;
        if (parallelCrossval) {
            foldStream = foldPaths.parallelStream();
        } else {
            foldStream = foldPaths.stream();
        }
        foldStream.forEach((testFold) -> {
            String testFoldPath = testFold + "_transformed.txt";
            try {
                datasetsETL(testFold, testFoldPath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String trainSet = foldPaths.stream().filter(foldPath -> (!foldPath.equals(testFold) || folds == 1)).map(trainFold -> {
                try {
                    return new String(Files.readAllBytes(Paths.get(trainFold)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return "";
            }).collect(Collectors.joining("\n"));
            try {
                File train = new File(testFold + "-test/trainSet.txt");
                train.getParentFile().mkdirs();
                Files.write(train.toPath(), trainSet.getBytes());
                //train SPI
                Pair<Results, String> trainResults = cycleSPI(train.getPath());
                //test resulting template
                Results testFoldResults = testLRNNtemplate(testFoldPath, trainResults);
                Glogger.process("Finished fold " + testFold);
                crossValidation.loadFoldStats(testFoldResults);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        crossValidation.finalizeCrossvalStats();
        return crossValidation;
    }

    public Pair<Results, String> cycleSPI(String datasetPath) throws IOException {
        return cycleSPI(datasetPath, null);
    }

    /**
     * I'd love to split this method into subparts as it is terribly long but there are too many cross-references and I don't want to return "quintupled outputs" everywhere
     *
     * @param datasetPath
     * @throws IOException
     */
    public Pair<Results, String> cycleSPI(String datasetPath, String learnedInitialTemplatePath) throws IOException {

        //save the split-transformed examples (bond/5 -> bond3 + types) for latter LRNN learning
        String examplesOutPath = datasetPath.substring(0, datasetPath.lastIndexOf(".")) + "_transformed.txt";
        Triple<List<Double>, List<Clause>, List<Clause>> data = datasetsETL(datasetPath, examplesOutPath);

        //create correct representation for initial LRNNs autoencoding
        Triple<String, String, StringBuilder> initialAutoencoding = createInitialTemplatesAndExamples(data.s, datasetPath, atomClusters, bondClusters);
        //run LRNN on it
        if (learnedInitialTemplatePath == null) {
            learnedInitialTemplatePath = trainLRNNtemplate(initialAutoencoding.r, initialAutoencoding.s, autoencodingSteps).s;
        }
        //extract weights/values of literals
        Pair<Map<String, Double>, Map<String, Map<String, Double>>> weightMapping = extractWeightMappingFromTemplate(learnedInitialTemplatePath);
        //transform examples into new soft-cluster representation
        Pair<List<Clause>, Map<Literal, Double>> reinventedExamples = transformToWeightedClusters(data.t, weightMapping);


        //-----------start SPI learning cycle-----------
        SimpleLearner sl = new SimpleLearner();

        //We can (and perhaps should) actually "learn" the symmetries from data
        SymmetrySaturator symmetrySaturator = new SymmetrySaturator();
        symmetrySaturator.setSymmetries("bond", 3, Sugar.list(new int[]{1, 0, 2}));
        sl.setSaturator(symmetrySaturator);

        MultiExampleDataset dataset = new MultiExampleDataset(reinventedExamples.r, data.r);
        Map<Literal, Double> clusterValues = reinventedExamples.s;

        StringBuilder template = mergeTemplates(initialAutoencoding.t.toString(), new String(Files.readAllBytes(Paths.get(learnedInitialTemplatePath))));

        int iter = 0;
        Pair<Results, String> learningResults = null;
        Set<HornClause> previousClauses = new LinkedHashSet<>();
        String templPath = "";
        while (true) {
            sl.setDataset(dataset);
            sl.setLanguageBias(dataset.allPredicates());
            sl.setLiteralWeights(clusterValues);

            Quadruple<HornClause, Double, Integer, Double> hornClause = sl.beamSearch(searchBeamSize, searchMaxSize, alternatingClasses ? 2 * (iter % 2) - 1 : 0);
            Glogger.LogTrain("Iteration: " + iter + " -> best found weighted horn clause classifier: " + hornClause);
            if (!previousClauses.add(hornClause.r)) break;

            template.append(templatePartFromClause(hornClause, iter));
            templPath = datasetPath.substring(0, datasetPath.lastIndexOf(".")) + "_template_cycle" + iter + ".txt";
            Files.write(Paths.get(templPath), template.toString().getBytes());

            learningResults = trainLRNNtemplate(examplesOutPath, templPath, trainingSteps);
            if (!reinitializeAllWeightsWithinSPIcycle) {   //reuse previously learned weights?
                template = mergeTemplates(template.toString(), new String(Files.readAllBytes(Paths.get(learningResults.s))));
            }
            if (iter >= maxSpiCycles) break;

            Pair<List<Clause>, List<Double>> subset = getMisclassifiedSubset(alternatingClasses ? -1 * hornClause.t : 0, learningResults.r, reinventedExamples.r, data.r);  //-1* = we want the next one
            Glogger.LogTrain("Iteration: " + iter + " after weight learning - " + (alternatingClasses ? hornClause.t == 1 ? " Number of TN + FN: " : " - Number of TP + FP: " : " Numer of all: ") + subset.s.size() + " examples");
            dataset = new MultiExampleDataset(subset.r, subset.s);
            if (subset.r.size() < minMissedExamples4ruleLearning) break;

            Pair<Map<String, Double>, Map<String, Map<String, Double>>> newWeightMapping = extractWeightMappingFromTemplate(learningResults.s);
            weightMapping.r.putAll(newWeightMapping.r);   //update all changed offsets if any
            for (Map.Entry<String, Map<String, Double>> ent : newWeightMapping.s.entrySet()) {  //update all changed weights if any
                weightMapping.s.get(ent.getKey()).putAll(ent.getValue());
            }
            clusterValues = transformToWeightedClusters(data.t, weightMapping).s;
            iter++;
        }
        learningResults = trainLRNNtemplate(examplesOutPath, templPath, 3 * trainingSteps);
        Glogger.process("...Finished SPI cycle!");
        return learningResults;
    }

    private StringBuilder mergeTemplates(String previous, String learned) {
        LinkedHashMap<String, String> merged = new LinkedHashMap<>();
        String[] strings = {previous, learned};
        for (int i = 0; i < strings.length; i++) {
            String template = strings[i];
            String[] lines = template.split("\n");
            for (String s : lines) {
                if (s.isEmpty()) continue;
                String weight = s.substring(0, s.indexOf(" "));
                if (s.contains("/")) {  //offset
                    String[] split = s.split(" ");
                    if (i > 0) {
                        if (merged.containsKey(split[0].trim())) merged.put(split[0].trim(), split[1].trim());
                    } else merged.put(split[0].trim(), split[1].trim());
                } else if (weight.matches("-?\\d+([.,]\\d+)?")) {   //kappa
                    if (i > 0) {
                        if (merged.containsKey(s.substring(s.indexOf(" ")).trim())) {
                            if (reinitializeFinalWeightsWithinSPIcycle && s.contains("finalLambda")) {
                                double v = Double.parseDouble(weight);
                                weight = v / Math.abs(v) + "";
                            }
                            merged.put(s.substring(s.indexOf(" ")).trim(), weight + " ");
                        }
                    } else merged.put(s.substring(s.indexOf(" ")).trim(), weight + " ");
                } else {    //lambda
                    if (i > 0) {
                        if (merged.containsKey(s)) merged.put(s, "");
                    } else merged.put(s, "");
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> ent : merged.entrySet()) {
            if (ent.getKey().contains("/")) {
                sb.append(ent.getKey() + " " + ent.getValue() + "\n");
            } else
                sb.append(ent.getValue() + ent.getKey() + "\n");
        }
        return sb;
    }

    private Triple<List<Double>, List<Clause>, List<Clause>> datasetsETL(String datasetPath, String examplesOutPath) throws IOException {
        //extract example clauses and targets (labels)
        Reader reader = new FileReader(datasetPath);
        List<Pair<Clause, String>> labeledExampleClauses = PseudoPrologParser.read(reader);
        List<Clause> originalClauses = labeledExampleClauses.stream().map(pair -> pair.r).collect(Collectors.toList());
        List<Double> targets = labeledExampleClauses.stream().map(pair -> Double.parseDouble(pair.s)).collect(Collectors.toList());
        List<Clause> splitClauses = labeledExampleClauses.stream().map(pair -> transform(pair.r)).collect(Collectors.toList());

        PrintWriter pw = new PrintWriter(examplesOutPath);
        for (int i = 0; i < targets.size(); i++) {
            pw.print(targets.get(i) + " " + splitClauses.get(i) + ".\n");
        }
        pw.close();

        return new Triple<>(targets, originalClauses, splitClauses);
    }


    private Results testLRNNtemplate(String testFold, Pair<Results, String> trainResults) {
        String[] args = new String[]{"-e", testFold, "-r", trainResults.s};
        List<String[]> inputs = lrnn.Main.setupFromArguments(args);

        String[] test = inputs.get(0);
        String[] exs = inputs.get(1);
        String[] rules = inputs.get(2);
        String[] pretrainedRules = inputs.get(3);

        //create ground networks dataset
        lrnn.LiftedDataset dataset = lrnn.Main.createDataset(test, exs, rules, pretrainedRules);

        //start testing
        Results foldRes = crossValidation.test(dataset.template, trainResults.r, dataset.sampleSplitter.samples);
        foldRes.majority = new LearningStep();
        foldRes.majority.setError(crossValidation.testMajority(dataset.sampleSplitter.samples, dataset.sampleSplitter.samples));

        return foldRes;
    }

    private Pair<Results, String> trainLRNNtemplate(String examplesPath, String rulesPath, int learningSteps) {
        String[] args = new String[]{"-e", examplesPath, "-r", rulesPath, "-ls", "" + learningSteps};
        List<String[]> inputs = lrnn.Main.setupFromArguments(args);

        //Global.shuffleExamples = false; //we want to keep the order of the input examples

        String[] test = inputs.get(0);
        String[] exs = inputs.get(1);
        String[] rules = inputs.get(2);
        String[] pretrainedRules = inputs.get(3);

        //create ground networks dataset
        lrnn.LiftedDataset dataset = lrnn.Main.createDataset(test, exs, rules, pretrainedRules);

        //start learning
        Results foldRes = crossValidation.train(dataset.template, dataset.sampleSplitter.samples);
        //also test n the same samples to properly extract misclassified ones
        foldRes = crossValidation.test(dataset.template, foldRes, dataset.sampleSplitter.samples);

        String exportPath = rulesPath.substring(0, rulesPath.lastIndexOf(".")) + "_learned";
        dataset.template.weightFolder = "";
        dataset.template.exportTemplate(exportPath);
        return new Pair<>(foldRes, exportPath + ".txt");
    }

    private String templatePartFromClause(Quadruple<HornClause, Double, Integer, Double> hornClause, int iter) {
        //String templ = "finalKappa" + "(" + tmpConstant + ") :- " + hornClause.r.body().toString().replaceAll(" ", "") + ".\n";
        String templ = "finalLambda" + iter + "(" + tmpConstant + ") :- " + hornClause.r.body().toString().replaceAll(" ", "") + ".\n";
        templ += (weightMultiplier * hornClause.t) + " finalKappa(" + tmpConstant + ") :- finalLambda" + iter + "(" + tmpConstant + ").\n";
        return templ;
    }

    private Pair<List<Clause>, List<Double>> getMisclassifiedSubset(int targetClass, Results r, List<Clause> origClauses, List<Double> origTargets) {
        List<Clause> clauses = new ArrayList<>();
        List<Double> targets = new ArrayList<>();
        int i = 0;
        int tp = 0;
        int tn = 0;
        int fp = 0;
        int fn = 0;

        for (Result res : r.results) {
            if (origTargets.get(i) != res.getExpected()) {
                Glogger.err("SoftClusteringSPI: example labels mismatch! (shuffled?)");
                return null;
            }
            double classified = res.getActual() > r.training.getThresh() ? 1.0 : 0.0;
            if (targetClass == 1) {
                //all negative!
                if (res.getExpected() == 0) {
                    tn++;
                    clauses.add(origClauses.get(i));
                    targets.add(origTargets.get(i));
                }
                //all false negative!
                if (res.getExpected() == 1 && classified == 0) {
                    fn++;
                    clauses.add(origClauses.get(i));
                    targets.add(origTargets.get(i));
                }
            } else if (targetClass == -1) {
                //all positive!
                if (res.getExpected() == 1) {
                    tp++;
                    clauses.add(origClauses.get(i));
                    targets.add(origTargets.get(i));
                }
                //all false positive!
                if (res.getExpected() == 0 && classified == 1) {
                    fp++;
                    clauses.add(origClauses.get(i));
                    targets.add(origTargets.get(i));
                }
            } else {
                clauses.add(origClauses.get(i));
                targets.add(origTargets.get(i));
            }
            i++;
        }
        Glogger.process("Misclassified subset consists of: " + tp + " TP; " + tn + " TN; " + fp + " FP; " + fn + " FN; ");
        return new Pair<>(clauses, targets);
    }

    public Clause transform(Clause c) {
        List<Literal> literals = new ArrayList<>();
        for (Literal l : c.literals()) {
            if (l.predicate().equals("bond")) {
                int bondID = -1;
                if (l.get(0).name().compareTo(l.get(1).name()) < 0) {
                    if (bondIDs.containsValue(new Pair<>(l.get(0).name(), l.get(1).name()))) {
                        bondID = bondIDs.getIndex(new Pair<>(l.get(0).name(), l.get(1).name()));
                    } else {
                        bondIDs.put(bondId, new Pair<>(l.get(0).name(), l.get(1).name()));
                        bondID = bondId;
                        bondId++;
                    }
                } else {
                    if (bondIDs.containsValue(new Pair<>(l.get(1).name(), l.get(0).name()))) {
                        bondID = bondIDs.getIndex(new Pair<>(l.get(1).name(), l.get(0).name()));
                    } else {
                        bondIDs.put(bondId, new Pair<>(l.get(1).name(), l.get(0).name()));
                        bondID = bondId;
                        bondId++;
                    }
                }
                Constant bond = Constant.construct("b" + bondID);
                literals.add(new Literal("bond", l.get(0), l.get(1), bond));
                literals.add(new Literal(l.get(2).name(), l.get(0)));
                literals.add(new Literal(l.get(3).name(), l.get(1)));
                literals.add(new Literal(l.get(4).name(), bond));
            }
        }
        return new Clause(literals);
    }

    private Pair<Map<String, Double>, Map<String, Map<String, Double>>> extractWeightMappingFromTemplate(String templatePath) throws IOException {
        Map<String, Double> offsets = new LinkedHashMap<>();
        Map<String, Map<String, Double>> res = new LinkedHashMap<>();
        BufferedReader br = Files.newBufferedReader(Paths.get(templatePath));
        String line;
        while ((line = br.readLine()) != null) {
            String[] split = line.split(" ");
            if (split[0].matches("-?\\d+([.,]\\d+)?")) { //weighted kappa clause to a cluster
                double weight = Double.parseDouble(split[0]);
                String atomType = split[3].substring(0, split[3].indexOf("("));
                String cluster = split[1].substring(0, split[1].indexOf("("));
                res.putIfAbsent(atomType, new HashMap<>());
                res.get(atomType).put(cluster, weight);
            } else if (split[1].matches("-?\\d+([.,]\\d+)?")) { //offset
                String kappa = split[0].substring(0, split[0].indexOf("/"));
                double offset = Double.parseDouble(split[1]);
                offsets.put(kappa, offset);
            }
        }
        return new Pair<>(offsets, res);
    }

    public Pair<List<Clause>, Map<Literal, Double>> transformToWeightedClusters(List<Clause> clauses, Pair<Map<String, Double>, Map<String, Map<String, Double>>> weightMapping) {
        List<Clause> newClauses = new ArrayList<>();
        Map<Literal, Double> weights = new HashMap<Literal, Double>();
        ClassifierR cls = new ClassifierR();

        for (Clause clause : clauses) {
            Set<Literal> lits = new HashSet<>();
            for (Literal l : clause.literals()) {
                if (l.arity() == 1) {
                    Map<String, Double> clusterWeights = weightMapping.s.get(l.predicate());
                    for (Map.Entry<String, Double> clusterWeight : clusterWeights.entrySet()) {
                        Literal cl = new Literal(clusterWeight.getKey(), l.get(0));
                        lits.add(cl);
                        Double offset = weightMapping.r.get(clusterWeight.getKey());
                        weights.put(cl, cls.getgDisj().apply(new double[]{clusterWeight.getValue(), offset})); //get value (=sigm(input+offset)), not just weight
                    }
                } else {
                    lits.add(l);
                }
            }
            newClauses.add(new Clause(lits));
        }
        return new Pair<>(newClauses, weights);
    }


    /**
     * Transforms literals from input clauses into (latent) soft cluster representation such as hydrogen(atom_0) -> cluster1(atom_0),...,cluster5(atom_0)
     * and also outputs separate value assignments to these newly created literals cluster1(atom_0) -> 1.0 , cluster5(atom_0) -> 0.0, etc.
     *
     * @param atomClusters
     * @return
     */
    public Triple<String, String, StringBuilder> createInitialTemplatesAndExamples(List<Clause> clauses, String outPath, int atomClusters, int bondClusters) throws IOException {
        String defaultWeight = "0.0";
        Set<String> entities = new HashSet<>();
        Set<String> relations = new HashSet<>();

        Map<Clause, Integer> examples = new LinkedHashMap<>();

        double globalCount = 0;

        //positive examples
        for (Clause c : clauses) {
            for (Literal literal : c.literals()) {
                if (literal.arity() == 5) {//bond
                    entities.add(literal.get(2).name());
                    entities.add(literal.get(3).name());
                    relations.add(literal.get(4).name());

                    Clause clause = new Clause(new Literal(literal.get(2).name(), Constant.construct(tmpConstant)), new Literal(literal.get(3).name(), Constant.construct(tmpConstant)), new Literal(literal.get(4).name(), Constant.construct(tmpConstant)));
                    Integer count = examples.get(clause);
                    if (count == null) {
                        count = 0;
                    }
                    examples.put(clause, ++count);
                    globalCount += 1;
                }
            }
        }
        //negative examples
        for (String e1 : entities) {
            for (String e2 : entities) {
                for (String rel : relations) {
                    Clause c = new Clause(new Literal(e1, Constant.construct(tmpConstant)), new Literal(e2, Constant.construct(tmpConstant)), new Literal(rel, Constant.construct(tmpConstant)));
                    examples.putIfAbsent(c, 0);
                }
            }
        }
        globalCount /= examples.size(); //avg occurrence of a bond

        //print out examples with sampling and labels according to how often the bond appeared
        StringBuilder exampleQueries = new StringBuilder();
        for (Map.Entry<Clause, Integer> cle : examples.entrySet()) {
            if (cle.getValue() > globalCount)
                for (int i = (int) (globalCount + 1); i < cle.getValue(); i++) {
                    exampleQueries.append("1.0 " + cle.getKey().toString() + ".\n");
                }
            else {
                for (int i = (int) globalCount; i > cle.getValue(); i--) {
                    exampleQueries.append("0.0 " + cle.getKey().toString() + ".\n");
                }
            }
        }
        String queriesPath = outPath.substring(0, outPath.lastIndexOf(".")) + "_initQueries.txt";
        PrintWriter pw = new PrintWriter(queriesPath);
        pw.print(exampleQueries.toString());
        pw.close();

        //rule part = template
        StringBuilder rules = new StringBuilder();
        for (int i = 0; i < atomClusters; i++) {
            for (String e1 : entities) {
                rules.append(defaultWeight + " " + atomClusterName + i + "(X) :- " + e1 + "(X).\n");
            }
            rules.append(atomClusterName + i + "/1 " + defaultWeight + "\n");
        }
        rules.append("\n");
        for (int i = 0; i < bondClusters; i++) {
            for (String r : relations) {
                rules.append(defaultWeight + " " + bondClusterName + i + "(X) :- " + r + "(X).\n");
            }
            rules.append(bondClusterName + i + "/1 " + defaultWeight + "\n");
        }
        StringBuilder softClusterBase = new StringBuilder(rules.toString() + "\n");

        rules.append("\n");
        int a = 0;
        for (int i = 0; i < atomClusters; i++) {
            for (int j = i; j < atomClusters; j++) {
                for (int k = 0; k < bondClusters; k++) {
                    if (Global.getRandomDouble() < subsampleTripleRules)
                        rules.append("finalLambda" + a++ + "(a) :- " + atomClusterName + i + "(A), " + atomClusterName + j + "(B), " + bondClusterName + k + "(C).\n");
                }
            }
        }
        rules.append("\n");
        for (int i = 0; i < a; i++) {
            rules.append(defaultWeight + " " + "finalKappa(a) :- finalLambda" + i + "(a).\n");
        }
        String rulesPath = outPath.substring(0, outPath.lastIndexOf(".")) + "_initRules.txt";
        PrintWriter pw2 = new PrintWriter(rulesPath);
        pw2.print(rules.toString());
        pw2.close();

        return new Triple<>(queriesPath, rulesPath, softClusterBase);
    }
}