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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Gusta
 */
public class LiftedDataset {

    public LiftedNetwork network;

    String[] pretrained = null;
    public static LiftedNetwork pretrainedNetwork = null;

    public List<Example> examples;
    public SampleSplitter es;

    //public HashMap<Object, Integer> weightMapping;  //Kappa offsets and KappaRule's weights to indicies in sharedWeights
    //public double[] sharedWeights; //the shared sharedWeights
    public LiftedDataset(String[] train, String[] test, String[] rules, String[] PretrainedRules) {
        pretrained = PretrainedRules;
        network = createNetworkMerge(rules);

        //createSharedWeights();
    }

    public LiftedDataset(String[] ex, String[] rules, String[] PretrainedRules) {
        pretrained = PretrainedRules;
        network = createNetworkMerge(rules);

        //createSharedWeights();
    }

    private LiftedNetwork createNetworkMerge(String[] rules) {
        if (pretrained != null) {
            pretrainedNetwork = createNetwork(pretrained, "pretrained"); // 1st
        }

        network = createNetwork(rules, "network"); // 2nd

        if (Global.getMerging() == Global.mergingOptions.weights) {
            network.merge(pretrainedNetwork); // 3rd
            Glogger.process("merged weights with template of : " + pretrainedNetwork);
        }
        if (Global.getMerging() == Global.mergingOptions.onTop) {
            network = network.mergeOnTop(pretrainedNetwork); // 3rd
            Glogger.process("merged structure on top with template of : " + pretrainedNetwork);
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
            if (Global.isManualLoadNetwork()) {
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
        List<Example> finExamples = new ArrayList<Example>();

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

}
