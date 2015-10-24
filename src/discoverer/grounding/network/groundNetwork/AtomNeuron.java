/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer.grounding.network.groundNetwork;

import discoverer.construction.network.rules.KappaRule;
import discoverer.global.Global;
import discoverer.global.Tuple;
import discoverer.grounding.network.GroundKappa;
import discoverer.grounding.network.GroundLambda;
import java.util.HashSet;

/**
 *
 * @author Gusta
 */
public class AtomNeuron extends GroundNeuron {

    int[] inputWeights; //array of indicies into Dataset.weights
    int offsetWeight;
    RuleAggNeuron[] inputNeurons;

    public AtomNeuron(GroundKappa grk) {
        offsetWeight = Global.groundDataset.weightMapping.get(grk.getGeneral());
        if (grk.getDisjunctsAvg().isEmpty()){
            return;
        }
        inputNeurons = new RuleAggNeuron[grk.getDisjunctsAvg().size()];
        inputWeights = new int[grk.getDisjunctsAvg().size()];
        int i = 0;
        for (Tuple<HashSet<GroundLambda>, KappaRule> grl : grk.getDisjunctsAvg()) {
            inputNeurons[i] = new RuleAggNeuron(grl.x.iterator().next());   //there shouldn't be more then 1 literal in KappaRule (it's not real rule)
            inputWeights[i++] = Global.groundDataset.weightMapping.get(grl.y);
        }
        Global.groundDataset.tmpActiveNet.addNeuron(this); //rather put these leaking "this" on the end of contructor
    }
}