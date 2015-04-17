package discoverer;

import java.io.*;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Class for drawing graphs with dot
 */
public class Dotter {
    private static String intro = "digraph G {";
    private static String outro = "}";
    private static List<String> dot = new ArrayList<String>();
    private static final String dotFileName = "graph.dot";
    private static Set<Object> visited = new HashSet<Object>();
    private static Set<KappaRule> actives;

    private static void writeToFile() {
        try {
            FileWriter fstream = new FileWriter(dotFileName, false);
            BufferedWriter out = new BufferedWriter(fstream);
            for (String s: dot) {
                out.write(s);
                out.newLine();
            }
            out.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void convertToPdf() {
        Runtime r = Runtime.getRuntime();
        try {
            Process p = r.exec("dot -Tpdf -ograph.pdf graph.dot");
            p.waitFor();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void draw(KL kl, Set<KappaRule> s) {
        actives = s;
        dot.add(intro);

        if (kl instanceof Kappa)
            draw((Kappa) kl);
        else
            draw((Lambda) kl);

        dot.add(outro);
        writeToFile();
        convertToPdf();
        visited.clear();
        dot.clear();
    }

    private static void draw(Kappa k) {
        if (k.isElement())
            return;

        for (KappaRule kr: k.getRules()) {
            draw(kr);
        }
    }

    private static void draw(KappaRule kr) {
        if (!visited.contains(kr) && actives.contains(kr)) {
            DecimalFormat df = new DecimalFormat("#.##");
            String s = "\"" + kr.getHead().getParent().getName() + "\" -> \"" + kr.getBody().getParent().getName() + "\" [ label = \"" + df.format(kr.getWeight()) +"\" ];";
            dot.add(s);
            visited.add(kr);
        }
        draw(kr.getBody().getParent());
    }

    private static void draw(Lambda l) {
        draw(l.getRule());
    }

    private static void draw(LambdaRule lr) {
        for (SubK sk: lr.getBody()) {
            if (!visited.contains(lr)) {
                String s = "\"" + lr.getHead().getParent().getName() + "\" -> \"" + sk.getParent().getName() + "\";";
                dot.add(s);
            }
            draw(sk.getParent());
        }
        visited.add(lr);
    }
}
