/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer;

import discoverer.crossvalidation.SampleSplitter;
import discoverer.construction.example.Example;
import discoverer.construction.network.KL;
import discoverer.construction.network.Kappa;
import discoverer.construction.network.Lambda;
import discoverer.construction.network.MolecularTemplate;
import discoverer.construction.network.WeightInitializator;
import discoverer.construction.network.rules.KappaRule;
import discoverer.construction.network.rules.Rule;
import discoverer.global.Global;
import discoverer.global.Glogger;
import discoverer.global.Settings;
import discoverer.grounding.ForwardChecker;
import discoverer.grounding.Grounder;
import discoverer.grounding.evaluation.GroundedTemplate;
import discoverer.grounding.evaluation.struct.GroundNetworkParser;
import discoverer.grounding.network.GroundKL;
import discoverer.grounding.network.groundNetwork.GroundNetwork;
import discoverer.grounding.network.groundNetwork.GroundNeuron;
import discoverer.learning.Sample;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Gusta this class fieds are not static but are accessed from various
 * other classes, i.e. mainly the shared weights thus a reference to
 * GroundedDataset object must be somewhere stored statically
 * (Global.neuralDataset)
 */
public class GroundedDataset extends LiftedDataset {

    public List<Example> examples;  //raw example structures
    public List<Sample> samples;    //samples contain groundedTemplates (=Balls), they are too memory expensive

    //public NeuralDataset neuralNetworks;
    public GroundedDataset(String[] ex, String[] rules, String[] PretrainedRules) {
        super(rules, PretrainedRules);

        Glogger.process("created lifted network structure");
        //creates examples with corresponding ID mapping and chunk representations
        examples = createExamples(ex, Settings.maxExamples);
        Glogger.process("created example structures");

        samples = prepareGroundings(examples, (MolecularTemplate) network);
        Glogger.process("prepared network groundings");

        //k-fold stratified example(same #positives in folds) splitting structure - treated as 1fold CV here
        sampleSplitter = new SampleSplitter(Settings.folds, samples);

    }

    public GroundedDataset(String[] train, String[] test, String[] rules, String[] Pretrainedrules) {
        super(rules, Pretrainedrules);

        Glogger.process("created lifted network structure");
        List<Example> trainEx = createExamples(train, Settings.maxExamples);
        List<Example> testEx = createExamples(test, Settings.maxExamples);
        Glogger.process("created example structures");

        List<Sample> trainSamples = prepareGroundings(examples, (MolecularTemplate) network);
        List<Sample> testSamples = prepareGroundings(examples, (MolecularTemplate) network);
        Glogger.process("prepared network groundings");

        examples = trainEx;
        examples.addAll(testEx);

        //k-fold stratified example(same #positives in folds) splitting structure - treated as 1fold CV here
        sampleSplitter = new SampleSplitter(trainSamples, testSamples);

    }

    /**
     * Method for handling the first learning run<p>
     * for each example finds it's maximal and average substitution's
     * GroundedTemplate result returned as couples in Sample
     *
     * @param examples examples
     * @param net
     * @param last output node
     * @return list with balls from first run
     */
    public final List<Sample> prepareGroundings(List<Example> examples, MolecularTemplate net) {
        //find max. and average substitution for all examples
        ForwardChecker.exnum = 0;
        List<Sample> sampleStore = new ArrayList<>(examples.size());

        Glogger.process("searching for initial substition prove-trees for each example...");
        ForwardChecker.exnum = 0;
        for (Example e : examples) {
            GroundedTemplate b = Grounder.solve(net.last, e);
            Glogger.info("example: " + e + " , maxVal: " + b.valMax + ", avgVal: " + b.valAvg);
            sampleStore.add(new Sample(e, b));
        }
        
        Glogger.process("...done with intial grounding of examples");

        //here calculate for each proof-tree(=GroundedTemplate b = ground network) numbers of parents for each GroundKappa/Lambda
        Set<GroundKL> neurons = null;
        for (Sample result : sampleStore) {
            GroundedTemplate b = result.getBall();
            if (Global.getGrounding() == Global.groundingSet.avg) {
                neurons = GroundNetworkParser.parseAVG(b);
            } else if (Global.getGrounding() == Global.groundingSet.max) {
                neurons = GroundNetworkParser.parseMAX(b);
            }
            b.loadGroundNeurons(neurons);   //store all ground L/K in an array for fast and simple operations instead of DFS for each simple pass
        }

        return sampleStore;
    }
}
