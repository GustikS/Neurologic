package lrnn.grounding.evaluation;

import lrnn.global.Global;
import lrnn.grounding.network.GroundKappa;
import lrnn.grounding.network.GroundLambda;
import lrnn.construction.template.rules.KappaRule;
import lrnn.learning.functions.Activations;
import lrnn.global.Tuple;
import lrnn.grounding.network.GroundKL;

import java.util.*;

/**
 * Evaluating lk-network output
 */
public class Evaluator {

    public static boolean ignoreDropout = true;

    /**
     * @param gls
     * @return
     */
    public static double getAvgValFrom(Set<GroundLambda> gls) {

        double avg = 0;
        for (GroundKL gl : gls) {
            avg += gl.getValueAvg();    //we will recursively sum up the average values valAvg (the max. values are calculated separately in val)
        }
        avg /= gls.size();
        return avg;
    }

    public static double getSumValFrom(Set<GroundLambda> gls) {
        double sum = 0;
        for (GroundKL gl : gls) {
            sum += gl.getValueAvg();    //we will recursively sum up the average values valAvg (the max. values are calculated separately in val)
        }
        return sum;
    }

    public static double evaluateMax(GroundedTemplate b) {
        if (b == null) {
            return Global.getFalseAtomValue();
        }
        //GroundInvalidator.invalidate(b);    //this means to delete all values of all ground literals
        b.invalidateNeurons();

        Object top = b.getLast();
        if (top == null) {
            return b.valMax;
        }
        if (top instanceof GroundKappa) {
            return evaluateMax((GroundKappa) top);
        } else {
            return evaluateMax((GroundLambda) top);
        }
    }

    private static double evaluateMax(GroundKappa gk) {
        if (!ignoreDropout && gk.dropMe) {
            gk.setValue(0.0);
            return 0;
        }

        if (gk.isElement()) {
            return gk.getValue();
        }

        Double out = gk.getValue();
        if (out != null) {
            return out;
        }

        //out = gk.getGeneral().getOffset();
        ArrayList<Double> inputs = new ArrayList<>(gk.getDisjuncts().size());
        for (Tuple<GroundLambda, KappaRule> t : gk.getDisjuncts()) {
            //out += evaluate(t.x) * t.y.getWeight();
            inputs.add(evaluateMax(t.x) * t.y.getWeight());
        }

        out = Activations.kappaActivation(inputs, gk.getGeneral().getOffset());
        gk.setValue(out);
        return out;
    }

    private static double evaluateMax(GroundLambda gl) {
        if (!ignoreDropout && gl.dropMe) {
            gl.setValue(0.0);
            return 0;
        }

        Double out = gl.getValue();
        if (out != null) {
            return out;
        }

        //out = gl.getGeneral().getOffset();
        ArrayList<Double> inputs = new ArrayList<>(gl.getConjuncts().size());
        for (GroundKappa gk : gl.getConjuncts()) {
            //out += evaluate(gk);
            inputs.add(evaluateMax(gk));
        }

        out = Activations.lambdaActivation(inputs, gl.getGeneral().getOffset());
        gl.setValue(out);
        return out;
    }

    public static double evaluateAvg(GroundedTemplate b) {
        if (b == null) {
            return Global.getFalseAtomValue();
        }
        //GroundInvalidator.invalidateAVG(b);    //this means to delete all values of all ground literals (will work as caching)
        b.invalidateNeurons();

        GroundKL top = b.getLast();
        if (top == null) {
            return b.valAvg;
        }
        if (top instanceof GroundKappa) {
            return evaluateAvg((GroundKappa) top);
        } else {
            return evaluateAvg((GroundLambda) top);
        }
    }

    private static double evaluateAvg(GroundKappa gk) {
        if (!ignoreDropout && gk.dropMe) {
            gk.setValueAvg(0.0);
            return 0;
        }

        if (gk.isElement()) {
            return gk.getValueAvg();
            //return gk.getValue(); //-should be the same
        }

        Double out = gk.getValueAvg();
        if (out != null) {
            return out;
        }

        //out = gk.getGeneral().getOffset();
        ArrayList<Double> inputs = new ArrayList<>(gk.getDisjunctsAvg().size());
        for (Tuple<HashSet<GroundLambda>, KappaRule> t : gk.getDisjunctsAvg()) {
            double avg = 0;
            /*if (t.x.size() > 1) {
                System.out.println("stop");
            }*/
            for (GroundLambda gl : t.x) {
                avg += evaluateAvg(gl);
            }
            /*if (t.x.isEmpty()){
             System.out.println("problem");
             }*/

            //avg /= t.x.size();    //there is no averaging here!
            //out += avg * t.y.getWeight();
            inputs.add(avg * t.y.getWeight());
        }
        out = Activations.kappaActivation(inputs, gk.getGeneral().getOffset());
        gk.setValueAvg(out);
        return out;
    }

    private static double evaluateAvg(GroundLambda gl) {
        if (!ignoreDropout && gl.dropMe) {
            gl.setValueAvg(0.0);
            return 0;
        }
        
        if (gl.isElement()) {
            return gl.getValueAvg();
            //return gk.getValue(); //-should be the same
        }

        Double out = gl.getValueAvg();
        if (out != null) {
            return out;
        }

        //out = gl.getGeneral().getOffset();
        ArrayList<Double> inputs = new ArrayList<>(gl.getConjunctsAvg().size());
        //double avg = 0;
        if (Global.uncompressedLambda){
            double avg = 0;
            for (List<GroundKappa> gks : gl.fullBodyGroundings) {
                inputs.clear();
                for (GroundKappa gk : gks) {
                    inputs.add(evaluateAvg(gk));
                }
                avg += Activations.lambdaActivation(inputs, gl.getGeneral().getOffset());
            }
            out = avg / gl.fullBodyGroundings.size();
        } else {
            for (Map.Entry<GroundKappa, Integer> gk : gl.getConjunctsAvg().entrySet()) {
                //avg += evaluateAvg(gk.getKey()) * gk.getValue();
                inputs.add(evaluateAvg(gk.getKey()) * gk.getValue() / gl.getConjunctsCountForAvg());    //AVERAGING HERE
            }
            //avg /= gl.getConjunctsCountForAvg();    //they are all averaged by the number of body groundings

            out = Activations.lambdaActivation(inputs, gl.getGeneral().getOffset());
        }

        gl.setValueAvg(out);
        return out;
    }
}
