/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer.grounding.evaluation;

import discoverer.global.Global;
import static discoverer.grounding.evaluation.Evaluator.ignoreDropout;
import discoverer.grounding.network.GroundKappa;
import discoverer.learning.functions.ActivationsFast;
import discoverer.grounding.network.groundNetwork.AtomNeuron;
import discoverer.grounding.network.groundNetwork.GroundNetwork;
import discoverer.grounding.network.groundNetwork.RuleAggNeuron;
import discoverer.learning.functions.Activations;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author Gusta
 */
public class EvaluatorFast extends Evaluator {

    private static final Global.groundingSet grounding = Global.getGrounding();
    private static final boolean fullLambda = Global.uncompressedLambda;
    protected static double[] sharedWeights;

    public static double evaluateFast(GroundNetwork gnet, double[] templateWeights) {
        sharedWeights = templateWeights;
        if (gnet == null) {
            return Global.getFalseAtomValue();
        }
        if (gnet.outputNeuron == null) {
            return Global.getFalseAtomValue();
        }

        gnet.invalidateNeuronValues();
        if (gnet.outputNeuron instanceof AtomNeuron) {
            return evaluateFast((AtomNeuron) gnet.outputNeuron);
        } else {
            return evaluateFast((RuleAggNeuron) gnet.outputNeuron);
        }
    }

    public static double evaluateFast(AtomNeuron an) {
        if (!ignoreDropout && an.dropMe) {
            an.outputValue = 0;
            return 0;
        }

        if (an.inputNeurons == null) {
            an.outputValue = 1;
            return 1;
        }

        if (an.outputValue != 0.0000000) {
            return an.outputValue;  //it doesnt make sense for fact neuron (isElement) to have zero value
        }

        //double[] inputs = new double[an.inputWeightIndices.length];
        an.sumedInputs = sharedWeights[an.offsetWeightIndex];
        for (int i = 0; i < an.inputNeurons.length; i++) {
            //inputs[i] = evaluateFast(an.inputNeurons[i]) * sharedWeights[an.inputWeightIndices[i]];
            //an.sumedInputs += inputs[i];
            an.sumedInputs += evaluateFast(an.inputNeurons[i]) * sharedWeights[an.inputWeightIndices[i]];
        }
        //an.outputValue = ActivationsFast.kappaActivation(inputs, sharedWeights[an.offsetWeightIndex]);
        /*
         if (an.outputValue != ActivationsFast.kappaActivation(an.sumedInputs)) {
         System.out.println("stop");
         }
         */
        if (Global.adaptiveActivations) {
            an.outputValue = ActivationsFast.kappaActivation(an.activation, an.sumedInputs);
        } else {
            an.outputValue = ActivationsFast.kappaActivation(an.sumedInputs);
        }
        return an.outputValue;
    }

    public static double evaluateFast(RuleAggNeuron rn) {
        if (!ignoreDropout && rn.dropMe) {
            rn.outputValue = 0;
            return 0;
        }

        if (rn.outputValue != 0.0000000) {
            return rn.outputValue;
        }

        if (rn.inputNeuronsCompressed != null && rn.inputNeuronsCompressed.length == 0) {
            rn.outputValue = 0.5;
            return 0.5;
        }

        if (fullLambda) {   //all body grounding calculated separately
            if (rn.ruleBodyGroundings.length == 0) {
                rn.outputValue = 0.5;
                return 0.5;
            }

            double[] outerInputs = new double[rn.ruleBodyGroundings.length];
            for (int i = rn.ruleBodyGroundings.length - 1; i >= 0; i--) {
                //double[] innerInputs = new double[rn.ruleBodyGroundings[i].length];  //inside one body-grounding
                rn.sumedInputsOfEachBodyGrounding[i] = 0;
                for (int j = rn.ruleBodyGroundings[i].length - 1; j >= 0; j--) {
                    //innerInputs[j] = evaluateFast(rn.ruleBodyGroundings[i][j]);
                    //rn.sumedInputsOfEachBodyGrounding[i] += innerInputs[j];
                    rn.sumedInputsOfEachBodyGrounding[i] += evaluateFast(rn.ruleBodyGroundings[i][j]);
                }
                if (Global.adaptiveActivations) {
                    outerInputs[i] = ActivationsFast.lambdaActivation(rn.activation, rn.sumedInputsOfEachBodyGrounding[i], rn.lambdaOffset);
                } else {
                    outerInputs[i] = ActivationsFast.lambdaActivation(rn.sumedInputsOfEachBodyGrounding[i], rn.lambdaOffset);
                }
            }
            if (grounding == Global.groundingSet.max) {
                int index = ActivationsFast.getMaximumIndex(outerInputs);
                rn.maxBodyGroundingIndex = index;
                rn.outputValue = outerInputs[index];
            } else {
                rn.outputValue = ActivationsFast.aggregation(outerInputs);
            }
        } else {    //compressed grounding representation is summed up and divided -> avg and then sigmoid
            //double[] inputs = new double[rn.inputNeuronsCompressed.length];  //inside one body-grounding
            rn.sumedInputs = rn.lambdaOffset;
            for (int i = 0; i < rn.inputNeuronsCompressed.length; i++) {
                //inputs[i] = evaluateFast(rn.inputNeuronsCompressed[i]) * rn.inputNeuronCompressedCounts[i] / rn.ruleBodyGroundingsCount;  //AVG trick!
                //rn.sumedInputs += inputs[i];
                rn.sumedInputs += evaluateFast(rn.inputNeuronsCompressed[i]) * rn.inputNeuronCompressedCounts[i] / rn.ruleBodyGroundingsCount;  //AVG trick!
            }
            /*
             if ((rn.outputValue != ActivationsFast.lambdaActivation(rn.sumedInputs))) {
             writeOutError(rn);
             }
             */
            if (Global.adaptiveActivations) {
                rn.outputValue = ActivationsFast.lambdaActivation(rn.activation, rn.sumedInputs);
            } else {
                rn.outputValue = ActivationsFast.lambdaActivation(rn.sumedInputs);
            }
        }
        return rn.outputValue;
    }

    /*
     private static final void writeOutError(RuleAggNeuron rn) {
     int i = 0;

     double neuron = 0;
     Double lambda = 0.0;

     ArrayList<Double> inputs = new ArrayList<>(rn.grl.getConjunctsAvg().size());

     for (Map.Entry<GroundKappa, Integer> grki : rn.grl.getConjunctsAvg().entrySet()) {
     neuron += rn.inputNeuronsCompressed[i].outputValue * rn.inputNeuronCompressedCounts[i] / rn.ruleBodyGroundingsCount;
     lambda += grki.getKey().getValueAvg() * grki.getValue() / rn.grl.getConjunctsCountForAvg();
     inputs.add(grki.getKey().getValueAvg() * grki.getValue() / rn.grl.getConjunctsCountForAvg());

     NumberFormat formatter = new DecimalFormat("#0.00000000000000000000000000000000000000000000");
     System.out.println(formatter.format(grki.getKey().getValueAvg()) + " -> " + grki.getValue());
     System.out.println(formatter.format(rn.inputNeuronsCompressed[i].outputValue) + " -> " + rn.inputNeuronCompressedCounts[i]);
     if (grki.getKey().getValueAvg() != rn.inputNeuronsCompressed[i].outputValue || grki.getValue() != rn.inputNeuronCompressedCounts[i]) {
     System.out.println("one input");
     }
     i++;
     }
     if (neuron != lambda) {
     System.out.println("summation");
     }
     if (rn.lambdaOffset != rn.grl.getGeneral().getOffset()) {
     System.out.println("offset");
     }
     if (neuron != rn.sumedInputs) {
     System.out.println("suma");
     }

     double before = rn.outputValue;

     double neuralOut = ActivationsFast.lambdaActivation(rn.sumedInputs, rn.lambdaOffset);
     double lambdaOut1 = ActivationsFast.lambdaActivation(lambda, rn.grl.getGeneral().getOffset());

     double lambdaOut2 = Activations.lambdaActivation(inputs, rn.grl.getGeneral().getOffset());

     System.out.println("before " + before + ", neuralOut " + neuralOut + ", " + "lambda1 " + lambdaOut1 + ", lambda2 " + lambdaOut2);
     }
     */
}
