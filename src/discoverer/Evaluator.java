package discoverer;

/**
 * Evaluating lk-network output
 */
public class Evaluator {
    public static double evaluate(Ball b) {
        GroundInvalidator.invalidate(b);
        Object top = b.getLast();
        if (top instanceof GroundKappa)
            return evaluate((GroundKappa) top);
        else
            return evaluate((GroundLambda) top);
    }

    private static double evaluate(GroundKappa gk) {
        if (gk.isElement())
            return gk.getValue();

        Double out = gk.getValue();
        if (out != null)
            return out;

        out = gk.getGeneral().getWeight();
        for (Tuple<GroundLambda, KappaRule> t: gk.getDisjuncts())
            out += evaluate(t.x) * t.y.getWeight();

        out = Sigmoid.sigmoid(out);
        gk.setValue(out);
        return out;
    }

    private static double evaluate(GroundLambda gl) {
        Double out = gl.getValue();
        if (out != null)
            return out;

        out = gl.getGeneral().getInitialW();
        for (GroundKappa gk: gl.getConjuncts())
            out += evaluate(gk);

        out = Sigmoid.sigmoid(out);
        gl.setValue(out);
        return out;
    }
}
