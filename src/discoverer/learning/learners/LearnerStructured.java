/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer.learning.learners;

import discoverer.construction.template.LightTemplate;
import discoverer.crossvalidation.SampleSplitter;
import discoverer.global.Global;
import discoverer.global.Glogger;
import discoverer.global.Settings;
import discoverer.grounding.evaluation.Evaluator;
import discoverer.grounding.evaluation.EvaluatorFast;
import discoverer.grounding.network.groundNetwork.GroundNetwork;
import discoverer.grounding.network.groundNetwork.GroundNeuron;
import discoverer.learning.LearningStep;
import discoverer.learning.Result;
import discoverer.learning.Results;
import discoverer.learning.Sample;
import discoverer.learning.backprop.BackpropFast;
import discoverer.learning.backprop.BackpropStructured;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author Gusta
 */
public class LearnerStructured extends Learning {

    LightTemplate liftedTemplate;

    //----SL parameters
    final int learningSteps;
    final int depth;
    final int regularization;
    //--
    final int restartCount = Settings.restartCount;
    final boolean isLearnDecay = Global.isLearnDecay();
    final boolean isSGD = Global.isSGD();
    final double dropout = Global.getDropout();
    final boolean saving = Global.isSave();
    final boolean weightMatrixExporting = Global.exporting;

    private double[] bestWeights;   //for saving
    private LearningStep bestResult;
    BackpropStructured backpropSL;

    public LearnerStructured(int learningSteps, int depth, int regularization) {
        backpropSL = new BackpropStructured(depth, regularization);
        this.learningSteps = learningSteps;
        this.depth = depth;
        this.regularization = regularization;
    }

    public Results solveStructured(LightTemplate net, List<Sample> roundStore) {
        liftedTemplate = net;
        List<List<Sample>> workFolds = null;
        double[] batchWeightUpdates = new double[net.sharedWeights.length];
        if (Global.batchMode) {
            workFolds = SampleSplitter.splitSampleList(roundStore, Global.numOfThreads);
        }
        Stream<Sample> stream;

        Glogger.clock("starting to solveStructured");

        for (int a = 0; a < restartCount; a++) {    //restarting the whole procedure
            Glogger.process("---------------------------SolveStructured--------------------------------------");
            Glogger.process("------------Restart: " + a);
            for (int i = 0; i < learningSteps; i++) {       //learningSteps = backpropagation steps
                if (isLearnDecay) {
                    Settings.learnRate = learnDecay(i, Settings.learnRate);
                }
                Glogger.process("---learning step: " + i);
                if (isSGD) {
                    Collections.shuffle(roundStore, Global.getRg());    //stochastic gradient descend
                }
                if (Global.parallelTraining) {
                    stream = roundStore.parallelStream();
                } else {
                    stream = roundStore.stream();
                }
                stream.filter((sample) -> !(sample.neuralNetwork == null)).forEach((sample) -> {//un-entailed sample exclusion
                    //for each example network
                    GroundNetwork gnet = sample.neuralNetwork;
                    if (dropout > 0) {
                        gnet.dropOut(dropout);
                        Evaluator.ignoreDropout = false;
                        EvaluatorFast.evaluateFast(gnet, net.sharedWeights);
                        Evaluator.ignoreDropout = true;
                    } else {
                        EvaluatorFast.evaluateFast(gnet, net.sharedWeights);
                    }
                    if (Global.batchMode) {
                        backpropSL = new BackpropStructured(depth, regularization);  //in the parallel mode we want each thread to have their own weight updates
                        double[] weightUpdates = backpropSL.getWeightUpdates(net.sharedWeights, sample);
                        for (int j = weightUpdates.length - 1; j >= 0; j--) {
                            batchWeightUpdates[j] += weightUpdates[j];
                        }
                    } else {
                        double[] weightUpdates = backpropSL.getWeightUpdates(net.sharedWeights, sample);
                        //now update the weights after each sample
                        for (int j = weightUpdates.length - 1; j >= 0; j--) {
                            net.sharedWeights[j] += weightUpdates[j];
                        }
                    }
                });
                if (Global.batchMode) {
                    for (int j = batchWeightUpdates.length - 1; j >= 0; j--) {
                        net.sharedWeights[j] += batchWeightUpdates[j];
                    }
                    Arrays.fill(batchWeightUpdates, 0); //renew the batch updates
                }
                if (saving) { //saving after each batch
                    saveBestWeights(roundStore, net.sharedWeights);
                }
            }
            saveBestWeights(roundStore, net.sharedWeights);    //save at the end of restart definitely
            Glogger.LogTrain("...finished restart : " + a);
            Glogger.clock("");
            net.invalidateWeights();
        }

        Glogger.clock("!!finished learning!!");

        net.sharedWeights = bestWeights; //=LOADING the final best model from training

        Glogger.process("---best template weights so far <- loaded---");
        Results evaluatedNetworks = evaluateNetworks(roundStore, bestWeights);

        Glogger.process(
                "backpropagation on fold finished");

        return evaluatedNetworks;
    }

    private void saveBestWeights(List<Sample> sams, double[] sharedW) {
        Results res = evaluateNetworks(sams, sharedW);
        if (res.actualResult.isBetterThen(bestResult)) {
            Glogger.process("----train error improvement, saving actual template weights----");
            bestWeights = new double[sharedW.length];
            for (int i = 0; i < sharedW.length; i++) {
                bestWeights[i] = sharedW[i];
            }
            bestResult = res.actualResult;
            if (weightMatrixExporting) {
                liftedTemplate.exportSharedWeights(sharedW, progress);
                liftedTemplate.exportWeightMatrix("improvement" + progress++);
            }
        }
    }

    private Results evaluateNetworks(List<Sample> sams, double[] sharedW) {
        results.clearResultList();
        for (Sample sam : sams) {
            if (sam.neuralNetwork == null) {
                results.add(new Result(Global.getFalseAtomValue(), sam.targetValue)); //unentailed sample
                continue;
            }
            sam.neuralNetwork.outputNeuron.outputValue = EvaluatorFast.evaluateFast(sam.neuralNetwork, sharedW);

            //System.out.println(gnet.name + "\n" + gnet.outputNeuron.outputValue);
            //writeOutNeurons(gnet.allNeurons);
            results.add(new Result(sam.neuralNetwork.outputNeuron.outputValue, sam.targetValue));
        }
        Glogger.LogTrain("backprop step : ", new Double[]{results.getLearningError(), results.getDispersion(), results.getMajorityClass(), results.getThreshold()});
        Glogger.process("All Ground Networks Evaluation : train error " + results.getLearningError() + " (maj: " + results.getMajorityClass() + ")" + " (disp: " + results.getDispersion() + ")");
        return results;
    }

    private void writeOutNeurons(GroundNeuron[] allNeurons) {
        for (GroundNeuron neuron : allNeurons) {
            System.out.println(neuron.name + " , " + neuron.outputValue);
        }
    }
}
