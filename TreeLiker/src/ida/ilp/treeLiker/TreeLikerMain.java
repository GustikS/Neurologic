/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ida.ilp.treeLiker;

import ida.ilp.logic.Clause;
import ida.ilp.logic.Literal;
import ida.ilp.logic.PrologList;
import ida.ilp.logic.io.Prolog2PseudoPrologReader;
import ida.utils.CommandLine;
import ida.utils.StringUtils;
import ida.utils.Sugar;
import ida.utils.VectorUtils;
import ida.utils.collections.MultiMap;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static ida.ilp.treeLiker.Algorithm.*;
/**
 * Class providing the command-line interface to algorithms RelF, HiFi and Poly.
 * 
 * @author admin
 */
public class TreeLikerMain {
    
    /**
     * 
     * @param a
     * @throws IOException
     */
    public static void main(String a[]) throws IOException {
        Map<String,String> params = CommandLine.parseParams(a);
        if (params.containsKey("-batch")){
            BufferedReader br = new BufferedReader(new FileReader(params.get("-batch")));
            Map<String,String> batchParams_set = new HashMap<String,String>();
            MultiMap<String,String> batchParams_add = new MultiMap<String,String>();
            Propositionalization prop = new Propositionalization();
            int maxSize = Integer.MAX_VALUE;
            Algorithm mode = RELF;
            final int PSEUDOPROLOG = 1, PROLOG = 2;
            int inputType = PSEUDOPROLOG;
            for (String line : Sugar.readLines(br)){
                line = line.trim();
                if (line.indexOf('.') > -1 && line.lastIndexOf('.') == line.length()-1){
                    line = line.substring(0, line.length()-1);
                }
                if (line.indexOf("%") > -1){
                    line = line.substring(0, line.indexOf("%"));
                }
                if (line.length() > 0){
                    Clause c  = Clause.parse(line);
                    for (Literal l : c.literals()){
                        if (l.predicate().equalsIgnoreCase("print")){
                            System.out.println(">> "+l.get(0).toString());
                        } else if (l.predicate().equalsIgnoreCase("get")){
                            String usrInput = CommandLine.read();
                            batchParams_set.put(l.get(0).name(), usrInput);
                        } else if (l.predicate().equalsIgnoreCase("set")){
                            if (l.get(1) instanceof PrologList){
                                PrologList pl = (PrologList)l.get(1);
                                StringBuilder sb = new StringBuilder();
                                for (int i = 0; i < pl.countItems(); i++){
                                    sb.append(pl.get(i));
                                    if (i < pl.countItems()-1){
                                        sb.append(", ");
                                    }
                                }
                                if (sb.charAt(0) == '\'' && sb.charAt(sb.length()-1) == '\''){
                                    batchParams_set.put(l.get(0).name(), sb.substring(1, sb.length()-1));
                                } else {
                                    batchParams_set.put(l.get(0).name(), sb.toString());
                                }
                            } else {
                                if (l.get(1).name().charAt(0) == '\'' && l.get(1).name().charAt(l.get(1).name().length()-1) == '\''){
                                    batchParams_set.put(l.get(0).name(), l.get(1).name().substring(1, l.get(1).name().length()-1));
                                } else {
                                    batchParams_set.put(l.get(0).name(), l.get(1).name().toString());
                                }
                            }
                        } else if (l.predicate().equalsIgnoreCase("add")){
                            if (l.get(1) instanceof PrologList){
                                PrologList pl = (PrologList)l.get(1);
                                StringBuilder sb = new StringBuilder();
                                for (int i = 0; i < pl.countItems(); i++){
                                    sb.append(pl.get(i));
                                    if (i < pl.countItems()-1){
                                        sb.append(", ");
                                    }
                                }
                                if (sb.charAt(0) == '\'' && sb.charAt(sb.length()-1) == '\''){
                                    batchParams_add.put(l.get(0).name(), sb.substring(1, sb.length()-1));
                                } else {
                                    batchParams_add.put(l.get(0).name(), sb.toString());
                                }
                            } else {
                                if (l.get(1).name().charAt(0) == '\'' && l.get(1).name().charAt(l.get(1).name().length()-1) == '\''){
                                    batchParams_add.put(l.get(0).name(), l.get(1).name().substring(1, l.get(1).name().length()-1));
                                } else {
                                    batchParams_add.put(l.get(0).name(), l.get(1).name().toString());
                                }
                            }
                        } else if (l.predicate().equalsIgnoreCase("work") || l.predicate().equalsIgnoreCase("whatever")){
                            String examples = batchParams_set.get("examples");
                            //if (!new File(examples).exists()){
                                examples = new File(params.get("-batch")).getAbsoluteFile().getParent()+File.separator+examples;
                            //}
                            String template = batchParams_set.get("template");
                            if (template == null){
                                System.out.println("Template was not found iterable "+params.get("-batch"));
                            }
                            String cv = batchParams_set.get("output_type");
                            if (cv == null){
                                cv = "single";
                            }
                            String hreduction = batchParams_set.get("use_hreduction");
                            if (hreduction == null){
                                hreduction = batchParams_set.get("use_h_reduction");
                            }
                            if (hreduction != null && hreduction.equalsIgnoreCase("true")){
                                Settings.USE_H_REDUCTION = true;
                            } else if (hreduction != null && hreduction.equalsIgnoreCase("false")){
                                Settings.USE_H_REDUCTION = false;
                            }
                            String redundancy = batchParams_set.get("use_redundancy");
                            if (redundancy != null && redundancy.equalsIgnoreCase("true")){
                                Settings.USE_REDUNDANCY_FILTERING = true;
                            } else if (redundancy != null && redundancy.equalsIgnoreCase("false")){
                                Settings.USE_REDUNDANCY_FILTERING = false;
                            }
                            String coveredClass = batchParams_set.get("covered_class");
                            String output = batchParams_set.get("output");
                            if (output == null){
                                System.out.println("Warning! Parameter output, which is where the propositionalized table(s) should be stored, is missing iterable "+params.get("-batch"));
                            }
                            if (output != null && !new File(output).exists()){
                                output = new File(params.get("-batch")).getAbsoluteFile().getParent()+File.separator+output;
                            }
                            String minFrequency = batchParams_set.get("min_frequency");
                            if (minFrequency == null){
                                minFrequency = batchParams_set.get("minimum_frequency");
                            }
                            if (minFrequency != null && StringUtils.isNumeric(minFrequency)){
                                prop.setMinFrequency(Double.parseDouble(minFrequency));
                            }
                            String regressionRedundancyTolerance = batchParams_set.get("regression_redundancy_tolerance");
                            if (regressionRedundancyTolerance != null){
                                if (StringUtils.isInteger(regressionRedundancyTolerance)){
                                    Settings.REGRESSION_REDUNDANCY_TOLERANCE = Integer.parseInt(regressionRedundancyTolerance);
                                } else {
                                    System.out.println("Parameter regression_redundancy_tolerance must be an integer.");
                                }
                            }
                            String algorithm = batchParams_set.get("algorithm");
                            if (algorithm != null && algorithm.equalsIgnoreCase("relf")){
                                mode = RELF;
                            } else if (algorithm != null && (algorithm.equalsIgnoreCase("relf_grounding_counting") || algorithm.equalsIgnoreCase("relf_groundings_counting"))){
                                mode = RELF_GROUNDING_COUNTING;
                            } else if (algorithm != null && algorithm.equalsIgnoreCase("hifi")){
                                mode = HIFI;
                            } else if (algorithm != null && (algorithm.equalsIgnoreCase("hifi_grounding_counting") || algorithm.equalsIgnoreCase("hifi_groundings_counting"))){
                                mode = HIFI_GROUNDING_COUNTING;
                            } else if (algorithm != null && algorithm.equalsIgnoreCase("poly")){
                                mode = POLY;
                            } else if (algorithm != null && (algorithm.equalsIgnoreCase("poly_grounding_counting") || algorithm.equalsIgnoreCase("poly_groundings_counting"))){
                                mode = POLY_GROUNDING_COUNTING;
                            } else if (algorithm != null && (algorithm.equalsIgnoreCase("relf_x") || algorithm.equalsIgnoreCase("relfx"))){
                                mode = RELF_X;
                            } else if (algorithm != null && (algorithm.equalsIgnoreCase("treelike_example_reduction") || algorithm.equalsIgnoreCase("lesa"))){
                                mode = LESA;
                            } else if (algorithm != null && algorithm.equalsIgnoreCase("lea")){
                                mode = LEA;
                            } else if (algorithm != null && (algorithm.equalsIgnoreCase("treelike_bull") || algorithm.equalsIgnoreCase("treelikebull"))){
                                mode = TREELIKE_BULL;
                            } else if (algorithm != null && (algorithm.equalsIgnoreCase("theta_bull") || algorithm.equalsIgnoreCase("thetabull"))) {
                                mode = THETA_BULL;
                            }
                            if (batchParams_set.containsKey("use_sampling")){
                                if (batchParams_set.get("use_sampling").equalsIgnoreCase("true")){
                                    prop.setUseSampling(true);
                                } else {
                                    prop.setUseSampling(false);
                                }
                            }
                            if (batchParams_set.containsKey("processors")){
                                Settings.PROCESSORS = Integer.parseInt(batchParams_set.get("processors"));
                            }
                            if (batchParams_set.containsKey("num_samples") && StringUtils.isInteger(batchParams_set.get("num_samples"))){
                                prop.setNumSamples(Integer.parseInt(batchParams_set.get("num_samples")));
                            }
                            if (batchParams_set.containsKey("sample_size") && StringUtils.isInteger(batchParams_set.get("sample_size"))){
                                prop.setSampleSize(Integer.parseInt(batchParams_set.get("sample_size")));
                            }
                            boolean useJustFirstExample = false;
                            if (batchParams_set.containsKey("use_just_first_example") && batchParams_set.get("use_just_first_example").equalsIgnoreCase("true")){
                                useJustFirstExample = true;
                            }
                            String size = batchParams_set.get("max_size");
                            if (size == null){
                                size = batchParams_set.get("maximum_size");
                            }
                            String normalizationFactor = batchParams_set.get("normalization_factor");
                            if (normalizationFactor != null){
                                String normF = normalizationFactor;
                                if (normF.contains("[")){
                                    normF = normF.substring(normF.indexOf("[")+1);
                                }
                                if (normF.contains("]")){
                                    normF = normF.substring(0, normF.lastIndexOf("]"));
                                }
                                prop.setNormalizationFactor(Block.parse(normF));
                            }
                            
                            if (batchParams_set.containsKey("verbosity")){
                                Settings.VERBOSITY = Integer.parseInt(batchParams_set.get("verbosity").trim());
                            }
                            if (batchParams_set.containsKey("max_degree")){
                                prop.setMaxDegree(Integer.parseInt(batchParams_set.get("max_degree")));
                            }
                            if (batchParams_set.containsKey("transduction")){
                                prop.setUseTransduction(Boolean.parseBoolean(batchParams_set.get("transduction")));
                            }
                            if (batchParams_set.containsKey("transductive")){
                                prop.setUseTransduction(Boolean.parseBoolean(batchParams_set.get("transductive")));
                            }
                            if (batchParams_set.containsKey("max_lgg_size")){
                                prop.setMaxLggSize(Integer.parseInt(batchParams_set.get("max_lgg_size")));
                            }
                            if (batchParams_set.containsKey("maxlggsize")){
                                prop.setMaxLggSize(Integer.parseInt(batchParams_set.get("maxlggsize")));
                            }
                            if (size != null){
                                if (StringUtils.isNumeric(size)){
                                    maxSize = (int)Double.parseDouble(size);
                                } else {
                                    System.out.println("Warning: max_size must be an integer!");
                                }
                            }
                            String inputFormat = batchParams_set.get("input_format");
                            if (inputFormat != null){
                                if (inputFormat.equalsIgnoreCase("pseudoprolog") || inputFormat.equalsIgnoreCase("pseudo_prolog")){
                                    inputType = PSEUDOPROLOG;
                                } else if (inputFormat.equalsIgnoreCase("prolog")){
                                    inputType = PROLOG;
                                }
                            }
                            String foldsCount = batchParams_set.get("folds_count");
                            if (foldsCount == null){
                                foldsCount = batchParams_set.get("num_folds");
                            }
                            prop.setConstructFeaturesOnlyFromFirstExample(useJustFirstExample);
                            long m1 = System.currentTimeMillis();
                            //TODO example reductions iterable prolog format, so far works only with pseudoprolog
                            if (mode == TREELIKE_BULL || mode == THETA_BULL){
//                                List<Clause> positiveExamples = new ArrayList<Clause>();
//                                List<Clause> negativeExamples = new ArrayList<Clause>();
//                                if (examples != null){
//                                    List<Pair<Clause,String>> examplesList = null;
//                                    if (inputType == PROLOG){
//                                        examplesList = PseudoPrologParser.read(new Prolog2PseudoPrologReader(new FileReader(examples)));
//                                    } else {
//                                        examplesList = PseudoPrologParser.read(new FileReader(examples));
//                                    }
//                                    for (Pair<Clause,String> pair : examplesList){
//                                        if (pair.s.equals("+")){
//                                            positiveExamples.add(pair.r);
//                                        } else {
//                                            negativeExamples.add(pair.r);
//                                        }
//                                    }
//                                }
//                                Bull bull = new Bull(template);
//                                if (mode == TREELIKE_BULL){
//                                    bull.setPresubsumption(PresubsumptionType.TREELIKE_PRESUBSUMPTION);
//                                } else if (mode == THETA_BULL){
//                                    bull.setPresubsumption(PresubsumptionType.THETA_SUBSUMPTION);
//                                }
//                                if (batchParams_set.containsKey("bull:sample_size")){
//                                    int sampleSize = Integer.parseInt(batchParams_set.get("bull:sample_size"));
//                                    bull.setNumSampledExamplesInSBull(sampleSize);
//                                }
//                                if (batchParams_set.containsKey("bull:repeats")){
//                                    int bullRepeats = Integer.parseInt(batchParams_set.get("bull:repeats"));
//                                    bull.setRepeats(bullRepeats);
//                                }
//                                if (batchParams_set.containsKey("bull:iters")){
//                                    int bullIters = Integer.parseInt(batchParams_set.get("bull:iters"));
//                                    bull.setItersPerSingleClause(bullIters);
//                                }
//                                if (batchParams_set.containsKey("bull:max_neg_covered")){
//                                    double maxNegCovered = Double.parseDouble(batchParams_set.get("bull:max_neg_covered"));
//                                    bull.setMaxPctNegCovered(maxNegCovered);
//                                }
//                                if (batchParams_set.containsKey("bull:min_pos_covered")){
//                                    double minPosCovered = Double.parseDouble(batchParams_set.get("bull:min_pos_covered"));
//                                    bull.setMinPctPosCovered(minPosCovered);
//                                }
//                                if (batchParams_set.containsKey("bull:max_lgg_size")){
//                                    int maxLggSize = Integer.parseInt(batchParams_set.get("bull:max_lgg_size"));
//                                    bull.setMaxLGGSize(maxLggSize);
//                                }
//                                if (batchParams_set.containsKey("bull:max_subsequent_failures")){
//                                    int maxSubsequentFailures = Integer.parseInt(batchParams_set.get("bull:max_subsequent_failures"));
//                                    bull.setMaxSubsequentFailures(maxSubsequentFailures);
//                                }
//                                if (batchParams_set.containsKey("bull:connected")){
//                                    boolean constructConnected = Boolean.parseBoolean(batchParams_set.get("bull:connected"));
//                                    bull.setCreateOnlyConnectedHypotheses(constructConnected);
//                                }
//                                if (batchParams_add.containsKey("bull:subsumption_constraint")){
//                                    List<Clause> subsumptionConstraints = new ArrayList<Clause>();
//                                    for (String param : batchParams_add.get("bull:subsumption_constraints")){
//                                        subsumptionConstraints.add(Clause.parse(param));
//                                    }
//                                    bull.addSubsumptionConstraint(subsumptionConstraints);
//                                }
//                                if (cv.equalsIgnoreCase("cv") || cv.equalsIgnoreCase("cross_validation") || cv.equalsIgnoreCase("cross-validation")){
//                                    BullCrossValidation bcv = new BullCrossValidation(positiveExamples, negativeExamples);
//                                    if (batchParams_set.containsKey("bull:seed")){
//                                        bcv.setSeed(Integer.parseInt(batchParams_set.get("bull:seed")));
//                                    }
//                                    if (foldsCount != null){
//                                        bcv.setNumFolds(Integer.parseInt(foldsCount));
//                                    }
//                                    bcv.crossValidate(bull);
//                                    System.out.println("BULL: Cross-validated accuracy: "+bcv.getAvgAccuracy()+" +- "+bcv.getStdDev()+"\n\n\n");
//                                } else if (cv.equalsIgnoreCase("single") || cv.equalsIgnoreCase("train")){
//                                    BullClassifier bullClassifier = bull.learn(positiveExamples, negativeExamples);
//                                    PrintWriter pw = new PrintWriter(new FileWriter(output));
//                                    pw.println("LEARNED THEORIES: ");
//                                    int i = 0;
//                                    for (List<Clause> theory : bullClassifier.theories()){
//                                        for (Clause hypo : theory){
//                                            pw.println("H_"+i+" = "+hypo+"\n");
//                                            i++;
//                                        }
//                                    }
//                                    pw.close();
//                                } else if (cv.equalsIgnoreCase("train_test") || cv.equals("train-test")){
//                                    System.out.println("Train-test not supported yet for BULL.");
//                                }
                            } else if (mode == LESA || mode == LEA){
//                                if (examples == null){
//                                    System.out.println("Parameter examples, which points to the file with examples, is missing iterable "+params.get("-batch"));
//                                    System.exit(0);
//                                }
//                                File outputFile = new File(output);
//                                File parent = outputFile.getAbsoluteFile().getParentFile();
//                                if (parent != null && !parent.exists()){
//                                    parent.mkdirs();
//                                }
//                                Reader examplesReader = new FileReader(examples);
//                                if (inputType == PROLOG){
//                                    examplesReader = new Prolog2PseudoPrologReader(examplesReader);
//                                }
//                                ExampleReductions ers = new ExampleReductions(template);
//                                Writer outputWriter = new FileWriter(output);
//                                if (mode == LESA){
//                                    ers.reduceLearningExamplesUsingLESA(examplesReader, outputWriter);
//                                } else if (mode == LEA){
//                                    ers.reduceLearningExamplesUsingLEA(examplesReader, outputWriter);
//                                }
//                                outputWriter.close();
                            } else if (cv.equalsIgnoreCase("single")){
                                if (examples == null){
                                    System.out.println("Parameter examples, which points to the file with examples, is missing iterable "+params.get("-batch"));
                                    System.exit(0);
                                }
                                File outputFile = new File(output);
                                File parent = outputFile.getAbsoluteFile().getParentFile();
                                if (parent != null && !parent.exists()){
                                    parent.mkdirs();
                                }
                                Reader examplesReader = new FileReader(examples);
                                if (inputType == PROLOG){
                                    examplesReader = new Prolog2PseudoPrologReader(examplesReader);
                                }
                                if (mode == RELF){
                                    if (coveredClass != null){
                                        prop.setRelfCoveredClasses(Sugar.<String>set(coveredClass));
                                    }
                                    Table<String,String> table = prop.relf(template, examplesReader);
                                    if (Settings.USE_REDUNDANCY_FILTERING){
                                        table.saveWithFiltering(new FileWriter(output));
                                    } else {
                                        table.saveWithoutFiltering(new FileWriter(output));
                                    }
                                } else if (mode == POLY || mode == POLY_GROUNDING_COUNTING){
                                    Table<String,String> table = prop.poly(template, maxSize, examplesReader);
                                    
                                    if (Settings.USE_REDUNDANCY_FILTERING){
                                        table.saveWithFiltering(new FileWriter(output));
                                    } else {
                                        table.saveWithoutFiltering(new FileWriter(output));
                                    }
                                } else if (mode == RELF_GROUNDING_COUNTING){
                                    if (coveredClass != null){
                                        prop.setRelfCoveredClasses(Sugar.<String>set(coveredClass));
                                    }
                                    Table<String,String> table = prop.relf(template, examplesReader, RELF_GROUNDING_COUNTING);
                                    if (Settings.USE_REDUNDANCY_FILTERING){
                                        table.saveWithFiltering(new FileWriter(output));
                                    } else {
                                        table.saveWithoutFiltering(new FileWriter(output));
                                    }
                                } else if (mode == HIFI || mode == HIFI_GROUNDING_COUNTING){
                                    Table<String,String> table = prop.hifi(template, maxSize, examplesReader, mode);
                                    if (Settings.USE_REDUNDANCY_FILTERING){
                                        table.saveWithFiltering(new FileWriter(output));
                                    } else {
                                        table.saveWithoutFiltering(new FileWriter(output));
                                    }
                                } else if (mode == RELF_X){
                                    if (coveredClass != null){
                                        prop.setRelfCoveredClasses(Sugar.<String>set(coveredClass));
                                    }
                                    Table<String,String> table = prop.relf_x(template, examplesReader);
                                    if (Settings.USE_REDUNDANCY_FILTERING){
                                        table.saveWithFiltering(new FileWriter(output));
                                    } else {
                                        table.saveWithoutFiltering(new FileWriter(output));
                                    }
                                } //else if (mode == )
                            } else if (cv.equalsIgnoreCase("train_test") || cv.equalsIgnoreCase("traintest")){
                                if (mode == RELF || mode == RELF_GROUNDING_COUNTING){
                                    if (coveredClass != null){
                                        prop.setRelfCoveredClasses(Sugar.<String>set(coveredClass));
                                    }
                                }
                                String trainSet = batchParams_set.get("train_set");
                                String testSet = batchParams_set.get("test_set");
                                if (trainSet != null && testSet != null){
                                    if (trainSet.startsWith("[") && testSet.startsWith("[")){
                                        Reader examplesReader = new FileReader(examples);
                                        if (inputType == PROLOG){
                                            examplesReader = new Prolog2PseudoPrologReader(examplesReader);
                                        }
                                        prop.trainTest(template, maxSize, VectorUtils.parseIntegerArray(trainSet), VectorUtils.parseIntegerArray(testSet), examplesReader, output, mode);
                                    } else {
                                        String trainSetPath = new File(params.get("-batch")).getAbsoluteFile().getParent()+File.separator+trainSet;
                                        String testSetPath = new File(params.get("-batch")).getAbsoluteFile().getParent()+File.separator+testSet;
                                        Reader trainSetReader = new FileReader(trainSetPath);
                                        Reader testSetReader = new FileReader(testSetPath);
                                        if (inputType == PROLOG){
                                            trainSetReader = new Prolog2PseudoPrologReader(trainSetReader);
                                            testSetReader = new Prolog2PseudoPrologReader(testSetReader);
                                        }
                                        prop.trainTest(template, maxSize, trainSetReader, testSetReader, output, mode);
                                    }
                                }
                            } else if (cv.equalsIgnoreCase("cv") || cv.equalsIgnoreCase("cross-validation")){
                                if (examples == null){
                                    System.out.println("Parameter examples, which points to the file with examples, is missing iterable "+params.get("-batch"));
                                    System.exit(0);
                                }
                                if (mode == RELF || mode == RELF_GROUNDING_COUNTING){
                                    if (coveredClass != null){
                                        prop.setRelfCoveredClasses(Sugar.<String>set(coveredClass));
                                    }
                                }
                                if (foldsCount != null){
                                    if (StringUtils.isInteger(foldsCount)){
                                        prop.setFoldsCount(Integer.parseInt(foldsCount));
                                    } else {
                                        System.out.println("Warning: Number of folds must be an integer! Ignoring this setting.");
                                    }
                                }
                                Reader examplesReader = new FileReader(examples);
                                if (inputType == PROLOG){
                                    examplesReader = new Prolog2PseudoPrologReader(examplesReader);
                                }
                                prop.cv(template, maxSize, examplesReader, output, mode);
                            }
                            long m2 = System.currentTimeMillis();
                            System.out.println("Finished iterable "+(m2-m1)/1000.0+" seconds");
                        }
                    }
                }
            }
        } else {
            System.out.println("Example usage: java -jar -Xmx1G TreeLiker.jar -batch settingsfile.treeliker");
        }
    }

}
