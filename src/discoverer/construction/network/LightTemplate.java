/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer.construction.network;

import discoverer.global.Global;
import discoverer.global.Glogger;
import discoverer.learning.Saver;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

/**
 * A lightweight class wrapping the lifted network structure, the subclass
 * offers more functionality (sophisticated merging)
 *
 * @author Gusta
 */
public class LightTemplate implements Serializable {

    public double[] sharedWeights; //the shared sharedWeights

    public HashMap<String, Integer> name2weight;

    public static String weightFolder = "weights/";

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

    public static void saveNetwork(LightTemplate network, String name) {
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

    public static LightTemplate loadNetwork() {
        File file = null;
        MolecularTemplate network = null;

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
            network = (MolecularTemplate) save.readObject();
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

    public void exportWeightMatrix(String string) {
        Glogger.err("weight matrix export with lightweight template not supported yet");
    }

    public void exportTemplate(String string) {
        Glogger.err("template export with lightweight template not supported yet");
    }

    public void merge(LightTemplate pretrainedNetwork) {
        Glogger.err("network mergin with lightweight template not supported yet");
    }

    public LiftedTemplate mergeOnTop(LiftedTemplate pretrainedN) {
        Glogger.err("network mergin with lightweight template not supported yet");
        return null;
    }

}
