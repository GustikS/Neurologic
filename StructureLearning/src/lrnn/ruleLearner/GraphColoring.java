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
import ida.utils.CommandLine;
import ida.utils.collections.MultiMap;
import ida.utils.tuples.Pair;
import ida.utils.tuples.Triple;
import lrnn.crossvalidation.Crossvalidation;
import lrnn.global.Global;
import lrnn.global.Glogger;
import lrnn.global.Settings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by gusta on 23.3.17.
 */
public class GraphColoring extends RelationalTemplateSPI {

    private boolean correctClusters = true;

    private int numColors = 3;
    private int numShadows = 3;

    MultiMap<String, String> createColors(int colorCount, int shadowCount) {
        MultiMap<String, String> colors = new MultiMap<>();
        for (int i = 0; i < colorCount; i++) {
            for (int j = 0; j < shadowCount; j++) {
                colors.put("cl" + i, "sh" + j);
            }
        }
        return colors;
    }

    List<String> createExampleEdges(MultiMap<String, String> colors) {
        List<String> examples = new ArrayList();
        int exampleCount = 0;
        for (Map.Entry<String, Set<String>> sse1 : colors.entrySet()) {
            for (Map.Entry<String, Set<String>> sse2 : colors.entrySet()) {
                if (sse1.getKey().equals(sse2.getKey())) {
                    for (String s1 : sse1.getValue()) {
                        for (String s2 : sse2.getValue()) {
                            examples.add("1 " + sse1.getKey() + s1 + "(" + exampleCount + "v1),edge(" + exampleCount + "v1," + exampleCount + "v2)," + sse2.getKey() + s2 + "(" + exampleCount + "v2).");
                            exampleCount++;
                        }
                    }
                } else {
                    for (String s1 : sse1.getValue()) {
                        for (String s2 : sse2.getValue()) {
                            examples.add("0 " + sse1.getKey() + s1 + "(" + exampleCount + "v1),edge(" + exampleCount + "v1," + exampleCount + "v2)," + sse2.getKey() + s2 + "(" + exampleCount + "v2).");
                            exampleCount++;
                        }
                    }
                }
            }
        }
        return examples;
    }

    public static void main(String[] args) throws IOException {
        double trainerr = 0;

        Map<String, String> arguments = CommandLine.parseParams(args);

        int num = 2;
        int perfect = 0;
        for (int i = 2; i <=num; i++) {


            Global.setSeed(i);
            Settings.setDataset(arguments.get("-dataset"));

            //create logger for all messages within the program
            Glogger.init();

            GraphColoring lc = new GraphColoring();
            lc.folds = 1;
            lc.autoencodingSteps = 0;
            lc.errorMeasure = "MSE";

            lc.searchBeamSize = arguments.get("-sbs") == null ? lc.searchBeamSize : Integer.parseInt(arguments.get("-sbs"));
            lc.searchMaxSize = arguments.get("-sms") == null ? lc.searchMaxSize : Integer.parseInt(arguments.get("-sms"));
            lc.trainingSteps = arguments.get("-trs") == null ? lc.trainingSteps : Integer.parseInt(arguments.get("-trs"));

            File datasetPath = new File(arguments.get("-dataset"));

            Crossvalidation cross = lc.crossvalidate(datasetPath);
            trainerr+= cross.trainErr;
            if (cross.trainErr < 0.01){
                perfect++;
            }
        }
        System.out.println("FINAL TRAIN ERROR: "+ trainerr/num + " with perfect shots: "+perfect);
    }

    public static void main2(String[] args) throws IOException {
        String outPath = args[0];
        GraphColoring gc = new GraphColoring();
        MultiMap<String, String> colors = gc.createColors(gc.numColors, gc.numShadows);
        List<String> exampleEdges = gc.createExampleEdges(colors);
        String examplesPath = outPath.substring(0, outPath.lastIndexOf(".")) + "exampleEdges43.txt";
        Files.write(Paths.get(examplesPath), exampleEdges, StandardOpenOption.CREATE);
        Pair<List<String>, StringBuilder> rules = gc.createTemplate(colors);
        String rulesPath = outPath.substring(0, outPath.lastIndexOf(".")) + "edgeRules43.txt";
        Files.write(Paths.get(rulesPath), rules.r, StandardOpenOption.CREATE);
    }

    public Triple<String, String, StringBuilder> createInitialTemplatesAndExamples(List<Clause> clauses, String outPath, int atomClusters, int bondClusters) throws IOException {
        GraphColoring gc = new GraphColoring();
        MultiMap<String, String> colors = gc.createColors(numColors, numShadows);
        List<String> exampleEdges = gc.createExampleEdges(colors);
        String examplesPath = outPath.substring(0, outPath.lastIndexOf(".")) + "exampleEdges.txt";
        Files.write(Paths.get(examplesPath), exampleEdges, StandardOpenOption.CREATE);
        Pair<List<String>, StringBuilder> rules = gc.createTemplate(colors);
        String rulesPath = outPath.substring(0, outPath.lastIndexOf(".")) + "_initRules.txt";
        Files.write(Paths.get(rulesPath), rules.r, StandardOpenOption.CREATE);
        return new Triple<>(examplesPath, rulesPath, rules.s);
    }

    Pair<List<String>, StringBuilder> createTemplate(MultiMap<String, String> colors) {
        List<String> rules = new ArrayList();
        for (Map.Entry<String, Set<String>> sse1 : colors.entrySet()) {
            for (Map.Entry<String, Set<String>> sse2 : colors.entrySet()) {
                if (sse1.getKey().equals(sse2.getKey())) {
                    for (String s2 : sse2.getValue()) {
                        rules.add((correctClusters ? "0.0" : 1000 * (Global.getRg().nextDouble() - 0.5)) + " " + sse1.getKey() + "(X) :- " + sse2.getKey() + s2 + "(X).");
                    }
                } else {
                    for (String s2 : sse2.getValue()) {
                        rules.add((correctClusters ? "0.0" : 1000 * (Global.getRg().nextDouble() - 0.5)) + " " + sse1.getKey() + "(X) :- " + sse2.getKey() + s2 + "(X).");
                    }
                }
            }
            rules.add(sse1.getKey() + "/1 0.000001");
        }
        StringBuilder sb = new StringBuilder();
        for (String rule : rules) {
            sb.append(rule).append("\n");
        }

        for (int i = 0; i < colors.size(); i++) {
            rules.add("notColorable" + i + "(a) :- cl" + i + "(X),edge(X,Y),cl" + i + "(Y).");
        }
        for (int i = 0; i < colors.size(); i++) {
            rules.add("0.0 finalKappa(a) :- notColorable" + i + "(a).");
        }
        return new Pair(rules, sb);
    }

}
