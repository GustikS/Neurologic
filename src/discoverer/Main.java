package discoverer;

import discoverer.global.Global;
import discoverer.global.FileToStringListJava6;
import discoverer.global.Glogger;
import discoverer.global.Settings;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 * Main class Params handling and calling appropriate methods
 */
public class Main {

    //cutoff on example number
    private static final String defaultMaxExamples = "100000";  //we can decrease the overall number of examples (stratified) for speedup
    //
    public static String defaultLearningSteps = "50";  //learnSteps per epocha
    public static String defaultLearningEpochs = "1";  //learn epochae = grounding cycles
    //  learnEpochae * LearningSteps = learning steps for AVG variant
    private static final String defaultFolds = "1"; // 1 = training only
    private static final String defaultLearningRate = "0.3"; //0.05 default from Vojta, it's good to increase, reasonable <0.1 , 1>
    //learnRate = 5 -> gets stuck very soon (<=10 steps) around 23% acc (+ jumping), unable to learn
    //learnRate = 1 -> plato around 600 steps with 10% acc (+ BIG jumping +-3%, but also +10%)
    //learnRate = 0.5 -> plato around 1000 steps with 8% acc (+ jumping +-3%) -> can break into some best results (0.3%) with saving
    //learnRate = 0.3 -> plato around 1000 steps with 8% acc, just a very mild jumping, very good apparent behavior
    //learnRate = 0.1 -> stable plato around 600 steps with 11% (no jumping, very stable, stuck)
    public static final String defaultRestartCount = "1";  //#restarts per fold
    //max-avg
    public static final String defaultGrounding = "avg";    //avg or max
    public static String defaultActivations = "sig_id";    //lambda_kappa activation functions
    public static String defaultInitialization = "longtail";    //handmade = 0.9:0.1
    //offsets
    public static boolean defaultKappaAdaptiveOffsetOn = false; //kappa offset is initialized based on number of input lambdas
    public static String defaultKappaAdaptiveOffset = "0";  //must stay zero as default if defaultKappaAdaptiveOffsetOn = false    
    public static String defaultLambdaAdaptiveOffset = "0"; //lambda offset add to the -1* (number of input kappas)

    public static String defaultSeed = "1"; //seeds the whole algorithm (shuffling, etc.), should make it completely deterministic

    private static String defaultSaving = "1"; // >0 => saving of template is ON after each bp-step (minibatch)  --> adds 10% extra computation time

    public static String defaultDropoutRate = "0";  // >0 => dropout is ON, at the particular rate
    private static String defaultSGD = "1";     // >0 => stochastic gradient descend is ON
    private static String defaultCumSteps = "0"; // "on" or number of steps, <= 0 => OFF
    private static String defaultLearnDecay = "0"; // >0 => learnRate decay strategy is ON
    private static int maxReadline = 10000; //cut-of reading input files (not used)

    public static Options getOptions() {
        Options options = new Options();
        OptionBuilder.withLongOpt("rules");
        OptionBuilder.hasArg(true);
        OptionBuilder.isRequired(true);
        OptionBuilder.withArgName("RULE-FILE");
        OptionBuilder.withDescription("File with rules");
        options.addOption(OptionBuilder.create("r"));

        OptionBuilder.withLongOpt("pretrained");
        OptionBuilder.hasArg();
        OptionBuilder.withArgName("PRETRAINED");
        OptionBuilder.withDescription("File with network template");
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

        return options;
    }

    public static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar discoverer.jar", options);
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
        CommandLine cmd = parseArguments(args);
        if (cmd == null) {
            return;
        }

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

        //parsing command line options - needs external library commons-CLI
        Global.setBatch(cmd.hasOption("b") ? Global.batch.YES : Global.batch.NO);

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

        //get examples one by one from file
        String dataset = cmd.getOptionValue("e");
        Settings.setDataset(dataset);
        String[] exs = FileToStringListJava6.convert(dataset, maxReadline);
        if (exs.length == 0) {
            Glogger.err("no examples");
            return;
        }

        String[] test=null;
        String tt = cmd.getOptionValue("test");
        if (tt != null) {
            Settings.setTestSet(tt);
            test = FileToStringListJava6.convert(tt, maxReadline);
        }

        //get rules one by one from file
        String rls = cmd.getOptionValue("r");
        Settings.setRules(rls);
        String[] rules = FileToStringListJava6.convert(rls, maxReadline);
        if (rules.length ==0){
            Glogger.err("no rules");
        }

        if (Global.getKappaActivation() == Global.activationSet.id) {
            rules = addFinalLambda(rules);  //a hack to end with lambda
        }

        String pretrained = cmd.getOptionValue("t");
        Settings.setPretrained(pretrained);
        String[] pretrainedRules = FileToStringListJava6.convert(pretrained, maxReadline);
        Glogger.out("pretrained= " + pretrained + " of length: " + pretrainedRules.length);

        Glogger.init();

        Glogger.process(Settings.getString());

        Crossvalidation solver = new Crossvalidation();

        //main solver method
        if (test == null) {
            Glogger.info("no test set, will do crossvalidation");
            solver.crossValidate(exs, rules, pretrainedRules);
        } else {
            Glogger.info("test set provided, will do simple evaluation");
            solver.trainTest(exs, test, rules);
        }
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
        rls[rls.length - 1] = "finalLambda :- " + fin + ".";

        return rls;
    }
}
