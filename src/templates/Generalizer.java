/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package templates;

import discoverer.construction.ConstantFactory;
import discoverer.global.TextFileReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static templates.Convertor.path;
import static templates.Templator.makeBaseUniverse;
import templates.input.Similarity;

/**
 *
 * @author Gusta
 */
public class Generalizer extends Templator {

    static String path = "C:\\Users\\Gusta\\googledrive\\Github\\LRNNoldVersion\\in\\jair";

    static int from = 3;
    static int to = 3;

    public static void main(String[] args) {
        //makeEmbeddingsTemplates();
        //makeChargeTemplates();
        makeNormalTemplates();
    }

    public static void makeEmbeddingsTemplates() {
        addToNormal = false;
        kernelTemplate = true;
        bondID = "a";

        File[] files = new File(path).listFiles();

        for (File file : files) {

            System.out.println(file);

            String[] ex = TextFileReader.readFile(file.getAbsolutePath() + "/examplesGeneral", 200000);

            makeBaseUniverse(ex, file.getAbsolutePath() + "/tmp_");

            ArrayList<String> atomrules = null;
            ArrayList<String> bondrules = null;
            atomrules = new ArrayList<>(atomSignatures);
            bondrules = new ArrayList<>(bondSignatures);

            LinkedHashSet<String> rows = new LinkedHashSet<>();

            List<String> ker = createKernelPart();
            rows.addAll(ker);

            ArrayList<String> rows2;
            if (randomFeatures) {
                rows2 = makeRandomFeatures(from, to, atomrules, bondrules);
            } else if (growing) {
                rows2 = makeGrowingFeatures(from, to, atomrules, bondrules);
            } else {
                rows2 = makeFullFeatures(from, to, atomrules, bondrules, atomClusters, bondClusters);
            }

            for (String string : rows2) {
                rows.add(string.replaceAll("atomKappa_A([1-9])\\(", "similarK$1(prototype$1,"));
            }

            writeSimple(new ArrayList<String>(rows), file.getAbsolutePath() + "/kernelTemplate");
        }
    }

    private static ArrayList<String> makeGeneral(String[] ex) {
        ArrayList<String> res = new ArrayList<>();
        for (String string : ex) {
            res.add(string.replaceAll(" (.+?)_[^ .]+?\\(", " $1("));
        }
        return res;
    }

    private static List<String> createKernelPart() {
        ArrayList<String> res = new ArrayList<>();
        String[] atoms = new String[atomSignatures.size()];
        int i = 0;
        for (String atom : atomSignatures) {
            atoms[i++] = atom.substring(0, atom.indexOf("/"));;
        }

        Similarity sim = new Similarity();
        res.add(sim.createSimilarityKernels(3,atoms));

        return res;
    }

    private static List<String> createChargeKernelPart() {
        ArrayList<String> res = new ArrayList<>();
        String[] atoms = new String[atomSignatures.size()];
        int i = 0;
        for (String atom : atomSignatures) {
            atoms[i++] = atom.substring(0, atom.indexOf("/"));;
        }

        Similarity sim = new Similarity();
        res.add(sim.createSimilarityChargeKernels(3, atoms));

        return res;
    }

    private static void makeChargeTemplates() {
        addToNormal = true;
        kernelTemplate = true;
        bondID = "a";

        File[] files = new File(path).listFiles();

        for (File file : files) {
            LinkedHashSet<String> rows = new LinkedHashSet<>();
            System.out.println(file);

            String[] ex = TextFileReader.readFile(file.getAbsolutePath() + "/examples", 200000);

            makeBaseUniverse(ex, file.getAbsolutePath() + "/tmp_");

            ArrayList<String> atomrules = null;
            ArrayList<String> bondrules = null;
            atomrules = new ArrayList<>(atomSignatures);
            bondrules = new ArrayList<>(bondSignatures);

            List<String> ker = createChargeKernelPart();
            rows.addAll(ker);

            ArrayList<String> rows2;
            if (randomFeatures) {
                rows2 = makeRandomFeatures(from, to, atomrules, bondrules);
            } else if (growing) {
                rows2 = makeGrowingFeatures(from, to, atomrules, bondrules);
            } else {
                rows2 = makeFullFeatures(from, to, atomrules, bondrules, atomClusters, bondClusters);
            }
            rows.addAll(rows2);

            writeSimple(new ArrayList<String>(rows), file.getAbsolutePath() + "/chargeJointTemplate");
        }
    }
    
    private static void makeNormalTemplates() {
        addToNormal = false;
        kernelTemplate = false;
        bondID = "a";
        
        bondPrefix = "bondK";
        
        File[] files = new File(path).listFiles();

        for (File file : files) {
            LinkedHashSet<String> rows = new LinkedHashSet<>();
            
            rows.add("bondL1(A,B,I) :- bond(A,B,I).");
            rows.add("bondL2(A,B,I) :- bond(A,C,I), bond(C,B,II).");
            rows.add("0.0 bondK(A,B,I) :- bondL1(A,B,I).");
            rows.add("0.0 bondK(A,B,I) :- bondL2(A,B,I).");
            
            System.out.println(file);

            String[] ex = TextFileReader.readFile(file.getAbsolutePath() + "/examples", 200000);

            LinkedHashSet<String> normal = createTemplate(ex, file.getAbsolutePath() + "/doubleEdgeTemplate", 3, 3);
            rows.addAll(normal);
            writeSimple(new ArrayList<String>(rows), file.getAbsolutePath() + "/doubleEdgeTemplate");
        }
    }
}
