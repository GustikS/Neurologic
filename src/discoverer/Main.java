package discoverer;

import discoverer.global.Global;
import discoverer.global.Batch;
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
    private static final String defaultMaxExample = "100000";
    //
    public static String defaultLearningSteps = "10";
    //
    private static String defaultLearningEpochs = "7";
    //crossval
    private static final String defaultFolds = "5";
    private static final String defaultLearningRate = "0.05";
    private static final String defaultRestartCount = "3";
    //max-avg
    public static final String defaultGrounding = "max";
    public static String defaultActivation = "sig_sig";
    public static String defaultInitialization = "handmade";
    //offsets
    public static boolean defaultKappaAdaptiveOffsetOn = false;
    public static String defaultLambdaAdaptiveOffset = "1";
    public static String defaultKappaAdaptiveOffset = "0";
    public static String defaultSeed = "1";

    //public static boolean avg = true;
    public static Options getOptions() {
        Options options = new Options();
        OptionBuilder.withLongOpt("rules");
        OptionBuilder.hasArg(true);
        OptionBuilder.isRequired(true);
        OptionBuilder.withArgName("RULE-FILE");
        OptionBuilder.withDescription("File with rules");
        options.addOption(OptionBuilder.create("r"));

        OptionBuilder.withLongOpt("examples");
        OptionBuilder.hasArg();
        OptionBuilder.isRequired();
        OptionBuilder.withArgName("EXAMPLE-FILE");
        OptionBuilder.withDescription("File with examples");
        options.addOption(OptionBuilder.create("e"));

        OptionBuilder.withLongOpt("example-size");
        OptionBuilder.withDescription("Maximal size of example to pick (default: " + defaultMaxExample + ")");
        OptionBuilder.withArgName("SIZE");
        OptionBuilder.hasArg();
        options.addOption(OptionBuilder.create("s"));

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
        OptionBuilder.withDescription("lambda-kappa activation functions (default: " + defaultActivation + ")");
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
        OptionBuilder.withDescription("Number of restarts (default: " + 1 + ")");
        OptionBuilder.withArgName("seed");
        OptionBuilder.hasArg();
        options.addOption(OptionBuilder.create("sd"));

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
        Global.setGrounding(ground);

        String activation = cmd.getOptionValue("ac", defaultActivation);
        Global.setActivations(activation);

        String initialization = cmd.getOptionValue("wi", defaultInitialization);
        Global.setInitialization(initialization);

        Global.kappaAdaptiveOffset = cmd.hasOption("ko");

        String koffset = cmd.getOptionValue("ko", defaultKappaAdaptiveOffset);
        Global.initKappaAdaptiveOffset = Double.parseDouble(koffset);

        String loffset = cmd.getOptionValue("lo", defaultLambdaAdaptiveOffset);
        Global.initLambdaAdaptiveOffset = Double.parseDouble(loffset);
        
        String seed = cmd.getOptionValue("sd", defaultSeed);
        Global.seed = Integer.parseInt(seed);

        //parsing command line options - needs external library commons-CLI
        Batch batch = cmd.hasOption("b") ? Batch.YES : Batch.NO;

        String tmp = cmd.getOptionValue("s", defaultMaxExample);
        int maxLine = Integer.parseInt(tmp);

        tmp = cmd.getOptionValue("ls", defaultLearningSteps);
        int steps = Integer.parseInt(tmp);

        tmp = cmd.getOptionValue("le", defaultLearningEpochs);
        int epochs = Integer.parseInt(tmp);

        tmp = cmd.getOptionValue("lr", defaultLearningRate);
        double learnRate = Double.parseDouble(tmp);

        tmp = cmd.getOptionValue("f", defaultFolds);
        int folds = Integer.parseInt(tmp);

        tmp = cmd.getOptionValue("rs", defaultRestartCount);
        int restartCount = Integer.parseInt(tmp);

        //get examples one by one from file
        String[] ex = FileToStringListJava6.convert(cmd.getOptionValue("e"), maxLine);

        //get rules one by one from file
        String[] rules = FileToStringListJava6.convert(cmd.getOptionValue("r"), Integer.MAX_VALUE);
        if (Global.kappaActivation == Global.activationSet.id) {
            rules = addFinalLambda(rules);  //a hack to end with lambda
        }

        Settings.create(ground, folds, steps, epochs, restartCount, learnRate, activation, initialization, loffset, koffset, seed);
        Glogger.init();

        Crossvalidation solver = new Crossvalidation();

        //main solver method
        solver.solve(folds, rules, ex, batch, steps, epochs, restartCount, learnRate);
    }

    private static String[] addFinalLambda(String[] rules) {

        String[] rls = new String[rules.length + 1];
        for (int i = 0; i < rules.length; i++) {
            rls[i] = rules[i];
        }
        rls[rls.length - 1] = "finalLambda :- finalKappa(DMY).";

        return rls;
    }
}
