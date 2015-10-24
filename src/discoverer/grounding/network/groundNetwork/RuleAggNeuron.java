/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer.grounding.network.groundNetwork;

import discoverer.global.Global;
import discoverer.grounding.network.GroundKappa;
import discoverer.grounding.network.GroundLambda;
import java.util.Map;

/**
 *
 * @author Gusta
 */
public class RuleAggNeuron extends GroundNeuron {

    AtomNeuron[] inputNeuronsCompressed;    // first counted sum of all grounded body literals, tehn avg, then sigmoid
    int[] inputNeuronCompressedCounts;

    AtomNeuron[][] ruleBodyGroundings = null; //uncompressed representation with proper rule neurons each with sigmoid, then avg

    RuleAggNeuron(GroundLambda gl) {
        if (gl.getConjunctsAvg().isEmpty()) {
            return;
        }
        int i = 0;
        if (Global.uncompressedLambda) {
            ruleBodyGroundings = new AtomNeuron[gl.getConjunctsCountForAvg()][gl.getConjuncts().size()];
            //TODO HERE
        } else {
            for (Map.Entry<GroundKappa, Integer> gki : gl.getConjunctsAvg().entrySet()) {
                inputNeuronsCompressed[i] = new AtomNeuron(gki.getKey());
                inputNeuronCompressedCounts[i] = gki.getValue();
            }
        }
        Global.groundDataset.tmpActiveNet.addNeuron(this); //rather put these "this" on the end of contructor
    }
}
