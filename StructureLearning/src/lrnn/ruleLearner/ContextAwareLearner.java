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

import ida.utils.tuples.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by gusta on 27.3.17.
 */
public class ContextAwareLearner extends SimpleLearner {

    ClassifierR template;

    public ContextAwareLearner() {
        super("MSE");
    }

    public Pair<ClassifierR, Double> beamSearch(int beamSize, int maxSize, ClassifierR previousTemplate) {
        HornClause[] rules = new HornClause[previousTemplate.rules().length + 1];
        for (int i = 0; i < previousTemplate.rules().length; i++) {
            rules[i] = previousTemplate.rules()[i];
        }
        double[] coeffs = new double[previousTemplate.coefficients().length + 1];
        for (int i = 0; i < previousTemplate.coefficients().length; i++) {
            coeffs[i] = previousTemplate.coefficients()[i];
        }
        template = new ClassifierR(rules, coeffs);
        return super.beamSearch(beamSize, maxSize);
    }

    @Override
    List<Pair<ClassifierR, Double>> selectTop(List<HornClause> list, int num, Pair<ClassifierR, Double> outBest) {
        List<Pair<ClassifierR, Double>> rules = new ArrayList<>();
        for (HornClause hc : list) {
            template.rules()[template.rules().length-1] = hc;
            HornClause[] newrules = new HornClause[template.rules().length];
            for (int i = 0; i < newrules.length; i++) {
                newrules[i] = template.rules()[i];
            }
            ClassifierR classifierR = new ClassifierR(newrules, new double[template.coefficients().length]);
            double err = dataset.error(classifierR, this.literalWeights, errorMeasure);
            rules.add(new Pair(classifierR, err));
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
}
