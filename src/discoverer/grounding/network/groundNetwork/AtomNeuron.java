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
        name = grk.toString();
        outputValue = grk.getValueAvg();

        groundParentsCount = grk.getGroundParents();
        offsetWeightIndex = Global.neuralDataset.weightMapping.get(grk.getGeneral());
        Global.neuralDataset.sharedWeights[offsetWeightIndex] = grk.getGeneral().getOffset();

        inputNeurons = new RuleAggNeuron[grk.getDisjunctsAvg().size()];
        if (grk.getDisjunctsAvg().isEmpty()) {
            return;
        }

        inputWeightIndices = new int[grk.getDisjunctsAvg().size()];
        int i = 0;
        for (Tuple<HashSet<GroundLambda>, KappaRule> grl : grk.getDisjunctsAvg()) {
            GroundLambda bodyLambda = grl.x.iterator().next();
            GroundNeuron gn = Global.neuralDataset.neuronMapping.get(bodyLambda);   //have we already visited this groundLambda?
            if (gn == null) {
                inputNeurons[i] = new RuleAggNeuron(bodyLambda);   //there shouldn't be more than 1 literal in KappaRule (it's not real rule)
                Global.neuralDataset.neuronMapping.put(bodyLambda, inputNeurons[i]);
            } else {
                inputNeurons[i] = (RuleAggNeuron) gn;
            }
            inputWeightIndices[i] = Global.neuralDataset.weightMapping.get(grl.y);
            Global.neuralDataset.sharedWeights[inputWeightIndices[i++]] = grl.y.getWeight();
        }
        Global.neuralDataset.tmpActiveNet.addNeuron(this); //rather put these leaking "this" on the end of contructor
    }

    public String toString() {
        return name;
    }
}
