/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer.grounding.network.groundNetwork;

import discoverer.construction.network.KL;
import discoverer.grounding.network.GroundKL;
import discoverer.grounding.network.GroundKappa;
import discoverer.grounding.network.GroundLambda;
import discoverer.learning.Sample;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Gusta
 */
public class GroundNetwork {

    GroundNeuron[] allNeurons;
    private int neuronCounter = 0;

    GroundNeuron outputNeuron;
    double targetValue;

    GroundNetwork createNetwork(Sample sample) {
        targetValue = sample.getExample().getExpectedValue();
        if (sample.getBall().getLast() instanceof GroundKappa) {
            GroundKappa gk = (GroundKappa) sample.getBall().getLast();  //runs the recursion down
            outputNeuron = new AtomNeuron(gk);
        } else {
            GroundLambda gl = (GroundLambda) sample.getBall().getLast();  //runs the recursion down
            outputNeuron = new RuleAggNeuron(gl);
        }
        return this;
    }

    void addNeuron(GroundNeuron gn) {
        allNeurons[neuronCounter++] = gn;
    }
}
