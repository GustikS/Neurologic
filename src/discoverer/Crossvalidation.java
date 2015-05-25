package discoverer;

import discoverer.learning.Invalidator;
import extras.BatchLearner;
import discoverer.construction.example.Example;
import discoverer.construction.ExampleFactory;
import discoverer.global.Global;
import discoverer.construction.network.KL;
import discoverer.construction.NetworkFactory;
import discoverer.construction.network.Network;
import discoverer.construction.network.rules.Rule;
import discoverer.global.Glogger;
import discoverer.global.Settings;
import discoverer.grounding.ForwardChecker;
import discoverer.grounding.evaluation.Ball;
import discoverer.grounding.Grounder;
import discoverer.learning.Result;
import discoverer.learning.Results;
import discoverer.learning.Sample;
import discoverer.learning.Saver;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Wrapper for running test with n-fold stratification
 */
public class Crossvalidation {

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
        network.merge(template); // 3rd
        network.exportTemplate("merged");
        network.exportWeightMatrix("merged");

        //creates examples with corresponding ID mapping and chunk representations
        List<Example> examples = createExamples(ex, Settings.maxExamples);
        //k-fold stratified example(same #positives in folds) splitting structure
        ExampleSplitter es = new ExampleSplitter(Settings.folds, examples);

        double testErr = 0;
        double testMaj = 0;
        double trainErr = 0;

        for (es.testFold = 0; es.testFold < es.foldCount; es.testFold++) { //iterating the test fold
            network.exportWeightMatrix("init-fold" + es.testFold);

            Glogger.process("--------------------processing fold " + es.testFold + "----------------------");
            Results res = train(network, es.getTrain());
            Glogger.process("-------finished trainin fold " + es.testFold + ", going to test----------------------");
            trainErr += res.getLearningError();
            testErr += test(network, res, es.getTest());
            testMaj += testM(es.getTest(), es.getTrain());

            network.exportTemplate("learned-fold" + es.testFold);
            network.exportWeightMatrix("learned-fold" + es.testFold);
            Network.saveNetwork(network, "learned-fold" + es.testFold);
            //Network nn = Network.loadNetwork();

            Invalidator.invalidate(network); //1st
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
            Glogger.err("network template -" + name + "- is empty, may try to load manually if GUI is on...");
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
        return err;
    }
}
