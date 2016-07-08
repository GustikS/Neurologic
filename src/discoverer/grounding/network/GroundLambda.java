package discoverer.grounding.network;

import discoverer.construction.template.Lambda;
import discoverer.construction.Variable;
import discoverer.construction.template.KL;
import discoverer.global.Global;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Grounded node -- lambda = rule neuron
 */
public class GroundLambda extends GroundKL implements Serializable {

    //change this into static arrays for performance!!
    private List<GroundKappa> conjuncts;
    private HashMap<GroundKappa, Integer> conjunctsAvg;

    public List<List<GroundKappa>> fullBodyGroundings;

    //public GroundKappa[] bodyLiterals;
    //public int[] bodyLiteralCounts;
    private int conjunctsCountForAvg = 0; //number of all body-groundings for AVG

    public GroundLambda(KL l, List<Variable> terms) {
        super(terms);
        general = l;
        conjuncts = new ArrayList<GroundKappa>();
        conjunctsAvg = new HashMap<>();
        fullBodyGroundings = new ArrayList<>();
    }

    @Override
    public GroundLambda cloneMe() {
        GroundLambda gl = new GroundLambda(general);
        gl.conjuncts.addAll(conjuncts);
        gl.conjunctsAvg.putAll(conjunctsAvg);
        gl.setTermList(getTermList());
        return gl;
    }

    public GroundLambda(KL k) {
        super();
        general = k;
        conjuncts = new ArrayList<GroundKappa>();
        conjunctsAvg = new HashMap<>();
    }

    public void addConjunct(GroundKappa gk) {
        conjuncts.add(gk);
    }

    /*
     private void addConjunctAvg(GroundKappa gk) {
     if (!conjunctsAvg.containsKey(gk)) {
     getConjunctsAvg().put(gk, 0);
     }
     getConjunctsAvg().put(gk, getConjunctsAvg().get(gk) + 1);
     }
     */
    public List<GroundKappa> getConjuncts() {
        return conjuncts;
    }

    public void addConjuctsAvgFrom(Set<GroundLambda> gls) {
        if (gls == null) {
            return;
        }

        conjunctsCountForAvg += gls.size();  //the number of body groundings

        for (GroundLambda gl : gls) {   //all GroundLambdas are the same here
            if (Global.uncompressedLambda) {
                fullBodyGroundings.add(gl.conjuncts);   //also store the uncompressed representation here
            }
            for (GroundKappa gk : gl.conjuncts) {
                if (!conjunctsAvg.containsKey(gk)) {
                    getConjunctsAvg().put(gk, 0);
                }
                getConjunctsAvg().put(gk, getConjunctsAvg().get(gk) + 1);
            }
        }
    }

    /**
     * @return the conjunctsAvg
     */
    public HashMap<GroundKappa, Integer> getConjunctsAvg() {
        return conjunctsAvg;
    }

    /**
     * @param conjunctsAvg the conjunctsAvg to set
     */
    public void setConjunctsAvg(HashMap<GroundKappa, Integer> conjunctsAvg) {
        this.conjunctsAvg = conjunctsAvg;
    }

    /**
     * @return the conjunctsCountForAvg
     */
    public int getConjunctsCountForAvg() {
        return conjunctsCountForAvg;
    }

    /**
     * @param conjunctsCountForAvg the conjunctsCountForAvg to set
     */
    public void setConjunctsCountForAvg(int conjunctsCountForAvg) {
        this.conjunctsCountForAvg = conjunctsCountForAvg;
    }

    /*
     @Override
     public void transform2Arrays() {
     bodyLiterals = new GroundKappa[conjunctsAvg.size()];
     int i=0;
     for (Map.Entry<GroundKappa, Integer> bodylit : conjunctsAvg.entrySet()) {
     bodyLiterals[i] = bodylit.getKey();
     bodyLiteralCounts[i++] = bodylit.getValue();
     }
     }
     */
    public boolean isElement() {
        return conjuncts.isEmpty() && conjunctsAvg.isEmpty();
    }
}
