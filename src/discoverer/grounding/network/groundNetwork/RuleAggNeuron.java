/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer.grounding.network.groundNetwork;

import discoverer.construction.network.MolecularTemplate;
import discoverer.global.Global;
import discoverer.grounding.network.GroundKappa;
import discoverer.grounding.network.GroundLambda;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Gusta
 */
public class RuleAggNeuron extends GroundNeuron {

    //compressed representation
    public AtomNeuron[] inputNeuronsCompressed;    // first counted sum of all grounded body literals, tehn avg, then sigmoid
    public int[] inputNeuronCompressedCounts;
    public int ruleBodyGroundingsCount;

    //uncompressed representation
    public AtomNeuron[][] ruleBodyGroundings = null; //uncompressed representation with proper rule neurons each with sigmoid, then avg
    public double[] sumedInputsOfEachBodyGrounding = null;
    public int maxBodyGroundingIndex;

    public double lambdaOffset;

    //public GroundLambda grl;
    RuleAggNeuron(GroundLambda gl, MolecularTemplate net) {
        name = gl.toString();
        outputValue = gl.getValueAvg();
        //grl = gl;

        lambdaOffset = gl.getGeneral().getOffset();
        groundParentsCount = gl.getGroundParents();

        ruleBodyGroundingsCount = gl.getConjunctsCountForAvg();

        if (Global.uncompressedLambda) {
            ruleBodyGroundings = new AtomNeuron[gl.getConjunctsCountForAvg()][gl.getConjuncts().size()];
            sumedInputsOfEachBodyGrounding = new double[gl.getConjunctsCountForAvg()];
            if (gl.getConjunctsAvg().isEmpty()) {
                return;
            }
            //TODO check correctness of the uncompressed version HERE
            for (int j = 0; j < inputNeuronsCompressed.length; j++) {
                List<GroundKappa> oneBodyGrounding = gl.fullBodyGroundings.get(j);
                for (int k = 0; k < oneBodyGrounding.size(); k++) {
                    GroundNeuron gn = net.neuronMapping.get(oneBodyGrounding.get(k));
                    if (gn == null) {
                        ruleBodyGroundings[j][k] = new AtomNeuron(oneBodyGrounding.get(k), net);
                        net.neuronMapping.put(oneBodyGrounding.get(k), ruleBodyGroundings[j][k]);
                    } else {
                        ruleBodyGroundings[j][k] = (AtomNeuron) gn;
                    }
                }
            }
        } else {
            int i = 0;
            inputNeuronsCompressed = new AtomNeuron[gl.getConjunctsAvg().size()];
            inputNeuronCompressedCounts = new int[gl.getConjunctsAvg().size()];
            if (gl.getConjunctsAvg().isEmpty()) {
                return;
            }
            for (Map.Entry<GroundKappa, Integer> gki : gl.getConjunctsAvg().entrySet()) {
                GroundNeuron gn = net.neuronMapping.get(gki.getKey());
                if (gn == null) {
                    inputNeuronsCompressed[i] = new AtomNeuron(gki.getKey(), net);
                    net.neuronMapping.put(gki.getKey(), inputNeuronsCompressed[i]);
                } else {
                    inputNeuronsCompressed[i] = (AtomNeuron) gn;
                }
                inputNeuronCompressedCounts[i++] = gki.getValue();
            }
        }
        net.tmpActiveNet.addNeuron(this); //rather put these "this" on the end of contructor
    }
}
