/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer.global;

/**
 *
 * @author Gusta
 */
public class Settings {

    private static String grounding;
    private static int folds;
    private static int steps;
    private static int epochs;
    private static int restart;
    private static double learnRate;
    private static String activations;
    private static String initials;
    private static String loffset;
    private static String koffset;
    private static String seed;

    public static void create(String iground, int ifolds, int isteps, int iepochs, int irestartCount, double ilearnRate, String act, String initial, String loff, String koff, String iseed) {
        setGrounding(iground);
        setFolds(ifolds);
        setSteps(isteps);
        setEpochs(iepochs);
        setRestart(irestartCount);
        setLearnRate(ilearnRate);
        setActivations(act);
        setInitials(initial);
        setLoffset(loff);
        setKoffset(koff);
        setSeed(iseed);
    }

    public static String getString() {
        StringBuilder sb = new StringBuilder();
        sb.append("gr-").append(grounding).append("_");
        sb.append("f-").append(folds).append("_");
        sb.append("r-").append(restart).append("_");
        sb.append("e-").append(epochs).append("_");
        sb.append("s-").append(steps).append("_");
        sb.append("l-").append(learnRate).append("_");
        sb.append("ac-").append(activations).append("_");
        sb.append("wi-").append(initials).append("_");
        sb.append("lo-").append(loffset).append("_");
        sb.append("ko-").append(koffset).append("_");
        sb.append("sd-").append(getSeed()).append("_");
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
    public static int getSteps() {
        return steps;
    }

    /**
     * @param aSteps the steps to set
     */
    public static void setSteps(int aSteps) {
        steps = aSteps;
    }

    /**
     * @return the epochs
     */
    public static int getEpochs() {
        return epochs;
    }

    /**
     * @param aEpochs the epochs to set
     */
    public static void setEpochs(int aEpochs) {
        epochs = aEpochs;
    }

    /**
     * @return the restart
     */
    public static int getRestart() {
        return restart;
    }

    /**
     * @param aRestart the restart to set
     */
    public static void setRestart(int aRestart) {
        restart = aRestart;
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
        seed = aSeed;
    }


}
