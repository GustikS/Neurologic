/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer.grounding.network.groundNetwork;

import discoverer.construction.network.KL;
import discoverer.construction.network.MolecularTemplate;
import discoverer.global.Global;
import discoverer.grounding.network.GroundKL;
import discoverer.grounding.network.GroundKappa;
import discoverer.grounding.network.GroundLambda;
import discoverer.learning.Sample;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Gusta
 */
public class GroundNetwork implements Serializable {

    String name;

    public GroundNeuron[] allNeurons;
    private int neuronCounter = 0;

    public GroundNeuron outputNeuron;

    public GroundNetwork createNetwork(Sample sample, MolecularTemplate net) {
        name = sample.position + " : " + sample.getExample().hash;
        if (sample.getBall().getLast() instanceof GroundKappa) {
            GroundKappa gk = (GroundKappa) sample.getBall().getLast();  //runs the recursion down
            outputNeuron = new AtomNeuron(gk, net);
            //Global.neuralDataset.neuronMapping.put(gk, outputNeuron);
        } else {
            GroundLambda gl = (GroundLambda) sample.getBall().getLast();  //runs the recursion down
            outputNeuron = new RuleAggNeuron(gl, net);
            //Global.neuralDataset.neuronMapping.put(gl, outputNeuron);
        }
        return this;
    }

    void addNeuron(GroundNeuron gn) {
        allNeurons[neuronCounter++] = gn;
    }

    public void invalidateNeuronValues() {
        for (int i = allNeurons.length - 1; i >= 0; i--) {
            allNeurons[i].invalidateValue();
        }
    }

    public void dropOut(double dropout) {
        for (int i = 0; i < allNeurons.length; i++) {
            allNeurons[i].dropMe = Global.getRg().nextDouble() < dropout;
        }
    }
}
