package lrnn.drawing;

import extras.GraphViz;
import lrnn.construction.template.*;
import lrnn.construction.template.rules.KappaRule;
import lrnn.construction.template.rules.LambdaRule;
import lrnn.construction.template.rules.SubK;
import lrnn.global.Glogger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Class for drawing graphs with dot
 */
public class Dotter {

    //public static String path = "../graphviz/bin/";	// Windows
    public static String path = "";	// linux
    public static String outPath = "../images/";
    public static String imgtype = "png";
    public static String name = "graph";
    public static int counter = 0;

    protected static String intro = "digraph G {";
    protected static String outro = "}";
    protected static List<String> dot = new ArrayList<String>();
    protected static final String dotFileName = "graph.dot";
    protected static Set<Object> visited = new HashSet<Object>();
    protected static Set<Object> open = new HashSet<Object>();
    protected static Set<KappaRule> actives;

    public static DecimalFormat df = new DecimalFormat("##.###################");

    static int count = 0;
    
     static void writeToFile() {
        try {
            FileWriter fstream = new FileWriter(dotFileName, false);
            BufferedWriter out = new BufferedWriter(fstream);
            for (String s : dot) {
                out.write(s);
                out.newLine();
            }
            out.close();
        } catch (Exception e) {
            Glogger.err(e.toString());
        }
    }

     static String sanitize(String name){
         String sane = name.replaceAll("[:.;'/\\\\]", "_");
         return sane;
     }
     
     static void convertToImage() {
        Runtime r = Runtime.getRuntime();
        try {
            //Process p = r.exec(path + "dot -T" + imgtype + " -o" + outPath + name + counter++ + "." + imgtype + " graph.dot");
            Process p = r.exec(path + "dot -T" + imgtype + " -o" + outPath + sanitize(name) + "." + imgtype + " graph.dot");
            p.waitFor();
        } catch (Exception e) {
            Glogger.err(e.toString());
        }
    }

   private static void drawImage() {
        GraphViz gv = new GraphViz("dot");
        String type = "gif";
        File out = new File("out." + type); // out.gif in this example
        gv.writeGraphToFile(gv.getGraph(gv.getDotSource(), type), out);
    }

    public static void draw(KL kl, Set<KappaRule> s) {
        actives = s;
        dot.add(intro);

        if (kl instanceof Kappa) {
            draw((Kappa) kl);
        } else {
            draw((Lambda) kl);
        }

        dot.add(outro);
        writeToFile();
        convertToImage();
        visited.clear();
        open.clear();
        dot.clear();
    }

    public static void draw(KL kl) {
        dot.add(intro);

        if (kl instanceof Kappa) {
            draw((Kappa) kl);
        } else {
            draw((Lambda) kl);
        }

        dot.add(outro);
        writeToFile();
        convertToImage();
        visited.clear();
        open.clear();
        dot.clear();
    }
    
    public static void draw(KL kl, String nam) {
        dot.add(intro);
        name = nam;

        if (kl instanceof Kappa) {
            draw((Kappa) kl);
        } else {
            draw((Lambda) kl);
        }

        dot.add(outro);
        writeToFile();
        convertToImage();
        visited.clear();
        open.clear();
        dot.clear();
    }

    public static void draw(LightTemplate net, String nam) {
        KL kl = null;
        if (net instanceof MolecularTemplate) {
            kl = ((MolecularTemplate) net).last;
        } else {
            Glogger.err("Drawing of the lightweight LiftedNetwork not supported yet");
        }
        dot.add(intro);
        name = nam;

        if (kl instanceof Kappa) {
            draw((Kappa) kl);
        } else {
            draw((Lambda) kl);
        }

        dot.add(outro);
        writeToFile();
        convertToImage();
        visited.clear();
        open.clear();
        dot.clear();
    }

    private static void draw(Kappa k) {
        if (k.isElement()) {
            return;
        }

        for (KappaRule kr : k.getRules()) {
            draw(kr);
        }
    }

    private static void draw(KappaRule kr) {
        //if (!visited.contains(kr) && actives.contains(kr)) {
        if (!visited.contains(kr) && !open.contains(kr)) {
            open.add(kr);
            String s = "\"" + kr.getHead().getParent().getName() + "\noffset: " + kr.getHead().getParent().offset + "\" -> \"" + kr.getBody().getParent().getName() + "\" [ label = \"" + df.format(kr.getWeight()) + "\" ];";
            dot.add(s);
            visited.add(kr);
        }
        draw(kr.getBody().getParent());
    }

    private static void draw(Lambda l) {
        draw(l.getRule());
    }

    private static void draw(LambdaRule lr) {
        if (lr != null && !visited.contains(lr) && !open.contains(lr)) {
            for (SubK sk : lr.getBody()) {
                open.add(lr);
                String s = "\"" + lr.getHead().getParent().getName() + "\" -> \"" + sk.getParent().getName() + "\noffset: " + sk.getParent().offset + "\";";
                dot.add(s);
                draw(sk.getParent());
            }
        }
        visited.add(lr);
    }

    public static void draw(Collection<KL> kls, String nam) {
        dot.add(intro);
        name = nam;

        for (KL kl : kls) {
            if (kl instanceof Kappa) {
                Kappa k = (Kappa) kl;
                for (KappaRule kr : k.getRules()) {
                    draw(kr);
                }
            } else {
                Lambda l = (Lambda) kl;
                draw(l.getRule());
            }
        }
        
        dot.add(outro);
        writeToFile();
        convertToImage();
        visited.clear();
        open.clear();
        dot.clear();
    }
}
