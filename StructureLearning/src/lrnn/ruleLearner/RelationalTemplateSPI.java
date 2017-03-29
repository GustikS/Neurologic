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
import ida.utils.CommandLine;
import ida.utils.tuples.Triple;
import lrnn.global.Global;
import lrnn.global.Glogger;
import lrnn.global.Settings;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by gusta on 17.3.17.
 */
public class RelationalTemplateSPI extends SoftClusteringSPI {

    public static void main(String[] args) throws IOException {
        Map<String, String> arguments = CommandLine.parseParams(args);

        Global.setSeed(1);
        Settings.setDataset(arguments.get("-dataset"));

        //create logger for all messages within the program
        Glogger.init();

        RelationalTemplateSPI lc = new RelationalTemplateSPI();
        lc.autoencodingSteps = 0;

        lc.searchBeamSize = arguments.get("-sbs") == null ? lc.searchBeamSize : Integer.parseInt(arguments.get("-sbs"));
        lc.searchMaxSize = arguments.get("-sms") == null ? lc.searchMaxSize : Integer.parseInt(arguments.get("-sms"));
        lc.trainingSteps = arguments.get("-trs") == null ? lc.trainingSteps : Integer.parseInt(arguments.get("-trs"));

        File datasetPath = new File(arguments.get("-dataset"));

        lc.crossvalidate(datasetPath);
    }

    Set<String> ignorePredicates = new HashSet<>();

    public RelationalTemplateSPI() {
        ignorePredicates.add("rel11");
        ignorePredicates.add("rel12");
        ignorePredicates.add("rel13");
    }


    public Triple<String, String, StringBuilder> createInitialTemplatesAndExamples(List<Clause> clauses, String outPath, int atomClusters, int bondClusters) throws IOException {
        String defaultWeight = "0.0";

        StringBuilder rules = new StringBuilder();
        Set<String> attributes = new HashSet<>();

        for (Clause cl : clauses) {
            for (Literal literal : cl.literals()) {
                if (!(ignorePredicates.contains(literal.predicate()) || literal.predicate().startsWith("^"))) {
                    attributes.add(literal.predicate());
                }
            }
        }

        for (int i = 0; i < atomClusters; i++) {
            for (String a1 : attributes) {
                rules.append(defaultWeight + " " + atomClusterName + i + "(X) :- " + a1 + "(X).\n");
            }
            rules.append(atomClusterName + i + "/1 " + defaultWeight + "\n");
        }
        StringBuilder softClusterBase = new StringBuilder(rules);
        rules.append("\n");
        rules.append("finalLambda1(a) :- ");
        for (int i = 0; i < atomClusters; i++) {
            rules.append(atomClusterName + i + "(A),");
        }
        rules.replace(rules.lastIndexOf(","), rules.lastIndexOf(",") + 1, ".");
        rules.append("\n");

        rules.append(defaultWeight + " " + "finalKappa(a) :- finalLambda1(a).\n");


        String rulesPath = outPath.substring(0, outPath.lastIndexOf(".")) + "_initRules.txt";
        PrintWriter pw2 = new PrintWriter(rulesPath);
        pw2.print(rules.toString());
        pw2.close();

        return new Triple(rulesPath, rulesPath, softClusterBase);

    }

    public Clause transform(Clause c) {

        List<Literal> literals = new ArrayList<>();
        for (Literal l : c.literals()) {
            if (!l.predicate().startsWith("^")) {
                literals.add(l);
            }
        }
        return new Clause(literals);
    }

}