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
import ida.ilp.logic.Variable;
import ida.ilp.logic.io.PseudoPrologParser;
import ida.utils.CommandLine;
import ida.utils.Sugar;
import ida.utils.collections.ValueToIndex;
import ida.utils.tuples.Pair;
import ida.utils.tuples.Triple;
import lrnn.construction.example.Example;
import lrnn.construction.template.WeightInitializator;
import lrnn.crossvalidation.Crossvalidation;
import lrnn.crossvalidation.NeuralCrossvalidation;
import lrnn.crossvalidation.SampleSplitter;
import lrnn.global.Global;
import lrnn.global.Glogger;
import lrnn.global.Settings;
import lrnn.grounding.network.GroundKL;
import lrnn.learning.LearningStep;
import lrnn.learning.Result;
import lrnn.learning.Results;
import lrnn.learning.Sample;
import lrnn.learning.functions.ActivationsFast;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by gusta on 31.1.17.
 */
public class SoftClusteringSPI {

    String errorMeasure = "MSE";

    String suffix = "";

    Crossvalidation crossValidation;

    boolean trainTestOnly = false;
    int folds = 10;

    private boolean parallelCrossval = false;   //not working yet! (but with parallel grounding and learning within LRNNs turned on it should run just as fast)

    int searchBeamSize = 10;
    int searchMaxSize = 3;

    int autoencodingSteps = 0;
    int trainingSteps = 1000;

    private boolean reinitializeAllWeightsWithinSPIcycle = false;
    private boolean reinitializeTopLayerWeightsWithinSPIcycle = true;

    private boolean reinitUnusedClusters = true;

    private int atomClusters = 3;
    private int bondClusters = 3;

    private int maxSpiCycles = 5;
    private double minMissedExamples4ruleLearning = 1;

    private boolean alternatingClasses = true;

    private boolean normalizeMseCoefs = true;

    private boolean deepLearning = false;
    private int maxHeadArity = 1;

    int ruleIndex = 0;
    //----------------------------

    String atomClusterName = "clA";
    String bondClusterName = "clB";
    String tmpConstant = "a";
    private double weightMultiplier = 1;

    private int bondId = 0;
    ValueToIndex<Pair<String, String>> bondIDs = new ValueToIndex();
    private double subsampleTripleRules = 1;

    public boolean datasetAlreadyTransformed = true;
    public boolean preparedCrossval = false;


    public static void main(String[] args) throws IOException {

        Map<String, String> arguments = CommandLine.parseParams(args);

        Global.setSeed(1);

        Settings.setDataset(arguments.get("-dataset"));
        SoftClusteringSPI lc = new SoftClusteringSPI();
        lc.suffix = arguments.get("-suf") == null ? lc.suffix : arguments.get("-suf");
        lc.atomClusters = arguments.get("-cls") == null ? lc.atomClusters : Integer.parseInt(arguments.get("-cls"));

        Glogger.suffix = lc.suffix;

        //create logger for all messages within the program
        Glogger.init();


        lc.autoencodingSteps = arguments.get("-aes") == null ? lc.autoencodingSteps : Integer.parseInt(arguments.get("-aes"));
        lc.searchBeamSize = arguments.get("-sbs") == null ? lc.searchBeamSize : Integer.parseInt(arguments.get("-sbs"));
        lc.searchMaxSize = arguments.get("-sms") == null ? lc.searchMaxSize : Integer.parseInt(arguments.get("-sms"));
        lc.trainingSteps = arguments.get("-ls") == null ? lc.trainingSteps : Integer.parseInt(arguments.get("-ls"));
        lc.maxSpiCycles = arguments.get("-cyc") == null ? lc.maxSpiCycles : Integer.parseInt(arguments.get("-cyc"));



        File datasetPath = new File(arguments.get("-dataset"));
        if (lc.preparedCrossval) {
            File[] folds = datasetPath.listFiles(File::isDirectory);
            lc.crossvalidate(folds);
        } else {
            lc.crossvalidate(datasetPath);
        }
    }

    Crossvalidation crossvalidate(File[] foldsPaths) throws IOException {
        crossValidation = new NeuralCrossvalidation(foldsPaths.length);
        for (File path : foldsPaths) {
            Pair<Results, String> trainResults = cycleSPI(new File(path + "/train.txt").getPath());
            //test resulting template
            Results testFoldResults = testLRNNtemplate(new File(path + "/test.txt").getPath(), trainResults);
            Glogger.process("Finished fold " + path);
            crossValidation.loadFoldStats(testFoldResults);
        }
        crossValidation.finalizeCrossvalStats();
        return crossValidation;
    }

    Crossvalidation crossvalidate(File datasetPath) throws IOException {

        List<Sample> samples = Files.lines(datasetPath.toPath()).map(line ->
                new Sample(new Example(line.substring(line.indexOf(" ") + 1) + (line.charAt(line.length() - 1) == '.' ? "" : ".")), line.substring(0, line.indexOf(" ")).matches("[1\\+]") ? 1.0 : 0.0))
                .collect(Collectors.toList());

        //stratified(!) crossvalidation split
        SampleSplitter ss = new SampleSplitter(this.folds, samples);
        List<String> foldPaths = ss.outputSplits(datasetPath.getParent());
        crossValidation = new NeuralCrossvalidation(ss);

        for (String testFold : foldPaths) {
            String testFoldPath = testFold + "_transformed.txt";
            try {
                datasetsETL(testFold, testFoldPath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String trainSet = foldPaths.stream().filter(foldPath -> (!foldPath.equals(testFold) || foldPaths.size() == 1)).map(trainFold -> {
                try {
                    return new String(Files.readAllBytes(Paths.get(trainFold)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return "";
            }).collect(Collectors.joining("\n"));

            try {
                File train = new File(testFold + suffix + "-test/trainSet.txt");
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
            if (trainTestOnly) {
                crossValidation.foldCount = 1;
                break;
            }
        }

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

        if (errorMeasure.equals("MSE")) {
            alternatingClasses = false;
            reinitializeTopLayerWeightsWithinSPIcycle = false;
        }

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
        SimpleLearner sl;
        if (errorMeasure.equals("MSE")) {
            sl = new ContextAwareLearner();
        } else {
            sl = new SimpleLearner(errorMeasure);
        }

        //We can (and perhaps should) actually "learn" the symmetries from data
        SymmetrySaturator symmetrySaturator = new SymmetrySaturator();
        symmetrySaturator.setSymmetries("bond", 3, Sugar.list(new int[]{1, 0, 2}));
        sl.setSaturator(symmetrySaturator);

        MultiExampleDataset dataset = new MultiExampleDataset(reinventedExamples.r, data.r);
        Map<Literal, Double> clusterValues = reinventedExamples.s;

        String a = initialAutoencoding.t.toString();
        String b = new String(Files.readAllBytes(Paths.get(learnedInitialTemplatePath)));
        StringBuilder template = mergeTemplates(a, b, false);

        int iter = 1;
        Pair<Results, String> actualResult = null;
        Pair<Results, String> bestResult = null;
        ClassifierR actualClassifier = new ClassifierR();
        String templPath = "";
        while (true) {
            sl.setDataset(dataset);
            sl.setLanguageBias(dataset.allPredicates());
            sl.setLiteralWeights(clusterValues);
            sl.targetClass = alternatingClasses ? 2 * (iter % 2) - 1 : 0;

            Pair<ClassifierR, Double> res;
            if (sl instanceof ContextAwareLearner) {
                res = ((ContextAwareLearner) sl).beamSearch(this.searchBeamSize, this.searchMaxSize, actualClassifier);
                actualClassifier = res.r;
            } else {
                res = sl.beamSearch(this.searchBeamSize, this.searchMaxSize);
                actualClassifier = res.r;
            }
            Glogger.LogTrain("Iteration: " + iter + " -> best found weighted horn clause classifier: " + res);

            //if (!add) break;

            if (sl instanceof ContextAwareLearner) {
                template = mergeTemplates(template.toString(), templatePartFromClassifier(actualClassifier, iter).toString(), true);
            } else {
                template.append(templatePartFromClassifier(actualClassifier, iter));
            }
            templPath = datasetPath.substring(0, datasetPath.lastIndexOf(".")) + "_template_cycle" + iter + suffix + ".txt";

            Files.write(Paths.get(templPath), template.toString().getBytes());

            actualResult = trainLRNNtemplate(examplesOutPath, templPath, trainingSteps);

            if (bestResult == null || actualResult.r.actualResult.isBetterThen(bestResult.r.actualResult)) {
                bestResult = actualResult;
            }
            if (bestResult.r != null && bestResult.r.actualResult.getMse() < 0.000001) break;

            if (!reinitializeAllWeightsWithinSPIcycle) {   //reuse previously learned weights?
                if (reinitUnusedClusters) {
                    template = mergeTemplates(template.toString(), initialAutoencoding.t.toString(), false);
                    StringBuilder sb = new StringBuilder();
                    for (String s : template.toString().split("\n")) {
                        sb.append(s.replaceAll("0\\.0", WeightInitializator.getWeight() + "") + "\n");
                    }
                    template = sb;
                }
                template = mergeTemplates(template.toString(), new String(Files.readAllBytes(Paths.get(actualResult.s))), true);
                templPath = templPath.substring(0, templPath.lastIndexOf(".")) + suffix + "_merged.txt";
                Files.write(Paths.get(templPath), template.toString().getBytes());
            }
            if (iter >= maxSpiCycles) break;

            if (deepLearning) {
                reinventedExamples = getExtendedExamples(reinventedExamples, examplesOutPath, templPath);   //compute the extended cluster values
                clusterValues = reinventedExamples.s;
            } else {
                Pair<Map<String, Double>, Map<String, Map<String, Double>>> newWeightMapping = extractWeightMappingFromTemplate(actualResult.s);
                weightMapping.r.putAll(newWeightMapping.r);   //update all changed offsets if any
                for (Map.Entry<String, Map<String, Double>> ent : newWeightMapping.s.entrySet()) {  //update all changed weights if any
                    if (weightMapping.s.containsKey(ent.getKey()))
                        weightMapping.s.get(ent.getKey()).putAll(ent.getValue());
                    else
                        weightMapping.s.put(ent.getKey(), ent.getValue());
                }
                clusterValues = transformToWeightedClusters(data.t, weightMapping).s;
            }

            Pair<List<Clause>, List<Double>> subset = getMisclassifiedSubset(alternatingClasses ? (int) (-1 * actualClassifier.coeffs[actualClassifier.coefficients().length - 1]) : 0, actualResult.r, reinventedExamples.r, data.r);  //-1* = we want the next one
            Glogger.LogTrain("Iteration: " + iter + " after weight learning - " + (alternatingClasses ? (int) (actualClassifier.coeffs[actualClassifier.coefficients().length - 1]) == 1 ? " Number of TN + FN: " : " - Number of TP + FP: " : " Numer of all: ") + subset.s.size() + " examples");
            dataset = new MultiExampleDataset(subset.r, subset.s);
            if (subset.r.size() < minMissedExamples4ruleLearning) break;

            iter++;
        }
        bestResult = trainLRNNtemplate(examplesOutPath, bestResult.s, 4 * trainingSteps);
        Glogger.process("...Finished SPI cycle!");
        return actualResult;
    }

    private Pair<List<Clause>, Map<Literal, Double>> getExtendedExamples(Pair<List<Clause>, Map<Literal, Double>> reinventedExamples, String examplesOutPath, String templPath) {
        String[] args = new String[]{"-e", examplesOutPath, "-r", templPath, "-draw", "0"};
        List<String[]> inputs = lrnn.Main.setupFromArguments(args);

        String[] test = inputs.get(0);
        String[] exs = inputs.get(1);
        String[] rules = inputs.get(2);
        String[] pretrainedRules = inputs.get(3);

        //Global.learnableElements = true;
        lrnn.LiftedDataset dataset = lrnn.Main.createDataset(test, exs, rules, pretrainedRules);
        //Global.learnableElements = false;

        Map<Literal, Double> literalValues = new LinkedHashMap<>();
        List<Clause> extClauses = new ArrayList<>();
        for (int i = 0; i < dataset.sampleSplitter.samples.size(); i++) {
            Map<Literal, Double> tmp = new LinkedHashMap<>();
            for (GroundKL groundLiteral : dataset.sampleSplitter.samples.get(i).getBall().groundLiterals) {
                tmp.put(Literal.parseLiteral(groundLiteral.toString(dataset.sampleSplitter.samples.get(i).getBall().constantNames)), Global.getGrounding() == Global.groundingSet.avg ? groundLiteral.getValueAvg() : groundLiteral.getValue());
            }
            literalValues.putAll(tmp);
            Set<Literal> lits = new HashSet<>();
            lits.addAll(reinventedExamples.r.get(i).literals());    //merge with previous clause (non-extended) version
            lits.addAll(tmp.keySet());
            extClauses.add(new Clause(lits.stream().filter(lit -> (lit.predicate().startsWith("cl") || lit.predicate().startsWith("bond"))).collect(Collectors.toList())));
        }

        return new Pair<>(extClauses, literalValues);
    }

    private StringBuilder mergeTemplates(String previous, String learned, boolean adding) {
        previous = previous.replaceAll(", ", ",");
        learned = learned.replaceAll(", ", ",");

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
                        if (adding || merged.containsKey(split[0].trim())) merged.put(split[0].trim(), split[1].trim());
                    } else merged.put(split[0].trim(), split[1].trim());
                } else if (weight.matches("-?\\d+([.,]\\d+)?([eE]-?\\d+)?")) {   //kappa
                    if (i > 0) {
                        if (adding || merged.containsKey(s.substring(s.indexOf(" ")).trim())) {
                            if (reinitializeTopLayerWeightsWithinSPIcycle && s.contains("finalLambda")) {
                                double v = Double.parseDouble(weight);
                                weight = v == 0 ? "0.0" : v / Math.abs(v) + "";
                            }
                            merged.put(s.substring(s.indexOf(" ")).trim(), weight + " ");
                        }
                    } else merged.put(s.substring(s.indexOf(" ")).trim(), weight + " ");
                } else {    //lambda
                    if (i > 0) {
                        if (adding || merged.containsKey(s)) merged.put(s, "");
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
        String[] args = new String[]{"-e", testFold, "-r", trainResults.s, "-draw", "0"};
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
        String[] args = new String[]{"-e", examplesPath, "-r", rulesPath, "-ls", "" + learningSteps, "-draw", "0"};
        List<String[]> inputs = lrnn.Main.setupFromArguments(args);

        //Global.shuffleExamples = false; //we want to keep the order of the input examples

        String[] test = inputs.get(0);
        String[] exs = inputs.get(1);
        String[] rules = inputs.get(2);
        String[] pretrainedRules = inputs.get(3);

        //if (deepLearning) {
        List<String> newrules = new ArrayList<>();
        for (String rule : rules) {
            if (!rule.contains("dummy")) {
                newrules.add(rule);
            }
        }
        rules = newrules.toArray(new String[newrules.size()]);
        //}

        //create ground networks dataset
        lrnn.LiftedDataset dataset = null;
        Results foldRes = null;
        if (learningSteps > 0) {

            dataset = lrnn.Main.createDataset(test, exs, rules, pretrainedRules);
            //start learning
            foldRes = crossValidation.train(dataset.template, dataset.sampleSplitter.samples);
            //also test n the same samples to properly extract misclassified ones
            foldRes = crossValidation.test(dataset.template, foldRes, dataset.sampleSplitter.samples);
        } else {

            dataset = new lrnn.LiftedDataset(rules, pretrainedRules);

        }

        String exportPath = rulesPath.substring(0, rulesPath.lastIndexOf(".")) + suffix + "_learned";
        dataset.template.weightFolder = "";
        dataset.template.exportTemplate(exportPath);
        return new Pair<>(foldRes, exportPath + ".txt");
    }

    private StringBuilder templatePartFromClassifier(ClassifierR classifier, int iter) {
        //String templ = "finalKappa" + "(" + tmpConstant + ") :- " + hornClause.r.body().toString().replaceAll(" ", "") + ".\n";

        if (classifier.rules().length > 1) iter = 1;
        StringBuilder templ = new StringBuilder();

        if (deepLearning) {
            if (errorMeasure.equals("MSE")) {
                ruleIndex = 0;
            }
            List<HornClause> newHcs = new ArrayList<>();
            for (HornClause hc : classifier.rules()) {
                if (hc.head() == null) {
                    Set<Literal> allHeads = new HashSet<>();
                    List<Variable> vars = hc.variables().stream().collect(Collectors.toList());
                    int lev = getMaximalLiteralLevel(hc);
                    addAllHeads("head" + (lev), vars, new ArrayList<>(), allHeads);
                    for (Literal head : allHeads) {
                        templ.append(head + " :- " + hc.body() + ".\n");
                        for (int i = 0; i < atomClusters; i++) {
                            templ.append("0.0 cluster" + (lev + 1) + "L" + i + "(" + Arrays.toString(head.arguments()).replaceAll("\\[", "").replaceAll("]", "") + ")" + " :- " + head + ".\n");
                        }
                    }
                    for (int i = 0; i < atomClusters; i++) {
                        templ.append("dummy" + (lev + 1) + "L" + i + "(a) :- cluster" + (lev + 1) + "L" + i + "(" + Arrays.toString(allHeads.iterator().next().arguments()).replaceAll("\\[", "").replaceAll("]", "") + ").\n");
                        templ.append("0.00000001 finalKappa(a) :- dummy" + (lev + 1) + "L" + i + "(a).\n");
                    }
                    //break;
                }
            }
        }


        for (int i = 0; i < classifier.rules().length; i++) {
            templ.append("finalLambda").append(iter + i).append("(").append(tmpConstant).append(") :- ").append(classifier.rules()[i].body().toString().replaceAll(" ", "")).append(".\n");
            templ.append(normalizeMseCoefs ? classifier.coefficients()[i] / Math.abs(classifier.coefficients()[i]) : classifier.coefficients()[i]).append(" finalKappa(").append(tmpConstant).append(") :- finalLambda").append(iter + i).append("(").append(tmpConstant).append(").\n");
        }
        if (classifier.coefficients().length > classifier.rules().length) {
            templ.append("finalKappa/1 " + (normalizeMseCoefs ? classifier.coeffs[classifier.rules().length] / Math.abs(classifier.coeffs[classifier.rules().length]) : classifier.coeffs[classifier.rules().length]) + "\n");
            if (classifier.coefficients().length > classifier.rules().length + 1)
                Glogger.err("Classifier has more coefficients than rules+offset!");
        }


        return templ;
    }


    private int getMaximalLiteralLevel(HornClause hc) {
        int maxLevel = 0;
        for (Literal literal : hc.body().literals()) {
            if (literal.predicate().startsWith("cluster")) {
                int level = Integer.parseInt(literal.predicate().substring(7, literal.predicate().indexOf("L")));
                if (level > maxLevel) {
                    maxLevel = level;
                }
            }
        }
        return maxLevel;
    }

    private void addAllHeads(String headName, List<Variable> allVars, List<Variable> selectedVars, Set<Literal> heads) {
        if (selectedVars.size() == maxHeadArity) {
            heads.add(new Literal(headName + "L" + ruleIndex++, selectedVars));
            return;
        }
        for (int i = 0; i < allVars.size(); i++) {
            selectedVars.add(allVars.get(i));
            addAllHeads(headName, allVars, selectedVars, heads);
            selectedVars.remove(allVars.get(i));
        }
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
        //bondId = 0;
        //bondIDs = new ValueToIndex();

        List<Literal> literals = new ArrayList<>();
        for (Literal l : c.literals()) {
            if (l.predicate().equals("bond") && !datasetAlreadyTransformed) {
                int bondID = -1;
                // get bondID
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
            } else if (datasetAlreadyTransformed) {
                literals.add(l);
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
            if (split[0].matches("-?\\d+([.,]\\d+)?([eE]-?\\d+)?")) { //weighted kappa clause to a cluster
                double weight = Double.parseDouble(split[0]);
                String atomType = split[3].substring(0, split[3].indexOf("("));
                String cluster = split[1].substring(0, split[1].indexOf("("));
                res.putIfAbsent(atomType, new HashMap<>());
                res.get(atomType).put(cluster, weight);
            } else if (split[1].matches("-?\\d+([.,]\\d+)?([eE]-?\\d+)?")) { //offset
                String kappa = split[0].substring(0, split[0].indexOf("/"));
                double offset = Double.parseDouble(split[1]);
                offsets.put(kappa, offset);
            }
        }
        return new Pair<>(offsets, res);
    }

    /**
     * weighted cluster are common for all examples, thus they need to have unique identifiers here!
     *
     * @param clauses
     * @param weightMapping
     * @return
     */
    public Pair<List<Clause>, Map<Literal, Double>> transformToWeightedClusters(List<Clause> clauses, Pair<Map<String, Double>, Map<String, Map<String, Double>>> weightMapping) {
        List<Clause> newClauses = new ArrayList<>();
        Map<Literal, Double> weights = new HashMap<Literal, Double>();
        ClassifierR cls = new ClassifierR();

        for (Clause clause : clauses) {
            Set<Literal> lits = new HashSet<>();
            for (Literal l : clause.literals()) {
                if (l.arity() == 1 /*&& !l.predicate().startsWith("rel") && (l.predicate().startsWith("atm") || l.predicate().matches("^[0-9]")) */) {
                    Map<String, Double> clusterWeights = weightMapping.s.get(l.predicate());
                    for (Map.Entry<String, Double> clusterWeight : clusterWeights.entrySet()) {
                        Literal cl = new Literal(clusterWeight.getKey(), l.get(0));
                        lits.add(cl);
                        Double offset = weightMapping.r.get(clusterWeight.getKey());
                        weights.put(cl, ActivationsFast.kappaActivation(new double[]{clusterWeight.getValue()}, offset)); //get value (=sigm(input+offset)), not just weight
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
        Set<String> attributes = new HashSet<>();
        Set<String> entities = new HashSet<>();
        Set<String> relations = new HashSet<>();

        Map<Clause, Integer> examples = new LinkedHashMap<>();

        double globalCount = 0;
        String queriesPath = "";
        if (datasetAlreadyTransformed) {
            for (Clause c : clauses) {
                for (Literal literal : c.literals()) {
                    if (literal.predicate().equals("bond")) {//bond
                        relations.add(literal.get(2).name());
                    } else if (literal.predicate().startsWith("atm_")) {
                        entities.add(literal.predicate());
                    } else if (literal.toString().contains("dummy")) {
                        attributes.add(literal.predicate());
                    } else if (literal.arity() == 1 && !relations.contains(literal.predicate())) {
                        entities.add(literal.predicate());
                    }
                }
            }
        } else {
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
            queriesPath = outPath.substring(0, outPath.lastIndexOf(".")) + suffix + "_initQueries.txt";
            PrintWriter pw = new PrintWriter(queriesPath);
            pw.print(exampleQueries.toString());
            pw.close();
        }
        //rule part = template
        StringBuilder rules = new StringBuilder();
        //zero arity = propositional attributes - plug in to final log.reg.
        for (String att : attributes) {
            rules.append("0.0 finalKappa(a) :- " + att + "(X).\n");
        }
        for (int i = 0; i < atomClusters; i++) {
            for (String e1 : entities) {
                rules.append(defaultWeight + " " + atomClusterName + i + "(X) :- " + e1 + "(X).\n");
            }
            rules.append(atomClusterName + i + "/1 " + defaultWeight + "\n");
            rules.append("dummy_baseA" + i + "(a) :- " + atomClusterName + i + "(X).\n");
            rules.append("0.00000001 finalKappa(a) :- dummy_baseA" + i + "(a).\n");
        }
        rules.append("\n");
        for (int i = 0; i < bondClusters; i++) {
            for (String r : relations) {
                rules.append(defaultWeight + " " + bondClusterName + i + "(X) :- " + r + "(X).\n");
            }
            rules.append(bondClusterName + i + "/1 " + defaultWeight + "\n");
            rules.append("dummy_baseB" + i + "(a) :- " + bondClusterName + i + "(X).\n");
            rules.append("0.00000001 finalKappa(a) :- dummy_baseB" + i + "(a).\n");
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
        String rulesPath = outPath.substring(0, outPath.lastIndexOf(".")) + suffix + "_initRules.txt";
        PrintWriter pw2 = new PrintWriter(rulesPath);
        pw2.print(rules.toString());
        pw2.close();

        return new Triple<>(queriesPath, rulesPath, softClusterBase);
    }
}