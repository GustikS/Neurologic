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
import ida.ilp.logic.Variable;
import ida.ilp.logic.special.IsoClauseWrapper;
import ida.utils.Sugar;
import ida.utils.collections.MultiMap;
import ida.utils.tuples.Pair;
import lrnn.global.Glogger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Does not handle constants. Constants need to be handled as unary literals. But you're free to implement this functionality :)
 * <p>
 * Created by kuzelkao_cardiff on 20/01/17.
 */
public class SimpleLearner {

    String errorMeasure = "acc";

    private boolean connectedOnly = true;

    private int minSupport = 1;

    Dataset dataset;

    Map<Literal, Double> literalWeights = new HashMap<Literal, Double>();

    private Set<Pair<String, Integer>> allAllowedPredicates;

    private boolean connected = true;

    private Saturator saturator;

    static Random random = new Random(SimpleLearner.class.getName().hashCode());

    int targetClass = 0;

    public SimpleLearner(String errorMeasure) {
        this.errorMeasure = errorMeasure;
    }

    public SimpleLearner(Dataset dataset) {
        this.dataset = dataset;
        this.allAllowedPredicates = dataset.allPredicates();
    }

    public SimpleLearner(Dataset dataset, Map<Literal, Double> literalWeights) {
        this(dataset);
        this.literalWeights = literalWeights;
    }

    //+1 = positive classifier, -1 = negative classifier, 0 = both
    public Pair<ClassifierR,Double> beamSearch(int beamSize, int maxSize) {

        MultiMap<HornClause, Literal> badRefinements = new MultiMap<HornClause, Literal>();
        Set<IsoClauseWrapper> processed = new HashSet<IsoClauseWrapper>();
        Set<Pair<String, Integer>> queryPredicates = this.dataset.queryPredicates();

        MultiMap<Integer, HornClause> history = new MultiMap<Integer, HornClause>();
        List<HornClause> current = new ArrayList<HornClause>();
        if (queryPredicates.isEmpty()) {
            current.add(new HornClause(new Clause()));
        } else {
            for (Pair<String, Integer> predicate : queryPredicates) {
                current.add(new HornClause(new Clause(LogicUtils.newLiteral(predicate.r, predicate.s))));
            }
        }

        Pair<ClassifierR, Double> best = new Pair<>();
        history.putAll(0, current);
        for (int i = 1; i <= maxSize; i++) {
            List<HornClause> candidates = new ArrayList<HornClause>();
            for (HornClause old : current) {
                candidates.addAll(refinements(old, badRefinements.get(old)));
            }
            candidates = filterIsomorphic(candidates);

            List<Pair<ClassifierR, Double>> top = selectTop(candidates, beamSize, best);
            history.putAll(i, top.stream().map(q -> q.r.rules()[q.r.rules().length-1]).collect(Collectors.toList()));
            current = top.stream().map(q -> q.r.rules()[q.r.rules().length-1]).collect(Collectors.toList());
            for (Pair<ClassifierR, Double> hc : top) {
                Glogger.info("in top: " + hc);
            }

            Glogger.info("Best so far: " + best);
        }
        return best;
    }

    List<Pair<ClassifierR, Double>> selectTop(List<HornClause> list, int num, Pair<ClassifierR, Double> outBest) {
        List<Pair<ClassifierR, Double>> rules = new ArrayList<>();
        for (HornClause hc : list) {

            if (targetClass > 0) {
                ClassifierR cl1 = new ClassifierR(hc, 1);
                double err1 = dataset.error(cl1, this.literalWeights, errorMeasure);
                rules.add(new Pair(cl1, err1));
            } else if (targetClass < 0) {
                ClassifierR cl1 = new ClassifierR(hc, -1);
                double err1 = dataset.error(cl1, this.literalWeights, errorMeasure);
                rules.add(new Pair(cl1, err1));
            } else {
                ClassifierR cl1 = new ClassifierR(hc, 1);
                double err1 = dataset.error(cl1, this.literalWeights, errorMeasure);
                ClassifierR cl2 = new ClassifierR(hc, -1);
                double err2 = dataset.error(cl1, this.literalWeights, errorMeasure);
                if (err1 < err2) {
                    rules.add(new Pair(cl1, err1));
                } else {
                    rules.add(new Pair(cl2, err2));
                }
            }
        }

        Collections.shuffle(rules, this.random);
        Collections.sort(rules, (o1, o2) -> o1.s.compareTo(o2.s));
        List<Pair<ClassifierR, Double>> retVal = new ArrayList<>();
        for (int i = 0; i < Math.min(num, rules.size()); i++) {
            retVal.add(rules.get(i));
        }
        if (retVal.size() > 0 && outBest != null && (outBest.r == null || outBest.s > rules.get(0).s)) {
            outBest.set(retVal.get(0).r, rules.get(0).s);
        }
        System.out.println();
        return retVal;
    }

    private List<HornClause> refinements(HornClause hc, Set<Literal> badRefinements) {
        Set<IsoClauseWrapper> set = new HashSet<IsoClauseWrapper>();
        for (Pair<String, Integer> predicate : allAllowedPredicates) {
            for (HornClause newHc : refinements(hc, predicate, badRefinements)) {
                set.add(new IsoClauseWrapper(newHc.toClause()));
            }
        }
        List<HornClause> retVal = new ArrayList<HornClause>();
        for (IsoClauseWrapper icw : set) {
            retVal.add(new HornClause(icw.getOriginalClause()));
        }
        return retVal;
    }

    private List<HornClause> filterIsomorphic(Collection<HornClause> coll) {
        Set<IsoClauseWrapper> set = new HashSet<IsoClauseWrapper>();
        for (HornClause newHc : coll) {
            set.add(new IsoClauseWrapper(newHc.toClause()));
        }
        List<HornClause> retVal = new ArrayList<HornClause>();
        for (IsoClauseWrapper icw : set) {
            retVal.add(new HornClause(icw.getOriginalClause()));
        }
        return retVal;
    }

    private List<HornClause> refinements(HornClause hc, Pair<String, Integer> predicate, Set<Literal> badRefinements) {
        long m1 = System.currentTimeMillis();
        Map<IsoClauseWrapper, Literal> refinements = new HashMap<IsoClauseWrapper, Literal>();
        Set<Variable> variables = hc.variables();
        Set<Variable> freshVariables = LogicUtils.freshVariables(variables, predicate.s);
        Literal freshLiteral = LogicUtils.newLiteral(predicate.r, predicate.s, freshVariables).negation();
        Clause originalClause = hc.toClause();
        Clause init = new Clause(Sugar.union(originalClause.literals(), freshLiteral));
        refinements.put(new IsoClauseWrapper(init), freshLiteral);

        for (int i = 0; i < predicate.s; i++) {
            Map<IsoClauseWrapper, Literal> newRefinements = new HashMap<IsoClauseWrapper, Literal>();
            for (Map.Entry<IsoClauseWrapper, Literal> entry : refinements.entrySet()) {
                Variable x = (Variable) entry.getValue().get(i);
                for (Variable v : entry.getKey().getOriginalClause().variables()) {
                    if (v != x) {
                        Clause substituted = LogicUtils.substitute(entry.getKey().getOriginalClause(), x, v);
                        Literal newLiteral = LogicUtils.substitute(entry.getValue(), x, v);
                        if (substituted.countLiterals() > originalClause.countLiterals() && !badRefinements.contains(newLiteral) &&
                                !substituted.containsLiteral(newLiteral.negation())) {
                            HornClause candidate = new HornClause(substituted);
                            if (dataset.numExistentialMatches(candidate, minSupport) >= minSupport) {
                                Clause candClause = candidate.toClause();
                                newRefinements.put(new IsoClauseWrapper(candClause), newLiteral);
                            } else {
                                badRefinements.add(newLiteral);
                            }
                        } else {
                            //System.out.println("bad: "+newLiteral+" for "+hc);
                        }
                    }
                }
            }
            refinements.putAll(newRefinements);
        }
        Set<IsoClauseWrapper> refinementSet;
        if (this.saturator != null) {
            Set<IsoClauseWrapper> saturatedRefinements = new HashSet<IsoClauseWrapper>();
            for (IsoClauseWrapper icw : refinements.keySet()) {
                saturatedRefinements.add(new IsoClauseWrapper(saturator.saturate(icw.getOriginalClause())));
            }
            refinementSet = saturatedRefinements;
        } else {
            refinementSet = refinements.keySet();
        }
        List<HornClause> retVal = new ArrayList<HornClause>();
        for (IsoClauseWrapper icw : refinementSet) {
            if ((!this.connectedOnly || icw.getOriginalClause().connectedComponents().size() == 1)) {
                retVal.add(new HornClause(icw.getOriginalClause()));
            }
        }
        long m2 = System.currentTimeMillis();
        //System.out.println((m2-m1)+"ms");
        return retVal;
    }

    public void setLiteralWeights(Map<Literal, Double> literalWeights) {
        this.literalWeights = literalWeights;
    }

    public void setLanguageBias(Set<Pair<String, Integer>> predicates) {
        this.allAllowedPredicates = predicates;
    }

    public void setSaturator(Saturator saturator) {
        this.saturator = saturator;
    }

    public void setDataset(MultiExampleDataset dataset) {
        this.dataset = dataset;
    }
}