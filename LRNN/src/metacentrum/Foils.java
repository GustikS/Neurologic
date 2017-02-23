/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metacentrum;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

import templates.Convertor;

/**
 *
 * @author Gusta
 */
public class Foils {

    static String common = "qsub -q q_1d@wagap.cerit-sc.cz -l walltime=1d -l mem=10gb -l scratch=50mb -l nodes=1:ppn=1 ";
    static String outPath = "C:\\Users\\IBM_ADMIN\\Google Drive\\Neuralogic\\sourcecodes\\gusta\\metacentrum\\kFoil\\";

    public static void main(String[] args) {
        //String path = "C:\\Users\\IBM_ADMIN\\Google Drive\\Neuralogic\\sourcecodes\\gusta\\extra-data\\NCIGI\\DATA\\out\\nfoil\\conv";
        //nFoil(path);
        getAccuracyNfoil("C:\\Users\\IBM_ADMIN\\Google Drive\\Neuralogic\\sourcecodes\\gusta\\extra-data\\NCIGI\\DATA\\out\\nfoil\\nfoilResults");
        
        //String path = "C:\\Users\\IBM_ADMIN\\Google Drive\\Neuralogic\\sourcecodes\\gusta\\extra-data\\NCIGI\\DATA\\out\\nfoil\\kfoil";
        //kFoil(path);
        //getAccuracyKfoil("C:\\Users\\IBM_ADMIN\\Google Drive\\Neuralogic\\sourcecodes\\gusta\\extra-data\\NCIGI\\DATA\\out\\nfoil\\kfoil\\archive");
    }

    public static void kFoil(String path) {
        String inq = "/storage/brno2/home/souregus/yap/src/arch/yap -L ";
        File[] files = new File(path).listFiles();
        String text = "";
        for (File dir : files) {
            if (dir.isDirectory()) {
                String tmp = "cd /storage/brno2/home/souregus/related_works/kfoil/nci/" + dir.getName() + "\n";
                text += common + dir.getName() + "_script.sh \n";
                //String script = tmp + inq + dir.getName() + "_fold0.pl > fold0.out.txt \n";
                //script += inq + dir.getName() + "_fold1.pl > fold1.out.txt \n";
                //script += inq + dir.getName() + "_fold2.pl > fold2.out.txt ";
                String script = tmp + inq + "train_test.pl > out.txt";
                writeOut(script, dir.getName() + "_script.sh");
            }
        }
        writeOut(text, "qsub.sh");
    }

    public static void nFoil(String path) {
        String inq = "cd /storage/brno2/home/souregus/related_works/nfoil/ \n";
        String params = " -P -c3 -C20 -b10 -t 0.01 > ./results/results_";
        File[] files = new File(path).listFiles();
        String text = "";
        for (File name : files) {
            if (name.isFile()) {
                text += common + name.getName() + "_script.sh \n";
                writeOut(inq + "./bin/nfoil " + "./nci/" + name.getName() + params + name.getName().substring(0, name.getName().length() - 3), name.getName() + "_script.sh");
            }
        }
        writeOut(text, "qsub.sh");
    }

    static void getAccuracyNfoil(String path) {
        File[] files = new File(path).listFiles();
        String actual;
        for (File name : files) {
            if (name.isFile()) {
                try {
                    String line;
                    BufferedReader buffReader = new BufferedReader(new FileReader(name.getAbsolutePath()));
                    while ((line = buffReader.readLine()) != null) {
                        if (line.contains("CV Error =")) {
                            String[] split = line.split(" ");
                            System.out.println(name.getName() + " : " + split[3]);
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Foils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    static void getAccuracyKfoil(String path) {
        File[] files = new File(path).listFiles();
        String actual;
        for (File name : files) {
            if (name.isDirectory()) {
                try {
                    actual = path + "/" + name.getName() + "/out.txt";
                    String line;
                    BufferedReader buffReader = new BufferedReader(new FileReader(actual));
                    while ((line = buffReader.readLine()) != null) {
                        if (line.contains("Test set accuracy")) {
                            String[] split = line.split(" ");
                            System.out.println(name.getName() + " : " + split[split.length - 1]);
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Foils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    static void writeOut(String str, String outfile) {
        Writer test;
        try {
            test = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outPath + outfile), "utf-8"));
            test.write(str);
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
