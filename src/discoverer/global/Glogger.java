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

    public static void init() {
        StringBuffer file = new StringBuffer();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm");
        Date date = new Date();
        String time = dateFormat.format(date); //2014/08/06 15:59:48
        file.append(time).append("_");
        file.append(Settings.getString());
        String glob = "_";
        glob += Global.pruning ? "pr1" : "pr0";
        glob += Global.forwardCheckEnabled ? "fw1" : "fw0";
        glob += Global.cacheEnabled ? "ch1" : "ch0";
        file.append(glob);
        try {
            File theDir = new File("results");
            if (!theDir.exists()) {
                System.out.println("creating directory results");
                boolean result = false;
                try {
                    theDir.mkdir();
                    result = true;
                } catch (SecurityException se) {
                    Glogger.err(se.getMessage());
                }
                if (result) {
                    System.out.println("DIR created");
                }
            }
            test = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("results/testfile"), "utf-8"));
            test.write("metacetrum file test : " + time);
            test.close();

            training = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("results/training_" + file.toString() + ".csv"), "utf-8"));
            training.write("state, learning_error, dispersion, majority_error, threshold \n");
            results = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("results/results_" + file.toString() + ".csv"), "utf-8"));
            training.flush();
        } catch (UnsupportedEncodingException | FileNotFoundException ex) {
            Logger.getLogger(Glogger.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Glogger.class.getName()).log(Level.SEVERE, null, ex);
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
        System.out.println(string);
    }

    public static void debug(String string) {
        if (Global.debugEnabled) {
            System.out.println(string);
        }
    }

    public static void process(String msg) {
        System.out.println(msg);
    }
}
