/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package structureLearning;

import discoverer.LiftedDataset;
import discoverer.Main;
import discoverer.crossvalidation.SampleSplitter;
import discoverer.global.Glogger;
import discoverer.global.Settings;
import java.util.List;

/**
 *
 * @author Gusta
 */
public class StructureLearning {
    
    public static void main(String[] args) {
        StructureLearning sl = new StructureLearning();
        String arguments = "-r -e";
        sl.init(arguments.split(" "));
    }
    
    /**
     * redirect to the usual startup and return some initial grounded dataset (may even be non-grounded, i.e. Samples with empty GroundedTemplates)
     * @param args
     * @return 
     */
    public LiftedDataset init(String[] args) {
        //setup all parameters and load all the necessary input files
        List<String[]> inputs = Main.setupFromArguments(args);
        //create logger for all messages within the program
        Glogger.init();

        String[] test = inputs.get(0);
        String[] exs = inputs.get(1);
        String[] rules = inputs.get(2);
        String[] pretrainedRules = inputs.get(3);

        //create ground networks dataset - if Globa.fastVersion is on, thsi will be a NeuralDataset
        LiftedDataset groundedDataset = Main.createDataset(test, exs, rules, pretrainedRules);

        return groundedDataset;
    }
    /*
    public LiftedDataset reGroundMe(GroundedDataset previousRound, String[] newTemplate){
        GroundedDataset dataset = (GroundedDataset)
                
        previousRound.network = previousRound.createNetworkMerge(newTemplate);
        Glogger.process("created lifted network structure");

        samples = prepareGroundings(previousRound.examples, network);
        Glogger.process("prepared network groundings");

        //k-fold stratified example(same #positives in folds) splitting structure - treated as 1fold CV here
        sampleSplitter = new SampleSplitter(Settings.folds, samples);

        return groundedDataset;
    }
    
    public LiftedDataset backprop(LiftedDataset groundSamples, Double[] weights, String backPropType){
        //TODO zpropagovat Results az na vystup crossvaldiace
        return null;
    }
*/
}
