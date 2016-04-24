package discoverer.drawing;

import discoverer.grounding.network.GroundKappa;
import discoverer.grounding.network.GroundLambda;
import discoverer.construction.network.rules.KappaRule;
import discoverer.global.Glogger;
import discoverer.global.Tuple;
import discoverer.grounding.evaluation.GroundedTemplate;
import discoverer.grounding.network.groundNetwork.AtomNeuron;
import discoverer.grounding.network.groundNetwork.GroundNetwork;
import discoverer.grounding.network.groundNetwork.GroundNeuron;
import discoverer.grounding.network.groundNetwork.RuleAggNeuron;
import java.io.BufferedWriter;
import java.io.FileWriter;

import java.util.HashSet;
import java.util.Map;

/**
 * Drawing already grounded graph -- lk-network
 */
public class GroundDotter extends Dotter {

    private static String name = "ground";
    public static Map<Integer, String> constantNames;

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

    private static void convertToPdf() {
        Runtime r = Runtime.getRuntime();
        try {
            //Process p = r.exec(path + "dot -T" + imgtype + " -o" + outPath + name + counter++ + "." + imgtype + " graph.dot");
            Process p = r.exec(path + "dot -T" + imgtype + " -o" + outPath + name + "." + imgtype + " graph.dot");
            p.waitFor();
        } catch (Exception e) {
            Glogger.err(e.toString());
        }
    }

    public static void draw(GroundedTemplate b) {
        if (b == null) {
            return;
        }
        constantNames = b.constantNames;

        dot.add(intro);
        Object top = b.getLast();
        if (top instanceof GroundKappa) {
            drawMax((GroundKappa) top);
        } else {
            drawMax((GroundLambda) top);
        }

        dot.add(outro);
        writeToFile();
        convertToPdf();
        visited.clear();
        dot.clear();
    }

    public static void drawMax(GroundedTemplate b, String nam) {
        if (b == null) {
            return;
        }

        name = nam;
        constantNames = b.constantNames;

        dot.add(intro);
        Object top = b.getLast();
        if (top instanceof GroundKappa) {
            drawMax((GroundKappa) top);
        } else {
            drawMax((GroundLambda) top);
        }

        dot.add(outro);
        writeToFile();
        convertToPdf();
        visited.clear();
        dot.clear();
    }

    public static void drawAVG(GroundedTemplate b, String nam) {
        if (b == null) {
            return;
        }

        name = nam;
        constantNames = b.constantNames;

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

    private static void drawMax(GroundKappa gk) {
        if (gk.isElement()) {
            return;
        }

        if (visited.contains(gk)) {
            return;
        }

        for (Tuple<GroundLambda, KappaRule> t : gk.getDisjuncts()) {
            String s = "\"" + gk.toString(constantNames) + "\nval: " + df.format(gk.getValue()) + "\noffset: " + df.format(gk.getGeneral().offset) + "\" -> " + "\"" + t.x.toString(constantNames) + "\nval: " + df.format(t.x.getValue()) + "\"" + " [ label = \"" + df.format(t.y.getWeight()) + "\" ];";
            dot.add(s);
        }

        visited.add(gk);

        for (Tuple<GroundLambda, KappaRule> t : gk.getDisjuncts()) {
            drawMax(t.x);
        }
    }

    private static void drawMax(GroundLambda gl) {
        if (visited.contains(gl)) {
            return;
        }

        for (GroundKappa gk : gl.getConjuncts()) {
            String s = "\"" + gl.toString(constantNames) + "\nval: " + df.format(gl.getValue()) + "\" -> \"" + gk.toString(constantNames) + "\nval: " + df.format(gk.getValue()) + "\noffset: " + df.format(gk.getGeneral().offset) + "\";";
            dot.add(s);
            drawMax(gk);
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
            String s = "\"" + gk.toString(constantNames) + "\nvalAVG: " + df.format(gk.getValueAvg()) + "\noffset: " + df.format(gk.getGeneral().offset) + "\" -> " + "\"kapR{" + t.x + "}\"" + " [ label = \"" + df.format(t.y.getWeight()) + "\" ];";
            dot.add(s);
            for (GroundLambda gl : t.x) {
                String ss = "\"" + "kapR{" + t.x + "}\"" + " -> " + "\"" + gl.toString(constantNames) + "\nvalAvg: " + df.format(gl.getValueAvg()) + "\n#ground: " + gl.getConjunctsCountForAvg() + "\"" + " [ label = \"1x\" ];";
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
            String s = "\"" + gl.toString(constantNames) + "\nvalAvg: " + df.format(gl.getValueAvg()) + "\n#ground: " + gl.getConjunctsCountForAvg() + "\"" + " -> \"" + t.getKey().toString(constantNames) + "\nvalAVG: " + df.format(t.getKey().getValueAvg()) + "\noffset: " + (t.getKey().isElement() ? "N/A" : df.format(t.getKey().getGeneral().offset)) + "\"" + " [ label = \"" + df.format(t.getValue()) + "x\" ];";
            dot.add(s);
            drawAvg(t.getKey());
        }

        visited.add(gl);

        /*for (GroundKappa gk : gl.getConjuncts()) {
         draw(gk);
         }*/
    }

    public static void draw(GroundNetwork net, String nam, double[] sharedweights) {
        if (net == null) {
            return;
        }

        name = nam;
        //constantNames = b.constantNames;

        dot.add(intro);
        GroundNeuron top = net.outputNeuron;
        if (top instanceof AtomNeuron) {
            draw((AtomNeuron) top, sharedweights);
        } else {
            draw((RuleAggNeuron) top, sharedweights);
        }

        dot.add(outro);
        writeToFile();
        convertToPdf();
        visited.clear();
        dot.clear();
    }

    private static void draw(AtomNeuron an, double[] sharedweights) {
        if (an.inputNeurons == null) {
            return;
        }

        if (visited.contains(an)) {
            return;
        }

        for (int i = 0; i < an.inputNeurons.length; i++) {
            RuleAggNeuron ragg = an.inputNeurons[i];
            String s = "\"" + an + "\nval: " + df.format(an.outputValue) + "\noffset: " + df.format(sharedweights[an.offsetWeightIndex]) + "\" -> " + "\"" + ragg + "\nval: " + df.format(ragg.outputValue) + "\"" + " [ label = \"" + df.format(sharedweights[an.inputWeightIndices[i]]) + "\" ];";
            dot.add(s);
            draw(ragg, sharedweights);
        }

        visited.add(an);
    }

    private static void draw(RuleAggNeuron ragg, double[] sharedweights) {
        if (visited.contains(ragg)) {
            return;
        }
        for (int i = 0; i < ragg.inputNeuronsCompressed.length; i++) {
            AtomNeuron an = ragg.inputNeuronsCompressed[i];
            String s = "\"" + ragg + "\nval: " + df.format(ragg.outputValue) + "\" -> \"" + an + "\nval: " + df.format(an.outputValue) + "\noffset: " + df.format(sharedweights[an.offsetWeightIndex]) + "\";";
            dot.add(s);
            draw(an, sharedweights);
        }

        visited.add(ragg);
    }
}
