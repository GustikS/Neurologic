/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer.crossvalidation;

import discoverer.LiftedDataset;
import discoverer.construction.template.LightTemplate;
import discoverer.global.Global;
import discoverer.grounding.evaluation.EvaluatorFast;
import discoverer.learning.learners.LearnerFast;
import discoverer.learning.Result;
import discoverer.learning.Results;
import discoverer.learning.Sample;
import discoverer.learning.learners.LearnerStructured;
import discoverer.learning.learners.Learning;
import java.util.List;

/**
 *
 * @author Gusta
 */
public class NeuralCrossvalidation extends Crossvalidation {

    public NeuralCrossvalidation() {
    }

    public NeuralCrossvalidation(LiftedDataset nns) {
        super(nns.sampleSplitter);
    }

    /**
     * fast test set error evaluation (the samples were already grounded at the
     * beginning, so we just evaluate them with the learned weights and
     * threshold)
     *
     * @param trainResults
     * @param testExamples
     * @return
     */
    @Override
    public Results test(LightTemplate net, Results trainResults, List<Sample> testExamples) {
        //we do not ground again here

        trainResults.clearResultList();
        for (Sample sample : testExamples) {
            double eval = EvaluatorFast.evaluateFast(sample.neuralNetwork, net.sharedWeights);
            trainResults.add(new Result(eval, sample.targetValue));
        }
        trainResults.computeTest();
        trainResults.testing = trainResults.actualResult;
        /*
         Glogger.LogRes("Fold Train error : " + trainResults.getLearningError());
         Glogger.LogRes("Fold Test error : " + error);
         Glogger.LogRes("Fold re-calculated threshold Test error (invalid, theoretical value) : " + results.getLearningError());
         */
        return trainResults;
    }

    @Override
    public Results train(LightTemplate network, List<Sample> examples) {
        Results res;
        LearnerFast learner = new LearnerFast();
        res = learner.solveFast(network, examples);
        res.training = res.actualResult;
        return res;
    }
}
