package discoverer.grounding.evaluation.struct;

import discoverer.global.Global;
import discoverer.grounding.network.GroundKL;
import discoverer.grounding.network.GroundKappa;
import discoverer.grounding.network.GroundLambda;
import discoverer.construction.network.rules.KappaRule;
import discoverer.global.Tuple;
import discoverer.grounding.evaluation.Ball;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Counts parent numbers, returns all grounded kappas for given ball
 * <p>
 * Also computes parent counts
 */
public class ParentCounter {

    private static Set<GroundKL> groundKLs = new HashSet<>();

    /**
     * for a given Ball(=result of maximal substitution) retrieves all grounded
     * Kappa(only) nodes recursively<p>
     * be aware - GroundKappas are unique objects, even if they share the same
     * values, so the Set will still contain copies
     *
     * @param b
     * @return
     */
    public static Set<GroundKL> countParentsAVG(Ball b) {
        groundKLs.clear();
        GroundKL o = b.getLast();
        if (o == null) {
            return groundKLs;
        }
        //o.setGroundParents(1);  //there is only one output for the output(Kappa) node

        if (o instanceof GroundKappa) {
            getAllGroundKappasAVG((GroundKappa) o);
        } else {
            getAllGroundKappasAVG((GroundLambda) o);
        }

        if (Global.debugEnabled) {
            for (GroundKL groundKappa : groundKLs) {
                System.out.println("counts: " + groundKappa + " - " + groundKappa.getGroundParents());
            }
        }
        if (true){
            for (GroundKL groundKL : groundKLs) {
                System.out.println("avgvals: " + groundKL + " -> " + groundKL.getValueAvg());
            }
        }
        return groundKLs;
    }

    public static Set<GroundKL> countParents(Ball b) {
        groundKLs.clear();
        GroundKL o = b.getLast();
        if (o == null) {
            return groundKLs;
        }
        //o.setGroundParents(1);  //there is only one output for the output(Kappa) node

        if (o instanceof GroundKappa) {
            getAllGroundKappas((GroundKappa) o);
        } else {
            getAllGroundKappas((GroundLambda) o);
        }

        return groundKLs;
    }

    private static void getAllGroundKappasAVG(GroundKappa gk) {
        if (Global.debugEnabled) {
            System.out.println("counting: " + gk);
        }

        gk.incrGroundParents(); //we came to this GroundKappa from some parent

        if (gk.isElement()) {
            return;
        }

        if (gk.getGroundParents() > 1) {
            return; // ha - if I already visited this node before - do not continue - first time visit pruning (oposite to backprop)
        }

        groundKLs.add(gk);
        for (Tuple<HashSet<GroundLambda>, KappaRule> element : gk.getDisjunctsAvg()) {
            for (GroundLambda gl : element.x) {
                getAllGroundKappasAVG(gl);
            }
        }
    }

    private static void getAllGroundKappasAVG(GroundLambda gl) {
        if (Global.debugEnabled) {
            System.out.println("counting: " + gl);
        }

        gl.incrGroundParents();  //we came to this GroundLambda from some parent

        if (gl.getGroundParents() > 1) {
            return; // ha - if I already visited this node before - do not continue - first time visit pruning (oposite to backprop)
        }

        groundKLs.add(gl);
        for (Map.Entry<GroundKappa, Integer> gk : gl.getConjunctsAvg().entrySet()) {
            getAllGroundKappasAVG(gk.getKey());
        }
    }

    private static void getAllGroundKappas(GroundKappa gk) {
        gk.incrGroundParents(); //we came to this GroundKappa from some parent

        if (gk.isElement()) {
            return;
        }

        if (gk.getGroundParents() > 1) {
            return; // ha - if I already visited this node before - do not continue - first time visit pruning (oposite to backprop)
        }

        groundKLs.add(gk);
        for (Tuple<GroundLambda, KappaRule> element : gk.getDisjuncts()) {

            getAllGroundKappas(element.x);

        }
    }

    private static void getAllGroundKappas(GroundLambda gl) {
        gl.incrGroundParents();  //we came to this GroundLambda from some parent

        if (gl.getGroundParents() > 1) {
            return; // ha - if I already visited this node before - do not continue - first time visit pruning (oposite to backprop)
        }
        
        groundKLs.add(gl);
        for (GroundKappa gk : gl.getConjuncts()) {
            getAllGroundKappas(gk);
        }
    }
}
