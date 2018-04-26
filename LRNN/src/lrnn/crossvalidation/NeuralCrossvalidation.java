/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lrnn.crossvalidation;

import lrnn.LiftedDataset;
import lrnn.construction.template.LiftedTemplate;
import lrnn.construction.template.LightTemplate;
import lrnn.global.Glogger;
import lrnn.grounding.evaluation.EvaluatorFast;
import lrnn.learning.Result;
import lrnn.learning.Results;
import lrnn.learning.Sample;
import lrnn.learning.learners.LearnerFast;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gusta
 */
public class NeuralCrossvalidation extends Crossvalidation {

    public NeuralCrossvalidation(int foldC) {
        super(foldC);
    }

    public NeuralCrossvalidation(SampleSplitter ss) {
        super(ss);
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
        Glogger.LogTrain("test set results...");
        trainResults.clearResultList();
        for (Sample sample : testExamples) {
            double eval = EvaluatorFast.evaluateFast(sample.neuralNetwork, net.sharedWeights);
            trainResults.add(new Result(eval, sample.targetValue));
            Glogger.LogTrain("Example #" + sample.position + "; expected: ; " + sample.targetValue + " ; actual: ; " + eval);
            Glogger.LogPred(sample.position + "," + sample.targetValue + "," + eval);
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
        List<Sample> roundStore = new ArrayList<>();
        roundStore.addAll(examples);
        Results res;
        LearnerFast learner = new LearnerFast();
        res = learner.solveFast(network, roundStore);

        Glogger.LogTrain("train set results...");
        for (Sample sample : roundStore) {
            double eval = EvaluatorFast.evaluateFast(sample.neuralNetwork, network.sharedWeights);
            Glogger.LogTrain("Example #" + sample.position + "; expected: ; " + sample.targetValue + " ; actual: ; " + eval);
        }

        try {
            res.computeAUC();
        } catch (Exception ex) {
            Logger.getLogger(Crossvalidation.class.getName()).log(Level.SEVERE, null, ex);
        }
        res.training = res.actualResult;

        LiftedTemplate templ = (LiftedTemplate) network;
        templ.setWeightsFromArray(templ.weightMapping, templ.sharedWeights);

        return res;
    }
}
