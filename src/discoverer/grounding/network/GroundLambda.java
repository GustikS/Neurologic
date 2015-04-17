package discoverer.grounding.network;

import discoverer.construction.network.Lambda;
import discoverer.construction.Terminal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Grounded node -- lambda
 */
public class GroundLambda extends GroundKL {

    private Lambda general;

    private List<GroundKappa> conjuncts;
    private HashMap<GroundKappa, Integer> conjunctsAvg;
    private int conjunctsCountForAvg = 0;

    public GroundLambda(Lambda l, List<Terminal> terms) {
        super(terms);
        general = l;
        conjuncts = new ArrayList<GroundKappa>();
        conjunctsAvg = new HashMap<>();
    }

    public void addConjunct(GroundKappa gk) {
        conjuncts.add(gk);
    }

    private void addConjunctAvg(GroundKappa gk) {
        if (!conjunctsAvg.containsKey(gk)) {
            getConjunctsAvg().put(gk, 0);
        }
        getConjunctsAvg().put(gk, getConjunctsAvg().get(gk) + 1);
    }

    public List<GroundKappa> getConjuncts() {
        return conjuncts;
    }

    public Lambda getGeneral() {
        return general;
    }

    @Override
    public String toString() {
        String s = general.getName() + "(";
        for (Integer i : getTermList()) {
            s += i + ",";
        }
        s = s.substring(0, s.length() - 1);
        s += ")#" + getId();
        return s;
    }

    public void addConjuctsAvgFrom(Set<GroundLambda> gls) {
        if (gls == null) {
            return;
        }

        conjunctsCountForAvg += gls.size();  //the number of body groundings

        for (GroundLambda gl : gls) {   //all GroundLambdas are the same here
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

}
