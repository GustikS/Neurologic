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
    public static boolean cacheEnabled = true;
    public static boolean forwardCheckEnabled = true;
    public static final boolean debugEnabled = false;
    public static boolean pruning = true;
    public static double falseAtomValue = -1;   //non-entailed example output
    //public static boolean lambdaSigmoid = false;
    public static double initLambdaAdaptiveOffset;
    public static double initKappaAdaptiveOffset;
    public static boolean kappaAdaptiveOffset;
    public static double dropout;

    //----taken as parameters from Main
    public static enum groundingSet {

        max, avg
    };

    public static enum activationSet {

        sig, id
    };

    public static enum weightInitSet {

        handmade, longtail
    };

    public static groundingSet grounding;
    public static activationSet lambdaActivation;
    public static activationSet kappaActivation;
    public static weightInitSet weightInit;

    public static void setAvg() {
        grounding = groundingSet.avg;
        pruning = false;    //important!
        forwardCheckEnabled = true;
        Main.defaultLearningSteps = "2000";
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
            default:
                throw new AssertionError();
        }
    }
}
