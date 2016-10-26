/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metacentrum;

import java.io.File;
import java.util.LinkedList;

/**
 *
 * @author Gusta
 */
public class Configurations {

    public static String dataPath = "../in/";
    public static String ruleFileName = "trichain3";

    //private static final String[] data = new String[]{"ptc/mm", "ptc/mr", "ptc/fm", "ptc/fr"};
    private static final String[] data = getAllDatasetsFrom("C:\\Users\\gusta\\googledrive\\Github\\LRNN\\in\\jair\\");
    
    //some prepared parameter-value configurations to choose from if one wants to try out a parameter
    public static String[] folds = new String[]{"-f", "1", "5", "10"};
    public static String[] groundings = new String[]{"-gr", "max", "avg"};
    public static String[] learnRates = new String[]{"-lr", "0.05", "0.3"};
    public static String[] bpSteps = new String[]{"-ls", "2000", "10000"};
    public static String[] restarts = new String[]{"-rs", "1", "2", "3"};
    public static String[] epochs = new String[]{"-le", "7", "10"};
    public static String[] activations = new String[]{"-ac", "sig_id", "sig_sig"};
    public static String[] initials = new String[]{"-wi", "handmade", "longtail", "uniform"};

    public static String[] lambdaOffsets = new String[]{"-lo", "1", "2"};
    public static String[] kappaOffsets = new String[]{"-ko", "0.01", "0.1", "1", "5"};

    public static String[] dropouts = new String[]{"-dr", "0", "0.01", "0.05", "0.2"};

    public static String[] seeds = new String[]{"-sd", "1", "2", "3"};
    public static String[] sgd = new String[]{"-sgd", "0", "1"};
    public static String[] cumSteps = new String[]{"-cum", "0", "diff"};    //on,diff,number
    public static String[] learnDecay = new String[]{"-lrd", "0", "on"};    //on,number

    //datasets
    public static String[] datasets = setUpDatasets();
    public static String[] templates = new String[]{"-t", "none", "../weights/rules.w"};    //none serves as dummy

    public static LinkedList<String> configurations;

    public static final String[] getAllDatasetsFrom(String path) {
        File[] files = new File(path).listFiles();
        String[] names = new String[files.length];
        for (int i = 0; i < names.length; i++) {
            names[i] = files[i].getName();
        }
        return names;
    }

    public static final String[] setUpDatasets() {
        String[] exs = new String[data.length + 1];
        exs[0] = "-e";
        for (int i = 0; i < data.length; i++) {
            exs[i + 1] = dataPath + data[i] + "/examples " + "-r " + dataPath + data[i] + "/" + ruleFileName;
        }
        return exs;
    }

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
