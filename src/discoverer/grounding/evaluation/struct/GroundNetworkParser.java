package discoverer.grounding.evaluation.struct;

import discoverer.global.Global;
import discoverer.grounding.network.GroundKL;
import discoverer.grounding.network.GroundKappa;
import discoverer.grounding.network.GroundLambda;
import discoverer.construction.network.rules.KappaRule;
import discoverer.global.Tuple;
import discoverer.grounding.evaluation.GroundedTemplate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Counts parent numbers, returns all grounded kappas for given ball
 * <p>
 * Also computes parent counts
 */
public class GroundNetworkParser {

    public static Set<GroundKL> neurons = new HashSet<>();
    public static List<GroundKL> facts = new ArrayList<>();

    private static void clear() {
        neurons.clear();
        facts.clear();
    }

    /**
     * traverse the ground tree/network from top literal by DFS post-order and
     * load all neurons on the way to "neurons" + sets parent-counts right
     *
     * @param b
     * @return
     */
    public static Set<GroundKL> parseAVG(GroundedTemplate b) {
        clear();
        GroundKL o = b.getLast();
        if (o == null) {
            return neurons;
        }
        //o.setGroundParents(1);  //there is only one output for the output(Kappa) node

        if (o instanceof GroundKappa) {
            parseAVG((GroundKappa) o);
        } else {
            parseAVG((GroundLambda) o);
        }

        if (Global.isDebugEnabled()) {
            for (GroundKL groundKappa : neurons) {
                System.out.println("counts: " + groundKappa + " - " + groundKappa.getGroundParents());
            }
        }
        if (Global.isDebugEnabled()) {
            for (GroundKL groundKL : neurons) {
                System.out.println("avgvals: " + groundKL + " -> " + groundKL.getValueAvg());
            }
        }
        return neurons;
    }

    /**
     * traverse the ground tree/network from top literal by DFS post-order and
     * load all neurons on the way to "neurons" + sets parent-counts right
     *
     * @param b
     * @return
     */
    public static Set<GroundKL> parseMAX(GroundedTemplate b) {
        clear();
        GroundKL o = b.getLast();
        if (o == null) {
            return neurons;
        }
        //o.setGroundParents(1);  //there is only one output for the output(Kappa) node

        if (o instanceof GroundKappa) {
            parseMAX((GroundKappa) o);
        } else {
            parseMAX((GroundLambda) o);
        }

        return neurons;
    }

    private static void parseAVG(GroundKappa gk) {
        if (Global.isDebugEnabled()) {
            System.out.println("counting: " + gk);
        }

        gk.incrGroundParents(); //we came to this GroundKappa from some parent

        if (gk.isElement()) {
            facts.add(gk);
            return;
        }

        if (gk.getGroundParents() > 1) {
            return; // ha - if I already visited this node before - do not continue - first time visit pruning (oposite to backprop)
        }

        neurons.add(gk);
        for (Tuple<HashSet<GroundLambda>, KappaRule> element : gk.getDisjunctsAvg()) {
            for (GroundLambda gl : element.x) {
                parseAVG(gl);
            }
        }
    }

    private static void parseAVG(GroundLambda gl) {
        if (Global.isDebugEnabled()) {
            System.out.println("counting: " + gl);
        }
        if (gl.isElement()) {
            facts.add(gl);
            return;
        }

        gl.incrGroundParents();  //we came to this GroundLambda from some parent

        if (gl.getGroundParents() > 1) {
            return; // ha - if I already visited this node before - do not continue - first time visit pruning (oposite to backprop)
        }

        neurons.add(gl);
        for (Map.Entry<GroundKappa, Integer> gk : gl.getConjunctsAvg().entrySet()) {
            parseAVG(gk.getKey());
        }
    }

    private static void parseMAX(GroundKappa gk) {
        gk.incrGroundParents(); //we came to this GroundKappa from some parent

        if (gk.isElement()) {
            facts.add(gk);
            return;
        }

        if (gk.getGroundParents() > 1) {
            return; // ha - if I already visited this node before - do not continue - first time visit pruning (oposite to backprop)
        }

        neurons.add(gk);
        for (Tuple<GroundLambda, KappaRule> element : gk.getDisjuncts()) {

            parseMAX(element.x);

        }
    }

    private static void parseMAX(GroundLambda gl) {
        gl.incrGroundParents();  //we came to this GroundLambda from some parent
        
        if (gl.isElement()) {
            facts.add(gl);
            return;
        }
        
        if (gl.getGroundParents() > 1) {
            return; // ha - if I already visited this node before - do not continue - first time visit pruning (oposite to backprop)
        }

        neurons.add(gl);
        for (GroundKappa gk : gl.getConjuncts()) {
            parseMAX(gk);
        }
    }
}
