/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer;

import discoverer.construction.template.Kappa;
import discoverer.construction.template.LiftedTemplate;
import discoverer.construction.template.MolecularTemplate;
import discoverer.construction.network.rules.KappaRule;
import discoverer.construction.network.rules.Rule;
import discoverer.drawing.GroundDotter;
import discoverer.global.Global;
import discoverer.grounding.network.groundNetwork.GroundNetwork;
import discoverer.grounding.network.groundNetwork.GroundNeuron;
import discoverer.learning.Sample;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a lightweight dataset representation - this class contains only all
 * the things necessary for learning phase - for memory saving and speedup
 */
public class NeuralDataset extends LiftedDataset implements Serializable {

    //this SampleSplitter contains small samples (low memory) as they do not contain Example and GroundTemplate (K/L) structures
    //public SampleSplitter sampleSplitter;  //those samples contain only groundNetworks (the same objects as bellow) and their target values
    //public GroundNetwork[] groundNetworks;
    public long timeToBuild;

    public NeuralDataset(LiftedDataset ld) {
        //super();  //copy necessary input network's variables
        //network = ld.network;

        pretrained = ld.pretrained;
        sampleSplitter = ld.sampleSplitter;

        LiftedTemplate net = ld.network;

        net.weightMapping = new HashMap<>(net.rules.size());
        net.neuronMapping = new HashMap<>(net.rules.size());
        createSharedWeights(net);

        makeNeuralNetworks(net, sampleSplitter.samples);

        makeTemplate(net);
    }

    /**
     * sets the Lifted Network template from previous LitedDataset or creates a
     * new lightweight version of it
     *
     * @param template
     */
    final void makeTemplate(LiftedTemplate template) {
        //makeMeSmall(network);
        template.name2weight = new LinkedHashMap<>(template.rules.size());
        for (Map.Entry<String, Integer> woi : template.weightMapping.entrySet()) {
            template.name2weight.put(woi.getKey(), woi.getValue());
        }
        if (Global.memoryLight) {
            super.network = new LiftedTemplate(template.sharedWeights, template.name2weight);
        } else {
            super.network = template;
        }
    }

    /**
     * mapping of lifted template weights into the sharedWeights vector
     *
     * @param network
     */
    final void createSharedWeights(LiftedTemplate network) {
        int weightCounter = 0;
        for (Rule rule : network.rules) {
            if (rule instanceof KappaRule) {
                network.weightMapping.put(((KappaRule) rule).toString(), weightCounter++);
            }
        }
        for (Kappa kappa : network.getKappas()) {
            //if (!kappa.getRules().isEmpty()) {    // - nope, let's learn Kappa elements offsets too in the fast version! :)
            network.weightMapping.put(kappa.toString(), weightCounter++);
            //}
        }
        network.sharedWeights = new double[weightCounter];
    }

    /**
     * creation of the fast neural network objects
     *
     * @param net
     * @param samples
     */
    public final void makeNeuralNetworks(LiftedTemplate net, List<Sample> samples) {
        //Global.neuralDataset = this; //important here - not anymore
        //groundNetworks = new GroundNetwork[samples.size()];
        for (int i = 0; i < samples.size(); i++) {
            Sample sample = samples.get(i);
            if (sample.getBall().getLast() != null) {
                sample.neuralNetwork = new GroundNetwork();
                sample.neuralNetwork.allNeurons = new GroundNeuron[sample.getBall().groundNeurons.size()];  //only higher layer (no-fact) neurons!
                net.tmpActiveNet = sample.neuralNetwork;
                net.tmpConstantNames = sample.getExample().constantNames;
                sample.neuralNetwork.createNetwork(sample, net);
            }
            sample.targetValue = sample.getExample().getExpectedValue();

            GroundDotter.drawAVG(sample.getBall(),"testNormal");
            
            GroundDotter.draw(sample.neuralNetwork,"testNeural",net.sharedWeights);
            
            if (Global.memoryLight) {
                sample.makeMeSmall();   //make the sample small!
            }
        }
    }

    /**
     * clear unnecessary structures for learning
     *
     * @param network
     */
    public final void makeMeSmall(MolecularTemplate network) {
        network.tmpActiveNet = null;
        network.neuronMapping = null;
        network.weightMapping = null;
    }
}
