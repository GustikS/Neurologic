/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer.grounding.network.groundNetwork;

import discoverer.NeuralDataset;
import discoverer.learning.learners.Learning;
import discoverer.construction.network.LiftedNetwork;
import discoverer.global.Global;
import discoverer.global.Glogger;
import discoverer.global.Settings;
import discoverer.grounding.evaluation.Evaluator;
import discoverer.grounding.evaluation.EvaluatorFast;
import discoverer.learning.LearningStep;
import discoverer.learning.Result;
import discoverer.learning.Results;
import discoverer.learning.Sample;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Gusta
 */
public class LearnerFast extends Learning {

    LiftedNetwork liftedTemplate;

    final int learningSteps = Settings.learningSteps;
    final int restartCount = Settings.restartCount;
    final boolean isLearnDecay = Global.isLearnDecay();
    final boolean isSGD = Global.isSGD();
    final double dropout = Global.getDropout();
    final boolean saving = Global.isSave();
    final boolean weightMatrixExporting = Global.exporting;

    private double[] bestWeights;   //for saving
    private LearningStep bestResult;

    public Results solveFast(LiftedNetwork net, List<Sample> roundStore) {
        liftedTemplate = net;

        Glogger.clock("starting to solveFast");

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
                for (Sample sample : roundStore) {  //for each example network
                    GroundNetwork gnet = sample.neuralNetwork;
                    if (dropout > 0) {
                        gnet.dropOut(dropout);
                        Evaluator.ignoreDropout = false;
                        EvaluatorFast.evaluateFast(gnet, net.sharedWeights);
                        Evaluator.ignoreDropout = true;
                    } else {
                        EvaluatorFast.evaluateFast(gnet, net.sharedWeights);
                    }
                    BackpropFast.updateWeights(net.sharedWeights, sample);
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
        Glogger.process("backpropagation on fold finished");

        return evaluatedNetworks;
    }

    private void saveBestWeights(List<Sample> sams, double[] sharedW) {
        Results res = evaluateNetworks(sams, sharedW);
        if (res.actual.isBetterThen(bestResult)) {
            Glogger.process("----train error improvement, saving actual template weights----");
            bestWeights = new double[sharedW.length];
            for (int i = 0; i < sharedW.length; i++) {
                bestWeights[i] = sharedW[i];
            }
            bestResult = res.actual;
            if (weightMatrixExporting) {
                liftedTemplate.exportWeightMatrix("improvement" + progress++);
            }
        }
    }

    private Results evaluateNetworks(List<Sample> sams, double[] sharedW) {
        Results res = new Results();
        for (Sample sam : sams) {
            sam.neuralNetwork.outputNeuron.outputValue = EvaluatorFast.evaluateFast(sam.neuralNetwork, sharedW);

            //System.out.println(gnet.name + "\n" + gnet.outputNeuron.outputValue);
            //writeOutNeurons(gnet.allNeurons);
            res.add(new Result(sam.neuralNetwork.outputNeuron.outputValue, sam.targetValue));
        }
        Glogger.LogTrain("backprop step : ", new Double[]{res.getLearningError(), res.getDispersion(), res.getMajorityClass(), res.getThreshold()});
        Glogger.process("All Ground Networks Evaluation : train error " + res.getLearningError() + " (maj: " + res.getMajorityClass() + ")" + " (disp: " + res.getDispersion() + ")");
        return res;
    }

    private void writeOutNeurons(GroundNeuron[] allNeurons) {
        for (GroundNeuron neuron : allNeurons) {
            System.out.println(neuron.name + " , " + neuron.outputValue);
        }
    }
}
