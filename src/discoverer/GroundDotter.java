package discoverer;

import java.io.BufferedWriter;
import java.io.FileWriter;

import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Drawing already grounded graph -- lk-network
 */
public class GroundDotter extends Dotter {

    private static String name = "ground";

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
            System.out.println(e);
        }
    }

    private static void convertToPdf() {
        Runtime r = Runtime.getRuntime();
        try {
            //Process p = r.exec(path + "dot -T" + imgtype + " -o" + outPath + name + counter++ + "." + imgtype + " graph.dot");
            Process p = r.exec(path + "dot -T" + imgtype + " -o" + outPath + name + "." + imgtype + " graph.dot");
            p.waitFor();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void draw(Ball b) {
        if (b == null) {
            return;
        }

        dot.add(intro);
        Object top = b.getLast();
        if (top instanceof GroundKappa) {
            draw((GroundKappa) top);
        } else {
            draw((GroundLambda) top);
        }

        dot.add(outro);
        writeToFile();
        convertToPdf();
        visited.clear();
        dot.clear();
    }

    public static void draw(Ball b, String nam) {
        if (b == null) {
            return;
        }

        name = nam;

        dot.add(intro);
        Object top = b.getLast();
        if (top instanceof GroundKappa) {
            draw((GroundKappa) top);
        } else {
            draw((GroundLambda) top);
        }

        dot.add(outro);
        writeToFile();
        convertToPdf();
        visited.clear();
        dot.clear();
    }

    public static void drawAVG(Ball b, String nam) {
        if (b == null) {
            return;
        }

        name = nam;

        dot.add(intro);
        Object top = b.getLast();
        if (top instanceof GroundKappa) {
            drawAvg((GroundKappa) top);
        } else {
            drawAvg((GroundLambda) top);
        }

        dot.add(outro);
        writeToFile();
        convertToPdf();
        visited.clear();
        dot.clear();
    }

    private static void draw(GroundKappa gk) {
        if (gk.isElement()) {
            return;
        }

        if (visited.contains(gk)) {
            return;
        }

        for (Tuple<GroundLambda, KappaRule> t : gk.getDisjuncts()) {
            String s = "\"" + gk + "\nval: " + df.format(gk.getValue()) + "\noffset: " + df.format(gk.getGeneral().weight) + "\" -> " + "\"" + t.x + "\nval: " + df.format(t.x.getValue()) + "\"" + " [ label = \"" + df.format(t.y.getWeight()) + "\" ];";
            dot.add(s);
        }

        visited.add(gk);

        for (Tuple<GroundLambda, KappaRule> t : gk.getDisjuncts()) {
            draw(t.x);
        }
    }

    private static void draw(GroundLambda gl) {
        if (visited.contains(gl)) {
            return;
        }

        for (GroundKappa gk : gl.getConjuncts()) {
            String s = "\"" + gl + "\nval: " + df.format(gl.getValue()) + "\" -> \"" + gk + "\nval: " + df.format(gk.getValue()) + "\noffset: " + df.format(gk.getGeneral().weight) + "\";";
            dot.add(s);
            draw(gk);
        }

        visited.add(gl);

        //for (GroundKappa gk : gl.getConjuncts()) {
        //    draw(gk);
        //}
    }

    private static void drawAvg(GroundKappa gk) {
        if (gk.isElement()) {
            return;
        }

        if (visited.contains(gk)) {
            return;
        }

        for (Tuple<HashSet<GroundLambda>, KappaRule> t : gk.getDisjunctsAvg()) {
            String s = "\"" + gk + "\nvalAVG: " + df.format(gk.getValueAvg()) + "\noffset: " + df.format(gk.getGeneral().weight) + "\" -> " + "\"kapR{" + t.x + "}\"" + " [ label = \"" + df.format(t.y.getWeight()) + "\" ];";
            dot.add(s);
            for (GroundLambda gl : t.x) {
                String ss = "\"" + "kapR{" + t.x + "}\"" + " -> " + "\"" + gl + "\nvalAvg: " + df.format(gl.getValueAvg()) + "\n#ground: " + gl.getConjunctsCountForAvg() + "\"" + " [ label = \"1x\" ];";
                dot.add(ss);
            }
        }
        visited.add(gk);

        for (Tuple<HashSet<GroundLambda>, KappaRule> t : gk.getDisjunctsAvg()) {
            for (GroundLambda gl : t.x) {
                drawAvg(gl);
            }
        }
    }

    private static void drawAvg(GroundLambda gl) {
        if (visited.contains(gl)) {
            return;
        }
        for (Map.Entry<GroundKappa, Integer> t : gl.getConjunctsAvg().entrySet()) {
            String s = "\"" + gl + "\nvalAvg: " + df.format(gl.getValueAvg()) + "\n#ground: " + gl.getConjunctsCountForAvg() + "\"" + " -> \"" + t.getKey() + "\nvalAVG: " + df.format(t.getKey().getValueAvg()) + "\noffset: " + (t.getKey().isElement() ? "N/A" : df.format(t.getKey().getGeneral().weight)) + "\"" + " [ label = \"" + df.format(t.getValue()) + "x\" ];";
            dot.add(s);
            drawAvg(t.getKey());
        }

        visited.add(gl);

        /*for (GroundKappa gk : gl.getConjuncts()) {
         draw(gk);
         }*/
    }
}
