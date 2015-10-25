package discoverer.crossvalidation;

import discoverer.LiftedDataset;
import discoverer.learning.learners.Learning;
import discoverer.learning.Invalidator;
import extras.BatchLearner;
import discoverer.construction.example.Example;
import discoverer.construction.ExampleFactory;
import discoverer.global.Global;
import discoverer.construction.network.KL;
import discoverer.construction.NetworkFactory;
import discoverer.construction.network.LiftedNetwork;
import discoverer.construction.network.rules.KappaRule;
import discoverer.construction.network.rules.Rule;
import discoverer.drawing.Dotter;
import discoverer.drawing.GroundDotter;
import discoverer.global.Glogger;
import discoverer.global.Settings;
import discoverer.global.Tuple;
import discoverer.grounding.ForwardChecker;
import discoverer.grounding.evaluation.GroundedTemplate;
import discoverer.grounding.Grounder;
import discoverer.grounding.network.GroundKappa;
import discoverer.grounding.network.GroundLambda;
import discoverer.grounding.network.groundNetwork.LearnerFast;
import discoverer.learning.Result;
import discoverer.learning.Results;
import discoverer.learning.Sample;
import discoverer.learning.Saver;
import discoverer.learning.learners.LearnerCheckback;
import discoverer.learning.learners.LearnerIterative;
import discoverer.learning.learners.LearnerStandard;
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

    double testErr = 0;
    double testMaj = 0;
    double trainErr = 0;

    public void trainTest(LiftedNetwork network, List<Sample> trainEx, List<Sample> testEx, int fold) {
        if (Global.exporting) {
            network.exportWeightMatrix("init-fold" + fold);
        }

        Glogger.process("--------------------processing fold " + fold + "----------------------");

        Results res = train(network, trainEx);
        Glogger.process("-------finished training, going to test----------------------");
        trainErr += res.getLearningError();
        testErr += test(network, res, testEx);
        testMaj += testM(testEx, trainEx);

        if (Global.exporting) {
            network.exportTemplate("learned-fold" + fold);
            network.exportWeightMatrix("learned-fold" + fold);
            LiftedNetwork.saveNetwork(network, "learned-fold" + fold);
        }
        if (Global.drawing) {
            Dotter.draw(network.last, "learned_fold" + fold);
        }
        //Network nn = LiftedNetwork.loadNetwork();
        Glogger.LogRes("--------------");
        Glogger.LogRes("Fold train error: " + trainErr);   //do NOT change the texts here (used in excel macro)
        Glogger.LogRes("Fold test error: " + testErr);
        Glogger.LogRes("Fold majority error: " + testMaj);

    }

    /**
     * Performs crossvalidation according to example-splitter of the lifted dataset
     * train-test = 1fold crossval
     *
     * @param dataset
     * @param network
     * @param rules
     * @param es
     * @param examples
     * @param pretrained
     * @param ex
     */
    public Crossvalidation(LiftedDataset dataset) {
        
        for (dataset.es.testFold = 0; dataset.es.testFold < dataset.es.foldCount; dataset.es.testFold++) { //iterating the test fold

            trainTest(dataset.network, dataset.es.getTrain(), dataset.es.getTest(), dataset.es.testFold);
            //Invalidator.invalidate(network); //1st
            dataset.network.invalidateWeights();

            dataset.network.merge(dataset.pretrainedNetwork); // 2nd
        }

        trainErr /= dataset.es.foldCount;
        testErr /= dataset.es.foldCount;
        testMaj /= dataset.es.foldCount;
        Glogger.LogRes("--------------");
        Glogger.LogRes("Final train error: " + trainErr);   //do NOT change the texts here (used in excel macro)
        Glogger.LogRes("Final test error: " + testErr);
        Glogger.LogRes("Final majority error: " + testMaj);
    }

    private double testM(List<Sample> test, List<Sample> train) {
        int pos = 0;
        for (Sample e : train) {
            if (e.getExample().getExpectedValue() == 1) {
                pos++;
            }
        }

        double err = 0;
        if (pos >= (double) train.size() / 2) {
            for (Sample e : test) {
                if (e.getExample().getExpectedValue() == 0) {
                    err++;
                }
            }
        } else {
            for (Sample e : test) {
                if (e.getExample().getExpectedValue() == 1) {
                    err++;
                }
            }
        }

        err /= test.size();
        Glogger.LogRes("Majority for this fold -> " + err + "\n");
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
    public Results train(LiftedNetwork network, List<Sample> examples) {
        //double thresh;
        Results res = null;

        if (Global.isCheckback()) {
            if (Global.getGrounding() == Global.groundingSet.avg) {
                LearnerCheckback s = new LearnerCheckback();
                res = s.checkbackAvg(network, examples);
            } else {
                LearnerCheckback s = new LearnerCheckback();
                res = s.checkback(network, examples);
            }
        } else if (Global.getBatch() == Global.batch.YES) {
            BatchLearner bs = new BatchLearner();
            res = bs.solve(network, examples);
        } else if (Global.isCumulativeRestarts()) {
            if (Global.getGrounding() == Global.groundingSet.avg) {
                LearnerIterative s = new LearnerIterative();
                res = s.solveAvgIterative(network, examples);
            } else if (Global.getGrounding() == Global.groundingSet.max) {
                LearnerIterative s = new LearnerIterative();
                res = s.solveMaxIterative(network, examples);
            }
        } else if (Global.fastVersion) {
            LearnerFast s = new LearnerFast();
            res = s.solveFast(network, examples);
        } else {
            if (Global.getGrounding() == Global.groundingSet.avg) {
                LearnerStandard s = new LearnerStandard();
                res = s.solveAvg(network, examples);
            } else if (Global.getGrounding() == Global.groundingSet.max) {
                LearnerStandard s = new LearnerStandard();
                res = s.solveMax(network, examples);
            }
        }

        return res;
    }

    public double test(LiftedNetwork net, Results res, List<Sample> examples) {
        KL network = net.last;
        ForwardChecker.exnum = 0;
        double error = 0.0;

        Results results = new Results();    //we didn't store the whole ground networks(one for each example), just the weights of program
        HashMap<String, Double> ballvalues = new HashMap<>();
        HashMap<String, Double> atoms = new HashMap<>();

        for (Sample example : examples) {
            GroundedTemplate b = Grounder.solve(network, example.getExample());

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
                    r5 += example.getExample().constantNames.get(term) + "-";
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
                    r4 += example.getExample().constantNames.get(term) + "-";
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
                    r3 += example.getExample().constantNames.get(term) + "-";
                }
                ballvalues.put(r3, ring3.getValueAvg());
            } else {
                //ballvalues.add(-1.0);
            }

            results.add(new Result(ballValue, example.getExample().getExpectedValue()));

            double clas = ballValue > res.getThreshold() ? 1.0 : 0.0;
            Glogger.info("Classified -> " + clas + " Expected -> " + example.getExample().getExpectedValue() + " Out -> " + ballValue + " Thresh -> " + res.getThreshold());
            if (clas != example.getExample().getExpectedValue()) {
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
