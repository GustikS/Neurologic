package discoverer;

import java.io.BufferedWriter;
import java.io.FileWriter;

import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Drawing already grounded graph -- lk-network
 */
public class GroundDotter {
    private static String intro = "digraph G {";
    private static String outro = "}";
    private static List<String> dot = new ArrayList<String>();
    private static final String dotFileName = "ground.dot";
    private static Set<Object> visited = new HashSet<Object>();

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
            Process p = r.exec("dot -Tpdf -oground.pdf ground.dot");
            p.waitFor();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void draw(Ball b) {
        if (b == null)
            return;

        dot.add(intro);
        Object top = b.getLast();
        if (top instanceof GroundKappa)
            draw((GroundKappa) top);
        else
            draw((GroundLambda) top);

        dot.add(outro);
        writeToFile();
        convertToPdf();
        visited.clear();
        dot.clear();
    }

    private static void draw(GroundKappa gk) {
        if (gk.isElement())
            return;

        if (visited.contains(gk))
            return;

        for (Tuple<GroundLambda, KappaRule> t: gk.getDisjuncts()) {
            DecimalFormat df = new DecimalFormat("#.##");
            String s = "\"" + gk + "\" -> " + "\"" + t.x + "\"" + " [ label = \"" + df.format(t.y.getWeight()) +"\" ];";
            dot.add(s);
        }

        visited.add(gk);


        for (Tuple<GroundLambda, KappaRule> t: gk.getDisjuncts()) {
            draw(t.x);
        }
    }

    private static void draw(GroundLambda gl) {
        if (visited.contains(gl))
            return;

        for (GroundKappa gk: gl.getConjuncts()) {
            String s = "\"" + gl + "\" -> \"" + gk + "\";";
            dot.add(s);
            draw(gk);
        }

        visited.add(gl);

        for (GroundKappa gk: gl.getConjuncts()) {
            draw(gk);
        }
    }
}
