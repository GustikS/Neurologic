package discoverer.grounding.evaluation;

import discoverer.grounding.network.GroundKappa;
import discoverer.grounding.network.GroundLambda;
import discoverer.construction.network.rules.KappaRule;
import discoverer.global.Tuple;
import java.util.HashSet;
import java.util.Map;

/**
 * Traverse the graph and invalidate values of all GroundKappa/Lambda literals
 */
public class GroundInvalidator {

    public static void invalidate(Ball b) {
        if (b == null) {
            return;
        }

        Object top = b.getLast();
        if (top == null) {
            return;
        }

        if (top instanceof GroundKappa) {
            invalidate((GroundKappa) top);
        } else {
            invalidate((GroundLambda) top);
        }
    }

    public static void invalidateAVG(Ball b) {
        if (b == null) {
            return;
        }

        Object top = b.getLast();
        if (top == null) {
            return;
        }

        if (top instanceof GroundKappa) {
            invalidateAVG((GroundKappa) top);
        } else {
            invalidateAVG((GroundLambda) top);
        }
    }

    private static void invalidate(GroundKappa gk) {
        if (gk.isElement()) {
            return;
        }

        for (Tuple<GroundLambda, KappaRule> t : gk.getDisjuncts()) {
            invalidate(t.x);
        }

        gk.setValue(null);
    }

    private static void invalidateAVG(GroundKappa gk) {
        if (gk.isElement()) {
            return;
        }

        for (Tuple<HashSet<GroundLambda>, KappaRule> t : gk.getDisjunctsAvg()) {
            for (GroundLambda gl : t.x) {
                invalidateAVG(gl);
            }
        }

        gk.setValueAvg(null);
    }

    private static void invalidate(GroundLambda gl) {
        for (GroundKappa gk : gl.getConjuncts()) {
            invalidate(gk);
        }

        gl.setValue(null);
    }

    private static void invalidateAVG(GroundLambda gl) {
        for (Map.Entry<GroundKappa, Integer> wgk : gl.getConjunctsAvg().entrySet()) {
            invalidateAVG(wgk.getKey());
        }

        gl.setValueAvg(null);
    }
}
