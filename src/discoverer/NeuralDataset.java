/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer;

import discoverer.construction.network.Kappa;
import discoverer.construction.network.LiftedNetwork;
import discoverer.construction.network.WeightInitializator;
import discoverer.construction.network.rules.KappaRule;
import discoverer.construction.network.rules.Rule;
import discoverer.crossvalidation.SampleSplitter;
import discoverer.global.Global;
import discoverer.global.Settings;
import discoverer.grounding.network.GroundKL;
import discoverer.grounding.network.groundNetwork.GroundNetwork;
import discoverer.grounding.network.groundNetwork.GroundNeuron;
import discoverer.learning.Sample;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Gusta this object is the only thing necessary for learning phase -
 * for future memory tuning
 */
public class NeuralDataset extends LiftedDataset implements Serializable {

    //this SampleSplitter contains small samples (low memory) as they do not contain Example and GroundTemplate (K/L) structures
    //public SampleSplitter sampleSplitter;  //those samples contain only groundNetworks (the same objects as bellow) and their target values
    //public GroundNetwork[] groundNetworks;
    public long timeToBuild;

    public NeuralDataset(LiftedDataset ld) {
        //super();  //copy necessary input network's variables
        network = ld.network;
        pretrained = ld.pretrained;
        sampleSplitter = ld.sampleSplitter;

        network.weightMapping = new HashMap<>(network.rules.size());
        network.neuronMapping = new HashMap<>(network.rules.size());
        createSharedWeights(network);

        makeNeuralNetworks(ld.network, sampleSplitter.samples);

        makeMeSmall();
    }

    /**
     * mapping of lifted template weights into the sharedWeights vector
     *
     * @param network
     */
    final void createSharedWeights(LiftedNetwork network) {
        int weightCounter = 0;
        for (Rule rule : network.rules) {
            if (rule instanceof KappaRule) {
                network.weightMapping.put(rule, weightCounter++);
            }
        }
        for (Kappa kappa : network.getKappas()) {
            //if (!kappa.getRules().isEmpty()) {    // - nope, let's learn Kappa elements offsets too in the fast version! :)
            network.weightMapping.put(kappa, weightCounter++);
            //}
        }
        network.sharedWeights = new double[weightCounter];
    }

    /**
     * creation of the fast neural network objects
     *
     * @param samples
     */
    public final void makeNeuralNetworks(LiftedNetwork net, List<Sample> samples) {
        //Global.neuralDataset = this; //important here - not anymore
        //groundNetworks = new GroundNetwork[samples.size()];
        for (int i = 0; i < samples.size(); i++) {
            Sample sample = samples.get(i);
            sample.neuralNetwork = new GroundNetwork();
            sample.neuralNetwork.allNeurons = new GroundNeuron[sample.getBall().groundNeurons.size()];
            net.tmpActiveNet = sample.neuralNetwork;
            sample.neuralNetwork.createNetwork(sample, net);
            sample.targetValue = sample.getExample().getExpectedValue();

            //make the sample small!
            sample.makeMeSmall();
        }
    }

    /**
     * clear unnecessary structures for learning
     */
    public final void makeMeSmall() {
        network.tmpActiveNet = null;
        network.neuronMapping = null;
        network.weightMapping = null;
    }
}
