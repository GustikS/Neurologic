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
import ida.ilp.logic.Term;
import ida.ilp.logic.subsumption.Matching;
import ida.utils.VectorUtils;
import ida.utils.tuples.Pair;
import ida.utils.tuples.Triple;
import lrnn.global.Glogger;
import lrnn.learning.functions.ActivationsFast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by kuzelkao_cardiff on 20/01/17.
 */
public class ClassifierR {

    double[] coeffs;

    private int sampleSize = 100, sampleStep = 1;

    private HornClause[] rules;

    /*
    private Sugar.Fun<double[], Double> gConj = new Sugar.Fun<double[], Double>() {
        @Override
        public Double apply(double[] doubles) {
            return Utils.sigmoid(VectorUtils.sum(doubles) - doubles.length + 1);
        }
    };

    private Sugar.Fun<double[], Double> gDisj = new Sugar.Fun<double[], Double>() {
        @Override
        public Double apply(double[] doubles) {
            return Utils.sigmoid(VectorUtils.sum(doubles));
        }
    };

    private Sugar.Fun<double[], Double> gStar = new Sugar.Fun<double[], Double>() {
        @Override
        public Double apply(double[] doubles) {
            if (doubles.length == 0) {
                return Double.valueOf(0.0);
            }
            return VectorUtils.mean(doubles);
        }
    };
    */

    public ClassifierR() {
        this.coeffs = new double[0];
        this.rules = new HornClause[0];
    }

    public ClassifierR(HornClause hc, double coeff) {
        this(new HornClause[]{hc}, new double[]{coeff});
    }

    public ClassifierR(HornClause[] rules, double[] coeffs) {
        this.coeffs = coeffs;
        this.rules = rules;
    }

    public double prediction(int dbIndex, Literal query, Matching matching, Map<Literal, Double> weights) {
        return VectorUtils.dotProduct(this.coeffs, predictions(dbIndex, query, matching, weights));
    }

    public double[] predictions(int dbIndex, Literal query, Matching matching, Map<Literal, Double> weights) {
        double[] retVal = new double[rules.length];
        int i = 0;
        for (HornClause rule : this.rules) {
            Clause unified;
            if (query == null) {
                unified = rule.body();
            } else {
                unified = rule.unify(query);
            }
            if (unified != null) {
                final int maxExhaustive = 10000;
                Pair<Term[], List<Term[]>> substitutions = matching.allSubstitutions(unified, dbIndex, maxExhaustive);
                if (substitutions.s.size() >= maxExhaustive) {
                    Glogger.process("searchTreeSampler: fire");
                    Triple<Term[], List<Term[]>, Double> t = matching.searchTreeSampler(unified, dbIndex, sampleSize, sampleStep);
                    substitutions = new Pair<Term[], List<Term[]>>(t.r, t.s);
                }
                double[] agg = new double[substitutions.s.size()];
                int j = 0;
                for (Term[] subs : substitutions.s) {
                    double[] bodyGrounding = new double[unified.countLiterals()];
                    int k = 0;
                    for (Literal l : substitute(unified.literals(), substitutions.r, subs)) {
                        Double weight;
                        if ((weight = weights.get(l)) != null) {
                            bodyGrounding[k] = weight.doubleValue();
                        } else {
                            bodyGrounding[k] = 1;
                        }
                        k++;
                    }
                    //agg[j] = this.getgConj().apply(bodyGrounding);
                    agg[j] = ActivationsFast.lambdaActivation(bodyGrounding, -bodyGrounding.length + 1);
                    j++;
                }
                retVal[i] = ActivationsFast.aggregation(agg);
            } else {
                retVal[i] = 0;
            }
            i++;
        }
        return retVal;
    }

    private List<Literal> substitute(Collection<Literal> literals, Term[] from, Term[] to) {
        List<Literal> retVal = new ArrayList<Literal>();
        for (Literal l : literals) {
            retVal.add(LogicUtils.substitute(l, from, to));
        }
        return retVal;
    }

    public double[] coefficients() {
        return this.coeffs;
    }

    public HornClause[] rules() {
        return this.rules;
    }

    public void setRule(HornClause rule, int index) {
        this.rules[index] = rule;
    }

    /*
    public Sugar.Fun<double[], Double> getgConj() {
        return gConj;
    }

    public Sugar.Fun<double[], Double> getgDisj() {
        return gDisj;
    }

    public Sugar.Fun<double[], Double> getgStar() {
        return gStar;
    }
    */

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rules().length; i++) {
            sb.append(coeffs[i] + " " + rules[i]).append("; ");
        }
        return sb.toString();
    }
}
