/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer.learning.learners;

import discoverer.construction.example.Example;
import discoverer.construction.template.MolecularTemplate;
import discoverer.global.Global;
import discoverer.global.Glogger;
import discoverer.global.Settings;
import discoverer.grounding.evaluation.GroundedTemplate;
import discoverer.grounding.evaluation.Evaluator;
import discoverer.grounding.evaluation.struct.Dropout;
import discoverer.learning.Invalidator;
import discoverer.learning.Results;
import discoverer.learning.Sample;
import discoverer.learning.Weights;
import discoverer.learning.backprop.BackpropDown;
import discoverer.learning.backprop.BackpropDownAvg;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Gusta
 */
public class LearnerStandard extends Learning {

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
     * upgrade each example-generated network's sharedWeights
     *
     * @param last
     * @param examples
     * @param learningSteps
     * @param learningEpochs
     * @param restartCount
     * @param learnRate
     * @return
     */
    public Results solveMax(MolecularTemplate last, List<Sample> roundStore) {

        Glogger.process("-------------solveMax----------------");
        for (int a = 0; a < Settings.restartCount; a++) {    //restarting the whole procedure
            if (Global.isInitWithAVG()) {
                Glogger.process("----initializing weights with AVG variant----");
                solveAvg(last, roundStore);
                results.clearResultList();
            }
            Glogger.process("---------------------------------------------------------------------------------------------------------------------");
            Glogger.process("------------Restart: " + a);
            for (int x = 0; x < Settings.learningEpochs; x++) {      //learningEpochs = maximal substitution cycles for all examples
                Glogger.process("-------epochae: " + x);
                results.trainingHistory.clear();
                for (int i = 0; i < Settings.learningSteps; i++) {       //learningSteps = backpropagation steps
                    if (Global.isLearnDecay()) {
                        Settings.learnRate = learnDecay(i + (x * Settings.learningSteps), Settings.learnRate);
                    }
                    Glogger.process("------learning step: " + i);
                    if (Global.isSGD()) {
                        Collections.shuffle(roundStore, Global.getRg());    //stochastic gradient descend!
                    }
                    for (Sample result : roundStore) {     //for each example(result)
                        Example e = result.getExample();
                        GroundedTemplate b = result.getBall();
                        double old = b.valMax;
                        if (Global.getDropout() > 0) {
                            Dropout.dropoutMax(b);
                            Evaluator.ignoreDropout = false;
                            b.valMax = Evaluator.evaluateMax(b);
                            Evaluator.ignoreDropout = true;
                        } else {
                            b.valMax = Evaluator.evaluateMax(b);  //forward propagation
                        }
                        Glogger.debug("Example: " + e + "Example's weight change from last minibatch (after 1-bp over all other examples) " + old + " -> " + b.valAvg);
                        Weights w = BackpropDown.getNewWeights(b, e);  //backpropagation
                        refreshWeights(w);  //update sharedWeights
                    }   //learning errors on this fixed ground tree (found as max subst before learning)
                    Glogger.process("preliminary train error without regrounding...:");
                    evaluate(roundStore);
                }
                reGround(roundStore, last);
            }
            saveTemplate(roundStore, last);
            Glogger.LogTrain("...restart " + a);
            Invalidator.invalidate(last);       //reset all sharedWeights before restart
        }

        endTraining(roundStore, last);

        return results;
    }

    /**
     * AVG version is principally different - new procedure
     *
     * @param last
     * @param roundStore
     * @param examples
     * @param learningSteps
     * @param learningEpochs
     * @param restartCount
     * @param learnRate
     * @return
     */
    public Results solveAvg(MolecularTemplate last, List<Sample> roundStore) {

        //evaluate(roundStore);
        long time0 = System.currentTimeMillis();
        for (int a = 0; a < Settings.restartCount; a++) {    //restarting the whole procedure
            results.trainingHistory.clear();
            Glogger.process("--------SolveAVG-----------------------------------------------------------------------------------------------------------------------");
            Glogger.process("------------Restart: " + a);
            for (int i = 0; i < Settings.learningSteps; i++) {       //learningSteps = backpropagation steps
                if (Global.isLearnDecay()) {
                    Settings.learnRate = learnDecay(i, Settings.learnRate);
                }
                Glogger.process("---learning step: " + i);
                if (Global.isSGD()) {
                    Collections.shuffle(roundStore, Global.getRg());    //stochastic gradient descend!
                }
                double old;
                for (Sample sample : roundStore) {     //for each example(result)
                    Example e = sample.getExample();
                    GroundedTemplate b = sample.getBall();
                    old = b.valAvg;
                    if (Global.getDropout() > 0) {
                        Dropout.dropoutAvg(b);
                        Evaluator.ignoreDropout = false;
                        b.valAvg = Evaluator.evaluateAvg(b);
                        Evaluator.ignoreDropout = true;
                    } else {
                        b.valAvg = Evaluator.evaluateAvg(b);  //forward propagation
                        //System.out.println(b.valAvg);
                    }
                    Glogger.debug("Example: " + e + "Weight change from last minibatch (after 1-bp over all other examples) " + old + " -> " + b.valAvg);
                    Weights w = BackpropDownAvg.getNewWeights(b, e);  //backpropagation
                    refreshWeights(w);  //update sharedWeights
                }
                if (Global.isSave()) {
                    saveTemplate(roundStore, last);
                }
            }
            saveTemplate(roundStore, last);
            Glogger.LogTrain("...restart " + a);
            last.invalidateWeights();
            //Invalidator.invalidate(last);       //reset all sharedWeights before restart
        }
        Glogger.info("finished training");
        Glogger.info("learning time : " + (System.currentTimeMillis() - time0));
        endTraining(roundStore, last);

        return results;
    }
}
