/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ida.utils.treeliker.crossValidation;

import ida.utils.Sugar;
import ida.utils.VectorUtils;
import ida.utils.collections.Counters;
import ida.utils.collections.MultiList;
import ida.utils.tuples.Pair;

import java.util.*;
/**
 *
 * @author Ondra
 */
public class CrossValidation {

    private boolean verbose = true;
    
    private List<List<LearningExample>> folds;
    
    public CrossValidation(Collection<? extends LearningExample> examples, int foldsCount, int seed){
        MultiList<String,LearningExample> ml = new MultiList<String,LearningExample>();
        List<? extends LearningExample> shuffledExamples = Sugar.listFromCollections(examples);
        Collections.shuffle(shuffledExamples, new Random(seed));
        for (LearningExample example : shuffledExamples){
            ml.put(example.classification(), example);
        }
        this.folds = new ArrayList<List<LearningExample>>();
        for (int i = 0; i < foldsCount; i++){
            this.folds.add(new ArrayList<LearningExample>());
        }
        for (String c : ml.keySet()){
            List<Collection<LearningExample>> splitted = Sugar.splitCollection(ml.get(c), foldsCount);
            int index = 0;
            for (Collection<LearningExample> oneClassFold : splitted){
                this.folds.get(index++).addAll(oneClassFold);
            }
        }
    }

    public List<LearningExample> getFold(int index){
        return this.folds.get(index);
    }

    public Pair<List<LearningExample>,List<LearningExample>> getTrainTestFolds(int testIndex){
        List<LearningExample> train = new ArrayList<LearningExample>();
        List<LearningExample> test = new ArrayList<LearningExample>();
        for (int i = 0; i < this.folds.size(); i++){
            if (i == testIndex){
                test.addAll(this.folds.get(i));
            } else {
                train.addAll(this.folds.get(i));
            }
        }
        return new Pair<List<LearningExample>,List<LearningExample>>(train, test);
    }

    public int countFolds(){
        return this.folds.size();
    }

    public Pair<Double,Double> evaluate(LearningAlgorithm learningAlgo){
        double[] accuracies = new double[folds.size()];
        //[predicted label, true label]
        for (int i = 0; i < folds.size(); i++){
            Counters<Pair<String,String>> confusions = new Counters<Pair<String,String>>();
            List<LearningExample> train = new ArrayList<LearningExample>();
            for (int j = 0; j < folds.size(); j++){
                if (j != i){
                    train.addAll(folds.get(j));
                }
            }
            List<LearningExample> test = folds.get(i);
            learningAlgo.train(train);
            double correct = 0;
            for (int j = 0; j < test.size(); j++){
                String predictedLabel = learningAlgo.classify(test.get(j));
                if (predictedLabel.equals(test.get(j).classification())){
                    correct++;
                }
                confusions.increment(new Pair<String,String>(predictedLabel, test.get(j).classification()));
            }
            accuracies[i] = correct/test.size();
            if (verbose){
                System.out.println("Correct: "+accuracies[i]);
                System.out.println("Confusions: "+confusions);
            }
        }
        return new Pair<Double,Double>(VectorUtils.mean(accuracies), Math.sqrt(VectorUtils.variance(accuracies)));
    }

    /**
     * @param verbose the verbose to set
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
}
