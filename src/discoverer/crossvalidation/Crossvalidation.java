package discoverer.crossvalidation;

import discoverer.LiftedDataset;
import discoverer.global.Global;
import discoverer.construction.template.KL;
import discoverer.construction.template.LightTemplate;
import discoverer.construction.template.MolecularTemplate;
import discoverer.drawing.Dotter;
import discoverer.global.Glogger;
import discoverer.global.Settings;
import discoverer.grounding.ForwardChecker;
import discoverer.grounding.evaluation.GroundedTemplate;
import discoverer.grounding.Grounder;
import discoverer.grounding.network.GroundKappa;
import discoverer.grounding.network.GroundLambda;
import discoverer.learning.LearningStep;
import discoverer.learning.Result;
import discoverer.learning.Results;
import discoverer.learning.Sample;
import discoverer.learning.learners.LearnerCheckback;
import discoverer.learning.learners.LearnerIterative;
import discoverer.learning.learners.LearnerStandard;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Wrapper for running test with n-fold stratification
 */
public class Crossvalidation {

    double testErr = 0;
    double testMaj = 0;
    double trainErr = 0;

    SampleSplitter splitter;

    List<Results> foldResults;

    public Crossvalidation(SampleSplitter ss) {
        splitter = ss;
        foldResults = new LinkedList<>();
    }

    public Results trainTestFold(LightTemplate network, List<Sample> trainEx, List<Sample> testEx, int fold) {

        if (Global.exporting) {
            network.exportWeightMatrix("init-fold" + fold);
        }

        Glogger.process("------------------processing fold " + fold + "----------------------");

        Results res = this.train(network, trainEx);
        Glogger.process("--------------finished training, going to test----------------------");
        res = this.test(network, res, testEx);
        res.majority = new LearningStep();
        res.majority.setError(testMajority(testEx, trainEx));

        if (Global.exporting) {
            network.exportTemplate("learned-fold" + fold);
            network.exportWeightMatrix("learned-fold" + fold);
            MolecularTemplate.saveTemplate(network, "learned-fold" + fold);
        }
        if (Global.drawing) {
            Dotter.draw(network, "learned_fold" + fold);
        }

        Glogger.LogRes("--------------");
        Glogger.LogRes("Fold train error: " + res.training.getError());   //do NOT change the texts here (used in excel macro)
        Glogger.LogRes("Fold test error: " + res.testing.getError());
        Glogger.LogRes("Fold majority error: " + res.majority.getError());

        trainErr += res.training.getError();
        testErr += res.testing.getError();
        testMaj += res.majority.getError();

        return res;
    }

    /**
     * Performs crossvalidation according to example-splitter of the lifted
     * dataset train-test = 1fold crossval
     *
     * @param dataset
     * @param network
     * @param rules
     * @param es
     * @param examples
     * @param pretrained
     * @param ex
     */
    public void crossvalidate(LiftedDataset dataset) {
        long time;
        Glogger.info("starting crossvalidation " + (time = System.currentTimeMillis()));
        for (dataset.sampleSplitter.testFold = 0; dataset.sampleSplitter.testFold < dataset.sampleSplitter.foldCount; dataset.sampleSplitter.testFold++) { //iterating the test fold

            Results foldRes = trainTestFold(dataset.network, dataset.sampleSplitter.getTrain(), dataset.sampleSplitter.getTest(), dataset.sampleSplitter.testFold);
            foldResults.add(foldRes);

            if (Global.exporting) {
                LightTemplate.exportSharedWeights(dataset.network.sharedWeights, 99);
                dataset.saveDataset(Settings.getDataset().replaceAll("-", "/") + ".ser");
            }

            //Invalidator.invalidate(network); //1st
            dataset.network.invalidateWeights();

            dataset.network.merge(dataset.pretrainedNetwork); // 2nd
        }
        Glogger.process("finished crossvalidation " + (System.currentTimeMillis() - time));

        trainErr /= dataset.sampleSplitter.foldCount;
        testErr /= dataset.sampleSplitter.foldCount;
        testMaj /= dataset.sampleSplitter.foldCount;

        Glogger.LogRes("--------------");
        Glogger.LogRes("Final train error: " + trainErr);   //do NOT change the texts here (used in excel macro)
        Glogger.LogRes("Final test error: " + testErr);
        Glogger.LogRes("Final majority error: " + testMaj);

        Glogger.process("finished learning");
    }

    private double testMajority(List<Sample> test, List<Sample> train) {
        int pos = 0;
        for (Sample e : train) {
            if (e.targetValue == 1) {
                pos++;
            }
        }

        double err = 0;
        if (pos >= (double) train.size() / 2) {
            for (Sample e : test) {
                if (e.targetValue == 0) {
                    err++;
                }
            }
        } else {
            for (Sample e : test) {
                if (e.targetValue == 1) {
                    err++;
                }
            }
        }

        err /= test.size();
        //Glogger.LogRes("Majority for this fold -> " + err + "\n");
        return err;
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
    public Results train(LightTemplate net, List<Sample> examples) {
        //double thresh;
        Results res = null;
        MolecularTemplate network = (MolecularTemplate) net;

        if (Global.isCheckback()) {
            if (Global.getGrounding() == Global.groundingSet.avg) {
                LearnerCheckback s = new LearnerCheckback();
                res = s.checkbackAvg(network, examples);
            } else {
                LearnerCheckback s = new LearnerCheckback();
                res = s.checkback(network, examples);
            }
        } else if (Global.batchMode) {
            //BatchLearner bs = new BatchLearner();
            //res = bs.solve(network, examples);
        } else if (Global.isCumulativeRestarts()) {
            if (Global.getGrounding() == Global.groundingSet.avg) {
                LearnerIterative s = new LearnerIterative();
                res = s.solveAvgIterative(network, examples);
            } else if (Global.getGrounding() == Global.groundingSet.max) {
                LearnerIterative s = new LearnerIterative();
                res = s.solveMaxIterative(network, examples);
            }
        } else if (Global.getGrounding() == Global.groundingSet.avg) {
            LearnerStandard s = new LearnerStandard();
            res = s.solveAvg(network, examples);
        } else if (Global.getGrounding() == Global.groundingSet.max) {
            LearnerStandard s = new LearnerStandard();
            res = s.solveMax(network, examples);
        }
        res.training = res.actualResult;

        return res;
    }

    /**
     * non-fast(neural) version! perform grounding on examples again!
     *
     * @param netw
     * @param net
     * @param trainResults
     * @param examples
     * @return
     */
    public Results test(LightTemplate netw, Results trainResults, List<Sample> examples) {
        Grounder grounder = new Grounder();
        MolecularTemplate net = (MolecularTemplate) netw;
        KL network = net.last;
        grounder.forwardChecker.exnum = 0;
        double error = 0.0;

        //we didn't store the whole ground networks(one for each example), just the weights of program
        HashMap<String, Double> ballvalues = new HashMap<>();
        HashMap<String, Double> atoms = new HashMap<>();

        trainResults.results.clear();;

        for (Sample example : examples) {
            GroundedTemplate b = grounder.groundTemplate(network, example.getExample());

            double ballValue = -1;
            if (b != null) {
                if (null != Global.getGrounding()) {
                    switch (Global.getGrounding()) {
                        case avg:
                            ballValue = b.valAvg;
                            break;
                        case max:
                            ballValue = b.valMax;
                            break;
                        default:
                            throw new AssertionError();
                    }
                }
            }
            //ballvalues.add(ballValue);

            trainResults.add(new Result(ballValue, example.getExample().getExpectedValue()));
        }
        trainResults.computeTest();
        trainResults.testing = trainResults.actualResult;

        Glogger.LogRes("Fold Train error : " + trainResults.training.getError());
        Glogger.LogRes("Fold Test error : " + trainResults.testing.getError());
        trainResults.actualResult.setError(null);
        Glogger.LogRes("Fold re-calculated threshold Test error : " + trainResults.getLearningError());

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

        return trainResults;
    }

}
