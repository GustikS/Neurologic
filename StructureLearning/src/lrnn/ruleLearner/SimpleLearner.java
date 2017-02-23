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
import ida.utils.MutableDouble;
import ida.utils.Sugar;
import ida.utils.collections.MultiMap;
import ida.utils.tuples.Pair;
import ida.utils.tuples.Quadruple;
import lrnn.global.Glogger;

import java.util.*;

/**
 * Does not handle constants. Constants need to be handled as unary literals. But you're free to implement this functionality :)
 * <p>
 * Created by kuzelkao_cardiff on 20/01/17.
 */
public class SimpleLearner {

    private Dataset dataset;

    private Map<Literal, Double> literalWeights = new HashMap<Literal, Double>();

    private Set<Pair<String, Integer>> allAllowedPredicates;

    private boolean connected = true;

    private Saturator saturator;

    private static Random random = new Random(SimpleLearner.class.getName().hashCode());

    public SimpleLearner() {
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
    public Quadruple<HornClause, Double, Integer, Double> beamSearch(int beamSize, int maxSize, int targetClass) {
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
        Quadruple<HornClause, Double, Integer, Double> best = new Quadruple();
        history.putAll(0, current);
        for (int i = 1; i <= maxSize; i++) {
            List<HornClause> candidates = new ArrayList<HornClause>();
            for (HornClause old : current) {
                candidates.addAll(refinements(old));
            }
            candidates = filterIsomorphic(candidates);

            List<HornClause> top = selectTop(candidates, beamSize, best, targetClass);
            history.putAll(i, top);
            current = top;
            for (HornClause hc : top) {
                Glogger.info("in top: " + hc);
            }
            Glogger.info("Best so far: " + best);
        }
        return best;
    }

    private List<HornClause> selectTop(List<HornClause> list, int num, Quadruple<HornClause, Double, Integer, Double> outBest, int targetClass) {
        List<Quadruple<HornClause, Double, Integer, Double>> rules = new ArrayList<>();
        for (HornClause hc : list) {

            if (targetClass > 0) {
                MutableDouble thresh1 = new MutableDouble(Double.NaN);
                double err1 = dataset.error(new ClassifierR(hc, 1), this.literalWeights, thresh1);
                rules.add(new Quadruple(hc, err1, 1, thresh1));
            } else if (targetClass < 0) {
                MutableDouble thresh2 = new MutableDouble(Double.NaN);
                double err2 = dataset.error(new ClassifierR(hc, -1), this.literalWeights, thresh2);
                rules.add(new Quadruple(hc, err2, -1, thresh2));
            } else {
                MutableDouble thresh1 = new MutableDouble(Double.NaN);
                double err1 = dataset.error(new ClassifierR(hc, 1), this.literalWeights, thresh1);
                MutableDouble thresh2 = new MutableDouble(Double.NaN);
                double err2 = dataset.error(new ClassifierR(hc, -1), this.literalWeights, thresh2);
                if (err1 < err2) {
                    rules.add(new Quadruple(hc, err1, 1, thresh1));
                } else {
                    rules.add(new Quadruple(hc, err2, -1, thresh2));
                }
            }
        }

        Collections.shuffle(rules, this.random);
        Collections.sort(rules, (o1, o2) -> o1.s.compareTo(o2.s));
        List<HornClause> retVal = new ArrayList<HornClause>();
        for (int i = 0; i < Math.min(num, rules.size()); i++) {
            retVal.add(rules.get(i).r);
        }
        if (retVal.size() > 0 && outBest != null && (outBest.r == null || outBest.s > rules.get(0).s)) {
            outBest.set(retVal.get(0), rules.get(0).s, rules.get(0).t, rules.get(0).u);
        }
        System.out.println();
        return retVal;
    }

    private List<HornClause> refinements(HornClause hc) {
        Set<IsoClauseWrapper> set = new HashSet<IsoClauseWrapper>();
        for (Pair<String, Integer> predicate : allAllowedPredicates) {
            for (HornClause newHc : refinements(hc, predicate)) {
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

    private List<HornClause> refinements(HornClause hc, Pair<String, Integer> predicate) {
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
                        if (substituted.countLiterals() > originalClause.countLiterals()) {
                            HornClause candidate = new HornClause(substituted);
                            if (dataset.numExistentialMatches(candidate, 1) > 0) {
                                Clause candClause = candidate.toClause();
                                newRefinements.put(new IsoClauseWrapper(candClause), LogicUtils.substitute(entry.getValue(), x, v));
                            }
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
            if (!connected || icw.getOriginalClause().connectedComponents().size() == 1) {
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
