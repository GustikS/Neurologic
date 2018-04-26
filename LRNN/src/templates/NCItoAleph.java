/*
 * Copyright (c) 2015 Ondrej Kuzelka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package templates;

import ida.ilp.logic.Clause;
import ida.ilp.logic.Literal;
import ida.ilp.logic.io.PseudoPrologParser;
import ida.utils.tuples.Pair;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by gusta on 18.10.17.
 */
public class NCItoAleph extends Convertor {
    String target = "active";

    public static void main(String[] args) throws IOException {
        String path = "/home/gusta/googledrive/NeuraLogic/datasets/NCI/";
        NCItoAleph nci = new NCItoAleph();
        nci.Aleph(path, 5);
        nci.RDNboost(path, 5);
    }

    public void Aleph(String path, int folds) throws IOException {
        boolean abduction = true;
        File[] files = new File(path).listFiles();

        for (File file : files) {
            if (file.isFile()) {
                String pth = file.getAbsolutePath().toString();
                File f = new File(file.getParentFile().getAbsolutePath().toString() + "/" + pth.substring(pth.indexOf("screen_") + 7, pth.indexOf(".txt")));
                f.mkdirs();
                List<String> positive = new ArrayList<>();
                List<String> negative = new ArrayList<>();
                List<String> facts = new ArrayList<>();

                Reader reader = new FileReader(file);
                List<Pair<Clause, String>> labeledExampleClauses = PseudoPrologParser.read(reader);
                for (int i = 0; i < labeledExampleClauses.size(); i++) {
                    if (labeledExampleClauses.get(i).s.contains("+")) {
                        positive.add(target + "(d" + i + ").");
                    } else {
                        negative.add(target + "(d" + i + ").");
                    }
                    facts.addAll(transformExample(labeledExampleClauses.get(i).r, i));
                }
                writeSimple(positive, f.getAbsolutePath() + "/train.f");
                writeSimple(negative, f.getAbsolutePath() + "/train.n");
                List<String> back = new ArrayList<>();
                // MODES
                if (abduction) {
                    back.addAll(generateAbduction(2, 2));
                } else {
                    //back.add(":- set(clauselength,10).");
                    back.add(":- modeh(1,active(+drug)).");
                    back.add(":- modeb(*,bond(+drug,+atomid,+atomid,#bondtype)).");
                    back.add(":- modeb(*,bond(+drug,+atomid,-atomid,#bondtype)).");
                    back.add(":- modeb(*,atm(+drug,-atomid,#atmtype)).");
                    back.add("\n");
                    back.add(":- determination(active/1,atm/3).");
                    back.add(":- determination(active/1,bond/4).");
                }
                //back.add(":- set(i,2).");
                //back.add(":- set(caching,false).");
                //back.add(":- set(samplesize,0).");
                back.add(":- set(record,true).");
                //back.add(":- set(recordfile,'aleph.trace').");
                back.add("\n");
                back.addAll(facts);
                writeSimple(back, f.getAbsolutePath() + "/train.b");

                //crossvalidation folds
                List<List<String>> posFolds = splitter(folds, positive);
                List<List<String>> negFolds = splitter(folds, negative);
                for (int i = 0; i < folds; i++) {
                    File fold = new File(f.getAbsolutePath() + "/fold" + i);
                    fold.mkdirs();
                    //test
                    writeSimple(posFolds.get(i), fold.getAbsolutePath() + "/test.f");
                    writeSimple(negFolds.get(i), fold.getAbsolutePath() + "/test.n");
                    List<String> l = new ArrayList<String>();
                    l.add(":- set(test_pos,'./test.f').");
                    l.add(":- set(test_neg,'./test.n').");
                    l.add(":- consult('../train.b').");
                    writeSimple(l, fold.getAbsolutePath() + "/train.b");
                    l.clear();
                    //train
                    List<String> trainPos = new ArrayList<>();
                    List<String> trainNeg = new ArrayList<>();
                    for (int j = 0; j < folds; j++) {
                        if (j == i) continue;
                        trainPos.addAll(posFolds.get(j));
                        trainNeg.addAll(negFolds.get(j));
                    }
                    writeSimple(trainPos, fold.getAbsolutePath() + "/train.f");
                    writeSimple(trainNeg, fold.getAbsolutePath() + "/train.n");
                }
            }
        }
    }

    private Collection<? extends String> generateAbduction(int clusters, int length) {
        ArrayList<String> lines = new ArrayList<>();

        lines.add(":- set(abduce,true).");
        for (int i = 0; i < clusters; i++) {
            lines.add(":- modeh(1,atmCluster" + i + "(+drug,+atomid)).");
            lines.add(":- abducible(atmCluster" + i + "/2).");
            lines.add(":- determination(active/1,atmCluster" + i + "/2).");
        }
        for (int i = 0; i < clusters; i++) {
            lines.add(":- modeh(1,bondCluster" + i + "(+drug,+atomid,+atomid)).");
            lines.add(":- abducible(bondCluster" + i + "/3).");
            lines.add(":- determination(active/1,bondCluster" + i + "/3).");
        }
        lines.add(":- modeb(1,atm(+drug,+atomid,#atmtype)).");
        lines.add(":- modeb(1,bond(+drug,+atomid,+atomid,#bondtype)).");
        createGraphlets(new ArrayList<>(), 0);
        lines.addAll(features);
        return lines;
    }

    List<String> features = new ArrayList<>();
    int featSize = 3;
    int atomClusters = 2;
    int bondClusters = 2;
    int atomIndex = 0;

    void createGraphlets(ArrayList<String> rule, int position) {
        String localid = "";
        if (position == featSize) {
            StringBuilder fin = new StringBuilder();
            fin.append("active(X) :- ");
            for (int i = 0; i < rule.size(); i++) {
                fin.append(rule.get(i));
            }
            fin.replace(fin.lastIndexOf(","), fin.lastIndexOf(",") + 1, ".");
            features.add(fin.toString());
            return;
        }
        if (position % 2 == 0) { //alternate atom-bond clusters
            for (int j = 0; j < atomClusters; j++) {
                rule.add("atmCluster" + j + "(X," + Templator.variables[atomIndex] + "), ");
                createGraphlets(rule, ++position);
                position--;
                rule.remove(rule.size() - 1);
            }
        } else {
            for (int j = 0; j < bondClusters; j++) {

                rule.add("bondCluster" + j + "(X," + Templator.variables[atomIndex] + "," + Templator.variables[++atomIndex] + "), ");
                createGraphlets(rule, ++position);
                position--;
                rule.remove(rule.size() - 1);
                atomIndex--;

            }
        }
    }

    public void RDNboost(String path, int folds) throws IOException {
        File[] files = new File(path).listFiles();

        for (File file : files) {
            if (file.isFile()) {
                String pth = file.getAbsolutePath().toString();
                File f = new File(file.getParentFile().getAbsolutePath().toString() + "/" + pth.substring(pth.indexOf("screen_") + 7, pth.indexOf(".txt")));

                new File(f.toString() + "/train").mkdirs();
                List<String> positive = new ArrayList<>();
                List<String> negative = new ArrayList<>();
                List<String> facts = new ArrayList<>();

                Reader reader = new FileReader(file);
                List<Pair<Clause, String>> labeledExampleClauses = PseudoPrologParser.read(reader);
                for (int i = 0; i < labeledExampleClauses.size(); i++) {
                    if (labeledExampleClauses.get(i).s.contains("+")) {
                        positive.add(target + "(d" + i + ").");
                    } else {
                        negative.add(target + "(d" + i + ").");
                    }
                    facts.addAll(transformExample(labeledExampleClauses.get(i).r, i));
                }
                writeSimple(positive, f.getAbsolutePath() + "/train/train_pos.txt");
                writeSimple(negative, f.getAbsolutePath() + "/train/train_neg.txt");
                List<String> l = new ArrayList<String>();
                l.add("import: \"../facts.txt\".");
                writeSimple(l, f.getAbsolutePath() + "/train/train_facts.txt");
                l.clear();
                l.add("import: \"../background.txt\".");
                writeSimple(l, f.getAbsolutePath() + "/train/train_bk.txt");
                l.clear();
                writeSimple(facts, f.getAbsolutePath() + "/facts.txt");

                // Params
                //l.add("setParam: maxTreeDepth=5.");
                //l.add("setParam: nodeSize=1.");
                //l.add("setParam: numOfClauses=8.");
                // MODES
                l.add("mode: active(+drug).");
                l.add("mode: atm(+drug,-atomid,#element).");
                l.add("mode: bond(+drug,+atomid,+atomid,#bondtype).");
                l.add("mode: bond(+drug,+atomid,-atomid,#bondtype).");
                writeSimple(l, f.getAbsolutePath() + "/background.txt");

                //crossvalidation folds
                List<List<String>> posFolds = splitter(folds, positive);
                List<List<String>> negFolds = splitter(folds, negative);
                for (int i = 0; i < folds; i++) {
                    File fold = new File(f.getAbsolutePath() + "/fold" + i);
                    fold.mkdirs();
                    File foldTrain = new File(f.getAbsolutePath() + "/fold" + i + "/train");
                    foldTrain.mkdirs();
                    File foldTest = new File(f.getAbsolutePath() + "/fold" + i + "/test");
                    foldTest.mkdirs();
                    //test
                    writeSimple(posFolds.get(i), foldTest.getAbsolutePath() + "/test_pos.txt");
                    writeSimple(negFolds.get(i), foldTest.getAbsolutePath() + "/test_neg.txt");
                    l.clear();
                    l.add("import: \"../../facts.txt\".");
                    writeSimple(l, foldTest.getAbsolutePath() + "/test_facts.txt");
                    l.clear();
                    l.add("import: \"../../background.txt\".");
                    writeSimple(l, foldTest.getAbsolutePath() + "/test_bk.txt");
                    l.clear();
                    //train
                    List<String> trainPos = new ArrayList<>();
                    List<String> trainNeg = new ArrayList<>();
                    for (int j = 0; j < folds; j++) {
                        if (j == i) continue;
                        trainPos.addAll(posFolds.get(j));
                        trainNeg.addAll(negFolds.get(j));
                    }
                    writeSimple(trainPos, foldTrain.getAbsolutePath() + "/train_pos.txt");
                    writeSimple(trainNeg, foldTrain.getAbsolutePath() + "/train_neg.txt");
                    l.clear();
                    l.add("import: \"../../facts.txt\".");
                    writeSimple(l, foldTrain.getAbsolutePath() + "/train_facts.txt");
                    l.clear();
                    l.add("import: \"../../background.txt\".");
                    writeSimple(l, foldTrain.getAbsolutePath() + "/train_bk.txt");
                    l.clear();
                }
            }
        }
    }

    private static List<String> transformExample(Clause cl, int i) {
        HashSet<String> facts = new HashSet<>();
        for (Literal lit : cl.literals()) {
            if (lit.predicate() == "bond") {
                String bond = "bond(d" + i + ",a" + lit.arguments()[0] + ",a" + lit.arguments()[1] + "," + lit.arguments()[4] + ").";
                facts.add(bond);
                if (lit.arguments()[0] != lit.arguments()[2]) { //due to special format in Mutagenesis
                    String atm1 = "atm(d" + i + ",a" + lit.arguments()[0] + "," + lit.arguments()[2] + ").";
                    String atm2 = "atm(d" + i + ",a" + lit.arguments()[1] + "," + lit.arguments()[3] + ").";
                    facts.add(atm1);
                    facts.add(atm2);
                }
            }
            if (lit.predicate() == "propatom") {
                String atm1 = "atm(d" + i + ",a" + lit.arguments()[0] + "," + lit.arguments()[1] + ").";
                facts.add(atm1);
            }
        }
        return facts.stream().collect(Collectors.toList());
    }

    private static <T> List<List<T>> splitter(int foldnum, List<T> data) {
        Collections.shuffle(data);
        List<List<T>> folds = new ArrayList<>();
        for (int i = 0; i < foldnum; i++) {
            folds.add(new ArrayList<>());
        }
        for (int i = 0; i < data.size(); i++) {
            folds.get(i % foldnum).add(data.get(i));
        }
        return folds;
    }
}
