/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer.grounding.network.groundNetwork;

import discoverer.global.Global;
import discoverer.global.Settings;
import discoverer.learning.Sample;
import java.util.Map;

/**
 *
 * @author Gusta
 */
public final class BackpropFast {

    private static double[] weightUpdates;
    private static double[] sharedWeights;
    private static final boolean fullLambda = Global.uncompressedLambda;
    private static final Global.groundingSet grounding = Global.getGrounding();

    public static boolean updateWeights(double[] sharedW, Sample sam) {
        sharedWeights = sharedW;
        GroundNetwork gnet = sam.neuralNetwork;
        weightUpdates = new double[sharedWeights.length];

        if (gnet.outputNeuron == null) {
            return false;
        }
        double baseDerivative = (sam.targetValue - gnet.outputNeuron.outputValue);  //output error-level derivative

        if (gnet.outputNeuron instanceof AtomNeuron) {
            derive((AtomNeuron) gnet.outputNeuron, Settings.learnRate * baseDerivative);
        } else {
            derive((RuleAggNeuron) gnet.outputNeuron, Settings.learnRate * baseDerivative);
        }

        for (int i = weightUpdates.length - 1; i >= 0; i--) {
            sharedWeights[i] += weightUpdates[i];
        }

        //writeoutUpdates(weightUpdates);

        return true;
    }

    private static void derive(AtomNeuron atomNeuron, double derivative) {
        if (atomNeuron.dropMe) {
            return;
        }

        if (atomNeuron.inputNeurons.length == 0) {
            return;
        }

        atomNeuron.groundParentsChecked++;
        atomNeuron.groundParentDerivativeAccumulated += derivative;

        if (atomNeuron.groundParentsChecked == atomNeuron.groundParentsCount) { //all parents have sent their message from the top
            double currentLevelDerivate = atomNeuron.groundParentDerivativeAccumulated * ActivationsFast.kappaActivationDerived(atomNeuron.sumedInputs);
            weightUpdates[atomNeuron.offsetWeightIndex] += currentLevelDerivate * 1; //offset weight update with current level (derived function on input value)

            for (int i = 0; i < atomNeuron.inputNeurons.length; i++) {
                weightUpdates[atomNeuron.inputWeightIndices[i]] += currentLevelDerivate * atomNeuron.inputNeurons[i].outputValue;
                derive(atomNeuron.inputNeurons[i], currentLevelDerivate * sharedWeights[atomNeuron.inputWeightIndices[i]]);
            }
        }
    }

    private static void derive(RuleAggNeuron ruleAggNeuron, double derivative) {
        if (ruleAggNeuron.dropMe) {
            return;
        }
        ruleAggNeuron.groundParentsChecked++;
        ruleAggNeuron.groundParentDerivativeAccumulated += derivative;

        if (ruleAggNeuron.groundParentsChecked == ruleAggNeuron.groundParentsCount) { //all parents have sent their message from the top
            double currentLevelDerivative;
            if (fullLambda) {
                currentLevelDerivative = ruleAggNeuron.groundParentDerivativeAccumulated;
                deriveBodyGroundings(ruleAggNeuron, currentLevelDerivative);
            } else {
                currentLevelDerivative = ruleAggNeuron.groundParentDerivativeAccumulated * ActivationsFast.lambdaActivationDerived(ruleAggNeuron.sumedInputs);
                for (int i = 0; i < ruleAggNeuron.inputNeuronsCompressed.length; i++) {
                    derive(ruleAggNeuron.inputNeuronsCompressed[i], currentLevelDerivative * ruleAggNeuron.inputNeuronCompressedCounts[i] * ActivationsFast.aggregationDerived(ruleAggNeuron.ruleBodyGroundingsCount));
                }
            }
        }
    }

    static void deriveBodyGroundings(RuleAggNeuron ruleAggNeuron, double currentLevelDerivative) {
        if (grounding == Global.groundingSet.max) {
            int i = ruleAggNeuron.maxBodyGroundingIndex;
            double oneGroundRuleDerivative = ActivationsFast.aggregationDerived(ruleAggNeuron.ruleBodyGroundings.length) * ActivationsFast.lambdaActivationDerived(ruleAggNeuron.sumedInputsOfEachBodyGrounding[i], ruleAggNeuron.lambdaOffset);
            for (int j = 0; j < ruleAggNeuron.ruleBodyGroundings[i].length; j++) {
                derive(ruleAggNeuron.ruleBodyGroundings[i][j], currentLevelDerivative * oneGroundRuleDerivative);
            }
        } else {
            for (int i = 0; i < ruleAggNeuron.ruleBodyGroundings.length; i++) {
                double oneGroundRuleDerivative = ActivationsFast.aggregationDerived(ruleAggNeuron.ruleBodyGroundings.length) * ActivationsFast.lambdaActivationDerived(ruleAggNeuron.sumedInputsOfEachBodyGrounding[i], ruleAggNeuron.lambdaOffset);
                for (int j = 0; j < ruleAggNeuron.ruleBodyGroundings[i].length; j++) {
                    derive(ruleAggNeuron.ruleBodyGroundings[i][j], currentLevelDerivative * oneGroundRuleDerivative);
                }
            }
        }
    }

    /*
    private static void writeoutUpdates(double[] weightUpdates) {
        for (Map.Entry<Object, Integer> w : Global.neuralDataset.weightMapping.entrySet()) {
            if (weightUpdates[Global.neuralDataset.weightMapping.get(w.getKey())] > 0) {
                System.out.println(w.getKey() + " += " + weightUpdates[Global.neuralDataset.weightMapping.get(w.getKey())]);
            }
        }

    }
    */
}
