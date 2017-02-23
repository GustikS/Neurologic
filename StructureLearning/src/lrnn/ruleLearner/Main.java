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
import ida.ilp.logic.io.PseudoPrologParser;
import ida.utils.Sugar;
import ida.utils.tuples.Pair;

import java.io.FileReader;
import java.io.Reader;
import java.util.*;

/**
 * Created by kuzelkao_cardiff on 23/01/17.
 */
public class Main {

    public static Clause transform(Clause c) {

        List<Literal> literals = new ArrayList<Literal>();
        for (Literal l : c.literals()) {
            if (l.predicate().equals("bond")) {
                literals.add(new Literal(l.get(4).name() + "_bond", l.get(0), l.get(1)));
                literals.add(new Literal(l.get(2).name(), l.get(0)));
                literals.add(new Literal(l.get(3).name(), l.get(1)));
            }
        }
        return new Clause(literals);
    }

    //example of a simple addition of weights for soft clusters
    public static Pair<List<Clause>, Map<Literal, Double>> weights(List<Clause> clauses) {
        Set<String> haloGroup = Sugar.<String>set("cl", "br", "i", "f");
        Set<String> cGroup = Sugar.<String>set("c", "si");
        Set<String> noGroup = Sugar.<String>set("n", "o");
        List<Clause> newClauses = new ArrayList<Clause>();
        Map<Literal, Double> weights = new HashMap<Literal, Double>();
        for (Clause c : clauses) {
            Set<Literal> lits = new HashSet<Literal>();
            for (Literal l : c.literals()) {
                if (l.arity() == 1) {
                    Literal halo = new Literal("hal", l.get(0));
                    lits.add(halo);
                    if (haloGroup.contains(l.predicate())) {
                        weights.put(halo, 1.0);
                    } else {
                        weights.put(halo, 0.0);
                    }

                    Literal cl = new Literal("carblike", l.get(0));
                    lits.add(cl);
                    if (cGroup.contains(l.predicate())) {
                        weights.put(cl, 1.0);
                    } else {
                        weights.put(cl, 0.0);
                    }

                    Literal no = new Literal("no", l.get(0));
                    lits.add(no);
                    if (noGroup.contains(l.predicate())) {
                        weights.put(no, 1.0);
                    } else {
                        weights.put(no, 0.0);
                    }
                } else {
                    lits.add(l);
                }
            }
            newClauses.add(new Clause(lits));
        }
        return new Pair<List<Clause>, Map<Literal, Double>>(newClauses, weights);
    }

    public static void main(String[] args) throws Exception {
        Reader reader = new FileReader("../in/ptcmr/ptc_mr.txt");
        List<Double> targets = new ArrayList<Double>();
        List<Clause> clauses = new ArrayList<Clause>();
        for (Pair<Clause, String> pair : PseudoPrologParser.read(reader)) {
            clauses.add(transform(pair.r));
            if (pair.s.equals("+")) {
                targets.add(1.0);
            } else {
                targets.add(0.0);
            }
        }

        Pair<List<Clause>, Map<Literal, Double>> pair = weights(clauses);

        MultiExampleDataset med = new MultiExampleDataset(pair.r, targets);

        SimpleLearner sl = new SimpleLearner(med, pair.s);

        //We can (and perhaps should) actually "learn" the symmetries from data
        SymmetrySaturator symmetrySaturator = new SymmetrySaturator();
        symmetrySaturator.setSymmetries("1_bond", 2, Sugar.<int[]>list(new int[]{1, 0}));
        symmetrySaturator.setSymmetries("2_bond", 2, Sugar.<int[]>list(new int[]{1, 0}));
        symmetrySaturator.setSymmetries("3_bond", 2, Sugar.<int[]>list(new int[]{1, 0}));
        symmetrySaturator.setSymmetries("7_bond", 2, Sugar.<int[]>list(new int[]{1, 0}));
        sl.setSaturator(symmetrySaturator);


        System.out.println("RESULT: " + sl.beamSearch(20, 10, 0));
    }

}