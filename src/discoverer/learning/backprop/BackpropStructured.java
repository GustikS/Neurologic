/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer.learning.backprop;

import discoverer.global.Global;
import discoverer.global.Settings;
import discoverer.grounding.network.groundNetwork.AtomNeuron;
import discoverer.grounding.network.groundNetwork.GroundNetwork;
import discoverer.grounding.network.groundNetwork.RuleAggNeuron;
import discoverer.learning.Sample;
import discoverer.learning.functions.ActivationsFast;

/**
 * backprop amended for structure learning
 *
 * @author Gusta
 */
public class BackpropStructured {

    //SL parameters
    final int depth;
    final int regularization;
    //---others
    private double[] weightUpdates;
    private double[] sharedWeights;
    private static final boolean fullLambda = Global.uncompressedLambda;
    private static final Global.groundingSet grounding = Global.getGrounding();

    public BackpropStructured(int depth, int regularization) {
        this.depth = depth;
        this.regularization = regularization;
    }

    public double[] getWeightUpdates(double[] sharedW, Sample sam) {
        sharedWeights = sharedW;
        GroundNetwork gnet = sam.neuralNetwork;
        weightUpdates = new double[sharedWeights.length];

        if (gnet.outputNeuron == null) {
            return weightUpdates;
        }
        double baseDerivative = (sam.targetValue - gnet.outputNeuron.outputValue);  //output error-level derivative

        if (gnet.outputNeuron instanceof AtomNeuron) {
            derive((AtomNeuron) gnet.outputNeuron, Settings.learnRate * baseDerivative, 0);
        } else {
            derive((RuleAggNeuron) gnet.outputNeuron, Settings.learnRate * baseDerivative, 0);
        }

        //writeoutUpdates(weightUpdates);
        return weightUpdates;
    }

    private void derive(AtomNeuron atomNeuron, double derivative, int level) {
        if (level > depth) {
            return;
        }
        if (atomNeuron.dropMe) {
            return;
        }

        if (atomNeuron.inputNeurons.length == 0) {
            return;
        }

        atomNeuron.groundParentsChecked++;
        atomNeuron.groundParentDerivativeAccumulated += derivative;

        if (atomNeuron.groundParentsChecked == atomNeuron.groundParentsCount) { //all parents have sent their message from the top
            double currentLevelDerivate;
            if (Global.adaptiveActivations) {
                currentLevelDerivate = atomNeuron.groundParentDerivativeAccumulated * ActivationsFast.kappaActivationDerived(atomNeuron.activation, atomNeuron.sumedInputs);
            } else {
                currentLevelDerivate = atomNeuron.groundParentDerivativeAccumulated * ActivationsFast.kappaActivationDerived(atomNeuron.sumedInputs);
            }
            weightUpdates[atomNeuron.offsetWeightIndex] += currentLevelDerivate * 1; //offset weight update with current level (derived function on input value)

            for (int i = 0; i < atomNeuron.inputNeurons.length; i++) {
                weightUpdates[atomNeuron.inputWeightIndices[i]] += currentLevelDerivate * atomNeuron.inputNeurons[i].outputValue;
                derive(atomNeuron.inputNeurons[i], currentLevelDerivate * sharedWeights[atomNeuron.inputWeightIndices[i]], level + 1);
            }
        }
    }

    private void derive(RuleAggNeuron ruleAggNeuron, double derivative, int level) {
        if (level > depth) {
            return;
        }
        if (ruleAggNeuron.dropMe) {
            return;
        }
        ruleAggNeuron.groundParentsChecked++;
        ruleAggNeuron.groundParentDerivativeAccumulated += derivative;

        if (ruleAggNeuron.groundParentsChecked == ruleAggNeuron.groundParentsCount) { //all parents have sent their message from the top
            double currentLevelDerivative;
            if (fullLambda) {
                currentLevelDerivative = ruleAggNeuron.groundParentDerivativeAccumulated;
                deriveBodyGroundings(ruleAggNeuron, currentLevelDerivative, level + 1);
            } else {
                if (Global.adaptiveActivations) {
                    currentLevelDerivative = ruleAggNeuron.groundParentDerivativeAccumulated * ActivationsFast.lambdaActivationDerived(ruleAggNeuron.activation, ruleAggNeuron.sumedInputs);
                } else {
                    currentLevelDerivative = ruleAggNeuron.groundParentDerivativeAccumulated * ActivationsFast.lambdaActivationDerived(ruleAggNeuron.sumedInputs);
                }
                for (int i = 0; i < ruleAggNeuron.inputNeuronsCompressed.length; i++) {
                    derive(ruleAggNeuron.inputNeuronsCompressed[i], currentLevelDerivative * ruleAggNeuron.inputNeuronCompressedCounts[i] * ActivationsFast.aggregationDerived(ruleAggNeuron.ruleBodyGroundingsCount), level + 1);
                }
            }
        }
    }

    void deriveBodyGroundings(RuleAggNeuron ruleAggNeuron, double currentLevelDerivative, int level) {
        if (grounding == Global.groundingSet.max) {
            int i = ruleAggNeuron.maxBodyGroundingIndex;
            double oneGroundRuleDerivative;
            if (Global.adaptiveActivations) {
                oneGroundRuleDerivative = ActivationsFast.aggregationDerived(ruleAggNeuron.ruleBodyGroundings.length) * ActivationsFast.lambdaActivationDerived(ruleAggNeuron.activation, ruleAggNeuron.sumedInputsOfEachBodyGrounding[i], ruleAggNeuron.lambdaOffset);
            } else {
                oneGroundRuleDerivative = ActivationsFast.aggregationDerived(ruleAggNeuron.ruleBodyGroundings.length) * ActivationsFast.lambdaActivationDerived(ruleAggNeuron.sumedInputsOfEachBodyGrounding[i], ruleAggNeuron.lambdaOffset);
            }
            for (int j = 0; j < ruleAggNeuron.ruleBodyGroundings[i].length; j++) {
                derive(ruleAggNeuron.ruleBodyGroundings[i][j], currentLevelDerivative * oneGroundRuleDerivative, level);
            }
        } else {
            for (int i = 0; i < ruleAggNeuron.ruleBodyGroundings.length; i++) {
                double oneGroundRuleDerivative;
                if (Global.adaptiveActivations) {
                    oneGroundRuleDerivative = ActivationsFast.aggregationDerived(ruleAggNeuron.ruleBodyGroundings.length) * ActivationsFast.lambdaActivationDerived(ruleAggNeuron.activation, ruleAggNeuron.sumedInputsOfEachBodyGrounding[i], ruleAggNeuron.lambdaOffset);
                } else {
                    oneGroundRuleDerivative = ActivationsFast.aggregationDerived(ruleAggNeuron.ruleBodyGroundings.length) * ActivationsFast.lambdaActivationDerived(ruleAggNeuron.sumedInputsOfEachBodyGrounding[i], ruleAggNeuron.lambdaOffset);
                }
                for (int j = 0; j < ruleAggNeuron.ruleBodyGroundings[i].length; j++) {
                    derive(ruleAggNeuron.ruleBodyGroundings[i][j], currentLevelDerivative * oneGroundRuleDerivative, level);
                }
            }
        }
    }

}
