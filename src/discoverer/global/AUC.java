/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer.global;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import templates.Convertor;

/**
 *
 * @author Gusta
 */
public class AUC {

    static String path = "c:/results/allNCI/output_parsing/";

    public static void main(String[] args) {
        //runAUC("C:\\results\\allNCI\\output_parsing\\out");

        getAccuracyLRNN("C:\\results\\nciShort\\");
        /*
         File[] files = new File(path).listFiles();
         for (File name : files) {
         if (name.isFile()) {
         ArrayList<double[]> vals = parseAUCfromLog(name.getAbsolutePath());
         writeOut(vals, name.getParent() + "/out/" + name.getName() + "-auc.txt");
         }
         }*/
    }

    static void runAUC(String path) {
        String line;
        String result = "";
        File[] files = new File(path).listFiles();
        for (File name : files) {
            if (name.isFile() && name.getName().endsWith(".txt")) {
                String filename = path + "\\" + name.getName();
                try {
                    Runtime rt = Runtime.getRuntime();
                    Process p = rt.exec("java -jar C:\\results\\allNCI\\output_parsing\\out\\auc.jar " + filename + " list");
                    BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    while ((line = input.readLine()) != null) {
                        result = line;
                    }
                    String[] split = result.split(" ");
                    result = split[split.length - 1];
                    System.out.println(name.getName() + " : " + result);
                    input.close();
                    //p.waitFor();
                } catch (IOException ex) {
                    Logger.getLogger(AUC.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    static void getAccuracyLRNN(String path) {
        File[] files = new File(path).listFiles();
        for (File name : files) {
            if (name.isFile()) {
                try {
                    String line;
                    double testAcc = 0;
                    int folds = 0;
                    BufferedReader buffReader = new BufferedReader(new FileReader(name.getAbsolutePath()));
                    while ((line = buffReader.readLine()) != null) {
                        if (line.contains("Fold Test error :")) {
                            String[] split = line.split(" : ");
                            testAcc += Double.parseDouble(split[1]);
                            folds++;
                        }
                    }
                    testAcc /= folds;
                    System.out.println(name.getName() + " : " + testAcc);
                } catch (IOException ex) {
                    Logger.getLogger(AUC.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public static ArrayList<double[]> parseAUCfromLog(String filename) {
        ArrayList<double[]> values = new ArrayList<>();
        BufferedReader buffReader = null;
        String line = null;
        try {
            buffReader = new BufferedReader(new FileReader(filename));
            while ((line = buffReader.readLine()) != null) {
                if (line.contains("going to test")) {
                    while (!line.contains("Fold Train error")) {
                        while (!line.startsWith("Classified")) {
                            line = buffReader.readLine();
                        }
                        try {
                            double classified = Double.parseDouble(line.substring(14, 16));
                            double expected = Double.parseDouble(line.substring(30, 32));
                            double out = Double.parseDouble(line.substring(41, line.indexOf(" ", 42)));
                            values.add(new double[]{out, expected});
                        } catch (Exception ioe) {
                            Glogger.out(ioe.getMessage() + " -> " + line + "parsing problem");
                        }
                        line = buffReader.readLine();
                    }
                }
            }
        } catch (Exception ioe) {
            Glogger.out(ioe.getMessage() + "problem");
        } finally {
            try {
                buffReader.close();
            } catch (Exception ioe1) {
                //Glogger.err(ioe1.getMessage());
            }
        }
        return values;
    }

    static void writeOut(ArrayList<double[]> values, String outfile) {
        Writer test;
        try {
            test = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outfile), "utf-8"));
            for (double[] lit : values) {
                test.write(lit[0] + " " + (int) lit[1]);
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
}
