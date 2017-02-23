package lrnn.construction.template.rules;

import lrnn.construction.Variable;
import lrnn.construction.ElementMapper;
import lrnn.construction.template.Kappa;
import java.io.Serializable;

/**
 * partially grounded kappa(in rules)
 */
public class SubK extends SubKL implements Serializable {

    private Kappa parent;

    public SubK(Kappa k, boolean isHead) {
        parent = k;
        if (!isHead && isElement() && !parent.hasId()) {
            parent.setId(ElementMapper.map(parent.getName()));
        }
    }

    @Override
    public Kappa getParent() {
        return parent;
    }

    public boolean isElement() {
        return parent.isElement();
    }

    /**
     * score = if this SubK contains the respective variable, then how much is
     * this SubK constraned already?
     *
     * @param var
     * @return
     */
    public int countScoreFor(Variable var) {
        boolean present = false;
        int score = 0;

        for (Variable t : termsList) {
            if (var == t) {
                present = true;
            }
            if (t.isBind()) {
                score += 1;
            }
        }

        return present ? score : 0;
    }

    /**
     * it should be a relative constrain, i.e. start with most contrained
     * literals, not just the ones with most binded variables
     *
     * @param var
     * @return
     */
    public int countScoreFor2(Variable var) {
        int score = 0;

        for (Variable t : termsList) {
            if (t != var && !t.isBind()) {
                score -= 1; //for every unbound variable
            }
        }
        return score;
    }

    @Override
    public SubK clone() {
        SubK sk = new SubK(this.getParent(), true);
        for (Variable t : this.getTerms()) {
            Variable tt = new Variable(t.name);
            tt.setBind(t.getBind());
            sk.addVariable(tt);
        }
        return sk;
    }

}
