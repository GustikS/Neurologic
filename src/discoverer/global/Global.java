package discoverer.global;

import discoverer.Main;
import discoverer.learning.backprop.functions.Activations;
import java.util.*;
//settings

public class Global {

    public static int seed;
    /**
     * generating random weights and offsets
     */
    public static Random rg;
    /**
     * stochastic gradient descend (shuffling examples at each learning step)
     */
    public static boolean cacheEnabled = true;
    public static boolean forwardCheckEnabled = true;
    public static final boolean debugEnabled = false;
    public static boolean infoEnabled = true;
    public static boolean pruning = true;
    public static double falseAtomValue = -1;   //non-entailed example output
    //public static boolean lambdaSigmoid = false;
    public static double initLambdaAdaptiveOffset;
    public static double initKappaAdaptiveOffset;
    public static boolean kappaAdaptiveOffset;
    //
    public static boolean SGD;
    public static double dropout;
    public static int cumMaxSteps;
    public static boolean cumulativeRestarts;
    public static boolean cumulativeDiffRestarts;
    public static boolean save;
    //learn decay curve
    public static double learnDecayA = 700;  //how high it goes (divided with B)
    public static double learnDecayB = 500; //how fast it degrades (1=very fast)
    public static boolean learnDecay;    //learn rate decay yes or no  
    //---
    public static boolean checkback = false;    //compatibility test with Vojta's version (keep false unless testing)
    public static boolean outputFolds = true;
    //convergence criteria for AVG (max is within function bellow)
    public static double convergenceLimit = 0.01;
    public static int history = 200;
    
    public static boolean initWithAVG = false;

    //----taken as parameters from Main
    public static enum groundingSet {

        max, avg
    };

    public static enum activationSet {

        sig, id, relu, softmax
    };

    public static enum weightInitSet {

        handmade, longtail, uniform
    };

    public static groundingSet grounding;
    public static activationSet lambdaActivation;
    public static activationSet kappaActivation;
    public static weightInitSet weightInit;

    public static void setAvg() {
        grounding = groundingSet.avg;
        pruning = false;    //important!
        forwardCheckEnabled = true;
        Main.defaultLearningSteps = "" + Integer.parseInt(Main.defaultLearningEpochs) * Integer.parseInt(Main.defaultLearningSteps);
    }

    public static void setMax() {
        grounding = groundingSet.max;
        pruning = true;
        forwardCheckEnabled = true;
    }

    public static void setGrounding(String ground) {
        switch (ground) {
            case "max":
                setMax();
                break;
            case "avg":
                setAvg();
                break;
            default:
                throw new AssertionError();
        }
    }

    public static void setActivations(String act) {
        String[] acts = act.split("_");
        switch (acts[0]) {
            case "sig":
                lambdaActivation = activationSet.sig;
                break;
            case "id":
                lambdaActivation = activationSet.id;
                break;
            default:
                throw new AssertionError();
        }
        switch (acts[1]) {
            case "sig":
                kappaActivation = activationSet.sig;
                break;
            case "id":
                kappaActivation = activationSet.id;
                break;
            default:
                throw new AssertionError();
        }
    }

    public static void setInitialization(String init) {
        switch (init) {
            case "handmade":
                weightInit = weightInitSet.handmade;
                break;
            case "longtail":
                weightInit = weightInitSet.longtail;
                break;
            case "uniform":
                weightInit = weightInitSet.uniform;
                break;
            default:
                throw new AssertionError();
        }
    }

    public static void setCumSteps(String cum) {

        switch (cum) {
            case "on":
                cumulativeRestarts = true;
                cumulativeDiffRestarts = false;
                if (grounding == groundingSet.max) {
                    cumMaxSteps = Integer.parseInt(Main.defaultLearningEpochs) * Integer.parseInt(Main.defaultLearningSteps) * Integer.parseInt(Main.defaultRestartCount);
                } else {
                    cumMaxSteps = Integer.parseInt(Main.defaultLearningSteps) * Integer.parseInt(Main.defaultRestartCount);
                }
                break;
            case "diff":
                cumulativeRestarts = true;
                cumulativeDiffRestarts = true;
                if (grounding == groundingSet.max) {
                    Global.history = 5;
                    Global.convergenceLimit = 0.03;
                    cumMaxSteps = Integer.parseInt(Main.defaultLearningEpochs) * Integer.parseInt(Main.defaultLearningSteps) * Integer.parseInt(Main.defaultRestartCount);
                } else {
                    cumMaxSteps = Integer.parseInt(Main.defaultLearningSteps) * Integer.parseInt(Main.defaultRestartCount);
                }
                break;
            default:
                try {
                    cumMaxSteps = Integer.parseInt(cum);
                    cumulativeRestarts = cumMaxSteps > 0;
                } catch (Exception e) {
                    cumulativeRestarts = false;
                    cumulativeDiffRestarts = false;
                    Glogger.err(e.getMessage());
                }
        }
    }

    public static void setLearnDecay(String decay) {
        switch (decay) {
            case "on":
                learnDecay = true;
                break;
            default:
                try {
                    learnDecayB = Integer.parseInt(decay);
                    learnDecay = learnDecayB > 0;
                } catch (Exception e) {
                    learnDecay = false;
                    Glogger.err(e.getMessage());
                }
        }
    }
}
