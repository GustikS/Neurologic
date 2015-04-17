package discoverer.grounding.evaluation;

import discoverer.grounding.network.GroundKL;
import discoverer.grounding.network.GroundKappa;
import discoverer.grounding.network.GroundLambda;
import discoverer.construction.network.rules.KappaRule;
import discoverer.learning.backprop.functions.Sigmoid;
import discoverer.global.Tuple;
import java.util.HashSet;
import java.util.Map;

/**
 * Evaluating lk-network output
 */
public class EvaluatorAvg {

    public static double evaluate(Ball b) {
        GroundInvalidator.invalidateAVG(b);    //this means to delete all values of all ground literals (will work as caching)
        GroundKL top = b.getLast();
        if (top == null) {
            return b.valAvg;
        }
        if (top instanceof GroundKappa) {
            return evaluate((GroundKappa) top);
        } else {
            return evaluate((GroundLambda) top);
        }
    }

    private static double evaluate(GroundKappa gk) {
        if (gk.isElement()) {
            return gk.getValueAvg();
            //return gk.getValue(); //-should be the same
        }

        Double out = gk.getValueAvg();
        if (out != null) {
            return out;
        }

        out = gk.getGeneral().getWeight();

        for (Tuple<HashSet<GroundLambda>, KappaRule> t : gk.getDisjunctsAvg()) {
            double avg = 0;
            for (GroundLambda gl : t.x) {
                avg += evaluate(gl);
            }
            /*if (t.x.isEmpty()){
                System.out.println("problem");
            }*/
            avg /= t.x.size();
            out += avg * t.y.getWeight();
        }
        out = Sigmoid.sigmoid(out);
        gk.setValueAvg(out);
        return out;
    }

    private static double evaluate(GroundLambda gl) {
        Double out = gl.getValueAvg();
        if (out != null) {
            return out;
        }

        out = gl.getGeneral().getInitialW();
        double avg = 0;
        for (Map.Entry<GroundKappa, Integer> gk : gl.getConjunctsAvg().entrySet()) {
            avg += evaluate(gk.getKey()) * gk.getValue();
        }
        avg /= gl.getConjunctsCountForAvg();    //they are all averaged by the number of body groundings

        out = Sigmoid.sigmoid(out+avg);
        gl.setValueAvg(out);
        return out;
    }
}
