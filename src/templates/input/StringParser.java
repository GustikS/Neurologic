/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package templates.input;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.monitor.MonitorNotification;
import static templates.Convertor.writeSimple;
import templates.Templator;
import static templates.Templator.makeFullFeatures;

/**
 *
 * @author Gusta
 */
public class StringParser {

    static HashSet<String> alphabet = new HashSet<>();

    public static void main(String[] args) {
        String filename = "dga";
        StringParser sp = new StringParser();
        String[] predicates = sp.createExamples(filename);

        ArrayList<String> template = sp.createTemplate("letter", predicates[1]);
        writeSimple(template, "in/strings/" + filename + "-rules.txt");
    }

    public String[] createExamples(String filename) {
        ArrayList<String> lines = readLines("in/strings/" + filename + ".csv");
        String header = lines.remove(0);
        String[] split = header.split(",");

        ArrayList<String> examples = new ArrayList<>();
        for (String line : lines) {
            examples.add(string2Structure(split, line));
        }
        writeSimple(examples, "in/strings/" + filename + "-examples.txt");

        return split;
    }

    public ArrayList<String> createTemplate(String typePredicate, String bondPredicate) {
        Templator.bondPrefix = bondPredicate;
        ArrayList<String> lambdaBindings = Templator.createLambdaBindings(alphabet, typePredicate);
        ArrayList<String> features = makeFullFeatures(2, 4, lambdaBindings, null, 3, 0);
        lambdaBindings.addAll(features);
        return lambdaBindings;
    }

    public String string2Structure(String[] header, String string) {
        StringBuilder sb = new StringBuilder();

        String[] split = string.split(",");

        sb.append(split[0]).append(" ");
        for (int a = 1; a < split.length; a++) {
            String str = split[a];
            System.out.println(str);
            for (int i = 0; i < str.length() - 1; i++) {
                alphabet.add(str.charAt(i) + "/1");
                sb.append(header[a]).append("(").append(str.charAt(i)).append("").append(i).append(",").append(str.charAt(i + 1)).append("").append(i + 1).append("), ");
                sb.append(str.charAt(i)).append("(").append(str.charAt(i)).append(i).append("), ");
            }
            sb.append(str.charAt(str.length() - 1)).append("(").append(str.charAt(str.length() - 1)).append(str.length() - 1).append("), ");
            alphabet.add(str.charAt(str.length() - 1) + "/1");
        }
        sb.replace(sb.lastIndexOf(","), sb.lastIndexOf(",") + 1, ".");
        return sb.toString();
    }

    public String string2Structure(String bond, String str, String id) {
        StringBuilder sb = new StringBuilder();

        System.out.println(str);
        for (int i = 0; i < str.length() - 1; i++) {
            alphabet.add(str.charAt(i) + "/1");
            sb.append(bond).append("(").append(id).append(",").append(str.charAt(i)).append("_").append(i).append(",").append(str.charAt(i + 1)).append("_").append(i + 1).append("), ");
            sb.append(str.charAt(i)).append("(").append(str.charAt(i)).append("_").append(i).append("), ");
        }
        sb.append(str.charAt(str.length() - 1)).append("(").append(str.charAt(str.length() - 1)).append("_").append(str.length() - 1).append("), ");
        alphabet.add(str.charAt(str.length() - 1) + "/1");

        return sb.toString();
    }

    public ArrayList<String> readLines(String infile) {
        ArrayList<String> res = new ArrayList<>();
        try {
            BufferedReader bi = new BufferedReader(new FileReader(infile));
            String line;
            while ((line = bi.readLine()) != null) {
                res.add(line);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(StringParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(StringParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }
}
