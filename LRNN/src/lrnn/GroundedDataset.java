/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lrnn;

import lrnn.construction.ConstantFactory;
import lrnn.construction.example.Example;
import lrnn.construction.template.LiftedTemplate;
import lrnn.construction.template.MolecularTemplate;
import lrnn.crossvalidation.SampleSplitter;
import lrnn.drawing.GroundDotter;
import lrnn.global.Global;
import lrnn.global.Glogger;
import lrnn.global.Settings;
import lrnn.grounding.BottomUpConnector;
import lrnn.grounding.Grounder;
import lrnn.grounding.evaluation.Evaluator;
import lrnn.grounding.evaluation.GroundedTemplate;
import lrnn.grounding.evaluation.struct.GroundNetworkParser;
import lrnn.grounding.network.GroundKL;
import lrnn.learning.Sample;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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

    /**
     * Used e.g. for regrounding existing examples with a new template
     *
     * @param inExamples
     * @param newTemplate
     */
    public GroundedDataset(List<Example> inExamples, String[] newTemplate) {
        super(newTemplate, null);

        Glogger.process("created new lifted template structure");
        examples = inExamples;
        Glogger.process("copied example structures");

        samples = prepareGroundings(examples, template);
        Glogger.process("prepared network groundings");

        sampleSplitter = new SampleSplitter(Settings.folds, samples);
    }

    public GroundedDataset(String[] ex, String[] rules, String[] PretrainedRules) {
        super(rules, PretrainedRules);

        Glogger.process("created lifted network structure");
        //creates examples with corresponding ID mapping and chunk representations
        examples = createExamples(ex, Settings.maxExamples);
        Glogger.process("created example structures");

        samples = prepareGroundings(examples, template);
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

        List<Sample> trainSamples = prepareGroundings(trainEx, (MolecularTemplate) template);
        List<Sample> testSamples = prepareGroundings(testEx, (MolecularTemplate) template);
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
     * @return list with balls from first run
     */
    public final List<Sample> prepareGroundings(List<Example> examples, LiftedTemplate template) {
        //find max. and average substitution for all examples
        List<Sample> sampleStore = new ArrayList<>(examples.size());

        Glogger.process("searching for initial substition proof-trees for each example...");

        //Global.savesomething(template, "cc");
        if (Global.bottomUp) {

            BottomUpConnector prover = new BottomUpConnector();
            prover.recalculateHerbrand = true;

            int i = 0;
            double avg_herbrand_size = 0;
            for (Example e : examples) {
                ConstantFactory.clearConstantFactory();
                ConstantFactory.construct("a");

                e.setWeightedFacts(null, nf.constructFacts(e.toString() + " " + e.hash));


                e.storedFacts = new LinkedHashMap<>();
                GroundedTemplate b = new GroundedTemplate();
                Glogger.info("# example " + i++ + " : " + e.storedFacts.size());
                b.setLast(prover.getGroundLRNN(new ArrayList(template.rules), e, "finalKappa(a)"));

                b.constantNames = ConstantFactory.getConstantNames();

                Glogger.info("cacheSize #" + prover.getBtmUpCache().size());
                avg_herbrand_size += prover.getBtmUpCache().size();
                sampleStore.add(new Sample(e, b));
                if (Global.getGrounding().equals(Global.groundingSet.avg)) {
                    Evaluator.evaluateAvg(b);
                } else {
                    Evaluator.evaluateMax(b);
                }

                if (Global.drawing) {
                    GroundDotter.draw(b, "b" + i);
                }
            }
            Glogger.info("=====avg_herbrand_size: " + avg_herbrand_size/i);
        } else if (Global.parallelGrounding) {
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
                                grounder.forwardChecker.printRuns();
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
                if (Global.drawing) {
                    GroundDotter.draw(b, "a" + grounder.forwardChecker.exnum);
                }
            }
        }

        Glogger.process("...done with intial grounding of examples");

        //here calculate for each proof-tree(=GroundedTemplate b = ground network) numbers of parents for each GroundKappa/Lambda
        Set<GroundKL> neurons = null;
        for (Sample result : sampleStore) {
            GroundedTemplate b = result.getBall();
            if (Global.getGrounding() == Global.groundingSet.avg || Global.bottomUp) {
                neurons = GroundNetworkParser.parseAVG(b);
            } else if (Global.getGrounding() == Global.groundingSet.max) {
                neurons = GroundNetworkParser.parseMAX(b);
            }
            b.loadGroundNeurons(neurons);   //store all ground L/K in an array for fast and simple operations instead of DFS for each simple pass
            if (Global.learnableElements) {
                b.groundLiterals.addAll(GroundNetworkParser.elements);  //also include fact neurons in the evaluation (as in neural)
            }
        }
        return sampleStore;
    }
}
