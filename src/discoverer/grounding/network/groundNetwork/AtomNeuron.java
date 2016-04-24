/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer.grounding.network.groundNetwork;

import discoverer.construction.template.LiftedTemplate;
import discoverer.construction.template.MolecularTemplate;
import discoverer.construction.network.rules.KappaRule;
import discoverer.global.Global;
import discoverer.global.Glogger;
import discoverer.global.Tuple;
import discoverer.grounding.network.GroundKappa;
import discoverer.grounding.network.GroundLambda;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author Gusta
 */
public class AtomNeuron extends GroundNeuron {

    public int[] inputWeightIndices; //array of indicies into Dataset.sharedWeights
    public RuleAggNeuron[] inputNeurons;
    public int offsetWeightIndex;

    public AtomNeuron(GroundKappa grk, LiftedTemplate net) {
        name = grk.toString(net.tmpConstantNames);
        activation = grk.getGeneral().activation;

        outputValue = grk.getValueAvg();

        groundParentsCount = grk.getGroundParents();
        offsetWeightIndex = net.weightMapping.get(grk.getGeneral().toString());
        net.sharedWeights[offsetWeightIndex] = grk.getGeneral().getOffset();

        ArrayList<RuleAggNeuron> dynInputNeurons = new ArrayList<>(grk.getDisjunctsAvg().size()*2); //unfortunatelly we do not know this size in advance, i.e. how many groundings each kapparule has

        if (grk.getDisjunctsAvg().isEmpty()) {
            //inputNeurons = new RuleAggNeuron[0];    //used as flag for fact neurons -> changed to null instead
            return;
        }

        ArrayList<Integer> dynInputWeightIndices = new ArrayList<>(grk.getDisjunctsAvg().size()*2); //unfortunatelly we do not know this size in advance
        int i = 0;
        for (Tuple<HashSet<GroundLambda>, KappaRule> grl : grk.getDisjunctsAvg()) {
            for (GroundLambda lambdaHead : grl.x) {
                GroundNeuron gn = net.neuronMapping.get(lambdaHead);   //have we already visited this groundLambda?
                if (gn == null) {
                    RuleAggNeuron ragg = new RuleAggNeuron(lambdaHead, net);
                    dynInputNeurons.add(ragg);
                    net.neuronMapping.put(lambdaHead, ragg);
                } else {
                    dynInputNeurons.add((RuleAggNeuron) gn);
                }
                Integer idx = net.weightMapping.get(grl.y.toString());  //index to this kapparule's weight
                dynInputWeightIndices.add(idx);
                net.sharedWeights[idx] = grl.y.getWeight();
                i++;
            }
        }
        inputNeurons = dynInputNeurons.toArray(new RuleAggNeuron[dynInputNeurons.size()]);
        inputWeightIndices = dynInputWeightIndices.stream().mapToInt(a->a).toArray();   //Integer array to int[]
        net.tmpActiveNet.addNeuron(this); //rather put these leaking "this" at the end of contructor
    }
}
