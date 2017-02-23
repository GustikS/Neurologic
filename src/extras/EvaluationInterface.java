/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package extras;

import lrnn.LiftedDataset;
import lrnn.global.Glogger;
import lrnn.grounding.evaluation.EvaluatorFast;
import lrnn.learning.Result;
import lrnn.learning.Results;
import lrnn.learning.Sample;
import java.util.List;

/**
 *
 * @author Gusta
 */
public class EvaluationInterface {

    static List<Sample> sampleStore = loadSampleStore("samples.ser");

    private static List<Sample> loadSampleStore(String arg) {
        LiftedDataset data = LiftedDataset.loadDataset(arg);
        return data.sampleSplitter.samples;
    }

    public static void main(String[] args) {
        int len = Integer.parseInt(args[0]);

        double[] weights = new double[len];

        for (int i = 0; i < weights.length; i++) {
            weights[i] = Double.parseDouble(args[i + 2]);
        }

        evaluate(sampleStore, weights);
    }

    private static void evaluate(List<Sample> sampleStore, double[] weights) {
        Results res = new Results();
        for (Sample sam : sampleStore) {
            sam.neuralNetwork.outputNeuron.outputValue = EvaluatorFast.evaluateFast(sam.neuralNetwork, weights);
            res.add(new Result(sam.neuralNetwork.outputNeuron.outputValue, sam.targetValue));
        }
        Glogger.process("All Ground Networks Evaluation : train error " + res.getLearningError() + " (maj: " + res.getMajorityClass() + ")" + " (disp: " + res.getDispersion() + ")");
    }
}
