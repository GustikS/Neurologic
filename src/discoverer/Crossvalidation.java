package discoverer;

import discoverer.learning.Invalidator;
import extras.BatchLearner;
import discoverer.construction.example.Example;
import discoverer.construction.ExampleFactory;
import discoverer.global.Global;
import discoverer.construction.network.KL;
import discoverer.construction.NetworkFactory;
import discoverer.construction.network.Network;
import discoverer.construction.network.rules.KappaRule;
import discoverer.construction.network.rules.Rule;
import discoverer.drawing.Dotter;
import discoverer.drawing.GroundDotter;
import discoverer.global.Glogger;
import discoverer.global.Settings;
import discoverer.global.Tuple;
import discoverer.grounding.ForwardChecker;
import discoverer.grounding.evaluation.Ball;
import discoverer.grounding.Grounder;
import discoverer.grounding.network.GroundKappa;
import discoverer.grounding.network.GroundLambda;
import discoverer.learning.Result;
import discoverer.learning.Results;
import discoverer.learning.Sample;
import discoverer.learning.Saver;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Wrapper for running test with n-fold stratification
 */
public class Crossvalidation {

    /**
     * simple version with no merging etc.
     *
     * @param train
     * @param test
     * @param rules
     */
    public void trainTest(String[] train, String[] test, String[] rules) {
        Network network = createNetwork(rules, "network"); // 2nd
        List<Example> trainEx = createExamples(train, Settings.maxExamples);
        List<Example> testEx = createExamples(test, Settings.maxExamples);

        Results res = train(network, trainEx);
        Glogger.process("------finished training--------");
        double trainErr = res.getLearningError();
        double testErr = test(network, res, testEx);
        double testMaj = testM(testEx, trainEx);

        Glogger.LogRes("--------------");
        Glogger.LogRes("Final train error: " + trainErr);   //do NOT change the texts here (used in excel macro)
        Glogger.LogRes("Final test error: " + testErr);
        Glogger.LogRes("Final majority error: " + testMaj);
    }

    /**
     * Main solving method
     *
     * @param rules
     * @param pretrained
     * @param ex
     */
    public void crossValidate(String[] ex, String[] rules, String[] pretrained) {

        Network template = createNetwork(pretrained, "pretrained"); // 1st
        Network network = createNetwork(rules, "network"); // 2nd

        if (Global.getMerging() == Global.mergingOptions.weights) {
            network.merge(template); // 3rd
            Glogger.process("merged weights with template of : " + template);
        }
        if (Global.getMerging() == Global.mergingOptions.onTop) {
            network = network.mergeOnTop(template); // 3rd
            Glogger.process("merged structure on top with template of : " + template);
        }
        //Dotter.draw(network.last);

        if (Global.exporting) {
            network.exportTemplate("merged");
            network.exportWeightMatrix("merged");
        }

        //creates examples with corresponding ID mapping and chunk representations
        List<Example> examples = createExamples(ex, Settings.maxExamples);
        //k-fold stratified example(same #positives in folds) splitting structure
        ExampleSplitter es = new ExampleSplitter(Settings.folds, examples);

        double testErr = 0;
        double testMaj = 0;
        double trainErr = 0;

        for (es.testFold = 0; es.testFold < es.foldCount; es.testFold++) { //iterating the test fold
            if (Global.exporting) {
                network.exportWeightMatrix("init-fold" + es.testFold);
            }

            Glogger.process("--------------------processing fold " + es.testFold + "----------------------");
            Results res = train(network, es.getTrain());
            Glogger.process("-------finished trainin fold " + es.testFold + ", going to test----------------------");
            trainErr += res.getLearningError();
            testErr += test(network, res, es.getTest());
            testMaj += testM(es.getTest(), es.getTrain());

            if (Global.exporting) {
                network.exportTemplate("learned-fold" + es.testFold);
                network.exportWeightMatrix("learned-fold" + es.testFold);
                Network.saveNetwork(network, "learned-fold" + es.testFold);
            }
            if (Global.drawing){
                Dotter.draw(network.last, "learned_fold" + es.testFold);
            }
            //Network nn = Network.loadNetwork();

            //Invalidator.invalidate(network); //1st
            network.invalidateWeights();
            
            network.merge(template); // 2nd
        }

        trainErr /= es.foldCount;
        testErr /= es.foldCount;
        testMaj /= es.foldCount;
        Glogger.LogRes("--------------");
        Glogger.LogRes("Final train error: " + trainErr);   //do NOT change the texts here (used in excel macro)
        Glogger.LogRes("Final test error: " + testErr);
        Glogger.LogRes("Final majority error: " + testMaj);
    }

    Network createNetwork(String[] rules, String name) {
        //factory + subfactories initialization
        NetworkFactory nf = null;
        //constructs the whole L-K network from rules with support of grounded classes and element mappers, return LAST line rule's literal(=KL)!
        Network network = null;

        if (rules.length == 0) {
            Glogger.out("network template -" + name + "- is empty, may try to load manually if GUI is on...");
            if (Global.isManualLoadNetwork()) {
                network = Network.loadNetwork();
                return network;
            }
            return null;
        }
        nf = new NetworkFactory();
        network = nf.construct(rules);

        network.exportTemplate(name);
        network.exportWeightMatrix(name);
        return network;
    }

    private double testM(List<Example> test, List<Example> train) {
        int pos = 0;
        for (Example e : train) {
            if (e.getExpectedValue() == 1) {
                pos++;
            }
        }

        double err = 0;
        if (pos >= (double) train.size() / 2) {
            for (Example e : test) {
                if (e.getExpectedValue() == 0) {
                    err++;
                }
            }
        } else {
            for (Example e : test) {
                if (e.getExpectedValue() == 1) {
                    err++;
                }
            }
        }

        err /= test.size();
        Glogger.LogRes("Majority for this fold -> " + err + "\n");
        return err;
    }

    /**
     * creates shuffled list of Examples from descriptions(conjunction of
     * literals)
     *
     * @param ex
     * @param maxExamples
     * @return
     */
    public List<Example> createExamples(String[] ex, int maxExamples) {
        ExampleFactory eFactory = new ExampleFactory();
        List<Example> examples = new ArrayList<>();
        int positives = 0;
        int negatives = 0;
        for (int i = 0; i < ex.length; i++) {
            //main creation of an example
            Example e = eFactory.construct(ex[i]);
            examples.add(e);
            if (e.getExpectedValue() > 0) {
                positives++;
            } else {
                negatives++;
            }
        }
        Collections.shuffle(examples, Global.getRg());

        if (maxExamples > examples.size()) {
            return examples;
        }
        //stratified decreasing the number of examples
        positives = (int) Math.round(1.0 * positives / (examples.size()) * maxExamples);
        negatives = (int) Math.round(1.0 * negatives / (examples.size()) * maxExamples);
        List<Example> finExamples = new ArrayList<Example>();

        for (Example e : examples) {
            if (e.getExpectedValue() > 0) {
                positives--;
                if (positives >= 0) {
                    finExamples.add(e);
                }
            } else {
                negatives--;
                if (negatives >= 0) {
                    finExamples.add(e);
                }
            }
        }

        return finExamples;
    }

    /**
     * the whole network training
     *
     * @param net
     * @param batch
     * @param network
     * @param examples
     * @param learningStepCount
     * @param learningEpochs
     * @param restartCount
     * @param learnRate
     * @return
     */
    public Results train(Network network, List<Example> examples) {
        //double thresh;
        Results res = null;
        if (Global.isCheckback()) {
            if (Global.getGrounding() == Global.groundingSet.avg) {
                Learner s = new Learner();
                res = s.checkbackAvg(network, examples);
            } else {
                Learner s = new Learner();
                res = s.checkback(network, examples);
            }
        } else if (Global.getBatch() == Global.batch.YES) {
            BatchLearner bs = new BatchLearner();
            res = bs.solve(network, examples);
        } else if (Global.isCumulativeRestarts()) {
            if (Global.getGrounding() == Global.groundingSet.avg) {
                Learner s = new Learner();
                res = s.solveAvgIterative(network, examples);
            } else if (Global.getGrounding() == Global.groundingSet.max) {
                Learner s = new Learner();
                res = s.solveMaxIterative(network, examples);
            }
        } else {
            if (Global.getGrounding() == Global.groundingSet.avg) {
                Learner s = new Learner();
                res = s.solveAvg(network, examples);
            } else if (Global.getGrounding() == Global.groundingSet.max) {
                Learner s = new Learner();
                res = s.solveMax(network, examples);
            }
        }

        return res;
    }

    public double test(Network net, Results res, List<Example> examples) {
        KL network = net.last;
        ForwardChecker.exnum = 0;
        double error = 0.0;

        Results results = new Results();    //we didn't store the whole ground networks(one for each example), just the weights of program
        HashMap<String, Double> ballvalues = new HashMap<>();
        HashMap<String, Double> atoms = new HashMap<>();

        for (Example example : examples) {
            Ball b = Grounder.solve(network, example);

            double ballValue = -1;
            if (b != null) {
                if (Global.getGrounding() == Global.groundingSet.avg) {
                    ballValue = b.valAvg;
                } else if (Global.getGrounding() == Global.groundingSet.max) {
                    ballValue = b.valMax;
                } else {
                    throw new AssertionError();
                }
            }
            //ballvalues.add(ballValue);

            GroundKappa toxic;
            if (b.getLast() instanceof GroundLambda) {
                GroundLambda last = (GroundLambda) b.getLast();
                List<GroundKappa> conjuncts = last.getConjuncts();
                toxic = conjuncts.get(0);
            } else {
                GroundKappa last = (GroundKappa) b.getLast();
                toxic = last;
            }

            GroundLambda ring5 = null;
            GroundLambda ring4 = null;
            GroundLambda ring3 = null;
            for (int i = 0; i < toxic.getDisjuncts().size(); i++) {
                if (toxic.getDisjuncts().get(i).x.getGeneral().getName().contains("ring5")) {
                    ring5 = toxic.getDisjuncts().get(i).x;
                }
                if (toxic.getDisjuncts().get(i).x.getGeneral().getName().contains("ring4")) {
                    ring4 = toxic.getDisjuncts().get(i).x;
                }
                if (toxic.getDisjuncts().get(i).x.getGeneral().getName().contains("ring3")) {
                    ring3 = toxic.getDisjuncts().get(i).x;
                }
            }
            if (ring5 != null) {
                String r5 = "";
                for (GroundKappa gk : ring5.getConjuncts()) {
                    //r5 += example.constantNames.get(term) + "-";
                    r5 += gk.getDisjuncts().get(0).x.getConjuncts().get(0).getGeneral().getName() + "-";
                }
                r5 += ring5.getConjuncts().get(ring5.getConjuncts().size() - 1).getDisjuncts().get(0).x.getConjuncts().get(1).getGeneral().getName();
                atoms.put(r5, ring5.getValueAvg());

                r5 = "";
                for (Integer term : ring5.getTermList()) {
                    r5 += example.constantNames.get(term) + "-";
                }
                ballvalues.put(r5, ring5.getValueAvg());
            }

            if (ring4 != null) {
                String r4 = "";
                for (GroundKappa gk : ring4.getConjuncts()) {
                    //r5 += example.constantNames.get(term) + "-";
                    r4 += gk.getDisjuncts().get(0).x.getConjuncts().get(0).getGeneral().getName() + "-";
                }
                r4 += ring4.getConjuncts().get(ring4.getConjuncts().size() - 1).getDisjuncts().get(0).x.getConjuncts().get(1).getGeneral().getName();
                atoms.put(r4, ring4.getValueAvg());

                r4 = "";
                for (Integer term : ring4.getTermList()) {
                    r4 += example.constantNames.get(term) + "-";
                }
                ballvalues.put(r4, ring4.getValueAvg());
            }

            if (ring3 != null) {
                String r3 = "";
                for (GroundKappa gk : ring3.getConjuncts()) {
                    //r5 += example.constantNames.get(term) + "-";
                    r3 += gk.getDisjuncts().get(0).x.getConjuncts().get(0).getGeneral().getName() + "-";
                }
                r3 += ring3.getConjuncts().get(ring3.getConjuncts().size() - 1).getDisjuncts().get(0).x.getConjuncts().get(1).getGeneral().getName();
                atoms.put(r3, ring3.getValueAvg());

                r3 = "";
                for (Integer term : ring3.getTermList()) {
                    r3 += example.constantNames.get(term) + "-";
                }
                ballvalues.put(r3, ring3.getValueAvg());
            } else {
                //ballvalues.add(-1.0);
            }

            results.add(new Result(ballValue, example.getExpectedValue()));

            double clas = ballValue > res.getThreshold() ? 1.0 : 0.0;
            Glogger.info("Classified -> " + clas + " Expected -> " + example.getExpectedValue() + " Out -> " + ballValue + " Thresh -> " + res.getThreshold());
            if (clas != example.getExpectedValue()) {
                error += 1.0;
            }
        }

        double err = error / examples.size();
        Glogger.LogRes("Fold Train error : " + res.getLearningError());
        Glogger.LogRes("Fold Test error : " + err);
        Glogger.LogRes("Fold re-calculated threshold Test error : " + results.getLearningError());

        Glogger.LogTrain("------test sample values-----");
        int a = 0;
        for (Map.Entry<String, Double> ent : ballvalues.entrySet()) {
            System.out.println(a++ + " : " + ent.getKey() + " : " + ent.getValue());
        }
        Glogger.LogTrain("--------------samples---------------");
        for (Map.Entry<String, Double> ent : atoms.entrySet()) {
            System.out.println(a++ + " : " + ent.getKey() + " : " + ent.getValue());
        }
        Glogger.LogTrain("------end of test sample values-----");

        return err;
    }
}
