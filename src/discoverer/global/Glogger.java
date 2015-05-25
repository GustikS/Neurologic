/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer.global;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gusta
 */
public class Glogger {

    static Writer training;
    static Writer results;
    static Writer test;
    private static final String resultsDir = "../results";

    public static void init() {
        StringBuilder file = new StringBuilder();

        file.append(Settings.getString());
        String glob = "_";
        glob += Global.isPruning() ? "pr1" : "pr0";
        glob += Global.isForwardCheckEnabled() ? "fw1" : "fw0";
        glob += Global.isCacheEnabled() ? "ch1" : "ch0";
        glob += "_";
        file.append(glob);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm");
        Date date = new Date();
        String time = dateFormat.format(date); //2014/08/06 15:59:48
        file.append(time).append("_");

        try {
            createDir(resultsDir);
            test = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resultsDir + "/testfile"), "utf-8"));
            test.write("metacetrum file test : " + time);
            test.close();
            test = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resultsDir + "/testfile"), "utf-8"));
            test.write("metacetrum file test : " + time);
            test.close();

            training = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resultsDir + "/training_" + file.toString() + ".csv"), "utf-8"));
            training.write("state, learning_error, dispersion, majority_error, threshold \n");
            results = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resultsDir + "/results_" + file.toString() + ".csv"), "utf-8"));
            training.flush();
        } catch (UnsupportedEncodingException | FileNotFoundException ex) {
            Logger.getLogger(Glogger.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Glogger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void createDir(String name) {
        File theDir = new File(name);
        if (!theDir.exists()) {
            System.out.println("creating directory :" + name);
            boolean result = false;
            try {
                theDir.mkdir();
                result = true;
            } catch (SecurityException se) {
                Glogger.err(se.getMessage());
            }
            if (result) {
                Glogger.process("DIR created");
            }
        }
    }

    public static void LogTrain(String state, Double[] res) {
        StringBuilder row = new StringBuilder();
        row.append(state).append(",");
        for (Double re : res) {
            row.append(re).append(",");
        }
        try {
            training.write(row.toString() + "\n");
            training.flush();
        } catch (IOException ex) {
            Logger.getLogger(Glogger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void LogTrain(String res) {
        Glogger.process(res);
        try {
            training.write(res + "\n");
            training.flush();
        } catch (IOException ex) {
            Logger.getLogger(Glogger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void LogRes(String res) {
        out(res);
        try {
            results.write(res + "\n");
            results.flush();
        } catch (IOException ex) {
            Logger.getLogger(Glogger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void out(String msg) {
        System.out.println(msg);
    }

    public static void err(String msg) {
        System.err.println(msg);
    }

    public static void info(String string) {
        if (Global.isInfoEnabled()) {
            System.out.println(string);
        }
    }

    public static void debug(String string) {
        if (Global.isDebugEnabled()) {
            System.out.println(string);
        }
    }

    public static void process(String msg) {
        System.out.println(msg);
    }
}
