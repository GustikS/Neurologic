package lrnn;

import lrnn.crossvalidation.Crossvalidation;
import lrnn.crossvalidation.NeuralCrossvalidation;
import lrnn.global.Global;
import lrnn.global.Glogger;
import lrnn.global.Settings;
import lrnn.global.TextFileReader;
import org.apache.commons.cli.*;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

/**
 * Main class Params handling and calling appropriate methods
 */
public class Main {

    //cutoff on example number
    private static final String defaultMaxExamples = "10000";  //we can decrease the overall number of examples (stratified) for speedup   //todo now draw and walkthrough NNs
    //
    public static String defaultLearningSteps = "3000";  //learnSteps per epocha
    public static String defaultLearningEpochs = "1";  //learn epochae = grounding cycles
    //  learnEpochae * LearningSteps = learning steps for AVG variant
    private static final String defaultFolds = "1"; // 1 = training only
    private static final String defaultLearningRate = "0.3"; //0.05 default from Vojta, it's good to increase, reasonable <0.1 , 1>
    //learnRate = 5 -> gets stuck very soon (<=10 steps) around 23% acc (+ jumping), unable to learnOn
    //learnRate = 1 -> plato around 600 steps with 10% acc (+ BIG jumping +-3%, but also +10%)
    //learnRate = 0.5 -> plato around 1000 steps with 8% acc (+ jumping +-3%) -> can break into some best results (0.3%) with saving
    //learnRate = 0.3 -> plato around 1000 steps with 8% acc, just a very mild jumping, very good apparent behavior
    //learnRate = 0.1 -> stable plato around 600 steps with 11% (no jumping, very stable, stuck)
    public static final String defaultRestartCount = "1";  //#restarts per fold
    //max-avg
    public static final String defaultGrounding = "avg";    //avg or max
    public static String defaultActivations = "sig_sig";    //lambda_kappa activation functions  //todo this has some effect, although not much
    public static String defaultInitialization = "uniform";    //handmade = 0.9:0.1 ; uniform = uniform ; longtail = longtail distribution  //todo this has some effect, esp. in combination wth the offsets
    //offsets
    public static boolean defaultKappaAdaptiveOffsetOn = false; //kappa offset is initialized based on number of input lambdas
    public static String defaultKappaAdaptiveOffset = "0";  //must stay zero as default if defaultKappaAdaptiveOffsetOn = false //todo keep ZERO!
    public static String defaultLambdaAdaptiveOffset = "0"; //lambda offset to add to the -1* (number of input kappas)  //todo this affects learning very much (probably saturation of sigmoids), better keep it ZERO!

    private static String defaultAlldiff = "1"; //todo this is of course better to be turned on, but for pairty check with LRNN2.0 it can be turned off

    private static String defaultCompressed = "1";    //todo compressed/uncompress lambdas, mainly for speed but also affects learning a bit

    public static String defaultSeed = "0"; //seeds the whole algorithm (shuffling, etc.), should make it completely deterministic

    private static String defaultSaving = "1"; // >0 => saving of template is ON after each bp-step (minibatch)  --> adds 10% extra computation time

    public static String defaultDropoutRate = "0";  // >0 => dropout is ON, at the particular rate
    private static String defaultSGD = "1";     // >0 => stochastic gradient descend is ON  //todo debugging
    private static String defaultCumSteps = "0"; // "on" or number of steps, <= 0 => OFF
    private static String defaultLearnDecay = "0"; // >0 => learnRate decay strategy is ON
    private static int maxReadline = 1000000; //cut-of reading input files (not used)
    private static String defaultDebug = "0";
    private static String defaultEmbeddings = "0";
    private static String defaultDrawing = "0";
    private static String defaultBottomUp = "1";
    private static String defaultOutput = "./lrnn_results";

    public static Options getOptions() {
        Options options = new Options();
        OptionBuilder.withLongOpt("rules");
        OptionBuilder.hasArg(true);
        OptionBuilder.isRequired(true);
        OptionBuilder.withArgName("RULE-FILE");
        OptionBuilder.withDescription("File with lifted rules");
        options.addOption(OptionBuilder.create("r"));

        OptionBuilder.withLongOpt("pretrained");
        OptionBuilder.hasArg();
        OptionBuilder.withArgName("PRETRAINED");
        OptionBuilder.withDescription("File with pretrained network template");
        options.addOption(OptionBuilder.create("t"));

        OptionBuilder.withLongOpt("examples");
        OptionBuilder.hasArg();
        OptionBuilder.isRequired();
        OptionBuilder.withArgName("EXAMPLE-FILE");
        OptionBuilder.withDescription("File with examples");
        options.addOption(OptionBuilder.create("e"));

        OptionBuilder.withLongOpt("testSet");
        OptionBuilder.hasArg();
        OptionBuilder.withArgName("TEST-FILE");
        OptionBuilder.withDescription("File with test examples");
        options.addOption(OptionBuilder.create("test"));

        OptionBuilder.withLongOpt("example-count");
        OptionBuilder.withDescription("Maximal count of examples (default: " + defaultMaxExamples + ")");
        OptionBuilder.withArgName("SIZE");
        OptionBuilder.hasArg();
        options.addOption(OptionBuilder.create("size"));

        OptionBuilder.withLongOpt("learning-steps");
        OptionBuilder.withDescription("Number of learning steps before resubstitution (default: " + defaultLearningSteps + ")");
        OptionBuilder.withArgName("STEPS");
        OptionBuilder.hasArg();
        options.addOption(OptionBuilder.create("ls"));

        OptionBuilder.withLongOpt("learning-epochs");
        OptionBuilder.withDescription("Number of learning epochs before test phase (default: " + defaultLearningEpochs + ")");
        OptionBuilder.withArgName("EPOCHS");
        OptionBuilder.hasArg();
        options.addOption(OptionBuilder.create("le"));

        OptionBuilder.withLongOpt("learning-rate");
        OptionBuilder.withDescription("Learning rate for backpropagation (default: " + defaultLearningRate + ")");
        OptionBuilder.withArgName("RATE");
        OptionBuilder.hasArg();
        options.addOption(OptionBuilder.create("lr"));

        OptionBuilder.withLongOpt("folds");
        OptionBuilder.withDescription("Number of folds for k-fold cross-validation (default: " + defaultFolds + ")");
        OptionBuilder.withArgName("FOLDS");
        OptionBuilder.hasArg();
        options.addOption(OptionBuilder.create("f"));

        OptionBuilder.withLongOpt("restart-count");
        OptionBuilder.withDescription("Number of restarts (default: " + defaultRestartCount + ")");
        OptionBuilder.withArgName("RESTARTS");
        OptionBuilder.hasArg();
        options.addOption(OptionBuilder.create("rs"));

        OptionBuilder.withLongOpt("grounding");
        OptionBuilder.withDescription("grounding variant (default: " + defaultGrounding + ")");
        OptionBuilder.withArgName("GROUNDING");
        OptionBuilder.hasArg();
        options.addOption(OptionBuilder.create("gr"));

        OptionBuilder.withLongOpt("activations");
        OptionBuilder.withDescription("lambda-kappa activation functions (default: " + defaultActivations + ")");
        OptionBuilder.withArgName("ACTIVATION");
        OptionBuilder.hasArg();
        options.addOption(OptionBuilder.create("ac"));

        OptionBuilder.withLongOpt("initialization");
        OptionBuilder.withDescription("weight initialization method (default: " + defaultInitialization + ")");
        OptionBuilder.withArgName("INITIALIZATION");
        OptionBuilder.hasArg();
        options.addOption(OptionBuilder.create("wi"));

        OptionBuilder.withLongOpt("batch");
        OptionBuilder.withDescription("Enable batch learning(RPROP) (default: off)");
        options.addOption(OptionBuilder.create("b"));

        OptionBuilder.withLongOpt("kappaAdaptiveOffset");
        OptionBuilder.withDescription("Enable kappa offset based on number of input literals (default: off)");
        OptionBuilder.withArgName("kappaAdaptiveOffset");
        OptionBuilder.hasArg();
        options.addOption(OptionBuilder.create("ko"));

        OptionBuilder.withLongOpt("lambdaAdaptiveOffset");
        OptionBuilder.withDescription("Enable lambda offset based on number of input literals (default: off)");
        OptionBuilder.withArgName("lambdaAdaptiveOffset");
        OptionBuilder.hasArg();
        options.addOption(OptionBuilder.create("lo"));

        OptionBuilder.withLongOpt("seed");
        OptionBuilder.withDescription("seed value (default: " + defaultSeed + ")");
        OptionBuilder.withArgName("SEED");
        OptionBuilder.hasArg();
        options.addOption(OptionBuilder.create("sd"));

        OptionBuilder.withLongOpt("dropout");
        OptionBuilder.withDescription("dropout rate (default: " + defaultDropoutRate + ")");
        OptionBuilder.withArgName("DROPOUT");
        OptionBuilder.hasArg();
        options.addOption(OptionBuilder.create("dr"));

        OptionBuilder.withLongOpt("SGD");
        OptionBuilder.withDescription("Stochastic grdient descend (default: " + defaultSGD + ")");
        OptionBuilder.hasArg();
        options.addOption(OptionBuilder.create("sgd"));

        OptionBuilder.withLongOpt("cumulativeSteps");
        OptionBuilder.withDescription("Cumulative number of learning steps (default: " + defaultCumSteps + " )");
        OptionBuilder.hasArg();
        options.addOption(OptionBuilder.create("cum"));

        OptionBuilder.withLongOpt("saving");
        OptionBuilder.withDescription("saving the best template after each bp-step over all examples (default: " + defaultSaving + " )");
        OptionBuilder.hasArg();
        options.addOption(OptionBuilder.create("save"));

        OptionBuilder.withLongOpt("learnDecay");
        OptionBuilder.withDescription("learning rate decay over time (default: " + defaultLearnDecay + " )");
        OptionBuilder.hasArg();
        options.addOption(OptionBuilder.create("lrd"));

        OptionBuilder.withLongOpt("embeddings");
        OptionBuilder.withDescription("Enable/load embeddings from a predefined csv file at ./in/embeddings.csv");
        OptionBuilder.hasArg();
        options.addOption(OptionBuilder.create("emb"));

        OptionBuilder.withLongOpt("debug");
        OptionBuilder.withDescription("Enable debug mode (=detailed output)");
        OptionBuilder.hasArg();
        options.addOption(OptionBuilder.create("debug"));

        OptionBuilder.withLongOpt("drawing");
        OptionBuilder.withDescription("Enable picture drawing/exporting into ./images");
        OptionBuilder.hasArg();
        options.addOption(OptionBuilder.create("draw"));

        OptionBuilder.withLongOpt("alldiff");
        OptionBuilder.withDescription("Differently named variables in a single rule must bind to different constants");
        OptionBuilder.hasArg();
        options.addOption(OptionBuilder.create("alldiff"));

        OptionBuilder.withLongOpt("bottomUp");
        OptionBuilder.withDescription("Use bottom-up grounder (from Ondrej) instead of top-down search (default)");
        OptionBuilder.hasArg();
        options.addOption(OptionBuilder.create("bug"));

        OptionBuilder.withLongOpt("compress");
        OptionBuilder.withDescription("Use compressed representation in ruleAgg neurons (Lambda)");
        OptionBuilder.hasArg();
        options.addOption(OptionBuilder.create("comp"));

        OptionBuilder.withLongOpt("output");
        OptionBuilder.withDescription("Results directory");
        OptionBuilder.hasArg();
        options.addOption(OptionBuilder.create("out"));

        return options;
    }

    public static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar lrnn.jar", options);
    }

    public static CommandLine parseArguments(String[] args) {
        Options options = getOptions();
        CommandLineParser parser = new PosixParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException exception) {
            Glogger.err(exception.getMessage());
            printHelp(options);
            return null;
        }

        return cmd;
    }

    public static void main(String[] args) {
//        args = "-r C:\\Users\\Gusta\\googledrive\\Github\\LRNN\\in\\mutagenesis\\3rules -e C:\\Users\\Gusta\\googledrive\\Github\\LRNN\\in\\mutagenesis\\examples -gr avg".split(" ");
        System.out.println("getParallelism=" + ForkJoinPool.commonPool().getParallelism());

        //setup all parameters and load all the necessary input files
        List<String[]> inputs = setupFromArguments(args);
        //create logger for all messages within the program
        Glogger.init();

        String[] test = inputs.get(0);
        String[] exs = inputs.get(1);
        String[] rules = inputs.get(2);
        String[] pretrainedRules = inputs.get(3);

        if (Global.internalValidation) {

            //Global.exporting = false;
            //Settings.folds = 2;
            double minError = 1;
            int minErrIndex = 0;
            CommandLine cmd = parseArguments(args);
            String rls = cmd.getOptionValue("r");
            for (int i = 1; i < 6; i++) {
                Settings.setRules(rls.replaceFirst("1", i + ""));
                Glogger.init();
                String[] rules2 = TextFileReader.readFile(rls.replaceFirst("1", i + ""), maxReadline);
                if (rules2.length == 0) {
                    Glogger.err("no rules for i:=" + i);
                    break;
                }
                LiftedDataset groundedDataset = createDataset(test, exs, rules2, pretrainedRules);
                double learnOn = learnOn(groundedDataset);
                if (learnOn < minError) {
                    minError = learnOn;
                    minErrIndex = i;
                }
            }
            Glogger.LogTrain("choosing the best internal validation template: " + minErrIndex + " with validation error " + minError);
            rules = TextFileReader.readFile(rls.replaceFirst("1", minErrIndex + ""), maxReadline);
            String tmp = cmd.getOptionValue("f", defaultFolds);
            Settings.setFolds(Integer.parseInt(tmp));
            Global.exporting = true;
        }

        //create ground networks dataset
        LiftedDataset groundedDataset = createDataset(test, exs, rules, pretrainedRules);

        //start learning
        learnOn(groundedDataset);
    }

    /**
     * parse commandline for all possible parameters
     *
     * @param cmd
     * @throws NumberFormatException
     */
    public static void setParameters(CommandLine cmd) throws NumberFormatException {
        //parsing command line options - needs external library commons-CLI
        String ground = cmd.getOptionValue("gr", defaultGrounding);
        Settings.setGrounding(ground);

        String activation = cmd.getOptionValue("ac", defaultActivations);
        Settings.setActivations(activation);

        String initialization = cmd.getOptionValue("wi", defaultInitialization);
        Settings.setInitials(initialization);

        Global.setKappaAdaptiveOffset(cmd.hasOption("ko"));

        String koffset = cmd.getOptionValue("ko", defaultKappaAdaptiveOffset);
        Settings.setKoffset(koffset);

        String loffset = cmd.getOptionValue("lo", defaultLambdaAdaptiveOffset);
        Settings.setLoffset(loffset);

        String seed = cmd.getOptionValue("sd", defaultSeed);
        Settings.setSeed(seed);

        String drop = cmd.getOptionValue("dr", defaultDropoutRate);
        Settings.setDropout(drop);

        String sgd = cmd.getOptionValue("sgd", defaultSGD);
        Settings.setSGD(sgd);

        String cum = cmd.getOptionValue("cum", defaultCumSteps);
        Settings.setCumSteps(cum);

        String save = cmd.getOptionValue("save", defaultSaving);
        Settings.setSave(save);

        String decay = cmd.getOptionValue("lrd", defaultLearnDecay);
        Settings.setLrDecay(decay);

        //Global.batchMode = cmd.hasOption("b");
        String tmp = cmd.getOptionValue("size", defaultMaxExamples);
        Settings.setMaxExamples(Integer.parseInt(tmp));

        tmp = cmd.getOptionValue("ls", defaultLearningSteps);
        Settings.setLearningSteps(Integer.parseInt(tmp));

        tmp = cmd.getOptionValue("le", defaultLearningEpochs);
        Settings.setLearningEpochs(Integer.parseInt(tmp));

        tmp = cmd.getOptionValue("lr", defaultLearningRate);
        Settings.setLearnRate(Double.parseDouble(tmp));

        tmp = cmd.getOptionValue("f", defaultFolds);
        Settings.setFolds(Integer.parseInt(tmp));

        tmp = cmd.getOptionValue("rs", defaultRestartCount);
        Settings.setRestartCount(Integer.parseInt(tmp));

        tmp = cmd.getOptionValue("emb", defaultEmbeddings);
        Settings.setEmbeddings(tmp);

        tmp = cmd.getOptionValue("debug", defaultDebug);
        Settings.setDebug(tmp);

        tmp = cmd.getOptionValue("draw", defaultDrawing);
        Global.setDrawing(tmp);

        tmp = cmd.getOptionValue("alldiff", defaultAlldiff);
        Global.setAlldiff(tmp);

        tmp = cmd.getOptionValue("bug", defaultBottomUp);
        Global.setBottomUp(tmp);

        tmp = cmd.getOptionValue("comp", defaultCompressed);
        Global.setCompressed(tmp);

        tmp = cmd.getOptionValue("out", defaultOutput);
        Settings.setResultsDir(tmp);
    }

    /**
     * creating some form of grounded dataset (learning process) after all
     * parameters are set
     *
     * @param test
     * @param exs
     * @param rules
     * @param pretrainedRules
     * @return
     */
    public static LiftedDataset createDataset(String[] test, String[] exs, String[] rules, String[] pretrainedRules) {

        //Glogger.process(Settings.getString());
        //---------------dataset-sample set creation
        LiftedDataset sampleSet = null;

        if (Global.loadGroundedDataset) {
            sampleSet = LiftedDataset.loadDataset(Settings.getDataset().replaceAll("-", "/") + ".ser");
        }

        if (sampleSet == null) {
            if (test == null || test.length == 0) {
                Glogger.info("no test set, will do " + Settings.folds + "-fold crossvalidation");
                sampleSet = new GroundedDataset(exs, rules, pretrainedRules);
            } else if (test != null && test.length == 0 && exs != null && exs.length != 0) {
                Glogger.info("test and train set provided, will do simple train-test evaluation as (1-fold) crossvalidation");
                sampleSet = new GroundedDataset(exs, test, rules, pretrainedRules);
            } else {
                Glogger.info("ONLY test set provided, will do just testing.");
                sampleSet = new GroundedDataset(null, test, rules, null);
            }
        }

        if (Global.fastVersion && !(sampleSet instanceof NeuralDataset)) {
            sampleSet = new NeuralDataset(sampleSet);
        }

        if (Global.saveGroundedDataset) {
            /*
             sampleSet.saveSampleSet(Settings.getDataset().replaceAll("-", "/") + "samplesLK.ser", sampleSet.sampleSplitter.samples);
            
             sampleSet.saveDataset(Settings.getDataset().replaceAll("-", "/") + "Groundeddataset.ser");
            
             sampleSet = new NeuralDataset(sampleSet);
             sampleSet.saveDataset(Settings.getDataset().replaceAll("-", "/") + "NeuralDataset.ser");
            
             sampleSet.saveSampleSet(Settings.getDataset().replaceAll("-", "/") + "samplesBoth.ser", sampleSet.sampleSplitter.samples);
             for (Sample sam : sampleSet.sampleSplitter.samples) {
             sam.makeMeSmall();
             }
             sampleSet.saveSampleSet(Settings.getDataset().replaceAll("-", "/") + "samplesNeural.ser", sampleSet.sampleSplitter.samples);
             sampleSet.saveDataset(Settings.getDataset().replaceAll("-", "/") + "NeuralDatasetSamples1.ser");
            
             sampleSet.network = new LightTemplate(sampleSet.network.sharedWeights, sampleSet.network.name2weight);
             */
            sampleSet.saveDataset(Settings.getDataset().replaceAll("-", "/") + ".ser");
        }
        return sampleSet;
    }

    public static double learnOn(LiftedDataset sampleSet) {
        //main
        Crossvalidation cross;
        if (Global.fastVersion) {
            cross = new NeuralCrossvalidation(sampleSet);
        } else {
            cross = new Crossvalidation(sampleSet.sampleSplitter);
        }

        return cross.crossvalidate(sampleSet);
    }

    /**
     * hack to end with lambda if there is no sigmoid on finalKappa, so that we
     * end with a sigmoid's output
     *
     * @param rules
     * @return
     */
    private static String[] addFinalLambda(String[] rules) {

        String[] rls = new String[rules.length + 1];
        for (int i = 0; i < rules.length; i++) {
            rls[i] = rules[i];
        }
        String fin = rls[rls.length - 2].substring(rls[rls.length - 2].indexOf(" "), rls[rls.length - 2].indexOf(")") + 1);
        rls[rls.length - 1] = "finalLambda(a) :- " + fin + ".";

        return rls;
    }

    /**
     * setup all parameters and load all the necessary input files
     *
     * @param args
     * @return
     */
    public static List<String[]> setupFromArguments(String[] args) {
        List<String[]> inputs = new LinkedList<>();

        CommandLine cmd = parseArguments(args);
        if (cmd == null) {
            return null;
        }

        setParameters(cmd);

        //---------------------loading all input files
        //get examples from file
        String dataset = cmd.getOptionValue("e");

        String[] exs = getExamples(dataset);

        //separate test set?
        String[] test = null;
        String tt = cmd.getOptionValue("test");
        if (tt != null) {
            Settings.setTestSet(tt);
            test = TextFileReader.readFile(tt, maxReadline);
            if (test.length == 0) {
                Glogger.err("Test set is empty or invalid!");
            }
        }

        //get rules one by one from a file
        String rls = cmd.getOptionValue("r");
        Settings.setRules(rls);
        String[] rules = TextFileReader.readFile(rls, maxReadline);
        if (rules.length == 0) {
            Glogger.err("no rules");
        }

        //we want sigmoid at the output, not identity (for proper error measurement)
        if (Global.getKappaActivation() == Global.activationSet.id) {
            if (rules[rules.length - 1].matches("^[0-9\\.]+.*")) {  //does it end with Kappa line?
                rules = addFinalLambda(rules);  //a hack to end with lambda
            }
        }

        //pretrained template with some lifted literals in common (will be mapped onto new template)
        String pretrained = cmd.getOptionValue("t");
        Settings.setPretrained(pretrained);
        String[] pretrainedRules = TextFileReader.readFile(pretrained, maxReadline);
        if (pretrainedRules != null) {
            Glogger.out("pretrained= " + pretrained + " of length: " + pretrainedRules.length);
        }

        inputs.add(test);
        inputs.add(exs);
        inputs.add(rules);
        inputs.add(pretrainedRules);

        return inputs;
    }

    /**
     * load examples from a file
     *
     * @param exampleFile
     * @return
     */
    public static String[] getExamples(String exampleFile) {
        String[] exs = null;
        Settings.setDataset(exampleFile);
        if (Global.multiLine) {
            exs = TextFileReader.readFileMultiline(exampleFile, maxReadline);
        } else {
            exs = TextFileReader.readFile(exampleFile, maxReadline);
        }

        if (exs.length == 0) {
            Glogger.err("no examples");
            return null;
        }
        return exs;
    }
}
