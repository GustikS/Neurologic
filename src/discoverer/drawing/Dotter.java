package discoverer.drawing;

import discoverer.construction.template.KL;
import discoverer.construction.template.Kappa;
import discoverer.construction.network.rules.KappaRule;
import discoverer.construction.template.Lambda;
import discoverer.construction.template.LiftedTemplate;
import discoverer.construction.template.LightTemplate;
import discoverer.construction.template.MolecularTemplate;
import discoverer.construction.network.rules.LambdaRule;
import discoverer.construction.network.rules.SubK;
import discoverer.global.Glogger;
import java.io.*;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Class for drawing graphs with dot
 */
public class Dotter {

    public static String path = "graphviz/bin/";	// Windows
    public static String outPath = "../images/";
    public static String imgtype = "png";
    private static String name = "graph";
    public static int counter = 0;

    protected static String intro = "digraph G {";
    protected static String outro = "}";
    protected static List<String> dot = new ArrayList<String>();
    protected static final String dotFileName = "graph.dot";
    protected static Set<Object> visited = new HashSet<Object>();
    protected static Set<KappaRule> actives;

    public static DecimalFormat df = new DecimalFormat("##.###################");

    private static void writeToFile() {
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

    private static void convertToImage() {
        Runtime r = Runtime.getRuntime();
        try {
            //Process p = r.exec(path + "dot -T" + imgtype + " -o" + outPath + name + counter++ + "." + imgtype + " graph.dot");
            Process p = r.exec(path + "dot -T" + imgtype + " -o" + outPath + name + "." + imgtype + " graph.dot");
            p.waitFor();
        } catch (Exception e) {
            Glogger.err(e.toString());
        }
    }

//   private static void drawImage() {
//        GraphViz gv = new GraphViz("dot");
//        String type = "gif";
//        File out = new File("out." + type); // out.gif in this example
//        gv.writeGraphToFile(gv.getGraph(gv.getDotSource(), type), out);
//    }
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
        if (!visited.contains(kr)) {
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
        if (lr != null) {
            for (SubK sk : lr.getBody()) {
                if (!visited.contains(lr)) {
                    String s = "\"" + lr.getHead().getParent().getName() + "\" -> \"" + sk.getParent().getName() + "\noffset: " + sk.getParent().offset + "\";";
                    dot.add(s);
                }
                draw(sk.getParent());
            }
        }
        visited.add(lr);
    }
}
