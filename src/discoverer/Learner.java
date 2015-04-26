package discoverer;

import discoverer.grounding.evaluation.struct.ParentCounter;
import discoverer.global.Batch;
import discoverer.construction.example.Example;
import discoverer.construction.network.KL;
import discoverer.construction.network.Kappa;
import discoverer.construction.network.rules.KappaRule;
import discoverer.drawing.GroundDotter;
import discoverer.global.Glogger;
import discoverer.grounding.ForwardChecker;
import discoverer.grounding.evaluation.Ball;
import discoverer.grounding.Grounder;
import extras.BackpropGroundKappa;
import discoverer.learning.backprop.BackpropDownAvg;
import discoverer.grounding.evaluation.Evaluator;
import discoverer.grounding.evaluation.struct.Dropout;
import discoverer.learning.Invalidator;
import discoverer.learning.Result;
import discoverer.learning.Results;
import discoverer.learning.Sample;
import discoverer.learning.Saver;
import discoverer.learning.Weights;
import discoverer.learning.backprop.BackpropDown;
import java.util.*;

/**
 * Perform learning algorithm
 */
public class Learner {

    /**
     * Method for handling the first learning run<p>
     * for each example finds it's maximal and average substitution's Ball
     * result returned as couples in RoundElement
     *
     * @param examples examples
     * @param last output node
     * @return list with balls from first run
     */
    public List<Sample> firstRun(List<Example> examples, KL last) {
        List<Sample> roundStore = new ArrayList<Sample>();
        ForwardChecker.exnum = 0;
        int i = 0;
        for (Example e : examples) {
            Ball b = Grounder.solve(last, e);
            //GroundDotter.draw(b, "sigSig" + i++);
            Glogger.info("example: " + e + " , maxVal: " + b.valMax + ", avgVal: " + b.valAvg);
            roundStore.add(new Sample(e, b));
        }
        
        return roundStore;
    }

    /**
     * Summing up actual weights with the new calculated gradient for general
     * Kappa nodes
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

    /**
     * main iterative learning procedure:
     * <p>
     * <p>
     * for a number of restarts
     * <p>
     * for a number of learning epochs
     * <p>
     * for a number of learning steps
     * <p>
     * upgrade each example-generated network's weights
     *
     * @param last
     * @param examples
     * @param learningSteps
     * @param learningEpochs
     * @param restartCount
     * @param learnRate
     * @return
     */
    public Results solve(KL last, List<Example> examples, int learningSteps, int learningEpochs, int restartCount, double learnRate) {
        //find max. substitution for all examples
        ForwardChecker.exnum = 0;
        Glogger.process("searching for initial max substition for each example...");
        List<Sample> roundStore = firstRun(examples, last);
        Glogger.process("...done with intial grounding");
        
        //here calculate for each avg-proof-tree(=Ball b) numbers of parents for each GroundKappa/Lambda
        for (Sample result : roundStore) {
            Ball b = result.getBall();
            ParentCounter.countParents(b);
        }
        
        Results results;
        
        for (int a = 0; a < restartCount; a++) {    //restarting the whole procedure
            Glogger.process("---------------------------------------------------------------------------------------------------------------------");
            Glogger.process("------------Restart: " + a);
            for (int x = 0; x < learningEpochs; x++) {      //learningEpochs = maximal substitution cycles for all examples
                Glogger.process("-------epochae: " + x);
                for (int i = 0; i < learningSteps; i++) {       //learningSteps = backpropagation steps
                    Results res = new Results();
                    Glogger.process("------learning step: " + i);
                    for (Sample result : roundStore) {     //for each example(result)
                        Example e = result.getExample();
                        Ball b = result.getBall();
                        Dropout.dropout(b);
                        Weights w = BackpropDown.getNewWeights(b, e, Batch.NO, learnRate);  //backpropagation
                        //Weights w = BackpropGroundKappa.getNewWeights(b, e, Batch.NO, learnRate);  //backpropagation
                        //Glogger.info(w.toString());
                        //Weights compare = w.compareTo(w2);
                        //Glogger.info(compare.toString());
                        refreshWeights(w);  //update
                        double old = b.valMax;
                        b.valMax = Evaluator.evaluate(b);  //forward propagation
                        res.add(new Result(b.valMax, e.getExpectedValue()));
                        Glogger.debug("Example: " + e + "\t : Weight learning: " + old + " -> " + b.valMax);
                    }   //learning errors on this fixed ground tree (found as max subst before learning)
                    Glogger.LogTrain("bp_step", new Double[]{res.getLearningError(), res.getDispersion(), res.getMajorityClass(), res.getThreshold()});
                    Glogger.process("Training error before max. subst. =\t" + res.getLearningError() + " (maj: " + res.getMajorityClass() + ")" + " (disp: " + res.getDispersion() + ")");
                }

                //max. subst. search again
                results = new Results();
                ForwardChecker.exnum = 0;
                for (Sample roundElement : roundStore) {
                    Example e = roundElement.getExample();
                    Ball b = Grounder.solve(last, e);    // resubstitution for every example
                    ParentCounter.countParents(b);
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
                    Saver.save(last, le, th, disp);     //save the best network (last = output node)
                }
                //KL llast =  last;
                //Dotter.draw(last, new HashSet(llast.getRules()));
            }
            Glogger.LogTrain("...restart " + a);
            Invalidator.invalidate(last);       //reset all weights before restart
        }

        //learning finished!
        Saver.load();
        Glogger.process("...Saver loaded the best network template");
        Glogger.process("Grounding the best template...");
        results = new Results();
        ForwardChecker.exnum = 0;
        for (Sample roundElement : roundStore) {
            Example e = roundElement.getExample();
            Ball b = Grounder.solve(last, e);
            roundElement.setBall(b);
            results.add(new Result(b.valMax, e.getExpectedValue()));
            Glogger.info("Substitution:\t" + e + "->\t" + b.valMax);
        }
        Glogger.LogTrain("final_train", new Double[]{results.getLearningError(), results.getDispersion(), results.getMajorityClass(), results.getThreshold()});
        Glogger.process("Saved Learning error after resubstition and all restarts =\t" + results.getLearningError() + " (maj: " + results.getMajorityClass() + ")" + " (th: " + results.getThreshold() + ")" + " (th: " + results.getDispersion() + ")");
        
        return results;
    }

    /**
     * AVG version is principally different - new procedure
     *
     * @param last
     * @param examples
     * @param learningSteps
     * @param learningEpochs
     * @param restartCount
     * @param learnRate
     * @return
     */
    public Results solveAvg(KL last, List<Example> examples, int learningSteps, int learningEpochs, int restartCount, double learnRate) {
        //find max. and average substitution for all examples
        ForwardChecker.exnum = 0;
        Glogger.process("searching for initial avg substition for each example...");
        List<Sample> roundStore = firstRun(examples, last);   //this stays as with no pruning both avg and max are found
        Glogger.process("...done with intial grounding");

        //here calculate for each avg-proof-tree(=Ball b) numbers of parents for each GroundKappa/Lambda
        for (Sample result : roundStore) {
            Ball b = result.getBall();
            ParentCounter.countParentsAVG(b);
        }
        
        for (int a = 0; a < restartCount; a++) {    //restarting the whole procedure
            Glogger.process("-------------------------------------------------------------------------------------------------------------------------------");
            Glogger.process("------------Restart: " + a);
            for (int i = 0; i < learningSteps; i++) {       //learningSteps = backpropagation steps
                Results res = new Results();
                Glogger.process("---learning step: " + i);
                for (Sample result : roundStore) {     //for each example(result)
                    Example e = result.getExample();
                    Ball b = result.getBall();
                    Dropout.dropoutAvg(b);
                    Weights w = BackpropDownAvg.getNewWeights(b, e, Batch.NO, learnRate);  //backpropagation
                    refreshWeights(w);  //update
                    double old = b.valAvg;
                    b.valAvg = Evaluator.evaluateAvg(b);  //forward propagation
                    res.add(new Result(b.valAvg, e.getExpectedValue()));    //store the average value output in the result
                    Glogger.debug("Example: " + e + "Weight learning: " + old + " -> " + b.valAvg);
                }
                
                double le = res.getLearningError();
                double th = res.getThreshold();
                double disp = res.getDispersion();
                
                Glogger.LogTrain("bp_step", new Double[]{res.getLearningError(), res.getDispersion(), res.getMajorityClass(), res.getThreshold()});
                Glogger.process("Training error = " + res.getLearningError() + " (maj: " + res.getMajorityClass() + ")" + " (disp: " + res.getDispersion() + ")");
                
                if (Saver.isBetterThenBest(le, th, disp)) {
                    Saver.save(last, le, th, disp);     //save the best network (last = output node)
                }
                //Kappa llast = (Kappa) last;
                //Dotter.draw(last, new HashSet(llast.getRules()));
            }
            Glogger.LogTrain("...restart " + a);
            Invalidator.invalidate(last);       //reset all weights before restart
        }

        //learning finished!
        Saver.load();   //loading the weights for Kappa-Lambda program
        Glogger.process("...Saver loaded the best network template");
        Glogger.process("Grounding the best template...");
        Results results = new Results();    //we didn't store the whole ground networks(one for each example), just the weights of program
        ForwardChecker.exnum = 0;
        for (Sample roundElement : roundStore) {  //so we need to calculate the avg-proof-tree output again
            Example e = roundElement.getExample();
            Ball b = Grounder.solve(last, e);   //so again create the avg-proof-tree
            roundElement.setBall(b);
            results.add(new Result(b.valAvg, e.getExpectedValue()));
            Glogger.info("example: " + e + " -> avgVal: " + b.valAvg);
        }
        Glogger.LogTrain("final_train", new Double[]{results.getLearningError(), results.getDispersion(), results.getMajorityClass(), results.getThreshold()});
        Glogger.process("Saved Learning error as best of all restarts =\t" + results.getLearningError() + " (maj: " + results.getMajorityClass() + ")" + " (th: " + results.getThreshold() + ")" + " (th: " + results.getDispersion() + ")");
        
        return results;
    }
}
