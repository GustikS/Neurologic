package discoverer.grounding.evaluation.struct;

import discoverer.grounding.network.GroundKL;
import discoverer.grounding.network.GroundKappa;
import discoverer.grounding.network.GroundLambda;
import discoverer.construction.network.rules.KappaRule;
import discoverer.global.Tuple;
import discoverer.grounding.evaluation.Ball;
import java.util.HashSet;
import java.util.Set;

/**
 * Return all grounded kappas for given ground network
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
