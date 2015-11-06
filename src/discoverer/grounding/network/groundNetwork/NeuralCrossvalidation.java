/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer.grounding.network.groundNetwork;

import discoverer.LiftedDataset;
import discoverer.NeuralDataset;
import discoverer.construction.network.LightTemplate;
import discoverer.construction.network.MolecularTemplate;
import discoverer.crossvalidation.Crossvalidation;
import discoverer.global.Global;
import discoverer.global.Glogger;
import discoverer.grounding.evaluation.EvaluatorFast;
import discoverer.learning.Result;
import discoverer.learning.Results;
import discoverer.learning.Sample;
import java.util.List;

/**
 *
 * @author Gusta
 */
public class NeuralCrossvalidation extends Crossvalidation {

    public NeuralCrossvalidation(LiftedDataset nns) {
        super(nns.sampleSplitter);
    }

    /**
     * fast test set error evaluation (the samples were already grounded at the
     * beginning, so we just evaluate them with the learned weights and
     * threshold)
     * @param trainResults
     */
    @Override
    public double test(LightTemplate net, Results trainResults, List<Sample> examples) {
        Results results = new Results();    //we do not ground again here
        double error = 0.0;

        for (Sample sample : examples) {
            double eval = EvaluatorFast.evaluateFast(sample.neuralNetwork, net.sharedWeights);

            results.add(new Result(eval, sample.targetValue));

            double clas = eval > trainResults.getThreshold() ? 1.0 : 0.0;
            Glogger.info("Example " + sample.position + " -" + sample.toString() + " : Classified -> " + clas + " Expected -> " + sample.targetValue + " Out -> " + eval + " Thresh -> " + trainResults.getThreshold());
            if (clas != sample.targetValue) {
                error += 1.0;
            }
        }
        error /= examples.size();
        Glogger.LogRes("Fold Train error : " + trainResults.getLearningError());
        Glogger.LogRes("Fold Test error : " + error);
        Glogger.LogRes("Fold re-calculated threshold Test error (invalid, theoretical value) : " + results.getLearningError());

        return error;
    }

    public Results train(LightTemplate network, List<Sample> examples) {
        LearnerFast s = new LearnerFast();
        return s.solveFast(network, examples);
    }
}
