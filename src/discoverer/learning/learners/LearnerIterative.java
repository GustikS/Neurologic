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
public class LearnerIterative extends LearnerStandard {

    /**
     * iterative AVG version is principally different - new procedure
     *
     * @param last
     * @param examples
     * @param learningSteps
     * @param learningEpochs
     * @param restartCount
     * @param learnRate
     * @return
     */
    public Results solveAvgIterative(MolecularTemplate last, List<Sample> roundStore) {

        int restart = 0;
        int step = 0;
        boolean learn = true;
        while (learn) {    //restarting the whole procedure
            Glogger.process("-------------------------------------------------------------------------------------------------------------------------------");
            Glogger.process("------------Restart: " + restart);
            int i = 0;
            results.trainingHistory.clear();
            while (continueRestart(i++, restart)) {       //learningSteps = backpropagation steps
                if (step++ >= Global.getCumMaxSteps()) {
                    learn = false;
                    break;
                }
                if (Global.isLearnDecay()) {
                    Settings.learnRate = learnDecay(i, Settings.learnRate);
                }
                Glogger.process("---learning step: " + i);
                if (Global.isSGD()) {
                    Collections.shuffle(roundStore, Global.getRg());    //stochastic gradient descend!
                }
                for (Sample result : roundStore) {     //for each example(result)
                    Example e = result.getExample();
                    GroundedTemplate b = result.getBall();
                    double old = b.valAvg;
                    if (Global.getDropout() > 0) {
                        Dropout.dropoutAvg(b);
                        Evaluator.ignoreDropout = false;
                        b.valAvg = Evaluator.evaluateAvg(b);
                        Evaluator.ignoreDropout = true;
                    } else {
                        b.valAvg = Evaluator.evaluateAvg(b);  //forward propagation
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
            Glogger.LogTrain("...end of restart " + restart);
            Invalidator.invalidate(last);       //reset all sharedWeights before restart
            restart++;
        }

        endTraining(roundStore, last);

        return results;
    }

    /**
     * iterative max version
     *
     * @param last
     * @param examples
     * @param learningSteps
     * @param learningEpochs
     * @param restartCount
     * @param learnRate
     * @return
     */
    public Results solveMaxIterative(MolecularTemplate last, List<Sample> roundStore) {
        
        int restart = 0;
        int step = 0;
        boolean learn = true;
        while (learn) {    //restarting the whole procedure
            Glogger.process("-------------------------------------------------------------------------------------------------------------------------------");
            Glogger.LogTrain("----Restart: " + restart);
            int epochae = 0;

            if (Global.isInitWithAVG()) {
                Glogger.process("----initializing weights with AVG variant----");
                solveAvg(last, roundStore);
                results.clearResultList();
            }

            results.trainingHistory.clear();
            while (learn & continueRestartEpochae(epochae++, restart)) {       //learningSteps = backpropagation steps

                Glogger.LogTrain("---epochae: " + epochae);
                int i = 0;
                while (continueEpocha(i, epochae, restart)) {       //learningSteps = backpropagation steps
                    Glogger.process("------learning step: " + i++);
                    if (step++ >= Global.getCumMaxSteps()) {
                        learn = false;
                        break;
                    }
                    if (Global.isLearnDecay()) {
                        Settings.learnRate = learnDecay(epochae * i, Settings.learnRate);
                    }
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
                    }   //learning errors on this fixed ground tree (found as max subst before learning)}
                }
                reGround(roundStore, last);
            }
            saveTemplate(roundStore, last);
            Glogger.LogTrain("...restart " + restart);
            Invalidator.invalidate(last);       //reset all sharedWeights before restart
            restart++;
        }

        Glogger.LogTrain(Global.getCumMaxSteps() + " cumulative learning steps depleted");

        results = endTraining(roundStore, last);

        return results;
    }
}
