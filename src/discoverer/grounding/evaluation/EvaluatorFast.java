/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer.grounding.evaluation;

import discoverer.global.Global;
import static discoverer.grounding.evaluation.Evaluator.ignoreDropout;
import discoverer.grounding.network.groundNetwork.ActivationsFast;
import discoverer.grounding.network.groundNetwork.AtomNeuron;
import discoverer.grounding.network.groundNetwork.GroundNetwork;
import discoverer.grounding.network.groundNetwork.RuleAggNeuron;

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

        if (an.outputValue != 0.0000000) {
            return an.outputValue;  //it doesnt make sense for fact neuron (isElement) to have zero value
        }

        //double[] inputs = new double[an.inputWeightIndices.length];
        an.sumedInputs = 0;
        for (int i = an.inputNeurons.length - 1; i >= 0; i--) {
            //inputs[i] = evaluateFast(an.inputNeurons[i]) * sharedWeights[an.inputWeightIndices[i]];
            //an.sumedInputs += inputs[i];
            an.sumedInputs += evaluateFast(an.inputNeurons[i]) * sharedWeights[an.inputWeightIndices[i]];
        }
        //an.outputValue = ActivationsFast.kappaActivation(inputs, sharedWeights[an.offsetWeightIndex]);
        an.outputValue = ActivationsFast.kappaActivation(an.sumedInputs, sharedWeights[an.offsetWeightIndex]);
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

        if (fullLambda) {   //all body grounding calculated separately
            double[] outerInputs = new double[rn.ruleBodyGroundings.length];
            for (int i = rn.ruleBodyGroundings.length - 1; i >= 0; i--) {
                //double[] innerInputs = new double[rn.ruleBodyGroundings[i].length];  //inside one body-grounding
                rn.sumedInputsOfEachBodyGrounding[i] = 0;
                for (int j = rn.ruleBodyGroundings[i].length - 1; j >= 0; j--) {
                    //innerInputs[j] = evaluateFast(rn.ruleBodyGroundings[i][j]);
                    //rn.sumedInputsOfEachBodyGrounding[i] += innerInputs[j];
                    rn.sumedInputsOfEachBodyGrounding[i] += evaluateFast(rn.ruleBodyGroundings[i][j]);
                }
                outerInputs[i] = ActivationsFast.lambdaActivation(rn.sumedInputsOfEachBodyGrounding[i], rn.lambdaOffset);
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
            rn.sumedInputs = 0;
            for (int i = rn.inputNeuronsCompressed.length - 1; i >= 0; i--) {
                //inputs[i] = evaluateFast(rn.inputNeuronsCompressed[i]) * rn.inputNeuronCompressedCounts[i] / rn.ruleBodyGroundingsCount;  //AVG trick!
                //rn.sumedInputs += inputs[i];
                rn.sumedInputs += evaluateFast(rn.inputNeuronsCompressed[i]) * rn.inputNeuronCompressedCounts[i] / rn.ruleBodyGroundingsCount;  //AVG trick!
            }
            rn.outputValue = ActivationsFast.lambdaActivation(rn.sumedInputs, rn.lambdaOffset);
        }
        return rn.outputValue;
    }
}
