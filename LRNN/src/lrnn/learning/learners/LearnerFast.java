/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lrnn.learning.learners;

import lrnn.construction.template.LightTemplate;
import lrnn.crossvalidation.SampleSplitter;
import lrnn.global.Global;
import lrnn.global.Glogger;
import lrnn.global.Settings;
import lrnn.grounding.evaluation.Evaluator;
import lrnn.grounding.evaluation.EvaluatorFast;
import lrnn.grounding.network.groundNetwork.GroundNetwork;
import lrnn.grounding.network.groundNetwork.GroundNeuron;
import lrnn.learning.LearningStep;
import lrnn.learning.Result;
import lrnn.learning.Results;
import lrnn.learning.Sample;
import lrnn.learning.backprop.BackpropFast;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Gusta
 */
public class LearnerFast extends Learning {

    LightTemplate liftedTemplate;

    final int learningSteps = Settings.learningSteps;
    final int restartCount = Settings.restartCount;
    final boolean isLearnDecay = Global.isLearnDecay();
    final boolean isSGD = Global.isSGD();
    final double dropout = Global.getDropout();
    final boolean saving = Global.isSave();
    final boolean weightMatrixExporting = Global.exporting;

    private double[] bestWeights;   //for saving
    private LearningStep bestResult;
    BackpropFast backpropFast = new BackpropFast();

    public Results solveFast(LightTemplate net, List<Sample> roundStore) {

        liftedTemplate = net;
        List<List<Sample>> workFolds = null;
        double[] batchWeightUpdates = new double[net.sharedWeights.length];
        if (Global.batchMode) {
            workFolds = SampleSplitter.splitSampleList(roundStore, Global.numOfThreads);
        }
        Stream<Sample> stream;

        Glogger.clock("starting to solveFast");
        Glogger.LogTrain("Starting to train...");

        Results beginning = evaluateNetworks(roundStore, net.sharedWeights);

        for (int a = 0; a < restartCount; a++) {    //restarting the whole procedure
            Glogger.process("---------------------------SolveFast--------------------------------------");
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
                stream.filter((sample) -> !(sample.neuralNetwork == null)).forEach((sample) -> {
                    //un-entailed sample
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
                        backpropFast = new BackpropFast();  //in the parallel mode we want each thread to have their own weight updates
                        double[] weightUpdates = backpropFast.getWeightUpdates(net.sharedWeights, sample);
                        for (int j = weightUpdates.length - 1; j >= 0; j--) {
                            if (net.isLearnable[j]) {
                                batchWeightUpdates[j] += weightUpdates[j];
                            }
                        }
                    } else {
                        double[] weightUpdates = backpropFast.getWeightUpdates(net.sharedWeights, sample);
                        //now update the weights after each sample
                        for (int j = weightUpdates.length - 1; j >= 0; j--) {
                            if (net.isLearnable[j]) {
                                net.sharedWeights[j] += weightUpdates[j];
                            }
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
        Glogger.LogTrain("Evaluating with the best weights found during training...");
        Results evaluatedNetworks = evaluateNetworks(roundStore, bestWeights);
        Glogger.LogTrain("...training finished!");
        Glogger.process("backpropagation on fold finished");

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
//                liftedTemplate.exportWeightMatrix("improvement" + progress++);
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
        Glogger.LogTrain("backprop step : ", new Double[]{results.getLearningError(), results.getDispersion(), results.getMajorityClass(), results.getThreshold(), results.getMSE()});
        Glogger.process("All Ground Networks Evaluation : train error " + results.getLearningError() + " (maj: " + results.getMajorityClass() + ")" + " (disp: " + results.getDispersion() + ")" + " (mse: " + results.getMSE() + ")");
        return results;
    }

    private void writeOutNeurons(GroundNeuron[] allNeurons) {
        for (GroundNeuron neuron : allNeurons) {
            System.out.println(neuron.name + " , " + neuron.outputValue);
        }
    }
}
