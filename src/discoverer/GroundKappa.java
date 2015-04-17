package discoverer;

import java.util.ArrayList;
import java.util.List;

/**
 * Grounded kappa node
 */
public class GroundKappa {
    private static int counter = 0;
    private Kappa general;
    private List<Tuple<GroundLambda, KappaRule>> disjuncts;
    private Double value;
    private int id;
    private List<Integer> termList;

    public GroundKappa(Kappa k, List<Terminal> terms) {
        general = k;
        disjuncts = new ArrayList<Tuple<GroundLambda, KappaRule>>();
        id = counter++;
        termList = new ArrayList<Integer>();
        if (terms != null) {
            for (Terminal t: terms) {
                termList.add(t.getBind());
            }
        }
    }

    public void addDisjunct(GroundLambda gl, KappaRule kr) {
        Tuple<GroundLambda, KappaRule> t;
        t = new Tuple<GroundLambda, KappaRule>(gl, kr);
        disjuncts.add(t);
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double val) {
        value = val;
    }

    public boolean isElement() {
        return disjuncts.isEmpty();
    }

    public List<Tuple<GroundLambda, KappaRule>> getDisjuncts() {
        return disjuncts;
    }

    public Kappa getGeneral() {
        return general;
    }

    @Override
    public String toString() {
        String s = general.getName() + "(";
        for (Integer i: termList) {
            s += i + ",";
        }
        s = s.substring(0, s.length() - 1);
        s += ")#" + id;
        return s;
    }
}
