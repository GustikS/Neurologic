/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer;

import discoverer.crossvalidation.SampleSplitter;
import discoverer.construction.example.Example;
import discoverer.construction.template.LiftedTemplate;
import discoverer.construction.template.MolecularTemplate;
import discoverer.global.Global;
import discoverer.global.Glogger;
import discoverer.global.Settings;
import discoverer.grounding.ForwardChecker;
import discoverer.grounding.Grounder;
import discoverer.grounding.evaluation.GroundedTemplate;
import discoverer.grounding.evaluation.struct.GroundNetworkParser;
import discoverer.grounding.network.GroundKL;
import discoverer.learning.Sample;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * An abstraction of a grounded dataset, i.e. set of ground proof-tree networks
 * this class fields are not static but are accessed from various other classes,
 * i.e. mainly the shared weights thus a reference to GroundedDataset object
 * must be somewhere stored statically (Global.neuralDataset)
 */
public class GroundedDataset extends LiftedDataset {

    public List<Example> examples;  //raw example structures
    public List<Sample> samples;    //samples contain groundedTemplates (=Balls), they are too memory expensive

    public GroundedDataset(String[] ex, String[] rules, String[] PretrainedRules) {
        super(rules, PretrainedRules);

        Glogger.process("created lifted network structure");
        //creates examples with corresponding ID mapping and chunk representations
        examples = createExamples(ex, Settings.maxExamples);
        Glogger.process("created example structures");

        samples = prepareGroundings(examples, network);
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

        List<Sample> trainSamples = prepareGroundings(trainEx, (MolecularTemplate) network);
        List<Sample> testSamples = prepareGroundings(testEx, (MolecularTemplate) network);
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
     * @param template
     * @param net
     * @param last output node
     * @return list with balls from first run
     */
    public final List<Sample> prepareGroundings(List<Example> examples, LiftedTemplate template) {
        //find max. and average substitution for all examples
        List<Sample> sampleStore = new ArrayList<>(examples.size());

        Glogger.process("searching for initial substition prove-trees for each example...");

        //Global.savesomething(template, "cc");
        if (Global.parallelGrounding) {
            Glogger.process("Parallel threads workfold splitting");
            List<List<Example>> workFolds = (List<List<Example>>) SampleSplitter.splitExampleList(examples, Global.numOfThreads);
            ExecutorService exec = Executors.newFixedThreadPool(Global.numOfThreads);
            int[] omg = new int[1]; //global example counter
            omg[0] = 0;
            Glogger.process("Parallel threads created");
            try {
                for (int j = 0; j < Global.numOfThreads; j++) {
                    List<Example> wex = workFolds.get(j);
                    int thrd = j;
                    exec.submit(new Runnable() {
                        @Override
                        public void run() {
                            Grounder grounder = new Grounder();
                            grounder.forwardChecker.exnum = 0;
                            //unfortunatelly the threads cannot be run completely parallel on the same template, it's way too complitated, we need separate templates
                            LiftedTemplate net = (LiftedTemplate) Global.makeDeepCopy(template);
                            //LiftedTemplate net = (LiftedTemplate) Global.loadSomething("cc");
                            for (Example e : wex) {
                                GroundedTemplate b = grounder.groundTemplate(net.last, e);
                                Glogger.info("example #" + omg[0]++ + " on thread " + thrd + "-> #forward checker runs:(" + grounder.forwardChecker.runs + ")" + " : target: " + e + " , maxVal: " + b.valMax + ", avgVal: " + b.valAvg);
                                sampleStore.add(new Sample(e, b));
                            }
                        }
                    });
                }
            } finally {
                exec.shutdown();
                try {
                    exec.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Glogger.err("Threads problem while grounding...");
                }
            }
        } else {
            Grounder grounder = new Grounder();
            grounder.forwardChecker.exnum = 0;
            for (Example e : examples) {
                GroundedTemplate b = grounder.groundTemplate(template.last, e);
                Glogger.info("example #" + grounder.forwardChecker.exnum++ + " -> #forward checker runs:(" + grounder.forwardChecker.runs + ")" + " : target: " + e + " , maxVal: " + b.valMax + ", avgVal: " + b.valAvg);
                sampleStore.add(new Sample(e, b));
            }
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
