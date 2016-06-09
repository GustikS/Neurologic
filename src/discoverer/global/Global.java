package discoverer.global;

import discoverer.Main;
import discoverer.GroundedDataset;
import discoverer.LiftedDataset;
import discoverer.construction.template.LiftedTemplate;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
//settings

public final class Global {
    //parallelTraining + SGD = funny experiment (not synchronized access to weights)
    //parallelTrainin + Batch = very natural, no hange in behavior

    //public static NeuralDataset neuralDataset;
    private static int seed;
    /**
     * generating random weights and offsets
     */
    private static Random rg;

    private static boolean cacheEnabled = true; //false -> creates trees instead of networks
    private static boolean forwardCheckEnabled = true; //do not try to turn this off (super-slow, and maybe also grounding may change due to alldiff=true (not sure))
    public static boolean debugEnabled = false;  //very detailed info (too slow for an actual run)
    private static boolean infoEnabled = true;
    private static boolean pruning = true;  //for AVG grounding and results , pruning=false is necessary!
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
    private static boolean loadNetworkObject = false;
    private static boolean GUI = false;
    private static mergingOptions merging = null;

    public static boolean exporting = false;
    public static boolean createWeightMatrix = true;   //matrix of neural weight for printing with Matlab etc.

    public static boolean drawing = false;
    public static boolean longName = false;
    public static boolean saveGroundedDataset = false;
    public static boolean loadGroundedDataset = false;
    public static boolean uncompressedLambda = false;
    public static boolean fastVersion = true;
    public static boolean memoryLight = true;  //saves 60% by removing groundKL structures (keeps only neural), makes sense with fastVersion on only
    public static boolean molecularTemplates = true;

    public static boolean multiLine = false; //example can spread to multiple lines, delimited by empty line (\n\n)
    public static boolean parallelGrounding = true;
    public static boolean parallelTraining = true; //experimental!!
    public static int numOfThreads = 4;
    public static boolean batchMode = false;

    public static boolean relativeVariableSelection = true; //ordering of variables when grounding goes for the ones that leave the least number of other variables free, otherwise goes just for the most contrained literals (Vojta's version)
    public static boolean alldiff = true;
    public static final boolean adaptiveActivations = false;
    public static boolean learnableFacts = false;
    
    public static String weightFolder = "weights/";
    
    //---Structure Learning parameters
    public static boolean regularizedBackprop = false;
    public static boolean forwardchecker;
    

    public static void setupThreads() {
        numOfThreads = Runtime.getRuntime().availableProcessors();
    }

    public static void setupStreamThreads(int count) {
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", count + "");
    }

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

    public static double getRandomDouble() {
        return rg.nextDouble();
        //return 0.5;
    }

    public static int getRandomInt(int i) {
        return rg.nextInt(i);
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
     * @return the loadNetworkObject
     */
    public static boolean isLoadLiftedNetworkObject() {
        return loadNetworkObject;
    }

    /**
     * @param aManualLoadNetwork the loadNetworkObject to set
     */
    public static void setLoadNetworkObject(boolean aManualLoadNetwork) {
        loadNetworkObject = aManualLoadNetwork;
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

    /**
     * Just a generic method for sorting map by values
     *
     * @param <K>
     * @param <V>
     * @param map
     * @return
     */
    public static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> sortByValues(Map<K, V> map) {
        SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<Map.Entry<K, V>>(
                new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
                int res = e1.getValue().compareTo(e2.getValue());
                return res != 0 ? res : 1; // Special fix to preserve items with equal values
            }
        }
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }

    public static Object makeDeepCopy(Object net) {
        ObjectOutputStream oos = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(net);
            oos.flush();
            oos.close();
            bos.close();
            byte[] byteData = bos.toByteArray();
            ByteArrayInputStream bais = new ByteArrayInputStream(byteData);
            Object newnet = new ObjectInputStream(bais).readObject();
            return newnet;
        } catch (IOException ex) {
            Logger.getLogger(GroundedDataset.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GroundedDataset.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                oos.close();
            } catch (IOException ex) {
                Logger.getLogger(GroundedDataset.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public static void savesomething(Object o, String path) {
        try {
            Glogger.process("Saving something...");
            FileOutputStream out = new FileOutputStream(path);
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(o);
            oos.flush();
            Glogger.process("Successfully Saved something into: " + path);
        } catch (Exception e) {
            Glogger.err("Problem serializing: " + e);
        }
    }

    public static Object loadSomething(String path) {
        try {
            Glogger.process("trying to load something from a file: " + path);
            FileInputStream in = new FileInputStream(path);
            ObjectInputStream ois = new ObjectInputStream(in);
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            Glogger.info("Problem loading " + e);
        }
        return null;
    }
}
