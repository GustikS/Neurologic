package discoverer;

import discoverer.grounding.evaluation.struct.ParentCounter;
import discoverer.global.Batch;
import discoverer.construction.example.Example;
import discoverer.global.Global;
import discoverer.construction.network.KL;
import discoverer.construction.network.Kappa;
import discoverer.construction.network.rules.KappaRule;
import discoverer.grounding.evaluation.Ball;
import discoverer.grounding.Grounder;
import discoverer.learning.backprop.BackpropGroundKappa;
import discoverer.learning.backprop.BackpropDownAvg;
import discoverer.grounding.evaluation.Evaluator;
import discoverer.grounding.evaluation.EvaluatorAvg;
import discoverer.learning.Invalidator;
import discoverer.learning.Result;
import discoverer.learning.Results;
import discoverer.learning.Sample;
import discoverer.learning.Saver;
import discoverer.learning.Weights;
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
        for (Example e : examples) {
            Ball b = Grounder.solve(last, e);
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
                k.setWeight(k.getWeight() + newWeight);
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
    public double solve(KL last, List<Example> examples, int learningSteps, int learningEpochs, int restartCount, double learnRate) {
        //find max. substitution for all examples
        List<Sample> roundStore = firstRun(examples, last);

        Results results = null;

        for (int a = 0; a < restartCount; a++) {    //restarting the whole procedure
            System.out.println("---------------------------------------------------------------------------------------------------------------------");
            System.out.println("------------Restart: " + a);
            for (int x = 0; x < learningEpochs; x++) {      //learningEpochs = maximal substitution cycles for all examples
                System.out.println("-------epocha: " + x);
                for (int i = 0; i < learningSteps; i++) {       //learningSteps = backpropagation steps
                    Results res = new Results();
                    System.out.println("------learning step: " + i);
                    for (Sample result : roundStore) {     //for each example(result)
                        Example e = result.getExample();
                        Ball b = result.getBall();
                        Weights w = BackpropGroundKappa.getNewWeights(b, e, Batch.NO, learnRate);  //backpropagation
                        refreshWeights(w);  //update
                        b.val = Evaluator.evaluate(b);  //forward propagation
                        res.add(new Result(b.val, e.getExpectedValue()));
                        if (Global.debugEnabled) {
                            System.out.println("Weight learning:\t" + e + "->\t" + b.val);
                        }
                    }   //learning errors on this fixed ground tree (found as max subst before learning)
                    System.out.println("Training error before max. subst. =\t" + res.getLearningError() + " (maj: " + res.getMajorityClass() + ")" + " (disp: " + res.getDispersion() + ")");
                }

                //max. subst. search again
                results = new Results();
                for (Sample roundElement : roundStore) {
                    Example e = roundElement.getExample();
                    Ball b = Grounder.solve(last, e);    // resubstitution for every example
                    roundElement.setBall(b);
                    results.add(new Result(b.val, e.getExpectedValue()));
                    if (Global.debugEnabled) {
                        System.out.println("Substitution:\t" + e + "->\t" + b.val);
                    }
                    //GroundDotter.draw(b);
                }

                System.out.println("Training error after resubstition =\t" + results.getLearningError() + " (maj: " + results.getMajorityClass() + ")" + " (th: " + results.getThreshold() + ")" + " (disp: " + results.getDispersion() + ")");
                double le = results.getLearningError();
                double th = results.getThreshold();
                double disp = results.getDispersion();

                if (Saver.isBetterThenBest(le, th, disp)) {
                    Saver.save(last, le, th, disp);     //save the best network (last = output node)
                }
                Kappa llast = (Kappa) last;
                //Dotter.draw(last, new HashSet(llast.getRules()));
            }
            Invalidator.invalidate(last);       //reset all weights before restart
        }

        //learning finished!
        Saver.load();
        System.out.println("Saver loaded the best one");
        results = new Results();
        for (Sample roundElement : roundStore) {
            Example e = roundElement.getExample();
            Ball b = Grounder.solve(last, e);
            roundElement.setBall(b);
            results.add(new Result(b.val, e.getExpectedValue()));
            System.out.println("Substitution:\t" + e + "->\t" + b.val);
        }
        System.out.println("Saved Learning error after resubstition =\t" + results.getLearningError() + " (maj: " + results.getMajorityClass() + ")" + " (th: " + results.getThreshold() + ")" + " (th: " + results.getDispersion() + ")");

        return results.getThreshold();
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
    public double solveAvg(KL last, List<Example> examples, int learningSteps, int learningEpochs, int restartCount, double learnRate) {
        //find max. and average substitution for all examples
        List<Sample> roundStore = firstRun(examples, last);   //this stays as with no pruning both avg and max are found

        //here calculate for each avg-proof-tree(=Ball b) numbers of parents for each GroundKappa/Lambda
        for (Sample result : roundStore) {
            Ball b = result.getBall();
            ParentCounter.countParents(b);
        }

        for (int a = 0; a < restartCount; a++) {    //restarting the whole procedure
            System.out.println("-------------------------------------------------------------------------------------------------------------------------------");
            System.out.println("------------Restart: " + a);
            for (int i = 0; i < learningSteps; i++) {       //learningSteps = backpropagation steps
                Results res = new Results();
                System.out.println("---learning step: " + i);
                for (Sample result : roundStore) {     //for each example(result)
                    Example e = result.getExample();
                    Ball b = result.getBall();
                    Weights w = BackpropDownAvg.getNewWeights(b, e, Batch.NO, learnRate);  //backpropagation
                    refreshWeights(w);  //update
                    b.valAvg = EvaluatorAvg.evaluate(b);  //forward propagation
                    res.add(new Result(b.valAvg, e.getExpectedValue()));    //store the average value output in the result
                    if (Global.debugEnabled) {
                        System.out.println("Weight learning:\t" + e + "->\t" + b.valAvg);
                    }
                }

                double le = res.getLearningError();
                double th = res.getThreshold();
                double disp = res.getDispersion();

                System.out.println("Learning error =\t" + le + " (maj: " + res.getMajorityClass() + ")" + " (disp: " + res.getDispersion() + ")");

                if (Saver.isBetterThenBest(le, th, disp)) {
                    Saver.save(last, le, th, disp);     //save the best network (last = output node)
                }
                Kappa llast = (Kappa) last;
                //Dotter.draw(last, new HashSet(llast.getRules()));
            }
            Invalidator.invalidate(last);       //reset all weights before restart
        }

        //learning finished!
        Saver.load();   //loading the weights for Kappa-Lambda program
        System.out.println("Saver loaded the best one");
        Results results = new Results();    //we didn't store the whole ground networks(one for each example), just the weights of program
        for (Sample roundElement : roundStore) {  //so we need to calculate the avg-proof-tree output again
            Example e = roundElement.getExample();
            Ball b = Grounder.solve(last, e);   //so again create the avg-proof-tree
            roundElement.setBall(b);
            results.add(new Result(b.valAvg, e.getExpectedValue()));
            System.out.println("proof-tree-avg output:\t" + e + "->\t" + b.valAvg);
        }
        System.out.println("Saved Learning final error =\t" + results.getLearningError() + " (maj: " + results.getMajorityClass() + ")" + " (th: " + results.getThreshold() + ")" + " (disp: " + results.getDispersion() + ")");

        return results.getThreshold();
    }
}
