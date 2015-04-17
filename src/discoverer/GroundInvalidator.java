package discoverer;

/**
 * Traverse the graph and invalidate weights
 */
public class GroundInvalidator {
    public static void invalidate(Ball b) {
        if (b == null)
            return;

        Object top = b.getLast();
        if (top instanceof GroundKappa)
            invalidate((GroundKappa) top);
        else
            invalidate((GroundLambda) top);
    }

    private static void invalidate(GroundKappa gk) {
        if (gk.isElement())
            return;

        for (Tuple<GroundLambda, KappaRule> t: gk.getDisjuncts())
            invalidate(t.x);

        gk.setValue(null);
    }

    private static void invalidate(GroundLambda gl) {
        for (GroundKappa gk: gl.getConjuncts())
            invalidate(gk);

        gl.setValue(null);
    }
}
