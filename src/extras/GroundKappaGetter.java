package extras;

import lrnn.grounding.network.GroundKL;
import lrnn.grounding.network.GroundKappa;
import lrnn.grounding.network.GroundLambda;
import lrnn.construction.template.rules.KappaRule;
import lrnn.global.Tuple;
import lrnn.grounding.evaluation.GroundedTemplate;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Return all grounded kappas for given ground network
 * <p>Also computes parent counts
 */
public class GroundKappaGetter {
    private static Set<GroundKappa> groundKappas  = new LinkedHashSet<GroundKappa>();

    /**
     * for a given GroundedTemplate(=result of maximal substitution) retrieves all grounded Kappa(only) nodes recursively<p>
     * be aware - GroundKappas are unique objects, even if they share the same values, so the Set will still contain copies
     * @param b
     * @return 
     */
    public static Set<GroundKappa> getAllGroundKappas(GroundedTemplate b) {
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
