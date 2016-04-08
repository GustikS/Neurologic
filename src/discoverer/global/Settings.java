/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer.global;

import java.util.Random;

/**
 *
 * @author Gusta
 */
public class Settings {

    public static String grounding;

    public static int folds;
    public static int learningSteps;
    public static int learningEpochs;
    public static int restartCount;
    public static int maxExamples;

    public static double learnRate;

    private static String activations;
    private static String initials;
    private static String loffset;
    private static String koffset;
    private static String dropout;
    private static String seed;
    private static String SGD;
    private static String cumSteps;
    private static String save;
    private static String lrDecay;
    private static String dataset;
    private static String rules;
    private static String pretrained;
    private static String testSet;

    /**
     * outdated...new way of creating Settings is directly! to synchronize with
     * Global
     *
     * @param iground
     * @param ifolds
     * @param isteps
     * @param iepochs
     * @param irestartCount
     * @param ilearnRate
     * @param act
     * @param initial
     * @param loff
     * @param koff
     * @param drop
     * @param cum
     * @param isgd
     * @param isave
     * @param idecay
     * @param iseed
     */
    public static void create(String iground, int ifolds, int isteps, int iepochs, int irestartCount, double ilearnRate, String act,
            String initial, String loff, String koff, String drop, String cum, String isgd, String isave, String idecay, String iseed) {
        setGrounding(iground);
        setFolds(ifolds);
        setLearningSteps(isteps);
        setLearningEpochs(iepochs);
        setRestartCount(irestartCount);
        setLearnRate(ilearnRate);
        setActivations(act);
        setInitials(initial);
        setLoffset(loff);
        setKoffset(koff);
        setSeed(iseed);
        setDropout(drop);
        setCumSteps(cum);
        setSGD(isgd);
        setSave(isave);
        setLrDecay(idecay);
    }

    public static String getString() {
        StringBuilder sb = new StringBuilder();
        //datapart
        sb.append("dat-").append(dataset).append("_");
        sb.append("test-").append(getTestSet()).append("_");
        sb.append("rul-").append(rules).append("_");
        sb.append("tem-").append(pretrained).append("_");
        sb.append("count-").append(maxExamples).append("_");
        //learning
        sb.append("f-").append(folds).append("_");
        sb.append("gr-").append(grounding).append("_");
        sb.append("r-").append(restartCount).append("_");
        sb.append("e-").append(learningEpochs).append("_");
        sb.append("s-").append(learningSteps).append("_");
        sb.append("l-").append(learnRate).append("_");
        sb.append("lrd-").append(getLrDecay()).append("_");
        sb.append("ac-").append(activations).append("_");
        sb.append("wi-").append(initials).append("_");
        sb.append("lo-").append(loffset).append("_");
        sb.append("ko-").append(koffset).append("_");
        sb.append("dr-").append(getDropout()).append("_");
        sb.append("sd-").append(getSeed()).append("_");
        sb.append("cum-").append(getCumSteps()).append("_");
        sb.append("sgd-").append(getSGD()).append("_");
        //others
        sb.append("save-").append(getSave()).append("_");
        return sb.toString();
    }

    /**
     * @return the grounding
     */
    public static String getGrounding() {
        return grounding;
    }

    /**
     * @param aGrounding the grounding to set
     */
    public static void setGrounding(String aGrounding) {
        Global.setGrounding(aGrounding);
        grounding = aGrounding;
    }

    /**
     * @return the folds
     */
    public static int getFolds() {
        return folds;
    }

    /**
     * @param aFolds the folds to set
     */
    public static void setFolds(int aFolds) {
        folds = aFolds;
    }

    /**
     * @return the steps
     */
    public static int getLearningSteps() {
        return learningSteps;
    }

    /**
     * @param aSteps the steps to set
     */
    public static void setLearningSteps(int aSteps) {
        learningSteps = aSteps;
    }

    /**
     * @return the epochs
     */
    public static int getLearningEpochs() {
        return learningEpochs;
    }

    /**
     * @param aEpochs the epochs to set
     */
    public static void setLearningEpochs(int aEpochs) {
        learningEpochs = aEpochs;
    }

    /**
     * @return the restart
     */
    public static int getRestartCount() {
        return restartCount;
    }

    /**
     * @param aRestart the restart to set
     */
    public static void setRestartCount(int aRestart) {
        restartCount = aRestart;
    }

    /**
     * @return the learnRate
     */
    public static double getLearnRate() {
        return learnRate;
    }

    /**
     * @param aLearnRate the learnRate to set
     */
    public static void setLearnRate(double aLearnRate) {
        learnRate = aLearnRate;
    }

    /**
     * @return the activations
     */
    public static String getActivations() {
        return activations;
    }

    /**
     * @param aActivations the activations to set
     */
    public static void setActivations(String aActivations) {
        Global.setActivations(aActivations);
        activations = aActivations;
    }

    /**
     * @return the initials
     */
    public static String getInitials() {
        return initials;
    }

    /**
     * @param aInitials the initials to set
     */
    public static void setInitials(String aInitials) {
        Global.setInitialization(aInitials);
        initials = aInitials;
    }

    /**
     * @return the loffset
     */
    public static String getLoffset() {
        return loffset;
    }

    /**
     * @param aLoffset the loffset to set
     */
    public static void setLoffset(String aLoffset) {
        Global.setInitLambdaAdaptiveOffset(Double.parseDouble(aLoffset));
        loffset = aLoffset;
    }

    /**
     * @return the koffset
     */
    public static String getKoffset() {
        return koffset;
    }

    /**
     * @param aKoffset the koffset to set
     */
    public static void setKoffset(String aKoffset) {
        Global.setInitKappaAdaptiveOffset(Double.parseDouble(aKoffset));
        koffset = aKoffset;
    }

    /**
     * @return the seed
     */
    public static String getSeed() {
        return seed;
    }

    /**
     * @param aSeed the seed to set
     */
    public static void setSeed(String aSeed) {
        Global.setSeed(Integer.parseInt(aSeed));
        seed = aSeed;
    }

    /**
     * @return the dropout
     */
    public static String getDropout() {
        return dropout;
    }

    /**
     * @param aDropout the dropout to set
     */
    public static void setDropout(String aDropout) {
        Global.setDropout(Double.parseDouble(aDropout));
        dropout = aDropout;
    }

    /**
     * @return the SGD
     */
    public static String getSGD() {
        return SGD;
    }

    /**
     * @param aSGD the SGD to set
     */
    public static void setSGD(String aSGD) {
        Global.setSGD(Double.parseDouble(aSGD) > 0);
        SGD = aSGD;
    }

    /**
     * @return the cumSteps
     */
    public static String getCumSteps() {
        return cumSteps;
    }

    /**
     * @param aCumSteps the cumSteps to set
     */
    public static void setCumSteps(String aCumSteps) {
        Global.setCumSteps(aCumSteps);
        cumSteps = aCumSteps;
    }

    /**
     * @return the save
     */
    public static String getSave() {
        return save;
    }

    /**
     * @param aSave the save to set
     */
    public static void setSave(String aSave) {
        Global.setSave(Integer.parseInt(aSave) > 0);
        save = aSave;
    }

    /**
     * @return the lrDecay
     */
    public static String getLrDecay() {
        return lrDecay;
    }

    /**
     * @param aLrDecay the lrDecay to set
     */
    public static void setLrDecay(String aLrDecay) {
        Global.setLearnDecay(aLrDecay);
        lrDecay = aLrDecay;
    }

    /**
     * @return the maxExamples
     */
    public static int getMaxExamples() {
        return maxExamples;
    }

    /**
     * @param aMaxExamples the maxExamples to set
     */
    public static void setMaxExamples(int aMaxExamples) {
        maxExamples = aMaxExamples;
    }

    /**
     * @return the dataset
     */
    public static String getDataset() {
        return dataset;
    }

    /**
     * @param aDataset the dataset to set
     */
    public static void setDataset(String aDataset) {
        dataset = aDataset.replace("/", "_").replaceAll("\\.", "");
    }

    /**
     * @return the pretrained
     */
    public static String getPretrained() {
        return pretrained;
    }

    /**
     * @param aPretrained the pretrained to set
     */
    public static void setPretrained(String aPretrained) {
        if (aPretrained == null) {
            aPretrained = "none";
        }
        pretrained = aPretrained.replace("/", "_").replaceAll("\\.", "");
    }

    /**
     * @return the rules
     */
    public static String getRules() {
        return rules;
    }

    /**
     * @param aRules the rules to set
     */
    public static void setRules(String aRules) {
        rules = aRules.replace("/", "_").replaceAll("\\.", "");
    }

    /**
     * @return the testSet
     */
    public static String getTestSet() {
        return testSet;
    }

    /**
     * @param aTestSet the testSet to set
     */
    public static void setTestSet(String aTestSet) {
        testSet = aTestSet.replace("/", "_").replaceAll("\\.", "");
    }

}
