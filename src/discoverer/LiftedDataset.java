/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this pretrainedTemplate file, choose Tools | Templates
 * and open the pretrainedTemplate in the editor.
 */
package discoverer;

import discoverer.crossvalidation.SampleSplitter;
import discoverer.construction.ExampleFactory;
import discoverer.construction.NetworkFactory;
import discoverer.construction.example.Example;
import discoverer.construction.network.Kappa;
import discoverer.construction.network.LiftedNetwork;
import discoverer.construction.network.rules.KappaRule;
import discoverer.construction.network.rules.Rule;
import discoverer.drawing.Dotter;
import discoverer.global.Global;
import discoverer.global.Glogger;
import discoverer.global.Settings;
import discoverer.learning.Sample;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Gusta
 */
public class LiftedDataset implements Serializable {

    public LiftedNetwork network;

    String[] pretrained = null;
    public LiftedNetwork pretrainedNetwork = null;

    public SampleSplitter sampleSplitter;

    public LiftedDataset() {
    }

    //public HashMap<Object, Integer> weightMapping;  //Kappa offsets and KappaRule's weights to indicies in sharedWeights
    //public double[] sharedWeights; //the shared sharedWeights
    public LiftedDataset(String[] rules, String[] PretrainedRules) {
        pretrained = PretrainedRules;
        network = createNetworkMerge(rules);

        //createSharedWeights();
    }

    private LiftedNetwork createNetworkMerge(String[] rules) {
        if (pretrained != null && pretrained.length > 0) {
            pretrainedNetwork = createNetwork(pretrained, "pretrained"); // 1st
        }

        network = createNetwork(rules, "network"); // 2nd

        if (Global.getMerging() == Global.mergingOptions.weights) {
            network.merge(pretrainedNetwork); // 3rd
            Glogger.process("merged weights with template of : " + pretrainedNetwork);
        }
        if (Global.getMerging() == Global.mergingOptions.onTop) {
            network = network.mergeOnTop(pretrainedNetwork); // 3rd
            Glogger.process("merged structures on top (side by side) with template of : " + pretrainedNetwork);
        }

        if (Global.drawing) {
            Dotter.draw(network.last);
        }

        if (Global.exporting) {
            network.exportTemplate("merged");
            network.exportWeightMatrix("merged");
        }

        return network;
    }

    final LiftedNetwork createNetwork(String[] rules, String name) {
        //factory + subfactories initialization
        NetworkFactory nf = null;
        //constructs the whole L-K network from rules with support of grounded classes and element mappers, return LAST line rule's literal(=KL)!
        LiftedNetwork net = null;

        if (rules.length == 0) {
            Glogger.out("network template -" + name + "- is empty, may try to load manually if GUI is on...");
            if (Global.isLoadLiftedNetworkObject()) {
                net = LiftedNetwork.loadNetwork();
                return net;
            }
            return null;
        }

        nf = new NetworkFactory();
        net = nf.construct(rules);

        net.exportTemplate(name);
        net.exportWeightMatrix(name);
        return net;
    }

    /**
     * creates shuffled list of Examples from descriptions(conjunction of
     * literals)
     *
     * @param ex
     * @param maxExamples
     * @return
     */
    public final List<Example> createExamples(String[] ex, int maxExamples) {
        ExampleFactory eFactory = new ExampleFactory();
        List<Example> examples = new ArrayList<>();
        int positives = 0;
        int negatives = 0;
        for (int i = 0; i < ex.length; i++) {
            //main creation of an example
            Example e = eFactory.construct(ex[i]);
            examples.add(e);
            if (e.getExpectedValue() > 0) {
                positives++;
            } else {
                negatives++;
            }
        }
        Collections.shuffle(examples, Global.getRg());

        if (maxExamples > examples.size()) {
            return examples;
        }
        //stratified decreasing the number of examples
        positives = (int) Math.round(1.0 * positives / (examples.size()) * maxExamples);
        negatives = (int) Math.round(1.0 * negatives / (examples.size()) * maxExamples);
        List<Example> finExamples = new ArrayList<>();

        for (Example e : examples) {
            if (e.getExpectedValue() > 0) {
                positives--;
                if (positives >= 0) {
                    finExamples.add(e);
                }
            } else {
                negatives--;
                if (negatives >= 0) {
                    finExamples.add(e);
                }
            }
        }
        return finExamples;
    }

    public boolean saveDataset(String path) {
        try {
            Glogger.process("Saving grounded dataset...");
            FileOutputStream out = new FileOutputStream(path);
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(this);
            oos.flush();
            Glogger.process("Successfully Saved grounded dataset into: " + path);
            return true;
        } catch (Exception e) {
            Glogger.err("Problem serializing: " + e);
            return false;
        }
    }

    public static LiftedDataset loadDataset(String path) {
        LiftedDataset sampleStore = null;
        try {
            Glogger.process("loading grounded dataset from a file: " + path + ".ser");
            FileInputStream in = new FileInputStream(path);
            ObjectInputStream ois = new ObjectInputStream(in);
            sampleStore = (LiftedDataset) (ois.readObject());
            Glogger.process("grounded network examples succesfully loaded from : " + path + ".ser");
        } catch (IOException | ClassNotFoundException e) {
            Glogger.err("Problem loading dataset: " + e);
        }
        return sampleStore;
    }

}
