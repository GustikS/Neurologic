/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package templates;

import discoverer.construction.ExampleFactory;
import discoverer.construction.Parser;
import discoverer.construction.example.Example;
import discoverer.global.FileToStringListJava6;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gusta
 */
public class Convertor {

    static HashSet<String> allLiterals = new HashSet();

    //static String in = "..\\extras\\predictive_toxicology\\ptc_fm.txt";
    //static String in = "in\\mutaGeneral\\examples";
    //static String out = "in\\ptc\\fm\\examples";
    //static String out = "in\\mutaGeneral\\examplesGeneral";
    
    static String in = "..\\extras\\NCIGI\\ncigi.txt";
    static String out = "in\\ncigi\\examples";

    private static boolean cutTogeneral = false;

    public static void main(String[] args) {

        String[] ex = FileToStringListJava6.convert(in, 10000);

        createCILP(ex);

        ArrayList<LinkedHashSet<String>> examples = transformExamples(ex);

        writeOut(examples, out);
        writeOut(allLiterals, out + "_literalSet");
    }

    static void createCILP(String[] ex) {
        HashSet<String> newExs = new LinkedHashSet<>();
        for (int i = 0; i < ex.length; i++) {
            String example = ex[i];
            example = example.replace("0.0", "~class:-");
            example = example.replace("1.0", "class:-");
            example = example.replaceAll("([^)]),", "$1;");
            example = example.replace(".", "");
            example = example.replaceAll(" ", "");
            newExs.add(example);
        }
        writeOut(newExs, out + "_CILP");
    }

    static ArrayList<LinkedHashSet<String>> transformExamples(String[] ex) {
        ArrayList<LinkedHashSet<String>> examples = new ArrayList<>();
        for (int i = 0; i < ex.length; i++) {
            String example = ex[i];
            LinkedHashSet<String> newEx = new LinkedHashSet<>();

            int headSeparator = example.indexOf(" ");
            String eClass = example.substring(0, headSeparator);    //I hope it's always separated by a space
            example = example.substring(headSeparator, example.length());
            setClass(eClass, newEx);

            example = example.replaceAll(" ", "");  //no spaces needed from now on
            if (example.endsWith(".")) {    //we dont want this terminal
                example = example.substring(0, example.length() - 1);
            }
            String[] literals = example.split("\\)[,]");
            for (String literal : literals) {
                addFromDictionary(literal, newEx);
            }
            for (String literal : newEx) {
                int ind = literal.indexOf("(");
                if (ind > 0) {
                    allLiterals.add(literal.substring(0, ind));
                }
            }
            examples.add(newEx);
        }
        for (String literal : allLiterals) {
            System.out.println(literal);
        }
        return examples;
    }

    static void setClass(String eClass, LinkedHashSet<String> newEx) {
        if (eClass.contains("+")) {
            newEx.add("1.0");
        } else if (eClass.contains("-")) {
            newEx.add("0.0");
        } else { //keep it the same way
            newEx.add(eClass);
        }
    }

    /**
     * the dictionary is not really nice, but it should cover the basic datasets
     * we work with
     *
     * @param literal
     * @param newEx
     */
    static void addFromDictionary(String literal, LinkedHashSet<String> newEx) {
        String newLit;
        String[] split = literal.replace("(", ",").replace(")", "").split(",");
        if (literal.startsWith("bond")) {
            if (split[1].equals(split[2])) {    //short duplicated bond,e.g.: bond(16, 16, 13, 13, 1) = NCIGI
                newLit = "bond(" + split[1] + ", " + split[3] + ", " + getNumber(split[1], split[3]) + ")";
                newEx.add(newLit);
                newLit = split[5] + "(" + getNumber(split[1], split[3]) + ")";
                newEx.add(newLit);
            } else if (split.length == 6) {    //long complete bond e.g., bond(tr000_4, tr000_2, cl, c, singlebond) = PTC
                newLit = "bond(" + split[1] + ", " + split[2] + ", " + getNumber(split[1], split[2]) + ")";
                newEx.add(newLit);
                newLit = split[3] + "(" + split[1] + ")";
                newEx.add(newLit);
                newLit = split[4] + "(" + split[2] + ")";
                newEx.add(newLit);
                newLit = split[5] + "(" + getNumber(split[1], split[2]) + ")";
                newEx.add(newLit);
            } else {    // our default format bond(d59_23, d59_5, 0), or some other bond = keep it
                if (literal.contains("(") && !literal.contains(")")) {
                    newEx.add(literal + ")");
                } else {
                    newEx.add(literal);
                }
            }
        } else if (literal.startsWith("atom")) { //e.g., atom(tr000, tr000_4)
            //skip this thing
        } else if (literal.startsWith("atm")) { // e.g., atm(8, C.3, 0.167)
            newLit = split[2].toLowerCase().replace(".", "_") + "(" + split[1] + ")";
            newEx.add(newLit);
        } else {    //some unknown literal, probably some specific atom/bond type
            if (cutTogeneral) {
                String head = literal.substring(0, literal.indexOf("("));
                if (head.contains("_")) {
                    head = head.substring(0, head.indexOf("_"));    //generalization
                    String body = literal.substring(literal.indexOf("("), literal.length());
                    literal = head + body;
                }
            }
            if (literal.contains("(") && !literal.contains(")")) {
                newEx.add(literal + ")");
            } else {
                newEx.add(literal);
            }
        }
    }

    static void writeOut(HashSet<String> literals, String outfile) {
        Writer test;
        try {
            test = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outfile), "utf-8"));
            for (String lit : literals) {
                test.write(lit);
                test.write("\n");
            }
            test.close();
        } catch (UnsupportedEncodingException ex1) {
            Logger.getLogger(Convertor.class.getName()).log(Level.SEVERE, null, ex1);
        } catch (FileNotFoundException ex1) {
            Logger.getLogger(Convertor.class.getName()).log(Level.SEVERE, null, ex1);
        } catch (IOException ex1) {
            Logger.getLogger(Convertor.class.getName()).log(Level.SEVERE, null, ex1);
        }
    }

    static void writeOut(ArrayList<LinkedHashSet<String>> examples, String outfile) {
        Writer test;
        try {
            test = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outfile), "utf-8"));
            for (LinkedHashSet<String> example : examples) {
                StringBuilder sb = new StringBuilder();
                Iterator<String> ite = example.iterator();

                sb.append(ite.next()).append(" ");  //the head is separate

                while (ite.hasNext()) {
                    sb.append(ite.next()).append(", ");
                }
                sb.replace(sb.length() - 2, sb.length(), ".");  //the termination

                test.write(sb.toString());
                test.write("\n");

            }
            test.close();
        } catch (UnsupportedEncodingException ex1) {
            Logger.getLogger(Convertor.class.getName()).log(Level.SEVERE, null, ex1);
        } catch (FileNotFoundException ex1) {
            Logger.getLogger(Convertor.class.getName()).log(Level.SEVERE, null, ex1);
        } catch (IOException ex1) {
            Logger.getLogger(Convertor.class.getName()).log(Level.SEVERE, null, ex1);
        }
    }

    private static HashMap<String, String> couples = new HashMap();
    static int a = 0;

    private static String getNumber(String lit1, String lit2) {
        String join;
        if (lit1.compareToIgnoreCase(lit2) < 0) {
            join = lit1 + "-" + lit2;
        } else {
            join = lit2 + "-" + lit1;
        }
        if (couples.containsKey(join)) {
            return couples.get(join);
        } else {
            String put = "b_" + a++;
            couples.put(join, put);
            return put;
        }
    }
}
