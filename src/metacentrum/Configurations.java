/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metacentrum;

import java.util.LinkedList;

/**
 *
 * @author Gusta
 */
public class Configurations {

    //some prepared parameter-value configurations to choose from if one wants to try out a parameter
    public static String[] folds = new String[]{"-f", "2", "3", "5", "10"};
    public static String[] groundings = new String[]{"-gr", "max", "avg"};
    public static String[] learnRates = new String[]{"-lr", "0.01", "0.02", "0.05", "0.1", "0.2"};
    public static String[] bpSteps = new String[]{"-ls", "10", "20", "50", "100", "1000", "3000"};
    public static String[] restarts = new String[]{"-rs", "1", "2", "3"};
    public static String[] epochs = new String[]{"-le", "7", "10"};
    public static String[] activations = new String[]{"-ac", "sig_id", "sig_sig"};
    public static String[] initials = new String[]{"-wi", "handmade", "longtail", "uniform"};

    public static String[] lambdaOffsets = new String[]{"-lo", "1", "2"};
    public static String[] kappaOffsets = new String[]{"-ko", "0.01", "0.1", "1", "5"};

    public static String[] dropouts = new String[]{"-dr", "0", "0.2", "0.5"};

    public static String[] seeds = new String[]{"-sd", "1", "2", "3"};
    public static String[] sgd = new String[]{"-sgd", "0", "1"};
    public static String[] cumSteps = new String[]{"-cum", "0", "50000"};
    public static String[] learnDecay = new String[]{"-lrd", "0", "10", "20", "100"};

    public static LinkedList<String> configurations;

    public static LinkedList<String> getConfigurations(LinkedList<String[]> pars) {
        configurations = new LinkedList<>();
        setConfigs(new LinkedList<String>(), pars, 0);
        return configurations;
    }

    private static void setConfigs(LinkedList<String> config, LinkedList<String[]> pars, int ind) {
        if (ind == pars.size()) {
            configurations.add(getString(config));
            return;
        }
        String[] parameter = pars.get(ind);
        config.add(parameter[0] + " ");
        for (int i = 1; i < parameter.length; i++) {
            config.add(parameter[i] + " ");
            setConfigs(config, pars, ind + 1);
            config.removeLast();
        }
        config.removeLast();
    }

    private static String getString(LinkedList<String> config) {
        StringBuilder res = new StringBuilder();
        for (String conf : config) {
            res.append(conf);
        }
        return res.toString();
    }

}
