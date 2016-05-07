/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer.construction.template;

import discoverer.global.Global;
import discoverer.global.Glogger;
import discoverer.learning.Saver;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

/**
 * A lightweight class wrapping the lifted network structure, the subclasses
 * offers more functionality (sophisticated merging e.g.)
 *
 * @author Gusta
 */
public class LightTemplate implements Serializable {

    public double[] sharedWeights; //the shared sharedWeights

    public HashMap<String, Integer> name2weight;
    
    public static String weightFolder = Global.weightFolder;

    public LightTemplate() {
    }

    public LightTemplate(double[] sharedW, HashMap<String, Integer> name2weights) {
        sharedWeights = sharedW;
        name2weight = name2weights;
    }

    /**
     * reinitialize all kappa offests and kapparule sharedWeights of the
     * template
     */
    public void invalidateWeights() {
        for (int i = 0; i < sharedWeights.length; i++) {
            sharedWeights[i] = WeightInitializator.getWeight();
        }
    }

    public static void saveTemplate(LightTemplate network, String name) {
        File file = null;

        if (Global.isGUI()) {
            JFrame jf = new JFrame();
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(jf) == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();
            }
        } else {
            file = new File(weightFolder + name + "-networkObject");
        }

        try {
            FileOutputStream fos = new FileOutputStream(file.getAbsoluteFile());
            ObjectOutputStream save = new ObjectOutputStream(fos);
            save.writeObject(network);
            save.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Saver.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Saver.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static LightTemplate loadTemplate() {
        File file = null;
        LiftedTemplate network = null;

        if (Global.isGUI()) {
            JFrame jf = new JFrame();
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(jf) == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();
            }
        } else {
            file = new File(weightFolder + "networkObject");
        }

        try {
            FileInputStream fos = new FileInputStream(file.getAbsoluteFile());
            ObjectInputStream save = new ObjectInputStream(fos);
            network = (LiftedTemplate) save.readObject();
            save.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Saver.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Saver.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Saver.class.getName()).log(Level.SEVERE, null, ex);
        }
        return network;
    }

    public void exportWeightMatrix(String name) {
        BufferedWriter test = null;
        StringBuilder sb = new StringBuilder();
        try {
            test = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(weightFolder + name + "-wector.w"), "utf-8"));
            
            SortedSet<Map.Entry<String, Integer>> sorted = Global.sortByValues(name2weight);
            for (Map.Entry<String, Integer> n2w : sorted) {
                String rule = n2w.getKey().substring(n2w.getKey().indexOf(" ")+1, n2w.getKey().length());
                sb.append(rule).append("; ");
            }
            sb.append("\n");
            for (Map.Entry<String, Integer> n2w : sorted) {
                sb.append(sharedWeights[n2w.getValue()]).append("; ");
            }

            test.write(sb.toString());
            test.close();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Saver.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Saver.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                test.close();
            } catch (IOException ex) {
                Logger.getLogger(Saver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void exportTemplate(String name) {
        BufferedWriter test = null;
        StringBuilder sb = new StringBuilder();
        try {
            test = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(weightFolder + name + "-wector.w"), "utf-8"));

            for (Map.Entry<String, Integer> n2w : name2weight.entrySet()) {
                sb.append(n2w.getKey()).append("; ");
            }
            sb.append("\n");
            for (Map.Entry<String, Integer> n2w : name2weight.entrySet()) {
                sb.append(n2w.getValue()).append("; ");
            }

            test.write(sb.toString());
            test.close();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Saver.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Saver.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                test.close();
            } catch (IOException ex) {
                Logger.getLogger(Saver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * not sure this will work
     *
     * @param pretrainedNetwork
     */
    public void merge(LightTemplate pretrainedNetwork) {
        if (pretrainedNetwork == null) {
            return;
        }
        double[] nsharedWeights = new double[sharedWeights.length + pretrainedNetwork.sharedWeights.length];
        for (int i = 0; i < sharedWeights.length; i++) {
            nsharedWeights[i] = sharedWeights[i];
        }
        for (int i = sharedWeights.length + 1; i < nsharedWeights.length; i++) {
            nsharedWeights[i] = pretrainedNetwork.sharedWeights[i];
        }
        name2weight.putAll(pretrainedNetwork.name2weight);
    }

    public LiftedTemplate mergeOnTop(LiftedTemplate pretrainedN) {
        Glogger.err("network mergin with lightweight template not supported yet");
        return null;
    }

    public static void exportSharedWeights(double[] sharedW, int progress) {
        BufferedWriter test = null;
        try {
            test = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(weightFolder + progress + "wector.csv"), "utf-8"));
            for (int i = 0; i < sharedW.length; i++) {
                test.write(sharedW[i] + ",");
            }
            test.flush();
            test.close();
        } catch (Exception e) {
            Glogger.err("file problem : " + e.getMessage());
        }
    }
}
