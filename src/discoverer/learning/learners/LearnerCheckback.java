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
import discoverer.learning.Invalidator;
import discoverer.learning.Result;
import discoverer.learning.Results;
import discoverer.learning.Sample;
import discoverer.learning.Weights;
import discoverer.learning.backprop.BackpropDown;
import discoverer.learning.backprop.BackpropDownAvg;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Gusta OLD STUFF (for backward compatibility testing)
 */
public class LearnerCheckback extends Learning {

    //----------------------OLD STUFF (for backward copatibility testing)-----------------------------
    public Results checkback(MolecularTemplate last, List<Sample> roundStore) {

        Glogger.process("-------------checkBack----------------");
        for (int a = 0; a < Settings.restartCount; a++) {    //restarting the whole procedure
            Glogger.process("---------------------------------------------------------------------------------------------------------------------");
            Glogger.process("------------Restart: " + a);
            for (int x = 0; x < Settings.learningEpochs; x++) {      //learningEpochs = maximal substitution cycles for all examples
                Glogger.process("-------epochae: " + x);
                for (int i = 0; i < Settings.learningSteps; i++) {       //learningSteps = backpropagation steps
                    results.clearResultList();
                    Glogger.process("------learning step: " + i);
                    for (Sample result : roundStore) {     //for each example(result)
                        Example e = result.getExample();
                        GroundedTemplate b = result.getBall();
                        double old = b.valMax;
                        //b.valMax = Evaluator.evaluateMax(b);  //forward propagation
                        //double old = b.valMax;
                        Weights w = BackpropDown.getNewWeights(b, e);  //backpropagation
                        refreshWeights(w);  //update
                        b.valMax = Evaluator.evaluateMax(b);  //forward propagation
                        results.add(new Result(b.valMax, e.getExpectedValue()));
                        Glogger.debug("Example: " + e + "\t : Weight learning: " + old + " -> " + b.valMax);
                    }   //learning errors on this fixed ground tree (found as max subst before learning)
                    Glogger.LogTrain("bp_step", new Double[]{results.getLearningError(), results.getDispersion(), results.getMajorityClass(), results.getThreshold()});
                    Glogger.process("Training error before max. subst. =\t" + results.getLearningError() + " (maj: " + results.getMajorityClass() + ")" + " (disp: " + results.getDispersion() + ")");

                    Glogger.process("-----------proper evaluation at the end of bp-step (minibatch)");
                    saveTemplate(roundStore, last);
                    Glogger.process("---<");
                }

                reGround(roundStore, last);
            }
            Glogger.LogTrain("...restart " + a);
            Invalidator.invalidate(last);       //reset all sharedWeights before restart
        }

        endTraining(roundStore, last);

        return results;
    }

    public Results checkbackAvg(MolecularTemplate last, List<Sample> roundStore) {
        Glogger.process("-------------checkBackAVG----------------");
        for (int a = 0; a < Settings.restartCount; a++) {    //restarting the whole procedure
            Glogger.process("-------------------------------------------------------------------------------------------------------------------------------");
            Glogger.process("------------Restart: " + a);
            for (int i = 0; i < Settings.learningSteps; i++) {       //learningSteps = backpropagation steps
                results.clearResultList();
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

                    Glogger.debug("Example: " + e + "Weight change from last minibatch (after 1-bp over all other examples) " + old + " -> " + b.valAvg);
                    Weights w = BackpropDownAvg.getNewWeights(b, e);  //backpropagation
                    refreshWeights(w);  //update sharedWeights
                    b.valAvg = Evaluator.evaluateAvg(b);  //forward propagation
                    results.add(new Result(b.valAvg, e.getExpectedValue()));
                }
                Glogger.LogTrain("bp_step", new Double[]{results.getLearningError(), results.getDispersion(), results.getMajorityClass(), results.getThreshold()});
                Glogger.process("Training error before max. subst. =\t" + results.getLearningError() + " (maj: " + results.getMajorityClass() + ")" + " (disp: " + results.getDispersion() + ")");
            }
            saveTemplate(roundStore, last);
            Glogger.LogTrain("...restart " + a);
            Invalidator.invalidate(last);       //reset all sharedWeights before restart
        }

        endTraining(roundStore, last);

        return results;
    }
}
