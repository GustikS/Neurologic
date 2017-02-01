/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer;

import discoverer.construction.ConstantFactory;
import discoverer.construction.ExampleFactory;
import discoverer.construction.Parser;
import static discoverer.construction.Parser.getWeightLen;
import discoverer.construction.TemplateFactory;
import discoverer.construction.Variable;
import discoverer.construction.example.Example;
import discoverer.construction.template.KL;
import discoverer.construction.template.LiftedTemplate;
import static discoverer.construction.template.LightTemplate.weightFolder;
import discoverer.construction.template.NLPtemplate;
import discoverer.construction.template.rules.Rule;
import discoverer.construction.template.rules.SubKL;
import discoverer.crossvalidation.Crossvalidation;
import discoverer.crossvalidation.NeuralCrossvalidation;
import discoverer.crossvalidation.SampleSplitter;
import discoverer.drawing.Dotter;
import discoverer.drawing.GroundDotter;
import discoverer.global.Global;
import discoverer.global.Glogger;
import discoverer.global.Settings;
import discoverer.grounding.BottomUpConnector;
import discoverer.grounding.evaluation.Evaluator;
import discoverer.grounding.evaluation.GroundedTemplate;
import discoverer.grounding.network.GroundKL;
import discoverer.learning.Result;
import discoverer.learning.Results;
import discoverer.learning.Sample;
import discoverer.learning.Saver;
import discoverer.learning.learners.LearnerStandard;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gusta
 */
public class NLPdataset extends Main {

    TemplateFactory templateFactory;
    NLPtemplate template;

    public HashMap<String, Integer> constantNames2Id;
    //facts
    public static Example facts;

    private boolean exportCache = false;

    public String embeddingsPath = "./in/embeddings.csv";

    public static boolean factRules = false;
    public static boolean selectingFacts = false;

    public static void main(String[] args) {
        Glogger.resultsDir = "./results/";
        //setup all parameters and load all the necessary input files
        List<String[]> inputs = setupFromArguments(args);
        //create logger for all messages within the program
        Glogger.init();

        if (inputs.size() < 3) {
            Glogger.err("Missing input files!");
            return;
        }

        String[] queries = inputs.get(0);
        String[] facts = inputs.get(1);
        String[] rules = inputs.get(2);

        String[] testQueries = inputs.get(3);

        //create ground networks dataset
        NLPdataset dataset = new NLPdataset(facts, rules);

        //start learning
        String[] results = dataset.learnOn(queries);
        //dataset.export(results, "NLPtrain");

        /*
        Settings.learningSteps = 0;
        String[] results2 = dataset.learnOn(testQueries);
        //dataset.evaluate();
        dataset.export(results2, "NLPtest");
         */
    }

    private NLPdataset(String[] iFacts, String[] iRules) {
        Global.weightFolder = "./weights/";
        weightFolder = "./weights/";
        Dotter.outPath = "./images/";

        Global.NLPtemplate = true;
        Global.molecularTemplates = false;
        Global.weightedFacts = true;

        //Global.templateConstants = true;
        //Global.recursion = true;
        //Global.alldiff = true;
        //Global.embeddings = true;
        //Global.cacheEnabled = true;
        if (factRules) {
            templateFactory = new TemplateFactory();
            NLPtemplate construct = (NLPtemplate) templateFactory.construct(iFacts);
            List<Rule> factRules = templateFactory.getRules();
            HashMap<String, HashSet<Rule>> specific = construct.createRelevantRules(factRules);

            templateFactory.templateRules = new LinkedList<>();
            template = (NLPtemplate) templateFactory.construct(iRules);
            LinkedHashSet<Rule> common = new LinkedHashSet<>();
            common.addAll(templateFactory.getRules());

            /*
            String[] result = Arrays.copyOf(iFacts, iFacts.length + iRules.length);
            System.arraycopy(iRules, 0, result, iFacts.length, iRules.length);
            
            templateFactory.templateRules = new LinkedList<>();
            template = (NLPtemplate) templateFactory.construct(result);
             */
            //template.rules.addAll(common);
            template.rules.addAll(factRules);

            template.commonRules = common;
            template.relevantRules = specific;

            ExampleFactory eFactory = new ExampleFactory();
            facts = eFactory.construct("1.0 fact(a,b).");
            construct.createRelevantFacts(facts);
            if (Global.weightedFacts) {
                facts.setWeightedFacts(new double[]{1.0}, templateFactory.constructFacts("1.0 fact(a,b)."));
            }
        } else {
            templateFactory = new TemplateFactory();
            template = (NLPtemplate) templateFactory.construct(iRules);
            //contruct a fact store = actually like a one huge example graph
            ExampleFactory eFactory = new ExampleFactory();
            double[] weights = new double[iFacts.length];
            StringBuilder sb = new StringBuilder("1.0 ");
            for (int i = 0; i < iFacts.length; i++) {
                int expLen = getWeightLen(iFacts[i]);
                if (expLen > 0) {
                    weights[i] = Double.parseDouble(iFacts[i].substring(0, expLen));
                } else {
                    weights[i] = 1;
                }
                sb.append(iFacts[i].substring(expLen, iFacts[i].length())).append(",");
            }
            sb.replace(sb.length() - 1, sb.length(), ".");

            facts = eFactory.construct(sb.toString());

            if (Global.weightedFacts) {
                facts.setWeightedFacts(weights, templateFactory.constructFacts(sb.toString()));
            }
        }

        for (Map.Entry<String, Integer> ent : ExampleFactory.getConstMap().entrySet()) {
            ConstantFactory.construct(ent.getKey());
        }

        template.constantNames = facts.constantNames;

        if (Global.drawing) {
        //    Dotter.draw(template.KLs.values(), "initNLPtemplate");
        }

        if (Global.embeddings) {
            ConstantFactory.loadEmbeddings(embeddingsPath);     //TO change
        }

        if (iFacts == null) {
            return;
        }
    }

    private void evaluate() {
        GroundedTemplate proof = template.evaluate(facts);
        GroundDotter.draw(proof, "eval");
    }

    private String[] learnOn(String[] queries) {
        Results stats = new Results();
        List<Sample> samples = new ArrayList<>();
        int i = 0;
        String[] results = new String[queries.length];
        List<String> names = new ArrayList<>();
        for (String query : queries) {
            Double targetValue = null;

            int wLen = Parser.getWeightLen(query);
            if (wLen > 0) {
                targetValue = Double.parseDouble(query.substring(0, wLen));
            }
            names.add(query.substring(wLen, query.length()));

            GroundedTemplate proof;
            if (Global.bottomUp) {
                System.out.println(i);
                Glogger.LogTrain("exampleGrounded: " + i);
                proof = template.queryBottomUp(facts, query.substring(wLen, query.length()));
            } else {
                String[][] queryTokens = Parser.parseQuery(query.substring(wLen, query.length()));
                String signature = queryTokens[0][0];
                KL target = template.KLs.get(signature);

                templateFactory.clearVarFactory();
                List<Variable> vars = new ArrayList<>();
                for (int j = 1; j < queryTokens[0].length; j++) {
                    Variable t = templateFactory.constructTerm(queryTokens[0][j]);
                    vars.add(t);
                }
                proof = template.queryTopDown(target, vars, facts);
            }

            if (Global.drawing) {
                GroundDotter.draw(proof, i + "beforeLearning_" + names.get(i));
            }
            Sample sample = new Sample(proof, targetValue);
            Example example = new Example();
            example.setExpectedValue(targetValue);
            example.constantNames = template.constantNames;
            sample.setExample(example);
            samples.add(sample);
            i++;
        }

        i = 0;
        if (Global.fastVersion) {
            LiftedDataset ld = new LiftedDataset();
            ld.sampleSplitter = new SampleSplitter(Settings.folds, samples);
            ld.template = template;

            if (Global.saveGroundedDataset) {
                ld.saveDataset(Settings.getDataset().replaceAll("-", "/") + ".ser");
            }

            NeuralDataset nd = new NeuralDataset(ld);
            if (Global.drawing) {
                for (Sample sam : nd.sampleSplitter.samples) {
                    GroundDotter.drawNeural(sam.neuralNetwork, i + "neural_" + names.get(i++), nd.template.sharedWeights);
                }

            }
            Crossvalidation cv = new NeuralCrossvalidation(nd);
            if (Settings.folds > 1) {
                cv.crossvalidate(nd);
            } else {
                cv.trainTestFold(template, samples, samples, 0);
            }

            LiftedTemplate templ = (LiftedTemplate) nd.template;
            templ.setWeightsFromArray(templ.weightMapping, templ.sharedWeights);    //map the learned weights back to original logical structures (rules)
            i=0;
            //map learned outputs back to ball Avg outputs (just to make sure), samples should be in the same order (it's the same SampleSplitter)
            for (Sample sam : nd.sampleSplitter.samples) {
                Evaluator.evaluateAvg(sam.getBall());
                if (Global.drawing) {
                    GroundDotter.draw(sam.getBall(), i + "afterEvaluation_" + names.get(i++));
                }
            }
            samples = nd.sampleSplitter.samples;

        } else {
            List<Sample> learningSamples = new ArrayList<>();
            learningSamples.addAll(samples);
            for (int j = 0; j < Settings.learningSteps; j++) {
                System.out.println("learning step: " + j);
                Collections.shuffle(learningSamples, Global.getRg());
                for (Sample sample : learningSamples) {
                    template.evaluateProof(sample.getBall());
                    template.updateWeights(sample.getBall(), sample.targetValue);
                    if (Global.drawing) {
                        //    template.evaluateProof(sample.getBall());
                        //    GroundDotter.draw(sample.getBall(), "afterLearning");
                    }

                }
            }

            i = 0;
            for (Sample sample : samples) {
                template.evaluateProof(sample.getBall());
                double res;
                if (Global.getGrounding() == Global.groundingSet.max) {
                    res = sample.getBall().valMax;
                } else {
                    res = sample.getBall().valAvg;
                }
                if (Global.drawing) {
                    GroundDotter.draw(sample.getBall(), i + "afterEvaluation_" + names.get(i));
                }
                stats.add(new Result(res, sample.targetValue));
                results[i] = (int) sample.targetValue + " " + res + " <- " + names.get(i);
                Glogger.out(results[i]);
                i++;
            }
            Glogger.process("Saved training error as best of all restarts =\t" + stats.getLearningError() + " (maj: " + stats.getMajorityClass() + ")" + " (th: " + stats.getThreshold() + ")" + " (disp: " + stats.getDispersion() + ")");
        }
        return results;
    }

    public void export(String[] results, String destination) {
        if (Global.embeddings) {
            ConstantFactory.exportEmbeddings(destination);
        }
        template.exportTemplate(destination);
//        template.exportWeightMatrix(destination);
//        template.exportValueMatrix(destination, ((BottomUpConnector) template.prover).getBtmUpCache());
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(weightFolder + destination + "-facts.w"), "utf-8"));
            for (GroundKL gkl : facts.storedFacts.values()) {
                double val;
                if (Global.getGrounding() == Global.groundingSet.avg) {
                    val = gkl.getValueAvg();
                } else {
                    val = gkl.getValue();
                }
                StringBuilder sb = new StringBuilder(val + "  " + gkl.getGeneral().getPredicateName() + "(");
                for (int i : gkl.getTermList()) {
                    sb.append(facts.constantNames.get(i)).append(",");
                }
                sb.replace(sb.length() - 1, sb.length(), ")\n");
                writer.write(sb.toString());
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Saver.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Saver.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(Saver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(weightFolder + destination + "-answers.w"), "utf-8"));
            for (String result : results) {
                writer.write(result + "\n");
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Saver.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Saver.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(Saver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (exportCache && Global.isCacheEnabled()) {
            try {
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(weightFolder + destination + "-cache.w"), "utf-8"));
                if (Global.bottomUp) {
                    Collection<GroundKL> cache = ((BottomUpConnector) template.prover).getBtmUpCache();
                    for (GroundKL ent : cache) {
                        if (ent == null || (ent.getValue() == null) && ent.getValueAvg() == null) {
                            continue;
                        }
                        //System.out.println(ent.getValue() + " : " + ent.getValue().getLast().toString(template.constantNames));
                        writer.write(ent.getValue() + " ; " + ent.getValueAvg() + " ; " + ent.toString(template.constantNames) + "\n");
                        //GroundDotter.draw(ent.getValue(), ent.getKey().getParent().name);
                    }
                } else {
                    HashMap<SubKL, GroundedTemplate> cache = template.prover.getCache();
                    for (Map.Entry<SubKL, GroundedTemplate> ent : cache.entrySet()) {
                        if (ent.getValue() == null) {
                            continue;
                        }
                        ent.getValue().constantNames = facts.constantNames;
                        //System.out.println(ent.getValue() + " : " + ent.getValue().getLast().toString(template.constantNames));
                        writer.write(ent.getValue() + " -> " + ent.getValue().getLast().toString(template.constantNames) + "\n");
                        //GroundDotter.draw(ent.getValue(), ent.getKey().getParent().name);
                    }
                }
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(Saver.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Saver.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    writer.close();
                } catch (IOException ex) {
                    Logger.getLogger(Saver.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

}
