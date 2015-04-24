package discoverer.grounding.evaluation;

import discoverer.global.Global;
import discoverer.grounding.network.GroundKappa;
import discoverer.grounding.network.GroundLambda;
import discoverer.construction.network.rules.KappaRule;
import discoverer.learning.backprop.functions.Activations;
import discoverer.global.Tuple;

/**
 * Evaluating lk-network output
 */
public class Evaluator {

    public static double evaluate(Ball b) {
        if (b == null) {
            return Global.falseAtomValue;
        }
        GroundInvalidator.invalidate(b);    //this means to delete all values of all ground literals
        Object top = b.getLast();
        if (top == null) {
            return b.valMax;
        }
        if (top instanceof GroundKappa) {
            return evaluate((GroundKappa) top);
        } else {
            return evaluate((GroundLambda) top);
        }
    }

    private static double evaluate(GroundKappa gk) {
        if (gk.isElement()) {
            return gk.getValue();
        }

        Double out = gk.getValue();
        if (out != null) {
            return out;
        }

        out = gk.getGeneral().getOffset();
        for (Tuple<GroundLambda, KappaRule> t : gk.getDisjuncts()) {
            out += evaluate(t.x) * t.y.getWeight();
        }

        out = Activations.kappaActivation(out);
        gk.setValue(out);
        return out;
    }

    private static double evaluate(GroundLambda gl) {
        Double out = gl.getValue();
        if (out != null) {
            return out;
        }

        out = gl.getGeneral().getOffset();
        for (GroundKappa gk : gl.getConjuncts()) {
            out += evaluate(gk);
        }

        out = Activations.lambdaActivation(out);
        gl.setValue(out);
        return out;
    }
}
