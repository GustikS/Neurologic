package discoverer;

import discoverer.learning.Invalidator;
import extras.BatchLearner;
import discoverer.global.Batch;
import discoverer.construction.example.Example;
import discoverer.construction.ExampleFactory;
import discoverer.global.Global;
import discoverer.construction.network.KL;
import discoverer.construction.NetworkFactory;
import discoverer.global.Glogger;
import discoverer.grounding.ForwardChecker;
import discoverer.grounding.evaluation.Ball;
import discoverer.grounding.Grounder;
import discoverer.learning.Results;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Wrapper for running test with n-fold stratification
 */
public class Crossvalidation {

    /**
     * Main solving method from input arguments, performs example splitting and
     * majority testing
     *
     * @param folds
     * @param rules
     * @param ex
     * @param batch
     * @param steps
     * @param epochs
     * @param restartCount
     * @param learnRate
     */
    public void solve(int folds, String[] rules, String[] ex, Batch batch, int steps, int epochs, int restartCount, double learnRate) {
        //factory + subfactories initialization
        NetworkFactory nf = new NetworkFactory();
        //constructs the whole L-K network from rules with support of grounded classes and element mappers, return LAST line rule's literal(=KL)!
        KL network = nf.construct(rules);
        //creates examples with corresponding ID mapping and chunk representations
        List<Example> examples = createExamples(ex);
        //k-fold stratified example(same #positives in folds) splitting structure
        ExampleSplitter es = new ExampleSplitter(folds, examples);

        double testErr = 0;
        double testMaj = 0;
        double trainErr = 0;
        int i;
        for (i = 0; es.hasNext(); es.next()) { //iterating the test fold
            Glogger.process("--------------------processing new fold----------------------");
            Results res = train(batch, network, es.getTrain(), steps, epochs, restartCount, learnRate);
            trainErr += res.getLearningError();
            testErr += test(network, res, es.getTest());
            testMaj += testM(es.getTest(), es.getTrain());
            Invalidator.invalidate(network);
            i++;
        }

        trainErr /= i;
        testErr /= i;
        testMaj /= i;
        Glogger.LogRes("--------------");
        Glogger.LogRes("Final train error: " + trainErr);
        Glogger.LogRes("Final test error: " + testErr);
        Glogger.LogRes("Final majority error: " + testMaj);
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
     * @return
     */
    public List<Example> createExamples(String[] ex) {
        ExampleFactory eFactory = new ExampleFactory();
        List<Example> examples = new ArrayList<Example>();
        for (int i = 0; i < ex.length; i++) {
            //main creation of an example
            Example e = eFactory.construct(ex[i]);
            examples.add(e);
        }

        Collections.shuffle(examples, Global.rg);
        return examples;
    }

    /**
     * the whole network training
     *
     * @param batch
     * @param network
     * @param examples
     * @param learningStepCount
     * @param learningEpochs
     * @param restartCount
     * @param learnRate
     * @return
     */
    public Results train(Batch batch, KL network, List<Example> examples, int learningStepCount, int learningEpochs, int restartCount, double learnRate) {
        //double thresh;
        Results res;

        if (Global.grounding == Global.groundingSet.avg) {
            Learner s = new Learner();
            res = s.solveAvg(network, examples, learningStepCount, learningEpochs, restartCount, learnRate);
        } else if (batch == Batch.NO && Global.grounding == Global.groundingSet.max) {
            Learner s = new Learner();
            res = s.solve(network, examples, learningStepCount, learningEpochs, restartCount, learnRate);
        } else {
            BatchLearner bs = new BatchLearner();
            res = bs.solve(network, examples, learningStepCount, learningEpochs, restartCount, learnRate);
        }

        return res;
    }

    public double test(KL network, Results res, List<Example> examples) {
        ForwardChecker.exnum = 0;
        double error = 0.0;
        for (Example example : examples) {
            Ball b = Grounder.solve(network, example);

            double ballValue = -1;
            if (b != null) {
                if (Global.grounding == Global.groundingSet.avg) {
                    ballValue = b.valAvg;
                } else if (Global.grounding == Global.groundingSet.max) {
                    ballValue = b.valMax;
                } else {
                    throw new AssertionError();
                }
            }

            double clas = ballValue > res.getThreshold() ? 1.0 : 0.0;
            Glogger.info("Classified -> " + clas + " Expected -> " + example.getExpectedValue() + " Out -> " + ballValue + " Thresh -> " + res.getThreshold());
            if (clas != example.getExpectedValue()) {
                error += 1.0;
            }
        }

        double err = error / examples.size();
        Glogger.LogRes("Fold Train error : " + res.getLearningError());
        Glogger.LogRes("Fold Test error : " + err);
        return err;
    }
}
