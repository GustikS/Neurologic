package discoverer.learning.learners;

import discoverer.grounding.evaluation.struct.GroundNetworkParser;
import discoverer.construction.example.Example;
import discoverer.construction.template.Kappa;
import discoverer.construction.template.MolecularTemplate;
import discoverer.construction.network.rules.KappaRule;
import discoverer.global.Global;
import discoverer.global.Glogger;
import discoverer.global.Settings;
import discoverer.grounding.ForwardChecker;
import discoverer.GroundedDataset;
import discoverer.grounding.evaluation.GroundedTemplate;
import discoverer.grounding.Grounder;
import discoverer.learning.backprop.BackpropDownAvg;
import discoverer.grounding.evaluation.Evaluator;
import discoverer.grounding.evaluation.struct.Dropout;
import discoverer.grounding.network.GroundKL;
import discoverer.learning.backprop.BackpropFast;
import discoverer.grounding.network.groundNetwork.GroundNetwork;
import discoverer.learning.Invalidator;
import discoverer.learning.Result;
import discoverer.learning.Results;
import discoverer.learning.Sample;
import discoverer.learning.Saver;
import discoverer.learning.Weights;
import discoverer.learning.backprop.BackpropDown;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

/**
 * Perform learning algorithm
 */
public class Learning {

    protected Results results = new Results();
    protected int progress = 0;
    Grounder grounder = new Grounder();

    /**
     * AVG variant with no grounding-epochae implements strategy for number of
     * learning steps based on a restart number
     *
     * @param restart
     * @return
     */
    public boolean continueRestart(int step, int restart) {

        if (Global.isCumulativeDiffRestarts()) {
            return !results.convergence();
        } else {
            int steps = 500 + restart * restart;
            Glogger.process("steps for restart " + restart + " = " + steps);
            if (step < steps) {
                return true;
            }
        }
        return false;
    }

    /**
     * number of grounding-epochae to perform for a given restart number
     * similarly to the strategy of learning steps for a restart
     *
     * @param restart
     * @return
     */
    protected boolean continueRestartEpochae(int epocha, int restart) {
        if (Global.isCumulativeDiffRestarts()) {
            return !results.convergence();
        } else {
            int epochae = 1 + restart * restart;
            Glogger.process("epochae for restart " + restart + " = " + epochae);
            if (epocha < epochae) {
                return true;
            }
        }
        return false;
    }

    /**
     * number of learning steps for a specific grounding-epocha running with a
     * specific restart can be a static number (10,100) or a strategy
     *
     * @param epocha
     * @param restart
     * @return
     */
    protected boolean continueEpocha(int step, int epocha, int restart) {

        //int steps = 10 + 10 * epocha * restart;
        int steps = 500;

        Glogger.process("epochae for restart " + restart + " = " + steps);
        if (step < steps) {
            return true;
        }

        return false;
    }

    public double learnDecay(int step, double learnRate) {
        return Global.getLearnDecayA() / (Global.getLearnDecayB() + step);
    }

    protected Results endTraining(List<Sample> roundStore, MolecularTemplate net) {
        //learning finished!
        Glogger.process("backpropagation on fold finished");
        //LOADING the final best model from training
        Saver.load();   //loading the sharedWeights for Kappa-Lambda program
        //-------------
        Glogger.process("Loading...Saver loaded the best network template");
        Glogger.process("Grounding the best template...");
        grounder.forwardChecker.exnum = 0;
        results.clearResultList();
        for (Sample roundElement : roundStore) {  //so we need to calculate the proof-tree output again
            Example e = roundElement.getExample();
            GroundedTemplate b = grounder.groundTemplate(net.last, e);   //so again create the proof-tree
            roundElement.setBall(b);
            if (Global.getGrounding() == Global.groundingSet.avg) {
                results.add(new Result(b.valAvg, e.getExpectedValue()));
                Glogger.info("example: " + e + " -> avgVal: " + b.valAvg);
            } else if (Global.getGrounding() == Global.groundingSet.max) {
                results.add(new Result(b.valMax, e.getExpectedValue()));
                Glogger.info("example: " + e + " -> maxVal: " + b.valMax);
            } else {
                throw new AssertionError();
            }
        }
        Glogger.LogTrain("final_train", new Double[]{results.getLearningError(), results.getDispersion(), results.getMajorityClass(), results.getThreshold()});
        Glogger.process("Saved training error as best of all restarts =\t" + results.getLearningError() + " (maj: " + results.getMajorityClass() + ")" + " (th: " + results.getThreshold() + ")" + " (disp: " + results.getDispersion() + ")");
        return results;
    }

    /**
     * Summing up actual sharedWeights with the new calculated gradient for
     * general Kappa nodes
     *
     * @param w
     */
    public void refreshWeights(Weights w) {
        for (Map.Entry<Object, Double> entryWeights : w.getWeights().entrySet()) {
            Object o = entryWeights.getKey();
            Double newWeight = entryWeights.getValue();
            if (o instanceof Kappa) {
                Kappa k = (Kappa) o;
                k.setOffset(k.getOffset() + newWeight);
            } else {
                KappaRule kr = (KappaRule) o;
                kr.setWeight(kr.getWeight() + newWeight);
            }
        }
    }

    protected Results saveTemplate(List<Sample> roundStore, MolecularTemplate net) {
        //need to evaluate results for the whole batch separatelly (after all example evaluations)
        evaluate(roundStore);

        double le = results.getLearningError();
        double th = results.getThreshold();
        double disp = results.getDispersion();

        if (Saver.isBetterThenBest(le, th, disp)) {
            Saver.save(net, le, th, disp);     //save the best network (last = output node)
            if (Global.exporting) {
                net.exportWeightMatrix("progress" + progress++);
            }
        }
        //Kappa llast = (Kappa) last;
        //Dotter.draw(last, new HashSet(llast.getRules()));
        return results;
    }

    protected void evaluate(List<Sample> roundStore) throws AssertionError {
        results.clearResultList();
        for (Sample result : roundStore) {
            Example e = result.getExample();
            GroundedTemplate b = result.getBall();
            if (null != Global.getGrounding()) {
                switch (Global.getGrounding()) {
                    case avg: {
                        double old = b.valAvg;
                        b.valAvg = Evaluator.evaluateAvg(b);  //forward propagation
                        //System.out.println(result.position + " : " + b.valAvg.toString());
                        //writeOutNeurons(result);

                        results.add(new Result(b.valAvg, e.getExpectedValue()));    //store the average value output in the result
                        Glogger.debug("Example: " + e + "Weight learned at the end of a minibatch: " + old + " -> " + b.valAvg);
                        break;
                    }
                    case max: {
                        double old = b.valMax;
                        b.valMax = Evaluator.evaluateMax(b);  //forward propagation
                        results.add(new Result(b.valMax, e.getExpectedValue()));    //store the average value output in the result
                        Glogger.debug("Example: " + e + "Weight learned at the end of a minibatch: " + old + " -> " + b.valMax);
                        break;
                    }
                    default:
                        throw new AssertionError();
                }
            }
        }

        Glogger.LogTrain("bp_step", new Double[]{results.getLearningError(), results.getDispersion(), results.getMajorityClass(), results.getThreshold()});
        Glogger.process("Training error = " + results.getLearningError() + " (maj: " + results.getMajorityClass() + ")" + " (disp: " + results.getDispersion() + ")");

    }

    protected void reGround(List<Sample> roundStore, MolecularTemplate net) {
        results.clearResultList();
        grounder.forwardChecker.exnum = 0;
        for (Sample roundElement : roundStore) {
            Example e = roundElement.getExample();
            GroundedTemplate b = grounder.groundTemplate(net.last, e);    // resubstitution for every example
            GroundNetworkParser.parseMAX(b);
            roundElement.setBall(b);
            results.add(new Result(b.valMax, e.getExpectedValue()));
            Glogger.info("example: " + e + " , bval: " + b.valMax + ", avg: " + b.valAvg);
            Glogger.debug("Substitution:\t" + e + "->\t" + b.valMax);
            //GroundDotter.draw(b);
        }
        Glogger.LogTrain("resub", new Double[]{results.getLearningError(), results.getDispersion(), results.getMajorityClass(), results.getThreshold()});
        Glogger.process("Training error after resubstition =\t" + results.getLearningError() + " (maj: " + results.getMajorityClass() + ")" + " (th: " + results.getThreshold() + ")" + " (disp: " + results.getDispersion() + ")");

        double le = results.getLearningError();
        double th = results.getThreshold();
        double disp = results.getDispersion();
        if (Saver.isBetterThenBest(le, th, disp)) {
            Saver.save(net, le, th, disp);     //save the best network (last = output node)
        }
        //KL llast =  last;
        //Dotter.draw(last, new HashSet(llast.getRules()));
    }

    private void writeOutNeurons(Sample result) {
        for (GroundKL gkl : result.getBall().groundNeurons) {
            System.out.println(gkl.toString() + " , " + gkl.getValueAvg());
        }

    }

}
