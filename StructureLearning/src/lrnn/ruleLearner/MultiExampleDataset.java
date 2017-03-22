/*
 * Copyright (c) 2015 Ondrej Kuzelka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package lrnn.ruleLearner;

import ida.ilp.logic.Clause;
import ida.ilp.logic.Literal;
import ida.ilp.logic.LogicUtils;
import ida.ilp.logic.subsumption.Matching;
import ida.utils.MutableDouble;
import ida.utils.Sugar;
import ida.utils.VectorUtils;
import ida.utils.tuples.Pair;

import java.util.*;

/**
 * Created by kuzelkao_cardiff on 20/01/17.
 */
public class MultiExampleDataset implements Dataset {

    private double[] targets;

    //one query per example, or no queries at all
    private List<Literal> queries;

    private List<Clause> examples;

    private Set<Pair<String, Integer>> allPredicates;

    private Set<Pair<String, Integer>> queryPredicates = new HashSet<Pair<String, Integer>>();

    private Matching matching;

    public MultiExampleDataset(List<Clause> examples, List<Double> targets) {
        this.targets = VectorUtils.toDoubleArray(targets);
        this.examples = examples;
        this.matching = new Matching(examples);
        this.matching.setSubsumptionMode(Matching.OI_SUBSUMPTION);
        this.allPredicates = LogicUtils.predicates(this.examples);
    }

    public MultiExampleDataset(List<Clause> examples, List<Literal> queries, List<Double> targets) {
        this(examples, targets);
        this.queries = queries;
    }

    /**
     * Analytically find weights for the last layer of classifiers by minimizing MSE given current classifier and all the previous
     * @param classifier
     * @param literalWeights
     * @return
     */
    public double optimalMSE(ClassifierR classifier, Map<Literal, Double> literalWeights) {
        double retVal = 0;
        for (int i = 0; i < examples.size(); i++) {
            double prediction = classifier.prediction(i, queries == null ? null : queries.get(i), matching, literalWeights);
            retVal += Sugar.square(prediction - targets[i]);
        }
        return retVal;
    }

    public double rmse(ClassifierR classifier, Map<Literal, Double> literalWeights) {
        double retVal = 0;
        for (int i = 0; i < examples.size(); i++) {
            double prediction = classifier.prediction(i, queries == null ? null : queries.get(i), matching, literalWeights);
            retVal += Sugar.square(prediction - targets[i]);
        }
        return retVal;
    }


    public double error(ClassifierR classifier, Map<Literal, Double> literalWeights) {
        return this.error(classifier, literalWeights, new MutableDouble(Double.NaN));
    }

    public double error(ClassifierR classifier, Map<Literal, Double> literalWeights, MutableDouble outThreshold) {
        double sumPos = 0, sumNeg = 0, bestErr = examples.size();
        List<Pair<Double, Integer>> pairs = new ArrayList<Pair<Double, Integer>>();
        for (int i = 0; i < examples.size(); i++) {
            pairs.add(new Pair<Double, Integer>((double) classifier.prediction(i, queries == null ? null : queries.get(i), matching, literalWeights), i));
        }
        Collections.sort(pairs, new Comparator<Pair<Double, Integer>>() {
            @Override
            public int compare(Pair<Double, Integer> o1, Pair<Double, Integer> o2) {
                return o1.r.compareTo(o2.r);
            }
        });

/*
        System.out.println("---------------------------------");
        for (Pair<Double, Integer> pair : pairs) {
            System.out.println(pair);
        }
*/

        int numPos = VectorUtils.occurrences(this.targets, 1.0);
        int numNeg = this.targets.length - numPos;
        int bestIndex = -1;
        int i = 0;
        while (true) {
            if (i >= pairs.size()) {
                break;
            }
            double err = (sumPos + numNeg - sumNeg) / (double) (numPos + numNeg);
            if (err < bestErr) {
                bestIndex = i;
                bestErr = err;
            }
            do {
                if (targets[pairs.get(i).s] == 1.0) {
                    sumPos++;
                } else if (targets[pairs.get(i).s] == 0.0) {
                    sumNeg++;
                } else {
                    throw new IllegalStateException();
                }
                i++;
            } while (i < pairs.size() - 1 && pairs.get(i).r.doubleValue() == pairs.get(i - 1).r.doubleValue());
        }
        outThreshold.set(pairs.get(bestIndex).r);
        System.out.print(".");
        //System.out.println(outThreshold);
        return bestErr;
    }

    @Override
    public int numExistentialMatches(HornClause hc, int maxNum) {
        int retVal = 0;
        if (this.queries == null) {
            Clause c = LogicUtils.flipSigns(hc.toClause());
            for (int i = 0; i < this.examples.size(); i++) {
                if (matching.subsumption(c, i)) {
                    retVal++;
                    if (retVal++ >= maxNum) {
                        break;
                    }
                }
            }
        } else {
            for (int i = 0; i < this.examples.size(); i++) {
                Clause c = LogicUtils.flipSigns(LogicUtils.flipSigns(hc.unify(this.queries.get(i))));
                if (matching.subsumption(c, i)) {
                    retVal++;
                    if (retVal++ >= maxNum) {
                        break;
                    }
                }
            }
        }
        return retVal;
    }

    public void addQueryPredicate(String predicateName, int arity) {
        this.queryPredicates.add(new Pair<String, Integer>(predicateName, arity));
    }

    @Override
    public Set<Pair<String, Integer>> queryPredicates() {
        return this.queryPredicates;
    }

    @Override
    public Set<Pair<String, Integer>> allPredicates() {
        return this.allPredicates;
    }

}
