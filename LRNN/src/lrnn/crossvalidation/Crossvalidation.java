package lrnn.crossvalidation;

import lrnn.LiftedDataset;
import lrnn.global.Global;
import lrnn.construction.template.KL;
import lrnn.construction.template.LightTemplate;
import lrnn.construction.template.MolecularTemplate;
import lrnn.global.Glogger;
import lrnn.grounding.evaluation.GroundedTemplate;
import lrnn.grounding.Grounder;
import lrnn.learning.LearningStep;
import lrnn.learning.Result;
import lrnn.learning.Results;
import lrnn.learning.Sample;
import lrnn.learning.learners.LearnerCheckback;
import lrnn.learning.learners.LearnerIterative;
import lrnn.learning.learners.LearnerStandard;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Wrapper for running test with n-fold stratification
 */
public class Crossvalidation {

    public int foldCount = 5;

    double trainErr = 0;
    double trainMajorityErr = 0;
    double trainDispersion = 0;
    double trainMSE = 0;
    double trainAUCpr = 0;
    double trainAUCroc = 0;

    double testErr = 0;
    double testMajorityErr = 0;
    double testrainMajorityErr = 0;
    double testDispersion = 0;
    double testMSE = 0;
    double testAUCpr = 0;
    double testAUCroc = 0;

    SampleSplitter splitter;

    List<Results> foldResults;

    public Crossvalidation(int foldC) {
        this.foldCount = foldC;
        foldResults = new LinkedList<>();
    }

    public Crossvalidation(SampleSplitter ss) {
        splitter = ss;
        foldCount = ss.foldCount;
        foldResults = new LinkedList<>();
    }

    public Results trainTestFold(LightTemplate network, List<Sample> trainEx, List<Sample> testEx, int fold) {

        if (Global.exporting) {
//            network.exportWeightMatrix("init-fold" + fold);
        }

        Glogger.process("------------------processing fold " + fold + "----------------------");

        Results res = this.train(network, trainEx);
        Glogger.process("--------------finished training, going to test----------------------");
        res = this.test(network, res, testEx);
        res.majority = new LearningStep();
        res.majority.setError(testMajority(testEx, trainEx));

        if (Global.exporting) {
            network.exportTemplate("learned-fold" + fold);
            
            ((MolecularTemplate) network).createWeightMatrix();
            ((MolecularTemplate) network).exportWeightMatrix("learned-fold" + fold);
//            MolecularTemplate.saveTemplate(network, "learned-fold" + fold);
        }
        if (Global.drawing) {
//            Dotter.draw(network, "learned_fold" + fold);
        }

        loadFoldStats(res);

        return res;
    }

    public void loadFoldStats(Results res) {
        Glogger.LogRes("--------------");
        Glogger.LogRes("Fold train error: " + res.training.getError());   //do NOT change the texts here (used in excel macro)
        Glogger.LogRes("Fold train threshold: " + res.training.getThresh());
        Glogger.LogRes("Fold train majority error: " + res.training.getMajorityErr());
        Glogger.LogRes("Fold train dispersion: " + res.training.getDispersion());
        Glogger.LogRes("Fold train MSE: " + res.training.getMse());
        Glogger.LogRes("Fold train AUCpr: " + res.training.getAUCpr());
        Glogger.LogRes("Fold train AUCroc: " + res.training.getAUCroc());

        Glogger.LogRes("Fold test error: " + res.testing.getError());
        Glogger.LogRes("Fold test optimal threshold: " + res.testing.getThresh());
        Glogger.LogRes("Fold test optimal threshold error: " + res.testing.getRecalculatedThrehError());
        Glogger.LogRes("Fold test majority error: " + res.testing.getMajorityErr());
        Glogger.LogRes("Fold test (train-calculated) majority error: " + res.majority.getError());
        Glogger.LogRes("Fold test dispersion: " + res.testing.getDispersion());
        Glogger.LogRes("Fold test MSE: " + res.testing.getMse());
        Glogger.LogRes("Fold test AUCpr: " + res.testing.getAUCpr());
        Glogger.LogRes("Fold test AUCroc: " + res.testing.getAUCroc());

        trainErr += res.training.getError();
        trainMajorityErr += res.training.getMajorityErr();
        trainDispersion += res.training.getDispersion();
        trainMSE += res.training.getMse();
        trainAUCpr += res.training.getAUCpr();
        trainAUCroc += res.training.getAUCroc();

        testErr += res.testing.getError();
        testMajorityErr += res.testing.getMajorityErr();
        testrainMajorityErr += res.majority.getError();
        testDispersion += res.testing.getDispersion();
        testMSE += res.testing.getMse();
        testAUCpr += res.testing.getAUCpr();
        testAUCroc += res.testing.getAUCroc();

        foldResults.add(res);

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
    public double crossvalidate(LiftedDataset dataset) {
        long time;
        Glogger.info("starting crossvalidation " + (time = System.currentTimeMillis()));
        for (dataset.sampleSplitter.testFold = 0; dataset.sampleSplitter.testFold < dataset.sampleSplitter.foldCount; dataset.sampleSplitter.testFold++) { //iterating the test fold

            Results foldRes = trainTestFold(dataset.template, dataset.sampleSplitter.getTrain(), dataset.sampleSplitter.getTest(), dataset.sampleSplitter.testFold);
            /*
            if (Global.exporting) {
                LightTemplate.exportSharedWeights(dataset.template.sharedWeights, 99);
                dataset.saveDataset(Settings.getDataset().replaceAll("-", "/") + ".ser");
            }
             */
            
            //Invalidator.invalidate(network); //1st
            dataset.template.exportWeightMatrix("matrix");
            dataset.template.invalidateWeights(); //TODO!!

            dataset.template.merge(dataset.pretrainedTemplate); // 2nd
        }
        Glogger.process("finished crossvalidation " + (System.currentTimeMillis() - time));

        finalizeCrossvalStats();

        return testErr;
    }

    public void finalizeCrossvalStats() {
        trainErr /= foldCount;
        trainMajorityErr /= foldCount;
        trainDispersion /= foldCount;
        trainMSE /= foldCount;
        trainAUCpr /= foldCount;
        trainAUCroc /= foldCount;

        testErr /= foldCount;
        testMajorityErr /= foldCount;
        testrainMajorityErr /= foldCount;
        testDispersion /= foldCount;
        testMSE /= foldCount;
        testAUCpr /= foldCount;
        testAUCroc /= foldCount;

        Glogger.LogRes("--------------");
        Glogger.LogRes("Final train error: " + trainErr);   //do NOT change the texts here (used in excel macro)
        Glogger.LogRes("Final train majority error: " + trainMajorityErr);
        Glogger.LogRes("Final train dispersion: " + trainDispersion);
        Glogger.LogRes("Final train MSE: " + trainMSE);
        Glogger.LogRes("Final train AUCpr: " + trainAUCpr);
        Glogger.LogRes("Final train AUCroc: " + trainAUCroc);

        Glogger.LogRes("Final test error: " + testErr);
        Glogger.LogRes("Final test majority error: " + testMajorityErr);
        Glogger.LogRes("Final test (train-calculated) majority error: " + testrainMajorityErr);
        Glogger.LogRes("Final test dispersion: " + testDispersion);
        Glogger.LogRes("Final test MSE: " + testMSE);
        Glogger.LogRes("Final test AUCpr: " + testAUCpr);
        Glogger.LogRes("Final test AUCroc: " + testAUCroc);

        Glogger.process("finished learning");
    }

    public double testMajority(List<Sample> test, List<Sample> train) {
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

        trainResults.results.clear();

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
