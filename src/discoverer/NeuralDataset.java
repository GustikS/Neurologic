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
import discoverer.global.Global;
import discoverer.grounding.network.GroundKL;
import discoverer.grounding.network.groundNetwork.GroundNetwork;
import discoverer.grounding.network.groundNetwork.GroundNeuron;
import discoverer.learning.Sample;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Gusta this object is the only thing necessary for learning phase -
 * for future memory tuning
 */
public class NeuralDataset {

    public GroundNetwork[] groundNetworks;
    public long timeToBuild;
    public GroundNetwork tmpActiveNet; //auxiliary to get reference from neurons to their mother network (without storing pointer in them cause of serialization)

    public double[] sharedWeights; //the shared sharedWeights

    public HashMap<Object, Integer> weightMapping;  //Kappa offsets and KappaRule's weights to indicies in sharedWeights
    
    public HashMap<GroundKL,GroundNeuron> neuronMapping;

    public NeuralDataset(List<Sample> samples, LiftedNetwork network) {
        weightMapping = new HashMap<>();
        neuronMapping = new HashMap<>();
        createSharedWeights(network);
    }

    final void createSharedWeights(LiftedNetwork network) {
        int weightCounter = 0;
        for (Rule rule : network.rules) {
            if (rule instanceof KappaRule) {
                weightMapping.put(rule, weightCounter++);
            }
        }
        for (Kappa kappa : network.getKappas()) {
            //if (!kappa.getRules().isEmpty()) {    // - nope, let's learn Kappa elements offsets too in the fast version! :)
                weightMapping.put(kappa, weightCounter++);
            //}
        }

        sharedWeights = new double[weightCounter];
    }

    public final void makeNeuralNetworks(List<Sample> samples) {
        Global.neuralDataset = this;
        groundNetworks = new GroundNetwork[samples.size()];
        for (int i = 0; i < samples.size(); i++) {
            Sample sample = samples.get(i);
            groundNetworks[i] = new GroundNetwork();
            groundNetworks[i].allNeurons = new GroundNeuron[sample.getBall().groundNeurons.size()];
            tmpActiveNet = groundNetworks[i];
            groundNetworks[i].createNetwork(sample);
        }
        tmpActiveNet = null;
    }

    /**
     * reinitialize all kappa offests and kapparule sharedWeights of the
     * template
     */
    public void invalidateWeights() {
        for (int i = 0; i < sharedWeights.length; i++) {
            sharedWeights[i] = WeightInitializator.getWeight();
        }
    }
}
