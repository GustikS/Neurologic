/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package structureLearning;

import discoverer.GroundedDataset;
import discoverer.LiftedDataset;
import discoverer.Main;
import discoverer.construction.network.rules.Rule;
import discoverer.construction.template.LiftedTemplate;
import discoverer.construction.template.LightTemplate;
import discoverer.crossvalidation.NeuralCrossvalidation;
import discoverer.crossvalidation.SampleSplitter;
import discoverer.global.Glogger;
import discoverer.global.Settings;
import discoverer.learning.Results;
import discoverer.learning.Sample;
import discoverer.learning.learners.LearnerFast;
import discoverer.learning.learners.LearnerStructured;
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

        //String[] test = inputs.get(0);
        String[] exs = inputs.get(1);
        String[] rules = inputs.get(2);
        String[] pretrainedRules = inputs.get(3);

        GroundedDataset groundedDataset = new GroundedDataset(exs, rules, pretrainedRules);

        return groundedDataset;
    }

    /**
     * reground previously groundedDataset with a new template, expects the
     * whole dataset (all train+test)
     *
     * @param previousRound - complete grounded dataset
     * @param newTemplate - list of rules as strings, corresponding template
     * object will be (re)created
     * @return complete new dataset
     */
    public GroundedDataset reGroundMe(GroundedDataset previousRound, String[] newTemplate) {
        GroundedDataset dataset = previousRound;

        dataset.network = dataset.createNetwork(newTemplate, "network");
        Glogger.process("created lifted network structure");

        List<Sample> preparedGroundings = dataset.prepareGroundings(previousRound.examples, previousRound.network);
        Glogger.process("prepared network groundings");

        //k-fold stratified example(same #positives in folds) splitting structure - treated as 1fold CV here
        dataset.sampleSplitter = new SampleSplitter(Settings.folds, preparedGroundings);

        return dataset;
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
    public Results train(LightTemplate template, List<Sample> samples, int learningSteps, int depth, int regularizer) {
        LearnerStructured learner = new LearnerStructured(learningSteps, depth, regularizer);
        Results results = learner.solveStructured(template, samples);
        results.training = results.actualResult;
        
        if (template instanceof LiftedTemplate){
            LiftedTemplate templ = (LiftedTemplate) template;
            templ.setWeightsFromArray(templ.weightMapping, templ.sharedWeights);    //map the learned weights back to original logical structures (rules)
        }
        
        return results;
    }

    /**
     * pure evaluation of a template on a given set of ground samples
     *
     * @param template - template object (already created from string rules)
     * @param samples - grounded samples w.r.t. that template
     * @return - Results object with testing filled
     */
    public Results test(LightTemplate template, List<Sample> samples) {
        NeuralCrossvalidation learning = new NeuralCrossvalidation();
        Results results = learning.test(template, new Results(), samples);
        return results;
    }

    /**
     * export template back into string-lines representation (with weights)
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
