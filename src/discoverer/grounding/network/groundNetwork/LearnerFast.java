/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer.grounding.network.groundNetwork;

import discoverer.GroundedDataset;
import discoverer.NeuralDataset;
import discoverer.learning.learners.Learning;
import discoverer.construction.example.Example;
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
import discoverer.learning.Saver;
import java.util.Arrays;
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
        NeuralDataset gdata = new NeuralDataset(roundStore, net);
        Glogger.clock("created neural networks dataset for this fold");
        roundStore = null; //releasing memory?
        Glogger.clock("groundings, groundKL and all substitution discarded from memory");

        for (int a = 0; a < restartCount; a++) {    //restarting the whole procedure
            Glogger.process("---------------------------SolveFast--------------------------------------");
            Glogger.process("------------Restart: " + a);
            for (int i = 0; i < learningSteps; i++) {       //learningSteps = backpropagation steps
                if (isLearnDecay) {
                    Settings.learnRate = learnDecay(i, Settings.learnRate);
                }
                Glogger.process("---learning step: " + i);
                if (isSGD) {
                    Collections.shuffle(Arrays.asList(gdata.groundNetworks), Global.getRg());    //stochastic gradient descend
                }
                for (int j = 0; i < gdata.groundNetworks.length; j++) {     //for each example network
                    GroundNetwork gnet = gdata.groundNetworks[j];
                    if (dropout > 0) {
                        gnet.dropOut(dropout);
                        Evaluator.ignoreDropout = false;
                        EvaluatorFast.evaluateFast(gnet, gdata.sharedWeights);
                        Evaluator.ignoreDropout = true;
                    } else {
                        EvaluatorFast.evaluateFast(gnet, gdata.sharedWeights);
                    }
                    BackpropFast.updateWeights(gnet);
                }
                if (saving) { //saving after each batch
                    saveBestWeights(gdata);
                }
            }
            saveBestWeights(gdata);    //save at the end of restart definitely
            Glogger.LogTrain("...finished restart : " + a);
            Glogger.clock("");
            gdata.invalidateWeights();
        }
        Glogger.clock("!!finished learning!!");

        gdata.sharedWeights = bestWeights; //=LOADING the final best model from training
        Glogger.process("---best template weights so far <- loaded---");
        Results evaluatedNetworks = evaluateNetworks(gdata);
        Glogger.process("backpropagation on fold finished");

        return evaluatedNetworks;
    }

    private void saveBestWeights(NeuralDataset gdata) {
        Results res = evaluateNetworks(gdata);
        if (res.actual.isBetterThen(bestResult)) {
            Glogger.process("----train error improvement, saving actual template weights----");
            bestWeights = new double[gdata.sharedWeights.length];
            for (int i = 0; i < gdata.sharedWeights.length; i++) {
                bestWeights[i] = gdata.sharedWeights[i];
            }
            bestResult = res.actual;
            if (weightMatrixExporting) {
                liftedTemplate.exportWeightMatrix("improvement" + progress++);
            }
        }
    }

    private Results evaluateNetworks(NeuralDataset gdata) {
        Results res = new Results();
        for (GroundNetwork gnet : gdata.groundNetworks) {
            gnet.outputNeuron.outputValue = EvaluatorFast.evaluateFast(gnet, gdata.sharedWeights);
            res.add(new Result(gnet.outputNeuron.outputValue, gnet.targetValue));
        }
        Glogger.LogTrain("backprop step : ", new Double[]{res.getLearningError(), res.getDispersion(), res.getMajorityClass(), res.getThreshold()});
        Glogger.process("All Ground Networks Evaluation : " + res.getLearningError() + " (maj: " + res.getMajorityClass() + ")" + " (disp: " + res.getDispersion() + ")");
        return res;
    }
}
