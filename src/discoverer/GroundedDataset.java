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
import discoverer.construction.network.LiftedNetwork;
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

    public List<Sample> samples;    //samples contain groundedTemplates (=Balls), they are too memory expensive

    //public NeuralDataset neuralNetworks;

    public GroundedDataset(String[] ex, String[] rules, String[] PretrainedRules) {
        super(ex, rules, PretrainedRules);

        //creates examples with corresponding ID mapping and chunk representations
        examples = createExamples(ex, Settings.maxExamples);

        samples = prepareGroundings(examples, network);
        //k-fold stratified example(same #positives in folds) splitting structure - treated as 1fold CV here
        es = new SampleSplitter(Settings.folds, samples);

    }

    public GroundedDataset(String[] train, String[] test, String[] rules, String[] Pretrainedrules) {
        super(train, test, rules, Pretrainedrules);

        List<Example> trainEx = createExamples(train, Settings.maxExamples);
        List<Example> testEx = createExamples(test, Settings.maxExamples);

        List<Sample> trainSamples = prepareGroundings(examples, network);
        List<Sample> testSamples = prepareGroundings(examples, network);

        examples = trainEx;
        examples.addAll(testEx);

        //k-fold stratified example(same #positives in folds) splitting structure - treated as 1fold CV here
        es = new SampleSplitter(trainSamples, testSamples);

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
    public final List<Sample> prepareGroundings(List<Example> examples, LiftedNetwork net) {
        //find max. and average substitution for all examples
        ForwardChecker.exnum = 0;
        List<Sample> sampleStore = new ArrayList<>(examples.size());

        if (Global.loadGroundings) {
            try {
                Glogger.process("loading ground network examples from a file: " + Settings.getDataset().replaceAll("-", "/") + ".ser");
                FileInputStream in = new FileInputStream(Settings.getDataset().replaceAll("-", "/") + ".ser");
                ObjectInputStream ois = new ObjectInputStream(in);
                sampleStore = (List<Sample>) (ois.readObject());
                return sampleStore;
            } catch (IOException | ClassNotFoundException e) {
                Glogger.err("Problem serializing: " + e);
            }
        }

        Glogger.process("searching for initial substition trees for each example...");
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
            b.loadGroundNeurons(neurons);
        }

        if (Global.saveGroundings) {
            try {
                FileOutputStream out = new FileOutputStream(Settings.getDataset().replaceAll("-", "/") + ".ser");
                ObjectOutputStream oos = new ObjectOutputStream(out);
                oos.writeObject(sampleStore);
                oos.flush();
                Glogger.process("Saved example ground network into: " + Settings.getDataset().replaceAll("-", "/") + ".ser");
            } catch (Exception e) {
                Glogger.err("Problem serializing: " + e);
            }
        }

        return sampleStore;
    }
}
