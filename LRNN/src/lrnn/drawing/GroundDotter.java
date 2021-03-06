package lrnn.drawing;

import lrnn.construction.template.rules.KappaRule;
import lrnn.global.Global;
import lrnn.global.Tuple;
import lrnn.grounding.evaluation.GroundedTemplate;
import lrnn.grounding.network.GroundKappa;
import lrnn.grounding.network.GroundLambda;
import lrnn.grounding.network.groundNetwork.AtomNeuron;
import lrnn.grounding.network.groundNetwork.GroundNetwork;
import lrnn.grounding.network.groundNetwork.GroundNeuron;
import lrnn.grounding.network.groundNetwork.RuleAggNeuron;

import java.util.HashSet;
import java.util.Map;

/**
 * Drawing already grounded graph -- lk-network
 */
public class GroundDotter extends Dotter {

    public static Map<Integer, String> constantNames;

    public static void draw(GroundedTemplate b, String name) {
        if (b == null || b.getLast() == null) {
            return;
        }
        if (Global.getGrounding().equals(Global.groundingSet.avg)) {
            drawAVG(b, name);
        } else {
            drawMax(b, name);
        }
    }

    public static void drawMax(GroundedTemplate b) {

        constantNames = b.constantNames;

        dot.add(intro);
        Object top = b.getLast();
        if (top instanceof GroundKappa) {
            GroundDotter.drawMax((GroundKappa) top);
        } else {
            GroundDotter.drawMax((GroundLambda) top);
        }

        dot.add(outro);
        writeToFile();
        convertToImage();
        visited.clear();
        dot.clear();
    }

    public static void drawMax(GroundedTemplate b, String nam) {

        name = nam;
        constantNames = b.constantNames;

        dot.add(intro);
        Object top = b.getLast();
        if (top instanceof GroundKappa) {
            GroundDotter.drawMax((GroundKappa) top);
        } else {
            GroundDotter.drawMax((GroundLambda) top);
        }

        dot.add(outro);
        writeToFile();
        convertToImage();
        visited.clear();
        dot.clear();
    }

    public static void drawAVG(GroundedTemplate b, String nam) {

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
        convertToImage();
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
            GroundDotter.drawMax(t.x);
        }
    }

    private static void drawMax(GroundLambda gl) {
        if (visited.contains(gl)) {
            return;
        }

        for (GroundKappa gk : gl.getConjuncts()) {
            String s = "\"" + gl.toString(constantNames) + "\nval: " + df.format(gl.getValue()) + "\" -> \"" + gk.toString(constantNames) + "\nval: " + df.format(gk.getValue()) + "\noffset: " + df.format(gk.getGeneral().offset) + "\";";
            dot.add(s);
            GroundDotter.drawMax(gk);
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
            //this was also drawing the KappaRule-Set KapR{}
            //String s = "\"" + gk.toString(constantNames) + "\nvalAVG: " + df.format(gk.getValueAvg()) + "\noffset: " + df.format(gk.getGeneral().offset) + "\" -> " + "\"kapR{" + t.x + "}\"" + " [ label = \"" + df.format(t.y.getWeight()) + "\" ];";
            //dot.add(s);
            for (GroundLambda gl : t.x) {
                String ss = "\"" + gk.toString(constantNames) + "\nvalAVG: " + df.format(gk.getValueAvg() == null ? 0 : gk.getValueAvg()) + "\noffset: " + df.format(gk.getGeneral().offset) + "\" -> " + "\"" + gl.toString(constantNames) + "\nvalAvg: " + df.format(gl.getValueAvg() == null ? 0 : gl.getValueAvg()) + "\n#ground: " + gl.getConjunctsCountForAvg() + "\"" + " [ label = \"" + df.format(t.y.getWeight()) + "\" ];";
                //String ss = "\"kapR{" + t.x + "}" + "\" -> " + "\"" + gl.toString(constantNames) + "\nvalAvg: " + df.format(gl.getValueAvg()) + "\n#ground: " + gl.getConjunctsCountForAvg() + "\"" + " [ label = \"" + df.format(t.y.getWeight()) + "\" ];";
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
            String s = "\"" + gl.toString(constantNames) + "\nvalAvg: " + df.format(gl.getValueAvg() == null ? 0 : gl.getValueAvg()) + "\n#ground: " + gl.getConjunctsCountForAvg() + "\"" + " -> \"" + t.getKey().toString(constantNames) + "\nvalAVG: " + df.format(t.getKey().getValueAvg() == null ? 0 : t.getKey().getValueAvg()) + "\noffset: " + (df.format(t.getKey().getGeneral().offset)) + "\"" + " [ label = \"" + df.format(t.getValue()) + "x\" ];";
            dot.add(s);
            drawAvg(t.getKey());
        }

        visited.add(gl);

        /*for (GroundKappa gk : gl.getConjuncts()) {
         draw(gk);
         }*/
    }

    public static void drawNeural(GroundNetwork net, String nam, double[] sharedweights) {
        if (net == null) {
            return;
        }

        name = nam;
        //constantNames = b.constantNames;

        dot.add(intro);
        GroundNeuron top = net.outputNeuron;
        if (top instanceof AtomNeuron) {
            drawNeural((AtomNeuron) top, sharedweights);
        } else {
            drawNeural((RuleAggNeuron) top, sharedweights);
        }

        dot.add(outro);
        writeToFile();
        convertToImage();
        visited.clear();
        dot.clear();
    }

    private static void drawNeural(AtomNeuron an, double[] sharedweights) {
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
            drawNeural(ragg, sharedweights);
        }

        visited.add(an);
    }

    private static void drawNeural(RuleAggNeuron ragg, double[] sharedweights) {
        if (visited.contains(ragg)) {
            return;
        }
        if (Global.uncompressedLambda) {
            for (int i = 0; i < ragg.ruleBodyGroundings.length; i++) {
                AtomNeuron[] ann = ragg.ruleBodyGroundings[i];
                for (AtomNeuron an : ann) {
                    String s = "\"" + ragg + "\nval: " + df.format(ragg.outputValue) + "\" -> \"" + an + "\nval: " + df.format(an.outputValue) + "\noffset: " + df.format(sharedweights[an.offsetWeightIndex]) + "\";";
                    dot.add(s);
                    drawNeural(an, sharedweights);
                }
            }
        } else
            for (int i = 0; i < ragg.inputNeuronsCompressed.length; i++) {
                AtomNeuron an = ragg.inputNeuronsCompressed[i];
                String s = "\"" + ragg + "\nval: " + df.format(ragg.outputValue) + "\" -> \"" + an + "\nval: " + df.format(an.outputValue) + "\noffset: " + df.format(sharedweights[an.offsetWeightIndex]) + "\";";
                dot.add(s);
                drawNeural(an, sharedweights);
            }

        visited.add(ragg);
    }
}
