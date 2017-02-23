/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package structureLearning;

import lrnn.GroundedDataset;
import lrnn.Main;
import lrnn.NeuralDataset;
import lrnn.construction.template.rules.Rule;
import lrnn.construction.template.LiftedTemplate;
import lrnn.construction.template.LightTemplate;
import lrnn.crossvalidation.NeuralCrossvalidation;
import lrnn.global.Global;
import lrnn.global.Glogger;
import lrnn.grounding.evaluation.Evaluator;
import lrnn.learning.Results;
import lrnn.learning.Sample;
import lrnn.learning.learners.LearnerStructured;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Gusta
 */
public class StructureLearning {

    public static void main(String[] args) {
        StructureLearning sl = new StructureLearning();
        String arguments = "-r ./templateFile -e ./exampleFile";
        sl.init(arguments);
    }

    /**
     * redirect to the usual startup and return some initial grounded dataset
     * (may even be non-grounded, i.e. Samples with empty GroundedTemplates)
     *
     * @param params - just as if starting the whole LRNN
     * @return
     */
    public GroundedDataset init(String params) {
        //setup all parameters and load all the necessary input files
        List<String[]> inputs = Main.setupFromArguments(params.split(" "));
        //create logger for all messages within the program
        Glogger.init();
        Global.memoryLight = false;
        Global.parallelTraining = false;
        Global.parallelGrounding = false;
        //
        Global.createWeightMatrix = false;
        Global.exporting = false;

        String[] test = inputs.get(0);
        String[] exs = inputs.get(1);
        String[] rules = inputs.get(2);
        String[] pretrainedRules = inputs.get(3);

        GroundedDataset groundedDataset = null;
        if (test != null) {
            groundedDataset = new GroundedDataset(exs, test, rules, pretrainedRules);
        } else {
            groundedDataset = new GroundedDataset(exs, rules, pretrainedRules);
        }
        new NeuralDataset(groundedDataset); //this is just to create sharedWeights and corresponding mappings

        return groundedDataset;
    }

    /**
     * reground previously groundedDataset with a new template, expects the
     * whole dataset (all train+test)
     *
     * @param previousDataset - complete grounded dataset
     * @param newTemplate object will be (re)created
     * @return complete new dataset
     */
    public GroundedDataset reGroundMe(GroundedDataset previousDataset, String[] newTemplate) {

        GroundedDataset newDataset = new GroundedDataset(previousDataset.examples, newTemplate);

        //map all (neurally) learned weights back to logical structures (kappa rules and offsets)
        newDataset.template.setWeightsFromArray(previousDataset.template.weightMapping, previousDataset.template.sharedWeights);    //map the learned weights back to original logical structures (rules)

        new NeuralDataset(newDataset); //this is just to create sharedWeights and corresponding mappings
        return newDataset;
    }

    /**
     * Learn by backprop on given list of grounded networks(samples) and a
     * template object. The weights of the template will be changed and Results
     * from training returned. The type of backprop is mean to be set through
     * Global.someFlag
     *
     * @param template - template object (already created from string rules)
     * @param samples - grounded samples w.r.t. that template
     * @param learningSteps - for BP
     * @param depth - for BP
     * @param regularizer - for special BP
     * @return Results object with training filled and error history
     */
    public Results train(GroundedDataset gdata, int learningSteps, int depth, int regularizer) {
        NeuralDataset neuraldata = new NeuralDataset(gdata);

        LearnerStructured learner = new LearnerStructured(learningSteps, depth, regularizer);
        Results results = learner.solveStructured(neuraldata.template, neuraldata.sampleSplitter.samples);
        results.training = results.actualResult;

        //map all (neurally) learned weights back to logical structures (kappa rules and offsets)
        if (neuraldata.template instanceof LiftedTemplate) {
            LiftedTemplate templ = (LiftedTemplate) neuraldata.template;
            templ.setWeightsFromArray(templ.weightMapping, templ.sharedWeights);    //map the learned weights back to original logical structures (rules)
        }

        //map learned outputs back to ball Avg outputs (just to make sure), samples should be in the same order (it's the same SampleSplitter)
        for (Sample sam : gdata.sampleSplitter.samples) {
            Evaluator.evaluateAvg(sam.getBall());
        }

        return results; //contains all the training information
    }

    /**
     * pure evaluation of a template on a given set of ground samples
     *
     * @param template - template object (already created from string rules)
     * @param samples - grounded samples w.r.t. that template
     * @return - Results object with testing filled
     */
    public Results test(LightTemplate template, List<Sample> samples, Results trainRes) {
        NeuralCrossvalidation learning = new NeuralCrossvalidation(1);
        Results results = learning.test(template, trainRes, samples);
        return results;
    }

    /**
     * export template back into string-lines representation (with weights)
     *
     * @param template
     * @return
     */
    public String exportTemplate(LiftedTemplate template) {
        StringBuilder sb = new StringBuilder();
        if (!template.rules.isEmpty()) {
            ArrayList<Rule> rulzz = new ArrayList(template.rules);
            for (int i = rulzz.size() - 1; i >= 0; i--) {
                sb.append(rulzz.get(i)).append("\n");
            }
        }
        return sb.toString();
    }
}
