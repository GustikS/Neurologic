package discoverer;

import java.util.HashSet;
import java.util.Set;

/**
 * Return all grounded kappas for given node
 */
public class GroundKappaGetter {
    private static Set<GroundKappa> groundKappas  = new HashSet<GroundKappa>();

    public static Set<GroundKappa> getAllGroundKappas(Ball b) {
        groundKappas.clear();
        Object o = b.getLast();
        if (o instanceof GroundKappa)
            getAllGroundKappas((GroundKappa) o);
        else
            getAllGroundKappas((GroundLambda) o);

        return groundKappas;
    }

    private static void getAllGroundKappas(GroundKappa gk) {
        if (gk.isElement())
            return;

        groundKappas.add(gk);
        for (Tuple<GroundLambda, KappaRule> element: gk.getDisjuncts())
            getAllGroundKappas(element.x);

    }

    private static void getAllGroundKappas(GroundLambda gl) {
        for (GroundKappa gk: gl.getConjuncts())
            getAllGroundKappas(gk);
    }
}
