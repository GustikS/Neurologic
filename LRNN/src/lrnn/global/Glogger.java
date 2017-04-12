/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lrnn.global;

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
public final class Glogger {

    static Writer training;
    static Writer results;
    static Writer test;
    public static String resultsDir = Settings.getResultsDir();

    static final boolean timeMeasures = true;
    private static long clock = System.currentTimeMillis();

    public static void init() {
        StringBuilder file = new StringBuilder();
        StringBuilder options = new StringBuilder();

        options.append(Settings.getString());
        String glob = "_";
        glob += Global.isPruning() ? "pr1" : "pr0";
        glob += Global.isForwardCheckEnabled() ? "fw1" : "fw0";
        glob += Global.isCacheEnabled() ? "ch1" : "ch0";
        glob += "_";
        options.append(glob);

        if (Global.longName) {
            file.append(options);
        } else {
            file.append(Settings.getDataset());
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm");
        Date date = new Date();
        String time = dateFormat.format(date);
        file.append("_").append(time);

        if (Global.exporting) {
            try {
                createDir(resultsDir);
                test = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resultsDir + "/testfile"), "utf-8"));
                test.write("metacetrum file test : " + time);
                test.close();

                training = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resultsDir + "/training_" + file.toString() + ".csv"), "utf-8"));
                training.write("state, learning_error, dispersion, majority_error, threshold, mse \n");
                results = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resultsDir + "/results_" + file.toString() + ".csv"), "utf-8"));
                LogRes(options.toString());
                training.flush();
            } catch (UnsupportedEncodingException | FileNotFoundException ex) {
                Logger.getLogger(Glogger.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Glogger.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void createDir(String name) {
        File theDir = new File(name);
        if (!theDir.exists()) {
            System.out.println("creating directory :" + name);
            boolean result = false;
            try {
                theDir.mkdirs();
                result = true;
            } catch (SecurityException se) {
                Glogger.err(se.getMessage());
            }
            if (result) {
                Glogger.process("DIR created");
            }
        }
    }

    public static final void LogTrain(String state, Double[] res) {
        if (!Global.exporting) {
            return;
        }
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

    public static final void LogTrain(String res) {
        Glogger.process(res);
        if (!Global.exporting) {
            return;
        }
        try {
            training.write(res + "\n");
            training.flush();
        } catch (IOException ex) {
            Logger.getLogger(Glogger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static final void LogRes(String res) {
        out(res);
        if (!Global.exporting) {
            return;
        }
        try {
            results.write(res + "\n");
            results.flush();
        } catch (IOException ex) {
            Logger.getLogger(Glogger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static final void out(String msg) {
        System.out.println(msg);
    }

    public static final void err(String msg) {
        System.err.println(msg);
    }

    public static final void info(String string) {
        if (Global.isInfoEnabled()) {
            System.out.println(string);
        }
    }

    public static final void debug(String string) {
        if (Global.isDebugEnabled()) {
            System.out.println(string);
        }
    }

    public static final void process(String msg) {
        if (timeMeasures) {
            clock(msg);
        } else {
            System.out.println(msg);
        }
    }

    public static final void clock(String update) {
        Glogger.info(update + " : " + (System.currentTimeMillis() - clock) + " ms");
        clock = System.currentTimeMillis();
    }
}
