/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package templates;

import discoverer.global.FileToStringListJava6;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import static templates.Convertor.writeOut;

/**
 * the templator operates on Neurologic example data format, to convert the examples first use convertor
 * @author Gusta
 */
public class Templator extends Convertor {

    static int clusterCount = 3;    //the width of a template (each cluster multiplies the underneath layer)
    
    static String[] variables = new String[]{"X", "Y", "Z", "U", "V", "W"};
    static String defWeight = "0.0";

    static HashSet<String> atoms = new HashSet();
    static HashSet<String> bonds = new HashSet();
    static HashSet<String> bondSignatures = new HashSet();
    static HashSet<String> atomSignatures = new HashSet();
    static HashSet<String> otherSignatures = new HashSet();

    //static String in = "in\\mutaGeneral\\examples";
    //static String in = "in\\muta\\examples";
    //static String out = "in\\mutaGeneral\\literals";
    //static String out = "in\\muta\\literals";
    
    static String in = "in\\ncigi\\examples";
    static String out = "in\\ncigi\\literals";

    public static void main(String[] args) {

        String[] ex = FileToStringListJava6.convert(in, 10000);

        createTemplates(ex);
    }

    static void createTemplates(String[] ex) {
        for (int i = 0; i < ex.length; i++) {
            String example = ex[i];
            ArrayList<String> newEx = new ArrayList<>();
            String[] split;
            String newLit;

            //dictionary resolvation:
            //String[] literals = example.substring(2).replaceAll("[ .]", "").split("\\)[,]");
            String[] literals = example.substring(example.indexOf(" ")).replaceAll(" ", "").split("\\)[,]");

            createSignatures(literals);

        }

        writeOut(atomSignatures, out + "_atoms");
        writeOut(bondSignatures, out + "_bonds");
        writeOut(otherSignatures, out + "_other");

        ArrayList<String> atomrules = createLambdaBindings(atomSignatures, "atom");
        ArrayList<String> bondrules = createLambdaBindings(bondSignatures, "bond");
        ArrayList<String> otherrules = createLambdaBindings(otherSignatures, "other");

        ArrayList<String> atomClusters = createKappaClusters(atomrules, clusterCount);
        ArrayList<String> bondClusters = createKappaClusters(bondrules, clusterCount);
        ArrayList<String> otherClusters = createKappaClusters(otherrules, clusterCount);

        HashSet<String> template = new LinkedHashSet<>();
        template.addAll(atomrules);
        template.addAll(bondrules);
        template.addAll(atomClusters);
        template.addAll(bondClusters);
        writeOut(template, out + "clusters");
    }

    static void createSignatures(String[] literals) {
        String[] split;
        for (int j = 0; j < literals.length; j++) {
            split = literals[j].replace("(", ",").replace(")", "").split(",");
            switch (split[0]) {
                case "bond":        //should have 3 arguments
                    atoms.add(split[1]);
                    atoms.add(split[2]);
                    bonds.add(split[3]);
                    break;
                default:
            }
        }
        for (int j = 0; j < literals.length; j++) {
            split = literals[j].replace("(", ",").replace(")", "").split(",");
            if (split.length == 2) {
                if (atoms.contains(split[1])) {
                    atomSignatures.add(split[0] + "/" + (split.length - 1));
                } else if (bonds.contains(split[1])) {
                    bondSignatures.add(split[0] + "/" + (split.length - 1));
                }
            } else {
                //System.out.println("warning, not sure what is: " + split[0] + "/" + (split.length - 1) + " -> assuming other-type");
                otherSignatures.add(split[0] + "/" + (split.length - 1));
            }
        }
    }

    static ArrayList<String> createKappaClusters(ArrayList<String> lambdarules, int multiple) {
        ArrayList<String> rules = new ArrayList<>();
        String prefix;
        String rule;
        String args;
        for (int i = 1; i <= multiple; i++) {
            for (String lambdarule : lambdarules) {
                String[] split = lambdarule.split(":-");
                prefix = split[0].substring(0, split[0].indexOf("Lambda"));
                args = split[0].substring(split[0].indexOf("(") + 1, split[0].indexOf(")"));
                rule = defWeight + " " + prefix + "Kappa_" + i + "(" + args + ") :- " + split[0].substring(0, split[0].length() - 1) + ".";
                rules.add(rule);
            }
        }
        return rules;
    }

    static ArrayList<String> createLambdaBindings(HashSet<String> lits, String prefix) {
        ArrayList<String> rules = new ArrayList<>();
        String rule;
        String litname;
        String argsA;
        String argsB;
        int i = 1;
        for (String lit : lits) {
            String[] split = lit.split("/");
            litname = split[0];
            int count = Integer.parseInt(split[1]);
            argsA = arguments(count);
            argsB = arguments(count);
            rule = prefix + "Lambda_" + i++ + "(" + argsA + ") :- " + litname + "(" + argsB + ").";
            rules.add(rule);
        }
        return rules;
    }

    static String arguments(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count - 1; i++) {
            sb.append(variables[i]).append(",");
        }
        sb.append(variables[count - 1]);
        return sb.toString();
    }

}
