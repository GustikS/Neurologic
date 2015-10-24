/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer.construction.network;

import discoverer.construction.network.rules.KappaRule;
import discoverer.construction.network.rules.LambdaRule;
import discoverer.construction.network.rules.Rule;
import discoverer.construction.network.rules.SubK;
import discoverer.global.Global;
import discoverer.global.Glogger;
import discoverer.global.Settings;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
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
public class Network implements Serializable {

    public KL last;

    int clustersCount;
    int elementsCount;

    LinkedList<KL> queue = new LinkedList<>();
    /*PriorityQueue<KL> queueSorted = new PriorityQueue<KL>(new Comparator<KL>() { //tmp for BFS
     public int compare(KL kl1, KL kl2) {
     return (kl1.toString().compareToIgnoreCase(kl2.toString()));    //lexicograhpical ordering
     }
     });*/

    public LinkedHashSet<Rule> rules = new LinkedHashSet<>();   //=for network input/output file
    private HashSet<Kappa> kappas = new HashSet<>();
    private HashSet<Lambda> lambdas = new HashSet<>();

    private LinkedHashSet<Kappa> elements = new LinkedHashSet<>();
    LinkedHashSet<Kappa> clusters = new LinkedHashSet<>();
    //for clustering
    LinkedHashMap<KappaRule, String> ruleToElement = new LinkedHashMap<>();  //atom/bond types
    LinkedHashMap<String, LinkedHashMap<String, KappaRule>> elementToRule = new LinkedHashMap<>();  //atom/bond types

    //for weight matrix export
    KappaRule[][] weightMatrix;

    public static String weightFolder = "weights/";

    public String toString() {
        return "rules=" + rules.size() + ",kappas=" + getKappas().size() + ", lambdas=" + getLambdas().size() + ",elements=" + getElements().size() + ",clusters=" + clustersCount;
    }

    /**
     * reinitialize all kappa offests and kapparule weights
     */
    public void invalidateWeights() {
        for (Kappa k : getKappas()) {
            k.initOffset();
        }
        for (Rule r : rules) {
            if (r instanceof KappaRule) {
                KappaRule kr = (KappaRule) r;
                kr.setWeight(WeightInitializator.getWeight());
            }
        }
    }

    public Network mergeOnTop(Network net) {
        if (net == null) {
            return this;
        }
        Kappa l1 = null;
        Kappa l2 = null;

        if (net.last instanceof Lambda) {
            if (last instanceof Lambda) {   //extract final kappas (hopefuly)
                Lambda tmp = (Lambda) last;
                l1 = tmp.getRule().getBody().get(0).getParent();
                tmp = (Lambda) net.last;
                l2 = tmp.getRule().getBody().get(0).getParent();
            } else {
                Glogger.err("not the same final structure");
                return this;
            }
        } else { //kappa
            if (last instanceof Kappa) {
                l1 = (Kappa) last;
                l2 = (Kappa) net.last;
            } else {
                Glogger.err("not the same final structure");
                return this;
            }
        }
        l1.getRules().addAll(l2.getRules());
        Network network = new Network(last);    //pro jistotu

        return network;
    }

    /**
     * we assume the same number and name-convention of clusters!! the order may
     * be switched and the number of elements may differ
     *
     * @param net
     */
    public void merge(Network net) {
        if (net == null) {
            return;
        }
        Glogger.process("merging network " + Settings.getRules() + " with " + elementsCount + " elements and " + clustersCount + " clusters...");
        Glogger.process("...with network " + Settings.getPretrained() + " with " + net.elementsCount + " elements and " + net.clustersCount + " clusters...");

        if (clustersCount != net.clustersCount) {
            Glogger.err("clusters count do not match!!");
        }

        int mrgs = 0;
        //now copy kapparule's weights
        for (KappaRule kr : ruleToElement.keySet()) {
            String atom = ruleToElement.get(kr); //final literal of this rule
            LinkedHashMap<String, KappaRule> cluster = net.elementToRule.get(atom);
            if (cluster != null) { //the other net contains it too?
                KappaRule kr2 = cluster.get(kr.getHead().toString());
                Glogger.process(ruleToElement.get(kr) + " : " + kr + " <- " + kr2);
                kr.setWeight(kr2.getWeight());  //take the pretrained weight of second net
                mrgs++;
            }
        }
        Glogger.process("...succesfully replaced " + mrgs + " cluster-rule weights from pretrained template!");
    }

    public Network(KL kl) {

        last = kl;
        queue.add(kl);

        (new File(weightFolder)).mkdirs();

        while (!queue.isEmpty()) {
            KL first = queue.remove();
            if (first instanceof Kappa) {
                getRules((Kappa) first);
            } else {
                getRules((Lambda) first);
            }
        }
        elementsCount = elements.size();
        clustersCount = clusters.size();
        createWeightMatrix();
    }

    private void getRules(Kappa k) {
        getKappas().add(k);
        if (k.isElement()) {
            getElements().add(k);
            return;
        }
        for (KappaRule kr : k.getRules()) {
            Lambda lam = kr.getBody().getParent();
            if (propagateToElement(kr)) {   //this kappa forms a cluster
                clusters.add(k);
            }
            queue.add(lam);
            rules.add(kr);
        }
    }

    private void getRules(Lambda l) {
        getLambdas().add(l);
        rules.add(l.getRule());
        for (SubK sk : l.getRule().getBody()) {
            queue.add(sk.getParent());
        }
    }

    /**
     * extra function for merging networks = matching clusterings we need to
     * which final element a kappa rule leads (if any single/unique)
     *
     * @param kr
     */
    private boolean propagateToElement(KappaRule kr) {
        List<SubK> body = kr.getBody().getParent().getRule().getBody();
        if (body.size() == 1) {
            SubK atom = body.get(0);
            if (atom.isElement()) {
                ruleToElement.put(kr, atom.getParent().name);
                LinkedHashMap<String, KappaRule> mapping = elementToRule.get(atom.getParent().name);
                if (mapping == null) {
                    mapping = new LinkedHashMap<>();
                    elementToRule.put(atom.getParent().name, mapping);
                }
                mapping.put(kr.getHead().toString(), kr);
                return true;
            }
        }
        return false;
    }

    public void exportTemplate(String name) {
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

    public static void saveNetwork(Network network, String name) {
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

    public static Network loadNetwork() {
        File file = null;
        Network network = null;

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
            network = (Network) save.readObject();
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
        try {
            test = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(weightFolder + name + "-weightMatrix.csv"), "utf-8"));
            test.write("weights,");
            for (int i = 1; i < weightMatrix[0].length; i++) {
                test.write(weightMatrix[0][i].original + ",");
            }
            test.write("\n");
            for (int i = 1; i < weightMatrix.length; i++) {
                test.write(weightMatrix[i][0].original + ",");
                for (int j = 1; j < weightMatrix[0].length; j++) {
                    if (weightMatrix[i][j] != null) {
                        test.write(weightMatrix[i][j].getWeight() + ",");
                    } else {
                        test.write(0 + ",");
                    }
                }
                test.write("\n");
            }
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

    void exportOffsets(BufferedWriter test, String name) throws IOException, FileNotFoundException, UnsupportedEncodingException {
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

    private void createWeightMatrix() {
        List<String> kappaRowOrder;
        LinkedHashMap<String, HashMap<String, KappaRule>> kappaLambdaWeights;
        LinkedHashMap<String, Integer> lambda2Column;

        BufferedWriter test = null;
        StringBuilder sb = new StringBuilder();
        kappaLambdaWeights = new LinkedHashMap<>();
        lambda2Column = new LinkedHashMap<>();
        int count = 1;
        //creating weight matrix
        for (Rule rule : rules) {
            if (rule instanceof LambdaRule) {
                continue;
            }
            KappaRule kr = (KappaRule) rule;
            String KappaHead = kr.getHead().getParent().toString();
            String LambdaBody = kr.getBody().getParent().toString();
            try {
                if (kr.getBody().getParent().getRule().getBody().get(0).isElement()) {
                    LambdaBody += " = " + kr.getBody().getParent().getRule().getBody().get(0).getParent();  //add the element explanation
                }
            } catch (NullPointerException ex) {
                Glogger.err(ex.getMessage());
            }
            if (!lambda2Column.containsKey(LambdaBody)) {
                lambda2Column.put(LambdaBody, count++);
            }

            HashMap<String, KappaRule> get = kappaLambdaWeights.get(KappaHead);
            if (get == null) {
                get = new HashMap<>();
                kappaLambdaWeights.put(KappaHead, get);
            }
            get.put(LambdaBody, kr);        //here the reference to the kappa-lambda rule-weight object is set
        }
        weightMatrix = new KappaRule[kappaLambdaWeights.size() + 1][lambda2Column.size() + 1];
        int i = 1;
        kappaRowOrder = new ArrayList(kappaLambdaWeights.keySet());
        Collections.sort(kappaRowOrder);
        for (String kappa : kappaRowOrder) {
            KappaRule kap = new KappaRule(-1);
            kap.original = kappa;
            weightMatrix[i++][0] = kap;
        }
        i = 1;
        //List<String> sortedLambdas = new ArrayList(lambdas1.keySet());
        //Collections.sort(sortedLambdas);
        for (String lambda : lambda2Column.keySet()) {
            KappaRule kap = new KappaRule(-1);
            kap.original = lambda;
            weightMatrix[0][i++] = kap;
        }
        i = 1;
        for (String kappa : kappaRowOrder) {
            HashMap<String, KappaRule> lams = kappaLambdaWeights.get(kappa);
            for (String lam : lams.keySet()) {
                Integer index = lambda2Column.get(lam);
                if (lams.get(lam) != null) {
                    weightMatrix[i][index] = lams.get(lam);
                } else {
                    KappaRule kap = new KappaRule(-1);
                    kap.setWeight(0);
                    weightMatrix[i][index] = kap;
                }
            }
            i++;
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
     * @return the elements
     */
    public LinkedHashSet<Kappa> getElements() {
        return elements;
    }

    /**
     * @param elements the elements to set
     */
    public void setElements(LinkedHashSet<Kappa> elements) {
        this.elements = elements;
    }
}
