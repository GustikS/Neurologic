package discoverer;

import java.util.HashSet;
import java.util.Set;

/**
 * Return all grounded kappas for given node
 * <p>Also computes parent counts
 */
public class GroundKappaGetter {
    private static Set<GroundKappa> groundKappas  = new HashSet<GroundKappa>();

    /**
     * for a given Ball(=result of maximal substitution) retrieves all grounded Kappa(only) nodes recursively<p>
     * be aware - GroundKappas are unique objects, even if they share the same values, so the Set will still contain copies
     * @param b
     * @return 
     */
    public static Set<GroundKappa> getAllGroundKappas(Ball b) {
        groundKappas.clear();
        GroundKL o = b.getLast();
        o.setGroundParents(1);  //there is only one output for the output(Kappa) node
        
        if (o instanceof GroundKappa)
            getAllGroundKappas((GroundKappa) o);
        else
            getAllGroundKappas((GroundLambda) o);

        return groundKappas;
    }

    private static void getAllGroundKappas(GroundKappa gk) {
        gk.incrGroundParents(); //we came to this GroundKappa from some parent
        
        if (gk.isElement())
            return;

        groundKappas.add(gk);
        for (Tuple<GroundLambda, KappaRule> element: gk.getDisjuncts())
            getAllGroundKappas(element.x);

    }

    private static void getAllGroundKappas(GroundLambda gl) {
        gl.incrGroundParents();  //we came to this GroundLambda from some parent
        
        for (GroundKappa gk: gl.getConjuncts())
            getAllGroundKappas(gk);
    }
}
