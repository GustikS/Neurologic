/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ida.ilp.treeLiker;

//import basic.Clause;
//import basic.Constant;
//import basic.Variable;
//import basic.subsumption.CSPThetaSubsumption;
//import ida.ilp.boundedOperations.BoundedOperations;
//import ida.ilp.bull.GeneralizationX;
//import ida.utils.Sugar;
//import ida.utils.tuples.Pair;

/**
 *
 * @author Andrea/Ondra
 */
public class RelfX {

//    private boolean aDominatesB(Map<String,String> attributeVector1, Map<String,String> attributeVector2, Table<String,String> attributeTable, String coveredClass){
//        for (Map.Entry<String,String> entry : attributeVector1.entrySet()){
//            if ((entry.getValue().equals("-") && attributeVector2.get(entry.getKey()).equals("+") && attributeTable.getClassification(entry.getKey()).equals(coveredClass)) ||
//                    (entry.getValue().equals("+") && attributeVector2.get(entry.getKey()).equals("-") && !attributeTable.getClassification(entry.getKey()).equals(coveredClass))){
//                return false;
//            }
//        }
//        return true;
//    }
//
//    private Pair<Clause,Set<Clause>> enforceSubsumption(Clause hypothesis, Set<Clause> allExamplesCoveredByOriginalFeature, Set<Clause> generalizedClauses, GeneralizationX generalization){
//        Set<Clause> newlyCoveredExamples = null;
//        Set<Clause> examplesCoveredByTheClauseWithEnforcedSubsumption = new HashSet<Clause>();
//        examplesCoveredByTheClauseWithEnforcedSubsumption.addAll(generalizedClauses);
//        do {
//            newlyCoveredExamples = newlyCoveredClauses(hypothesis, allExamplesCoveredByOriginalFeature, generalizedClauses);
//            generalizedClauses = Sugar.union(generalizedClauses, newlyCoveredExamples);
//            List<Clause> clausesToBeGeneralized = new ArrayList<Clause>();
//
//            clausesToBeGeneralized.add(hypothesis);
//            clausesToBeGeneralized.addAll(newlyCoveredExamples);
//
//            examplesCoveredByTheClauseWithEnforcedSubsumption.addAll(newlyCoveredExamples);
//            hypothesis = generalization.generalize(clausesToBeGeneralized, PresubsumptionType.TREELIKE_PRESUBSUMPTION);
//            if (hypothesis == null){
//                return null;
//            }
//        } while (!newlyCoveredExamples.isEmpty());
//        return new Pair<Clause,Set<Clause>>(hypothesis, examplesCoveredByTheClauseWithEnforcedSubsumption);
//    }
//
//    private Set<Clause> newlyCoveredClauses(Clause hypo, Set<Clause> allExamples, Set<Clause> alreadyCoveredClauses){
//        Set<Clause> newlyCovered = new HashSet<Clause>();
//        for (Clause example : allExamples){
//            if (!alreadyCoveredClauses.contains(example)){
//                if (BoundedOperations.boundedSubsumption(hypo, example)){
//                    newlyCovered.add(example);
//                }
//            }
//        }
//        return newlyCovered;
//    }
//
//    public Table<Integer,String> constructTable(List<Clause> features, PresubsumptionType presubsumptionType){
//        Table<Integer,String> table = new Table<Integer,String>();
//        Dataset d = dataset.shallowCopy();
//        d.reset();
//        while (d.hasNextExample()){
//            Clause example = new Clause(d.nextExample().literals());
//            int exampleIndex = d.currentIndex();
//            for (int i = 0; i < features.size(); i++){
//                boolean presubsumptionResult = false;
//                if (presubsumptionType == PresubsumptionType.THETA_SUBSUMPTION){
//                    presubsumptionResult = CSPThetaSubsumption.subsumption(features.get(i), example);
//                } else if (presubsumptionType == PresubsumptionType.TREELIKE_PRESUBSUMPTION){
//                    presubsumptionResult = BoundedOperations.boundedSubsumption(features.get(i), example);
//                } else {
//                    throw new UnsupportedOperationException("The presubsumption "+presubsumptionType+" is not supported.");
//                }
//                if (presubsumptionResult){
//                    table.add(exampleIndex, features.get(i).toString(), "+");
//                } else {
//                    table.add(exampleIndex, features.get(i).toString(), "-");
//                }
//            }
//
//            table.addClassification(exampleIndex, d.classificationOfCurrentExample());
//            exampleIndex++;
//        }
//        return table;
//    }
//
//    /**
//     * @param maxLggSize the maxLggSize to set
//     */
//    public void setMaxLggSize(int maxLggSize) {
//        this.maxLggSize = maxLggSize;
//    }
//
//    /**
//     * @param minPosCoveredSoThatTheFeatureWouldBeLggied the minPosCoveredSoThatTheFeatureWouldBeLggied to set
//     */
//    public void setMinPosCoveredSoThatTheFeatureWouldBeLggied(int minPosCoveredSoThatTheFeatureWouldBeLggied) {
//        this.minPosCoveredSoThatTheFeatureWouldBeLggied = minPosCoveredSoThatTheFeatureWouldBeLggied;
//    }

//    private int maxLggSize = Integer.MAX_VALUE;
//
//    private int minPosCoveredSoThatTheFeatureWouldBeLggied = 1;
//
//    //private int improvementIterations = 1;
//
//    private Dataset dataset;
//
//    public RelfX(Dataset dataset){
//        this.dataset = dataset;
//    }
//
//    /**
//     * Constructing new features - input: features generated by RelF -> construct new
//     * features by calculating bounded LGG of sets of examples covered by RelF features
//     *
//     * @param template
//     * @param coveredClass
//     * @return
//     */
//    public List<Clause> constructFeatures(String template, String coveredClass, Table<String, String> relfTable){
//        System.out.println("RELFX STARTS \n");
//        GeneralizationX generalization = new GeneralizationX(template);
//        generalization.setMaxLggSize(maxLggSize);
//
//        Map<Set<Clause>,Clause> features = new HashMap<Set<Clause>,Clause>();
//        Map<Integer, Clause> examples = new HashMap<Integer, Clause>();
//        Map<Clause,Integer> examplesToIDs = new HashMap<Clause,Integer>();
//        Map<Clause,String> classificationOfExamples = new HashMap<Clause,String>();
//        Dataset dataset = this.dataset.shallowCopy();
//        dataset.reset();
//
//
//        Set<Clause> setOfAllExamples = new HashSet<Clause>();
//        // fill a map with clauses
//        while (dataset.hasNextExample()){
//            Example example = dataset.nextExample();
//            examples.put(dataset.currentIndex(), new Clause(example.literals()));
//            Clause exampleAsClause = new Clause(example.literals());
//            setOfAllExamples.add(exampleAsClause);
//            classificationOfExamples.put(exampleAsClause, dataset.classificationOfCurrentExample());
//            examplesToIDs.put(exampleAsClause, dataset.currentIndex());
//        }
//
//        Variable.clearCache();
//        Constant.clearCache();
//
//        System.out.println("RelF constructed "+relfTable.filteredAttributes().size()+" seed features.");
//
//        Set<String> notToBeLgged = new HashSet<String>();
//
//        List<String> featuresGeneratedByRelF = Sugar.listFromCollections(relfTable.filteredAttributes());
//        Map<String,Integer> scoresForSorting = new HashMap<String,Integer>();
//        for (String relfFeature : featuresGeneratedByRelF){
//            int score = 0;
//            for (Map.Entry<String,String> entry : relfTable.getAttributeVector(relfFeature).entrySet()){
//                if (relfTable.getClassification(entry.getKey()).equals(coveredClass)){
//                    if (entry.getValue().equals("+")){
//                        score++;
//                    } else {
//                        score--;
//                    }
//                } else {
//                    if (entry.getValue().equals("+")){
//                        score--;
//                    } else {
//                        score++;
//                    }
//                }
//            }
//            scoresForSorting.put(relfFeature, score);
//        }
//        Sugar.sortDesc(featuresGeneratedByRelF);
//
//        Map<Clause,Set<Clause>> coveredBySeedFeaturesOfFeatures = new HashMap<Clause,Set<Clause>>();
//
//        // iterate over features generated by RelF
//        outerLoop: for (int i = 0; i < featuresGeneratedByRelF.size(); i++){
//            String relfFeature = featuresGeneratedByRelF.get(i);
//            if (!notToBeLgged.contains(relfFeature)){
//                Set<Clause> coveredPositiveExamples = new HashSet<Clause>();
//                Set<Clause> coveredNegativeExamples = new HashSet<Clause>();
//                Map<String,String> exampleFeaturePairs = relfTable.getAttributeVector(relfFeature);
//
//                // get examples covered by RelF features
//                for (Map.Entry<String, String> entry : exampleFeaturePairs.entrySet()){
//                    if (relfTable.getClassification(entry.getKey()).equals(coveredClass) && entry.getValue().equals("+")){
//                        coveredPositiveExamples.add(examples.get(new Integer(entry.getKey())));
//                    } else if (!relfTable.getClassification(entry.getKey()).equals(coveredClass) && entry.getValue().equals("+")){
//                        coveredNegativeExamples.add(examples.get(new Integer(entry.getKey())));
//                    }
//                }
//
//                // now, we 'least-generalize' the examples covered by relfFeature
//                if (coveredPositiveExamples.size() > minPosCoveredSoThatTheFeatureWouldBeLggied && coveredNegativeExamples.size() > 0 && !features.containsKey(Sugar.union(coveredPositiveExamples, coveredNegativeExamples))){
//                    Clause lggX = generalization.generalize(coveredPositiveExamples, PresubsumptionType.TREELIKE_PRESUBSUMPTION);
//                    if (lggX != null){
//                        Set<Clause> allExamplesCoveredByOriginalFeature = Sugar.setFromCollections(coveredPositiveExamples, coveredNegativeExamples);
//                        Pair<Clause,Set<Clause>> p = enforceSubsumption(lggX, allExamplesCoveredByOriginalFeature, coveredPositiveExamples, generalization);
//                        if (p != null){
//                            coveredBySeedFeaturesOfFeatures.put(p.r, allExamplesCoveredByOriginalFeature);
//                            lggX = p.r;
//                            if (!features.containsKey(p.s) || features.get(p.s).countLiterals() > lggX.countLiterals()){
//                                features.put(p.s, lggX);
//                                System.out.println("LGG_X: " + lggX + "\n");
//                            } else {
//                                System.out.println("Redundant LGG_X: " + lggX + "\n");
//                            }
//                            for (int j = i+1; j < featuresGeneratedByRelF.size(); j++){
//                                if (!notToBeLgged.contains(featuresGeneratedByRelF.get(j))){
//                                    Map<String,String> attributeVectorForLGG = new HashMap<String,String>();
//                                    //pre-filling with "-"
//                                    for (Integer exampleID : examples.keySet()){
//                                        attributeVectorForLGG.put(String.valueOf(exampleID), "-");
//                                    }
//                                    //adding the "+"'s
//                                    for (Clause coveredClause : p.s){
//                                        attributeVectorForLGG.put(String.valueOf(examplesToIDs.get(coveredClause)), "+");
//                                    }
//                                    if (aDominatesB(attributeVectorForLGG, relfTable.getAttributeVector(featuresGeneratedByRelF.get(j)), relfTable, coveredClass)){
//                                        notToBeLgged.add(featuresGeneratedByRelF.get(j));
//                                        System.out.println("Dominated feature: "+featuresGeneratedByRelF.get(j));
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
////        for (int i = 0; i < this.improvementIterations; i++){
////            Map<Set<Clause>,Clause> newFeatures = new HashMap<Set<Clause>,Clause>();
////            for (Map.Entry<Set<Clause>,Clause> entryA : features.entrySet()){
////                Map.Entry<Set<Clause>,Clause> nearestEntryToA = null;
////                double similarity = 0;
////                for (Map.Entry<Set<Clause>,Clause> entryB : features.entrySet()){
////                    if (!entryA.equals(entryB)){
////                        Set<Clause> intersection = Sugar.intersection(entryA.getKey(), entryB.getKey());
////                        double sim = 2.0*intersection.size()/(entryA.getKey().size()+entryB.getKey().size());
////                        if (sim > similarity){
////                            nearestEntryToA = entryB;
////                            similarity = sim;
////                        }
////                    }
////                }
////                if (nearestEntryToA != null){
////                    Set<Clause> upperBoundOnCoveredExamples = Sugar.intersection(coveredBySeedFeaturesOfFeatures.get(entryA.getValue()), coveredBySeedFeaturesOfFeatures.get(nearestEntryToA.getValue()));
////                    Set<Clause> positiveExamplesToBeGeneralized = new HashSet<Clause>();
////                    for (Clause example : upperBoundOnCoveredExamples){
////                        if (classificationOfExamples.get(example).equals(coveredClass)){
////                            positiveExamplesToBeGeneralized.add(example);
////                        }
////                    }
////                    if (positiveExamplesToBeGeneralized.size() > 1){
////                        Clause newFeature = generalization.generalize(positiveExamplesToBeGeneralized, Bull.TREELIKE_CLAUSES);
////                        if (newFeature != null){
////                            Pair<Clause,Set<Clause>> p = enforceSubsumption(newFeature, upperBoundOnCoveredExamples, positiveExamplesToBeGeneralized, generalization);
////                            if (p != null){
////                                newFeature = p.r;
////                                if (!features.containsKey(p.s) && !newFeatures.containsKey(p.s) && !p.s.equals(Sugar.intersection(entryA.getKey(), nearestEntryToA.getKey()))){
////                                    newFeatures.put(p.s, newFeature);
////                                    System.out.println("----\n"+entryA.getValue());
////                                    System.out.println("x\n"+nearestEntryToA.getValue()+" --> \n");
////                                    System.out.println(" Improved LGG_X: " + newFeature + "\n");
////                                }
////                            }
////                        }
////                    }
////                }
////            }
////            features.putAll(newFeatures);
////        }
//        System.out.println("RELFX FINISHED \n");
//
//        return Sugar.listFromCollections(features.values());
//    }
}
