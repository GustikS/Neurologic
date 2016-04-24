/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer.construction.template;

import static discoverer.construction.template.LightTemplate.weightFolder;
import discoverer.construction.network.rules.KappaRule;
import discoverer.construction.network.rules.Rule;
import discoverer.construction.network.rules.SubK;
import discoverer.global.Global;
import discoverer.grounding.network.GroundKL;
import discoverer.grounding.network.groundNetwork.GroundNetwork;
import discoverer.grounding.network.groundNetwork.GroundNeuron;
import discoverer.learning.Saver;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

/**
 * A placeholder for the whole network and affiliated functions the network
 * itself is otherwise treated recursively by the last/final KL node
 *
 * @author Gusta
 */
public class LiftedTemplate extends LightTemplate implements Serializable {

    public HashMap<String, Integer> weightMapping;  //Kappa offsets and KappaRule's weights to indicies in sharedWeights

    public HashMap<GroundKL, GroundNeuron> neuronMapping; //for checking if we have already visited this groundKL?

    public GroundNetwork tmpActiveNet; //auxiliary to get reference from neurons to their mother network (without storing pointer in them cause of serialization)
    public HashMap<Integer, String> tmpConstantNames;

    public KL last;

    LinkedList<KL> queue = new LinkedList<>();
    /*PriorityQueue<KL> queueSorted = new PriorityQueue<KL>(new Comparator<KL>() { //tmp for BFS
     public int compare(KL kl1, KL kl2) {
     return (kl1.toString().compareToIgnoreCase(kl2.toString()));    //lexicograhpical ordering
     }
     });*/

    public LinkedHashSet<Rule> rules = new LinkedHashSet<>();   //=for network input/output file
    private HashSet<Kappa> kappas = new HashSet<>();
    private HashSet<Lambda> lambdas = new HashSet<>();

    public LiftedTemplate(double[] sharedW, HashMap<String, Integer> name2weights) {
        sharedWeights = sharedW;
        name2weight = name2weights;
    }

    public static LiftedTemplate loadNetwork() {
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

    public LiftedTemplate(KL kl) {
        last = kl;
        queue.add(kl);

        (new File(weightFolder)).mkdirs();

        while (!queue.isEmpty()) {
            KL first = queue.remove();
            if (first instanceof Kappa) {
                this.getRules((Kappa) first);
            } else {
                getRules((Lambda) first);
            }
        }
    }

    private void getRules(Kappa k) {
        getKappas().add(k);
        for (KappaRule kr : k.getRules()) {
            Lambda lam = kr.getBody().getParent();
            queue.add(lam);
            rules.add(kr);
        }
    }

    private void getRules(Lambda l) {
        getLambdas().add(l);
        if (l.getRule() == null) {
            return;
        }
        rules.add(l.getRule());
        for (SubK sk : l.getRule().getBody()) {
            queue.add(sk.getParent());
        }
    }

    void exportOffsets(BufferedWriter test, String name) throws IOException, FileNotFoundException, UnsupportedEncodingException {
        if (getKappas().isEmpty()) {
            return;
        }
        LinkedList<String> kapString = new LinkedList<>();
        test = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(weightFolder + name + "-offsets.w"), "utf-8"));

        for (Kappa kap : getKappas()) {
            kapString.add(kap + " : " + kap.getOffset() + "\n");
        }
        Collections.sort(kapString);
        for (String ks : kapString) {
            test.write(ks);
        }
        test.close();
    }

    @Override
    public void exportTemplate(String name) {
        if (rules.isEmpty()) {
            super.exportTemplate(name);
            return;
        }
        BufferedWriter test = null;
        StringBuilder sb = new StringBuilder();
        try {
            test = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(weightFolder + name + "-rules.w"), "utf-8"));
            ArrayList<Rule> rulzz = new ArrayList(rules);
            for (int i = rulzz.size() - 1; i >= 0; i--) {
                sb.append(rulzz.get(i)).append("\n");
            }
            test.write(sb.toString());
            test.close();
            exportOffsets(test, name);
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
     * @return the kappas
     */
    public HashSet<Kappa> getKappas() {
        return kappas;
    }

    /**
     * @param kappas the kappas to set
     */
    public void setKappas(HashSet<Kappa> kappas) {
        this.kappas = kappas;
    }

    /**
     * @return the lambdas
     */
    public HashSet<Lambda> getLambdas() {
        return lambdas;
    }

    /**
     * @param lambdas the lambdas to set
     */
    public void setLambdas(HashSet<Lambda> lambdas) {
        this.lambdas = lambdas;
    }

    /**
     * backwards mapping of learned weights to template's rules
     * @param weightMapping
     * @param sharedWeights
     * @return 
     */
    public boolean setWeightsFromArray(HashMap<String, Integer> weightMapping, double[] sharedWeights) {
        for (Rule rule : rules) {
            if (rule instanceof KappaRule) {
                KappaRule kr = (KappaRule) rule;
                kr.setWeight(sharedWeights[weightMapping.get(kr.toString())]);
            }
        }
        for (Kappa kappa : getKappas()) {
            kappa.setOffset(sharedWeights[weightMapping.get(kappa.toString())]);
        }
        return true;
    }
}
