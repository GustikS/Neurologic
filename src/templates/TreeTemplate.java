/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package templates;

import discoverer.global.TextFileReader;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import static templates.Templator.prefix;

/**
 *
 * @author Gusta
 */
public class TreeTemplate extends Templator {

    public static void createTemplate(int size, String in, String out) {

        String[] ex = TextFileReader.readFile(in, 10000);

        ArrayList<String> atomrules = null;
        ArrayList<String> bondrules = null;

        makeBaseUniverse(ex, out);
        atomrules = new ArrayList<>(atomSignatures);
        if (bondTypes) {
            bondrules = new ArrayList<>(bondSignatures);
        }
        ArrayList<String> makeTreeFeatures = makeTreeFeatures(size, "../in/graphlets.txt", atomrules, bondrules);
        writeSimple(makeTreeFeatures, out);
    }

    private static ArrayList<String> makeTreeFeatures(int size, String path, ArrayList<String> atomrules, ArrayList<String> bondrules) {

        ArrayList<String> rows = new ArrayList<>();
        String[] readFile = TextFileReader.readFile(path, 10000);

        int maxSize = 0;
        int block = 0;
        String prepre = "0_";
        for (int i = 0; i < readFile.length; i++) {
            
            if (readFile[i].startsWith("%")) {
                maxSize = 0;
                prepre = block + "_";
                rows.add("");
                f = 0;

                if (++block == size + 1) {
                    break;
                }

                continue;
            }
            
            prefix = prepre + variables[maxSize++];
            
            atomClusters = block;
            bondClusters = block - 1;

            ArrayList<String> atomCl = createKappaClusters(atomrules, atomClusters, "atom");
            rows.addAll(atomCl);
            if (bondTypes) {
                ArrayList<String> bondCl = createKappaClusters(bondrules, bondClusters, "bond");
                rows.addAll(bondCl);
            }
            

            LinkedHashSet<String> rule = new LinkedHashSet<>();
            StringBuilder sb = new StringBuilder();
            sb.append("lambda_").append(prefix).append(f).append("(").append(bondID).append(") :- ");
            String[] split = readFile[i].split(" ");
            for (int j = 0; j < split.length; j++) {
                if (split[j].contains("-")) {
                    String[] split1 = split[j].split("-");
                    rule.add(bondPrefix + "(" + variables[Integer.parseInt(split1[0]) - 1] + "," + variables[Integer.parseInt(split1[1]) - 1] + ",B" + (j) + "), bondKappa_" + prefix + (j + 1) + "(B" + (j) + "), ");
                    for (int k = 0; k < split1.length; k++) {
                        String s = kappaPrefix + "Kappa_" + prefix + split1[k] + "(" + variables[Integer.parseInt(split1[k]) - 1] + "), ";
                        rule.add(s);
                    }
                } else {
                    String s = kappaPrefix + "Kappa_" + prefix + split[j] + "(" + variables[Integer.parseInt(split[j]) - 1] + "), ";
                    rule.add(s);
                }
            }
            for (String string : rule) {
                sb.append(string);
            }
            sb.replace(sb.length() - 2, sb.length(), ".");
            rule.clear();
            rows.add(sb.toString());
            rows.add("0.0 finalKappa :- lambda_" + prefix + f + "(DMY2).");
            f++;
        }

        return rows;
    }
}
