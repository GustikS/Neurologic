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

    public int[] inputWeightIndices; //array of indicies into Dataset.sharedWeights
    public RuleAggNeuron[] inputNeurons;
    public int offsetWeightIndex;

    public AtomNeuron(GroundKappa grk) {
        offsetWeightIndex = Global.neuralDataset.weightMapping.get(grk.getGeneral());
        if (grk.getDisjunctsAvg().isEmpty()) {
            return;
        }
        inputNeurons = new RuleAggNeuron[grk.getDisjunctsAvg().size()];
        inputWeightIndices = new int[grk.getDisjunctsAvg().size()];
        int i = 0;
        for (Tuple<HashSet<GroundLambda>, KappaRule> grl : grk.getDisjunctsAvg()) {
            inputNeurons[i] = new RuleAggNeuron(grl.x.iterator().next());   //there shouldn't be more then 1 literal in KappaRule (it's not real rule)
            inputWeightIndices[i++] = Global.neuralDataset.weightMapping.get(grl.y);
        }
        Global.neuralDataset.tmpActiveNet.addNeuron(this); //rather put these leaking "this" on the end of contructor
    }
}
