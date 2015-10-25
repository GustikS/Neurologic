package discoverer.global;

import discoverer.Main;
import discoverer.GroundedDataset;
import discoverer.NeuralDataset;
import discoverer.learning.functions.Activations;
import java.util.*;
//settings

public final class Global {
    
    public static NeuralDataset neuralDataset;

    private static int seed;
    /**
     * generating random weights and offsets
     */
    private static Random rg;

    private static boolean cacheEnabled = true;
    private static boolean forwardCheckEnabled = true;
    private static boolean debugEnabled = false;
    private static boolean infoEnabled = true;
    private static boolean pruning = true;
    private static double falseAtomValue = -1;   //non-entailed example output
    //public static boolean lambdaSigmoid = false;
    private static double initLambdaAdaptiveOffset;
    private static double initKappaAdaptiveOffset;
    private static boolean kappaAdaptiveOffset;
    /**
     * stochastic gradient descend (shuffling examples at each learning step)
     */
    private static boolean SGD;
    private static double dropout;
    private static int cumMaxSteps;
    private static boolean cumulativeRestarts;
    private static boolean cumulativeDiffRestarts;
    private static boolean save;
    //learn decay curve
    private static double learnDecayA = 700;  //how high it goes (divided with B)
    private static double learnDecayB = 500; //how fast it degrades (1=very fast)
    private static boolean learnDecay;    //learn rate decay yes or no  
    //---
    private static boolean checkback = false;    //compatibility test with Vojta's version (keep false unless testing)
    private static boolean outputFolds = false;
    //convergence criteria for AVG (max is within function bellow)
    private static double convergenceLimit = 0.01;
    private static int history = 200;
    //---special stuff
    private static boolean initWithAVG = false;
    private static boolean manualLoadNetwork = false;
    private static boolean GUI = false;
    private static mergingOptions merging = mergingOptions.weights;

    public static boolean exporting = true;
    public static boolean drawing = true;
    public static boolean longName = false;
    public static boolean saveGroundings = false;
    public static boolean loadGroundings = false;
    public static boolean uncompressedLambda = false;
    public static boolean fastVersion = false;

    /**
     * @return the merging
     */
    public static mergingOptions getMerging() {
        return merging;
    }

    /**
     * @param aMerging the merging to set
     */
    public static void setMerging(mergingOptions aMerging) {
        merging = aMerging;
    }

    //----taken as parameters from Main
    public static enum mergingOptions {

        weights, onTop
    };

    /**
     * @return the batch
     */
    public static batch getBatch() {
        return batch;
    }

    /**
     * @param aBatch the batch to set
     */
    public static void setBatch(batch aBatch) {
        batch = aBatch;
    }

    //----taken as parameters from Main
    public static enum groundingSet {

        max, avg
    };

    public static enum batch {

        NO, YES;
    }

    public static enum activationSet {

        sig, id, relu, softmax
    };

    public static enum weightInitSet {

        handmade, longtail, uniform
    };
    private static batch batch;
    private static groundingSet grounding;
    private static activationSet lambdaActivation;
    private static activationSet kappaActivation;
    private static weightInitSet weightInit;

    public static void setAvg() {
        setGrounding(groundingSet.avg);
        setPruning(false);    //important!
        setForwardCheckEnabled(true);
        Main.defaultLearningSteps = "" + Integer.parseInt(Main.defaultLearningEpochs) * Integer.parseInt(Main.defaultLearningSteps);
    }

    public static void setMax() {
        setGrounding(groundingSet.max);
        setPruning(true);
        setForwardCheckEnabled(true);
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
                setLambdaActivation(activationSet.sig);
                break;
            case "id":
                setLambdaActivation(activationSet.id);
                break;
            default:
                throw new AssertionError();
        }
        switch (acts[1]) {
            case "sig":
                setKappaActivation(activationSet.sig);
                break;
            case "id":
                setKappaActivation(activationSet.id);
                break;
            default:
                throw new AssertionError();
        }
    }

    public static void setInitialization(String init) {
        switch (init) {
            case "handmade":
                setWeightInit(weightInitSet.handmade);
                break;
            case "longtail":
                setWeightInit(weightInitSet.longtail);
                break;
            case "uniform":
                setWeightInit(weightInitSet.uniform);
                break;
            default:
                throw new AssertionError();
        }
    }

    public static void setCumSteps(String cum) {

        switch (cum) {
            case "on":
                setCumulativeRestarts(true);
                setCumulativeDiffRestarts(false);
                if (getGrounding() == groundingSet.max) {
                    setCumMaxSteps(Integer.parseInt(Main.defaultLearningEpochs) * Integer.parseInt(Main.defaultLearningSteps) * Integer.parseInt(Main.defaultRestartCount));
                } else {
                    setCumMaxSteps(Integer.parseInt(Main.defaultLearningSteps) * Integer.parseInt(Main.defaultRestartCount));
                }
                break;
            case "diff":
                setCumulativeRestarts(true);
                setCumulativeDiffRestarts(true);
                if (getGrounding() == groundingSet.max) {
                    Global.setHistory(5);
                    Global.setConvergenceLimit(0.03);
                    setCumMaxSteps(Integer.parseInt(Main.defaultLearningEpochs) * Integer.parseInt(Main.defaultLearningSteps) * Integer.parseInt(Main.defaultRestartCount));
                } else {
                    setCumMaxSteps(Integer.parseInt(Main.defaultLearningSteps) * Integer.parseInt(Main.defaultRestartCount));
                }
                break;
            default:
                try {
                    setCumMaxSteps(Integer.parseInt(cum));
                    setCumulativeRestarts(getCumMaxSteps() > 0);
                } catch (Exception e) {
                    setCumulativeRestarts(false);
                    setCumulativeDiffRestarts(false);
                    Glogger.err(e.getMessage());
                }
        }
    }

    public static void setLearnDecay(String decay) {
        switch (decay) {
            case "on":
                setLearnDecay(true);
                break;
            default:
                try {
                    setLearnDecayB(Integer.parseInt(decay));
                    setLearnDecay(getLearnDecayB() > 0);
                } catch (Exception e) {
                    setLearnDecay(false);
                    Glogger.err(e.getMessage());
                }
        }
    }

    /**
     * @return the seed
     */
    public static int getSeed() {
        return seed;
    }

    /**
     * @param aSeed the seed to set
     */
    public static void setSeed(int aSeed) {
        Global.setRg(new Random(Global.getSeed()));
        seed = aSeed;
    }

    /**
     * @return the rg
     */
    public static Random getRg() {
        return rg;
    }

    /**
     * @param aRg the rg to set
     */
    public static void setRg(Random aRg) {
        rg = aRg;
    }

    /**
     * @return the cacheEnabled
     */
    public static boolean isCacheEnabled() {
        return cacheEnabled;
    }

    /**
     * @param aCacheEnabled the cacheEnabled to set
     */
    public static void setCacheEnabled(boolean aCacheEnabled) {
        cacheEnabled = aCacheEnabled;
    }

    /**
     * @return the forwardCheckEnabled
     */
    public static boolean isForwardCheckEnabled() {
        return forwardCheckEnabled;
    }

    /**
     * @param aForwardCheckEnabled the forwardCheckEnabled to set
     */
    public static void setForwardCheckEnabled(boolean aForwardCheckEnabled) {
        forwardCheckEnabled = aForwardCheckEnabled;
    }

    /**
     * @return the debugEnabled
     */
    public static boolean isDebugEnabled() {
        return debugEnabled;
    }

    /**
     * @param aDebugEnabled the debugEnabled to set
     */
    public static void setDebugEnabled(boolean aDebugEnabled) {
        debugEnabled = aDebugEnabled;
    }

    /**
     * @return the infoEnabled
     */
    public static boolean isInfoEnabled() {
        return infoEnabled;
    }

    /**
     * @param aInfoEnabled the infoEnabled to set
     */
    public static void setInfoEnabled(boolean aInfoEnabled) {
        infoEnabled = aInfoEnabled;
    }

    /**
     * @return the pruning
     */
    public static boolean isPruning() {
        return pruning;
    }

    /**
     * @param aPruning the pruning to set
     */
    public static void setPruning(boolean aPruning) {
        pruning = aPruning;
    }

    /**
     * @return the falseAtomValue
     */
    public static double getFalseAtomValue() {
        return falseAtomValue;
    }

    /**
     * @param aFalseAtomValue the falseAtomValue to set
     */
    public static void setFalseAtomValue(double aFalseAtomValue) {
        falseAtomValue = aFalseAtomValue;
    }

    /**
     * @return the initLambdaAdaptiveOffset
     */
    public static double getInitLambdaAdaptiveOffset() {
        return initLambdaAdaptiveOffset;
    }

    /**
     * @param aInitLambdaAdaptiveOffset the initLambdaAdaptiveOffset to set
     */
    public static void setInitLambdaAdaptiveOffset(double aInitLambdaAdaptiveOffset) {
        initLambdaAdaptiveOffset = aInitLambdaAdaptiveOffset;
    }

    /**
     * @return the initKappaAdaptiveOffset
     */
    public static double getInitKappaAdaptiveOffset() {
        return initKappaAdaptiveOffset;
    }

    /**
     * @param aInitKappaAdaptiveOffset the initKappaAdaptiveOffset to set
     */
    public static void setInitKappaAdaptiveOffset(double aInitKappaAdaptiveOffset) {
        initKappaAdaptiveOffset = aInitKappaAdaptiveOffset;
    }

    /**
     * @return the kappaAdaptiveOffset
     */
    public static boolean isKappaAdaptiveOffset() {
        return kappaAdaptiveOffset;
    }

    /**
     * @param aKappaAdaptiveOffset the kappaAdaptiveOffset to set
     */
    public static void setKappaAdaptiveOffset(boolean aKappaAdaptiveOffset) {
        kappaAdaptiveOffset = aKappaAdaptiveOffset;
    }

    /**
     * @return the SGD
     */
    public static boolean isSGD() {
        return SGD;
    }

    /**
     * @param aSGD the SGD to set
     */
    public static void setSGD(boolean aSGD) {
        SGD = aSGD;
    }

    /**
     * @return the dropout
     */
    public static double getDropout() {
        return dropout;
    }

    /**
     * @param aDropout the dropout to set
     */
    public static void setDropout(double aDropout) {
        dropout = aDropout;
    }

    /**
     * @return the cumMaxSteps
     */
    public static int getCumMaxSteps() {
        return cumMaxSteps;
    }

    /**
     * @param aCumMaxSteps the cumMaxSteps to set
     */
    public static void setCumMaxSteps(int aCumMaxSteps) {
        cumMaxSteps = aCumMaxSteps;
    }

    /**
     * @return the cumulativeRestarts
     */
    public static boolean isCumulativeRestarts() {
        return cumulativeRestarts;
    }

    /**
     * @param aCumulativeRestarts the cumulativeRestarts to set
     */
    public static void setCumulativeRestarts(boolean aCumulativeRestarts) {
        cumulativeRestarts = aCumulativeRestarts;
    }

    /**
     * @return the cumulativeDiffRestarts
     */
    public static boolean isCumulativeDiffRestarts() {
        return cumulativeDiffRestarts;
    }

    /**
     * @param aCumulativeDiffRestarts the cumulativeDiffRestarts to set
     */
    public static void setCumulativeDiffRestarts(boolean aCumulativeDiffRestarts) {
        cumulativeDiffRestarts = aCumulativeDiffRestarts;
    }

    /**
     * @return the save
     */
    public static boolean isSave() {
        return save;
    }

    /**
     * @param aSave the save to set
     */
    public static void setSave(boolean aSave) {
        save = aSave;
    }

    /**
     * @return the learnDecayA
     */
    public static double getLearnDecayA() {
        return learnDecayA;
    }

    /**
     * @param aLearnDecayA the learnDecayA to set
     */
    public static void setLearnDecayA(double aLearnDecayA) {
        learnDecayA = aLearnDecayA;
    }

    /**
     * @return the learnDecayB
     */
    public static double getLearnDecayB() {
        return learnDecayB;
    }

    /**
     * @param aLearnDecayB the learnDecayB to set
     */
    public static void setLearnDecayB(double aLearnDecayB) {
        learnDecayB = aLearnDecayB;
    }

    /**
     * @return the learnDecay
     */
    public static boolean isLearnDecay() {
        return learnDecay;
    }

    /**
     * @param aLearnDecay the learnDecay to set
     */
    public static void setLearnDecay(boolean aLearnDecay) {
        learnDecay = aLearnDecay;
    }

    /**
     * @return the checkback
     */
    public static boolean isCheckback() {
        return checkback;
    }

    /**
     * @param aCheckback the checkback to set
     */
    public static void setCheckback(boolean aCheckback) {
        checkback = aCheckback;
    }

    /**
     * @return the outputFolds
     */
    public static boolean isOutputFolds() {
        return outputFolds;
    }

    /**
     * @param aOutputFolds the outputFolds to set
     */
    public static void setOutputFolds(boolean aOutputFolds) {
        outputFolds = aOutputFolds;
    }

    /**
     * @return the convergenceLimit
     */
    public static double getConvergenceLimit() {
        return convergenceLimit;
    }

    /**
     * @param aConvergenceLimit the convergenceLimit to set
     */
    public static void setConvergenceLimit(double aConvergenceLimit) {
        convergenceLimit = aConvergenceLimit;
    }

    /**
     * @return the history
     */
    public static int getHistory() {
        return history;
    }

    /**
     * @param aHistory the history to set
     */
    public static void setHistory(int aHistory) {
        history = aHistory;
    }

    /**
     * @return the initWithAVG
     */
    public static boolean isInitWithAVG() {
        return initWithAVG;
    }

    /**
     * @param aInitWithAVG the initWithAVG to set
     */
    public static void setInitWithAVG(boolean aInitWithAVG) {
        initWithAVG = aInitWithAVG;
    }

    /**
     * @return the manualLoadNetwork
     */
    public static boolean isManualLoadNetwork() {
        return manualLoadNetwork;
    }

    /**
     * @param aManualLoadNetwork the manualLoadNetwork to set
     */
    public static void setManualLoadNetwork(boolean aManualLoadNetwork) {
        manualLoadNetwork = aManualLoadNetwork;
    }

    /**
     * @return the GUI
     */
    public static boolean isGUI() {
        return GUI;
    }

    /**
     * @param aGUI the GUI to set
     */
    public static void setGUI(boolean aGUI) {
        GUI = aGUI;
    }

    /**
     * @return the grounding
     */
    public static groundingSet getGrounding() {
        return grounding;
    }

    /**
     * @param aGrounding the grounding to set
     */
    public static void setGrounding(groundingSet aGrounding) {
        grounding = aGrounding;
    }

    /**
     * @return the lambdaActivation
     */
    public static activationSet getLambdaActivation() {
        return lambdaActivation;
    }

    /**
     * @param aLambdaActivation the lambdaActivation to set
     */
    public static void setLambdaActivation(activationSet aLambdaActivation) {
        lambdaActivation = aLambdaActivation;
    }

    /**
     * @return the kappaActivation
     */
    public static activationSet getKappaActivation() {
        return kappaActivation;
    }

    /**
     * @param aKappaActivation the kappaActivation to set
     */
    public static void setKappaActivation(activationSet aKappaActivation) {
        kappaActivation = aKappaActivation;
    }

    /**
     * @return the weightInit
     */
    public static weightInitSet getWeightInit() {
        return weightInit;
    }

    /**
     * @param aWeightInit the weightInit to set
     */
    public static void setWeightInit(weightInitSet aWeightInit) {
        weightInit = aWeightInit;
    }

}
