/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package extras;

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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gusta
 */
public class Convertor {

    static HashSet<String> allLiterals = new HashSet();

    //static String in = "in\\ptcmrExtra\\predictive_toxicology\\ptc_mm.txt";
    static String in = "in\\gi50\\786.txt";
    //static String out = "in\\ptcmrExtra\\mm\\examples";
    static String out = "in\\gi50\\examples";

    public static void main(String[] args) {

        String[] ex = FileToStringListJava6.convert(in,10000);

        ArrayList<ArrayList<String>> examples = new ArrayList<>();

        for (int i = 0; i < ex.length; i++) {
            String example = ex[i];
            ArrayList<String> newEx = new ArrayList<>();
            if (example.startsWith("+")) {
                newEx.add("1.0");
            } else {
                newEx.add("0.0");
            }
            String newLit;

            //dictionary resolvation:
            //String[] literals = example.substring(2).replaceAll("[ .]", "").split("\\)[,]");
            String[] literals = example.substring(2).replaceAll(" ", "").split("\\)[,]");
            for (String literal : literals) {
                if (literal.startsWith("bond")) {
                    String[] split = literal.replace("(", ",").replace(")", "").split(",");
                    if (split[1].equals(split[2])) {    //short duplicated bond,e.g.: bond(16, 16, 13, 13, 1)
                        newLit = "bond(" + split[1] + "," + split[3] + "," + getNumber(split[1], split[3]) + ")";
                        newEx.add(newLit);
                        newLit = split[5] + "(" + getNumber(split[1], split[3]) + ")";
                        newEx.add(newLit);
                    } else {    //long complete bond e.g., bond(tr000_4, tr000_2, cl, c, singlebond)
                        newLit = "bond(" + split[1] + "," + split[2] + "," + getNumber(split[1], split[2]) + ")";
                        newEx.add(newLit);
                        newLit = split[3] + "(" + split[1] + ")";
                        newEx.add(newLit);
                        newLit = split[4] + "(" + split[2] + ")";
                        newEx.add(newLit);
                        newLit = split[5] + "(" + getNumber(split[1], split[2]) + ")";
                        newEx.add(newLit);
                    }
                } else if (literal.startsWith("atom")) { //e.g., atom(tr000, tr000_4)
                    //skip this thing
                } else if (literal.startsWith("atm")) { // e.g., atm(8, C.3, 0.167)
                    String[] split = literal.replace("(", ",").split(",");
                    newLit = split[2].toLowerCase().replace(".", "_") + "(" + split[1] + ")";
                    newEx.add(newLit);
                }
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

        writeOut(examples, out);
        writeOut(allLiterals, out + "_literalSet");

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

    static void writeOut(ArrayList<ArrayList<String>> examples, String outfile) {
        Writer test;
        try {
            test = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outfile), "utf-8"));
            for (ArrayList<String> example : examples) {
                test.write(example.get(0) + " ");
                for (int i = 1; i < example.size() - 1; i++) {
                    String lit = example.get(i);
                    test.write(lit + ", ");
                }
                test.write(example.get(example.size() - 1));
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
        if (couples.containsKey(lit1 + "-" + lit2)) {
            return couples.get(lit1 + "-" + lit2);
        } else {
            String put = "b_" + a++;
            couples.put(lit1 + "-" + lit2, put);
            return put;
        }
    }
}
