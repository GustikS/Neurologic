/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer.grounding.network.groundNetwork;

import discoverer.construction.network.KL;
import discoverer.construction.network.Kappa;
import discoverer.construction.network.Lambda;
import discoverer.construction.network.Network;
import discoverer.construction.network.rules.KappaRule;
import discoverer.construction.network.rules.Rule;
import discoverer.grounding.network.GroundKL;
import discoverer.learning.Sample;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 *
 * @author Gusta
 */
public class GroundedDataset {

    public double[] weights; //the shared weights

    public HashMap<Object, Integer> weightMapping;

    public GroundNetwork[] groundNetworks;
    public long timeToBuild;

    public GroundNetwork tmpActiveNet; //auxiliary to get reference from neurons to their mother network (without storing pointer in them cause of serialization)

    public GroundedDataset(Network net, List<Sample> samples) {
        int weightCounter = 0;
        for (Rule rule : net.rules) {
            if (rule instanceof KappaRule) {
                weightMapping.put(rule, weightCounter++);
            }
        }
        for (Kappa kappa : net.getKappas()) {
            weightMapping.put(kappa, weightCounter++);
        }

        weights = new double[weightCounter];

        groundNetworks = new GroundNetwork[samples.size()];
        int i = 0;
        samples.stream().forEach((sample) -> {
            groundNetworks[i] = new GroundNetwork();
            groundNetworks[i].allNeurons = new GroundNeuron[sample.getBall().groundNeurons.size()];
            tmpActiveNet = groundNetworks[i];
            groundNetworks[i].createNetwork(sample);
        });
        tmpActiveNet = null;
    }
}
