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
    public static boolean save;
    public static double learnDecayA = 30;  //reasonable to keep this
    public static double learnDecayB = 200; //how fast it degrades (1=very fast)
    public static boolean learnDecay;    //learn rate decay yes or no  
    //---
    public static boolean checkback = false;    //compatibility test with Vojta's version (keep false unless testing)

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
        Main.defaultLearningSteps = "" + Integer.parseInt(Main.defaultLearningEpochs) * Integer.parseInt(Main.defaultLearningSteps) * 2;
        Main.defaultLearningEpochs = "0";
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
            default:
                throw new AssertionError();
        }
    }
}
