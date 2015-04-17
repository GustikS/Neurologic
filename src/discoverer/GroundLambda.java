package discoverer;

import java.util.ArrayList;
import java.util.List;

/**
 * Grounded node -- lambda
 */
public class GroundLambda {
    private static int counter = 0;
    private Lambda general;
    private List<GroundKappa> conjuncts;
    private Double value;
    private int id;
    private List<Integer> termList;

    public GroundLambda(Lambda l, List<Terminal> terms) {
        general = l;
        conjuncts = new ArrayList<GroundKappa>();
        id = counter++;
        termList = new ArrayList<Integer>();
        if (terms != null) {
            for (Terminal t: terms) {
                termList.add(t.getBind());
            }
        }
    }

    public void addConjunct(GroundKappa gk) {
        conjuncts.add(gk);
    }

    public List<GroundKappa> getConjuncts() {
        return conjuncts;
    }

    public Lambda getGeneral() {
        return general;
    }

    public void setValue(Double d) {
        value = d;
    }

    public Double getValue() {
        return value;
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
