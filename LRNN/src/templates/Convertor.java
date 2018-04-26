/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package templates;

import lrnn.global.TextFileReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
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
    //static String in = "..\\extras\\NCIGI\\ncigi.txt";
    //static String out = "in\\ncigi\\examples";
    private static boolean cutTogeneral = false;
    //static String path = "C:\\Users\\IBM_ADMIN\\Google Drive\\Neuralogic\\sourcecodes\\gusta\\extra-data\\NCIGI\\DATA\\out\\";
    //static String path = "C:\\Users\\gusta\\googledrive\\Github\\LRNN\\in\\nci";
    static String path = "C:\\data\\lastjair\\kernels\\";

    public static void main(String[] args) throws IOException {
        File[] files = new File(path).listFiles();

        for (File file : files) {
            if (file.isFile()) {

                String[] ex = TextFileReader.readFile(file.getAbsolutePath(), 200000);

                if (ex[0].contains("_kernelTemplate_")) {
                    Files.copy(file.toPath(), new File(path + "embeddings/" + file.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
                } else if (ex[0].contains("_chargeTemplate_")) {
                    Files.copy(file.toPath(), new File(path + "charge/" + file.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
                } else if (ex[0].contains("_chargeJointTemplate_")) {
                    Files.copy(file.toPath(), new File(path + "chargejoint/" + file.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

    public static void main2(String[] args) {
        File[] files = new File(path).listFiles();

        for (File file : files) {
            if (file.isFile()) {
                ArrayList<String> doubleToSingle = doubleToSingle(file.getAbsolutePath());
                writeSimple(doubleToSingle, file.getParent() + "/nfoil/" + file.getName());
            }
        }
    }

    public static void main0(String[] args) {
        File[] files = new File(path).listFiles();

        for (File file : files) {
            //String newname = file.getName();

            String newname = file.getName();
            new File("C:\\Users\\gusta\\googledrive\\Github\\LRNN\\in\\jair\\" + newname).mkdir();
            try {
                Files.copy(new File(path + "/" + file.getName() + "/examples").toPath(), new File("C:\\Users\\gusta\\googledrive\\Github\\LRNN\\in\\jair\\" + newname + "/examples").toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                Logger.getLogger(Convertor.class.getName()).log(Level.SEVERE, null, ex);
            }

            System.out.println(newname);

            String[] ex = TextFileReader.readFile("C:\\Users\\gusta\\googledrive\\Github\\LRNN\\in\\jair" + "/" + newname + "/examples", 200000);

            Templator.createTemplate(ex, "C:\\Users\\gusta\\googledrive\\Github\\LRNN\\in\\jair" + "/" + newname + "/atomic1", 1, 1);

            Templator.createTemplate(ex, "C:\\Users\\gusta\\googledrive\\Github\\LRNN\\in\\jair" + "/" + newname + "/atomic3", 3, 1);

            Templator.createTemplate(ex, "C:\\Users\\gusta\\googledrive\\Github\\LRNN\\in\\jair" + "/" + newname + "/trichain3", 3, 3);

            //TreeTemplate.createTemplate(i, path + "/" + newname + "/examples", path + "/" + newname + "/trees" + i);
            /*
            Templator.createTemplate(ex, path + "/" + newname + "/1", 1, 1);
            Templator.createTemplate(ex, path + "/" + newname + "/2", 2, 2);
            Templator.createTemplate(ex, path + "/" + newname + "/3", 3, 3);
             */
        }
    }

    public static void main1(String[] args) {
        File[] files = new File(path).listFiles();

        for (File file : files) {
            if (file.isFile()) {
                String ddr = path + file.getName().substring(file.getName().lastIndexOf("screen_") + 7, file.getName().length() - 4);
                new File(ddr).mkdirs();
                convert(path + file.getName(), ddr + "/examplesCharge");
            }
        }
    }

    public static void main3(String[] args) {
        String[] ex = TextFileReader.readFile("C:\\Users\\IBM_ADMIN\\Google Drive\\Neuralogic\\sourcecodes\\gusta\\Neurologic\\in\\muta\\cilp\\examples", 10000);
        createCILP(ex, "C:\\Users\\IBM_ADMIN\\Google Drive\\Neuralogic\\sourcecodes\\gusta\\Neurologic\\in\\muta\\cilp\\out");
    }

    public static void convert(String in, String out) {

        String[] ex = TextFileReader.readFile(in, 10000);

        //createCILP(ex, out);
        ArrayList<LinkedHashSet<String>> examples = transformExamples(ex);

        writeOut(examples, out);
        //writeOut(allLiterals, out + "_literalSet");
    }

    static void createCILP(String[] ex, String out) {
        HashSet<String> newExs = new LinkedHashSet<>();
        for (int i = 0; i < ex.length; i++) {
            String example = ex[i];
            example = transform2CILP(example);
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
            } else // our default format bond(d59_23, d59_5, 0), or some other bond = keep it
             if (literal.contains("(") && !literal.contains(")")) {
                    newEx.add(literal + ")");
                } else {
                    newEx.add(literal);
                }
        } else if (literal.startsWith("atom")) { //e.g., atom(tr000, tr000_4)
            //skip this thing
        } else if (literal.startsWith("atm")) { // e.g., atm(8, C.3, 0.167)
            newLit = split[2].toLowerCase().replace(".", "_") + "(" + split[1] + ")";
            newEx.add(newLit);
            //add the charge!
            newEx.add("charge(" + split[1] + "," + split[3] + ")");
            if (Double.parseDouble(split[3]) < -0.5) {
                System.out.println(split[3]);
            }
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

    public static void writeOut(HashSet<String> literals, String outfile) {
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

    public static void writeOut(ArrayList<LinkedHashSet<String>> examples, String outfile) {
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

    public static void writeSimple(List<String> examples, String outfile) {
        Writer test;
        try {
            test = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outfile), "utf-8"));
            for (String example : examples) {
                test.write(example);
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

    public static ArrayList<String> doubleToSingle(String in) {
        ArrayList<String> out = new ArrayList<>();
        String[] ex = TextFileReader.readFile(in, 10000);
        for (String row : ex) {
            HashMap<String, String> atoms = new HashMap<>();
            String[] split = (row.substring(3, row.length())).split("\\), ");
            //add all atom types
            for (int i = 0; i < split.length; i++) {
                if (split[i].startsWith("atm")) {
                    String[] atm = split[i].split(" ");
                    atoms.put(atm[0].substring(atm[0].indexOf("(") + 1, atm[0].indexOf(",")), atm[1].substring(0, atm[1].indexOf(",") + 1));
                }
            }
            String bb = row.substring(0, 1);
            for (int i = 0; i < split.length; i++) {
                if (split[i].startsWith("bond")) {
                    String[] bond = split[i].split(" ");
                    bb += ",";
                    String prvni = bond[0].substring(bond[0].indexOf("(") + 1, bond[0].indexOf(","));
                    bb += "bond(" + prvni + "," + bond[3] + atoms.get(prvni) + atoms.get(bond[3].substring(0, bond[3].indexOf(","))) + bond[4] + ")";
                }
            }
            String fin = bb.replaceFirst(",", " ");
            fin = fin.replaceAll("\\.", "");
            fin = fin.replaceAll("\\)\\)", ")");
            fin = fin.replaceAll(",", ", ");
            fin = fin.toLowerCase();
            out.add(fin);
        }
        return out;
    }

    private static String transform2CILP(String example) {
        StringBuilder newone = new StringBuilder();
        int cl = example.indexOf(" ");
        newone.append(example.subSequence(0, cl + 1));
        String[] split = example.substring(cl).split("\\),");

        for (String s : split) {
            if (s.contains("bond")) {
                int i = s.indexOf("(");
                int b = s.indexOf(",", i);
                String first = s.substring(i + 1, b);
                String second = s.substring(b + 2, s.indexOf(",", b + 1));

                int indF = example.lastIndexOf("(" + first + ")");
                String ff = example.substring(indF - 10, indF + 1);
                ff = ff.substring(ff.indexOf("), ") + 3, ff.indexOf("("));
                int indS = example.lastIndexOf("(" + second + ")");
                String ss = example.substring(indS - 10, indS + 1);
                ss = ss.substring(ss.indexOf("), ") + 3, ss.indexOf("("));
                newone.append("bond(" + ff + "," + ss + "), ");
            }
        }

        return newone.toString().substring(0, newone.length() - 2);
    }
}
