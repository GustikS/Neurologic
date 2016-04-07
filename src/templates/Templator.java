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
 * the templator operates on Neurologic example data format, to convert the
 * examples first use convertor
 *
 * @author Gusta
 */
public class Templator extends Convertor {

    public static boolean randomFeatures = false; //randomness in asigning underlying atom and bond clusters
    public static boolean bondTypes = true;
    public static boolean noLambdaBindings = true;

    public static int atomClusters = 3;    //the width of a template (each cluster multiplies the underneath layer)
    public static int bondClusters = 3;

    public static int graphletsCount = 300;
    //static int maxGraphletSize = 3; // (min is 2)
    public static String kappaPrefix = "atom";
    public static String bondPrefix = "bond";
    public static String bondID = "DMY"; // this is a flows object identifier
    //------------------------------------------------------

    static String[] variables = new String[]{"A", "B", "C", "D", "E", "F", "G"}; //some  for variables
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
    //static String in = "in\\ncigi\\examples";
    //static String out = "in\\ncigi\\literals";
    static String[] featuresTemplate = null;    //this can put some default part of template to the end

    public static void main(String[] args) {
        //template = FileToStringListJava6.convert("C:\\Users\\IBM_ADMIN\\Google Drive\\Neuralogic\\sourcecodes\\gusta\\Neurologic\\in\\genericTemplate.txt", 10000);

        String in = "in\\muta\\examples";
        String out = "in\\muta\\rules1";

        String[] ex = FileToStringListJava6.convert(in, 10000);

        //createRings(ex, out);
        createTemplate(ex, out, 1, 1);
    }

    static ArrayList<String> atomComb = new ArrayList<>();

    /**
     * creates chains of atoms of up to a given length and finalization
     *
     * @param chainSize
     * @return
     */
    static ArrayList<String> createFeatures(int chainSize) {
        f = 0;
        featSize = chainSize * 2 - 1;
        atomIndex = 0;
        ArrayList<String> fins = new ArrayList<>();

        if (randomFeatures) {
            int cc = Math.min(graphletsCount - featCount, (int) (Math.pow(atomClusters, chainSize) * Math.pow(bondClusters, chainSize - 1)) / 2);
            createRandomGraphlets(cc);
        } else {
            createGraphlets(new ArrayList<String>(), 0);
        }
        fins.addAll(features);
        int lambdas = features.size();
        features.clear();
        for (int i = 0; i < lambdas; i++) {
            fins.add("0.0 finalKappa(" + bondID + ") :- lambda_" + prefix + i + "(" + bondID + "2).");
        }
        featCount += lambdas;
        return fins;
    }

    static String prefix = "";

    static int featCount = 0;

    static int featSize = 0;
    static ArrayList<String> features = new ArrayList<>();
    static int f = 0;
    static int atomIndex = 0;

    /**
     * creates chains of atoms of a given length
     *
     * @param rule
     * @param position
     */
    public static void createGraphlets(ArrayList<String> rule, int position) {
        String localid = "";
        /*
        if (!bondID.equals("")) {
            localid = bondID + ",";
        }
         */
        if (position == featSize) {
            StringBuilder fin = new StringBuilder();
            fin.append("lambda_").append(prefix).append(f++).append("(").append(bondID).append(") :- ");
            for (int i = 0; i < rule.size(); i++) {
                fin.append(rule.get(i));
            }
            fin.replace(fin.lastIndexOf(","), fin.lastIndexOf(",") + 1, ".");
            features.add(fin.toString());
            return;
        }
        if (position % 2 == 0) { //alternate atom-bond clusters
            for (int j = 1; j <= atomClusters; j++) {
                rule.add(kappaPrefix + "Kappa_" + prefix + j + "(" + variables[atomIndex] + "), ");
                createGraphlets(rule, ++position);
                position--;
                rule.remove(rule.size() - 1);
            }
        } else {
            for (int j = 1; j <= bondClusters; j++) {
                if (bondTypes) {
                    rule.add(bondPrefix + "(" + localid + variables[atomIndex] + "," + variables[++atomIndex] + ",B" + (atomIndex - 1) + "), bondKappa_" + prefix + j + "(B" + (atomIndex - 1) + "), ");
                } else {
                    rule.add(bondPrefix + "(" + localid + variables[atomIndex] + "," + variables[++atomIndex] + "), ");
                }

                createGraphlets(rule, ++position);
                position--;
                rule.remove(rule.size() - 1);
                atomIndex--;

                if (!bondTypes) {
                    break;
                }
            }
        }
    }

    public static void createRandomGraphlets(int rowcount) {
        int j = 0;
        String localid = "";
        if (!bondID.equals("")) {
            localid = bondID + ",";
        }
        ArrayList<String> rule = new ArrayList<>();
        for (int i = 0; i < rowcount; i++) {
            atomIndex = 0;
            rule.clear();
            for (int position = 0; position < featSize; position++) {
                if (position % 2 == 0) { //alternate atom-bond clusters
                    j = (int) (Math.random() * atomClusters + 1);
                    rule.add(kappaPrefix + "Kappa_" + j + "(" + variables[atomIndex] + "), ");
                } else {
                    j = (int) (Math.random() * bondClusters + 1);
                    if (bondTypes) {
                        rule.add(bondPrefix + "(" + localid + variables[atomIndex] + "," + variables[++atomIndex] + ",B" + (atomIndex - 1) + "), bondKappa_" + j + "(B" + (atomIndex - 1) + "), ");
                    } else {
                        rule.add(bondPrefix + "(" + localid + variables[atomIndex] + "," + variables[++atomIndex] + "), ");
                    }
                }
            }
            StringBuilder fin = new StringBuilder();
            fin.append("lambda_").append(prefix).append(f++).append("(" + bondID + ") :- ");
            for (int a = 0; a < rule.size(); a++) {
                fin.append(rule.get(a));
            }
            fin.replace(fin.lastIndexOf(","), fin.lastIndexOf(",") + 1, ".");
            features.add(fin.toString());
        }
    }

    public static void atomCombinations(ArrayList<String> atoms, int chain) {
        if (chain == 0) {
            StringBuilder ex = new StringBuilder("1.0 ");
            for (int i = 0; i < atoms.size() - 1; i++) {
                ex.append(atoms.get(i)).append("(").append(atoms.get(i)).append(i).append("),");
                ex.append(bondPrefix + "(").append(atoms.get(i)).append(i).append(",").append(atoms.get(i + 1)).append(i + 1).append(",b0),");
            }
            ex.append(atoms.get(atoms.size() - 1)).append("(").append(atoms.get(atoms.size() - 1)).append(atoms.size() - 1).append(").");
            atomComb.add(ex.toString());
            return;
        }

        for (String atom1 : atomSignatures) {
            atoms.add(atom1.substring(0, atom1.indexOf("/")));
            atomCombinations(atoms, chain - 1);
            atoms.remove(atoms.size() - 1);
        }
    }

    static ArrayList<String> ringrules = new ArrayList<>();

    /**
     * create new concept of edges as couples of atom-types
     *
     * @param ex
     * @param out
     */
    public static void createRings(String[] ex, String out) {

        int chainLength = 5;
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

        atomCombinations(new ArrayList<String>(), 4);
        writeSimple(atomComb, out + "_examples");

        int tmp = 0;
        for (String atom1 : atomSignatures) {
            for (String atom2 : atomSignatures) {
                ringrules.add("edge" + tmp++ + "(X,Y) :- " + atom1.substring(0, atom1.indexOf("/")) + "(X)," + atom2.substring(0, atom2.indexOf("/")) + "(Y),bond(X,Y,B1).");
            }
        }
        for (int i = 1; i <= chainLength; i++) {
            tmp = 0;
            for (String atom1 : atomSignatures) {
                for (String atom2 : atomSignatures) {
                    ringrules.add("0.0 e" + i + "(X,Y) :- edge" + tmp++ + "(X,Y).");
                }
            }
        }

        for (int i = 3; i <= chainLength; i++) {
            StringBuilder ring = new StringBuilder("ring" + i + "(");
            //StringBuilder ring = new StringBuilder("chain" + i + "(");
            for (int j = 0; j < i; j++) {
                ring.append(variables[j]).append(",");
            }
            ring.replace(ring.lastIndexOf(","), ring.lastIndexOf(",") + 1, ")");
            ring.append(" :- ");
            for (int j = 0; j < i - 1; j++) {
                ring.append("e").append(j + 1).append("(").append(variables[j]).append(",").append(variables[j + 1]).append("),");
            }
            //ring.append("e").append(i).append("(").append(variables[i - 1]).append(",").append(variables[0]).append(").");
            ring.replace(ring.lastIndexOf(","), ring.lastIndexOf(",") + 1, ".");
            ringrules.add(ring.toString());
        }

        for (int i = 3; i <= chainLength; i++) {
            StringBuilder ring = new StringBuilder();
            ring.append("0.0 toxic(DMY) :- ");
            ring.append("ring").append(i).append("(");
            for (int j = 0; j < i; j++) {
                ring.append(variables[j]).append(",");
            }
            ring.replace(ring.lastIndexOf(","), ring.lastIndexOf(",") + 1, ")");
            ring.append(".");
            ringrules.add(ring.toString());
        }
        writeSimple(ringrules, out);
    }

    public static void createTemplate(String[] ex, String out, int from, int to) {

        makeBaseUniverse(ex, out);

        LinkedHashSet<String> rows = new LinkedHashSet<>();

        ArrayList<String> atomrules = null;
        ArrayList<String> bondrules = null;
        if (!noLambdaBindings) {
            atomrules = createLambdaBindings(atomSignatures, "atom");
            rows.addAll(atomrules);
            if (bondTypes) {
                bondrules = createLambdaBindings(bondSignatures, "bond");
                rows.addAll(bondrules);
            }
        } else {
            atomrules = new ArrayList<>(atomSignatures);
            if (bondTypes) {
                bondrules = new ArrayList<>(bondSignatures);
            }
        }

        //ArrayList<String> otherrules = createLambdaBindings(otherSignatures, "other");
        ArrayList<String> rows2;
        if (randomFeatures) {
            rows2 = makeRandomFeatures(from, to, atomrules, bondrules);
        } else {
            rows2 = makeFullFeatures(from, to, atomrules, bondrules, atomClusters, bondClusters);
        }
        rows.addAll(rows2);
        writeOut(rows, out + "rules");
    }

    public static ArrayList<String> makeFullFeatures(int from, int to, ArrayList<String> atomrules, ArrayList<String> bondrules, int atomC, int bondC) {
        ArrayList<String> rows = new ArrayList<>();
        for (int i = from; i <= to; i++) {  //chain length
            prefix = variables[i - from];

            if (bondC == 0) {
                bondTypes = false;
                bondClusters = 1;
            } else {
                bondTypes = true;
            }
            atomClusters = atomC;

            ArrayList<String> atomCl = createKappaClusters(atomrules, atomC, "atom");
            rows.addAll(atomCl);
            if (bondTypes) {
                ArrayList<String> bondCl = createKappaClusters(bondrules, bondC, "bond");
                rows.addAll(bondCl);
            }
            //ArrayList<String> otherClusters = createKappaClusters(otherrules, clusterCount);

            //writeOut(clusters, out + "clusters");
            if (featuresTemplate != null) {
                for (int j = 0; j < featuresTemplate.length; j++) {
                    rows.add(featuresTemplate[j]);
                }
            } else {
                ArrayList<String> feats = createFeatures(i);
                rows.addAll(feats);
            }
        }
        return rows;
    }

    public static ArrayList<String> makeRandomFeatures(int from, int to, ArrayList<String> atomrules, ArrayList<String> bondrules) {
        ArrayList<String> atomClusters = createKappaClusters(atomrules, Templator.atomClusters, "atom");
        ArrayList<String> bondClusters = createKappaClusters(bondrules, Templator.bondClusters, "bond");
        ArrayList<String> rows = new ArrayList<>();
        rows.addAll(atomClusters);
        rows.addAll(bondClusters);

        for (int i = from; i <= to; i++) {  //chain length
            prefix = variables[i - from];

            //writeOut(clusters, out + "clusters");
            if (featuresTemplate != null) {
                for (int j = 0; j < featuresTemplate.length; j++) {
                    rows.add(featuresTemplate[j]);
                }
            } else {
                ArrayList<String> feats = createFeatures(i);
                rows.addAll(feats);
            }
        }
        return rows;
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

    public static ArrayList<String> createKappaClusters(ArrayList<String> lambdarules, int multiple, String atombondPref) {
        ArrayList<String> rules = new ArrayList<>();
        if (lambdarules == null) {
            return rules;
        }
        String atomBond = atombondPref;
        String atom;
        String args;
        String rule;
        for (int i = 1; i <= multiple; i++) {
            for (String lambdarule : lambdarules) {

                if (noLambdaBindings) {
                    String[] split = lambdarule.split("/");
                    atom = split[0];
                    int count;
                    try {
                        count = Integer.parseInt(split[1]);
                    } catch (Exception e) {
                        count = 1;
                        atom = "/";
                    }
                    args = arguments(count);
                } else {
                    String[] split = lambdarule.split(":-");
                    atom = split[0];
                    args = atom.substring(atom.indexOf("(") + 1, atom.indexOf(")"));
                }

                rule = defWeight + " " + atomBond + "Kappa_" + prefix + i + "(" + args + ") :- " + atom + "(" + args + ")" + ".";
                rules.add(rule);
            }
        }
        //kappaPrefix = atomBond;
        return rules;
    }

    public static ArrayList<String> createLambdaBindings(HashSet<String> lits, String prefix) {
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

    private static void makeBaseUniverse(String[] ex, String out) {
        for (int i = 0; i < ex.length; i++) {
            String example = ex[i];
            ArrayList<String> newEx = new ArrayList<>();
            String[] split;
            String newLit;

            //dictionary resolvation:
            //String[] literals = example.substring(2).replaceAll("[ .]", "").split("\\)[,]");
            String str = example.substring(example.indexOf(" ")).replaceAll(" ", "");
            str = str.substring(0, str.length()-1);
            String[] literals = str.split("\\)[,]");

            createSignatures(literals);

        }

        writeOut(atomSignatures, out + "_atoms");
        writeOut(bondSignatures, out + "_bonds");
        writeOut(otherSignatures, out + "_other");
    }

}
