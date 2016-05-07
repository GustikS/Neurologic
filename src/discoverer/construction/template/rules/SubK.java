package discoverer.construction.template.rules;

import discoverer.construction.Variable;
import discoverer.construction.ElementMapper;
import discoverer.construction.template.Kappa;
import java.io.Serializable;

/**
 * partially grounded kappa(in rules)
 */
public class SubK extends SubKL implements Serializable {

    private Kappa parent;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(parent.toString());
        if (getTermsList().size() > 0) {
            sb.append("(");
            for (Variable v : getTermsList()) {
                sb.append(v);
                sb.append(",");
            }
            sb.replace(sb.length() - 1, sb.length(), ")");
        }
        return sb.toString();
    }

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

    public Integer getId() {
        return parent.getId();
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
    public int hashCode() {
        int hash = 17;
        hash = 31 * hash + parent.hashCode();
        for (Variable term : termsList) {
            Integer bind = term.getBind();
            if (bind != null) {
                hash = 31 * hash + bind.hashCode();
            }
        }

        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof SubK)) {
            return false;
        }

        SubK sk = (SubK) o;

        if (sk.getParent() != this.getParent()) {
            return false;
        }

        for (int i = 0; i < getTermsList().size(); i++) {
            int bind1 = this.getTerms().get(i).getBind();
            int bind2 = sk.getTerms().get(i).getBind();
            if (bind1 == -1 && bind2 != -1) {
                return false;
            }
            if (bind1 != -1 && bind2 == -1) {
                return false;
            }
            if (bind1 == -1 && bind2 == -1) {
                continue;
            }
            if (bind1!=bind2) {
                return false;
            }
        }

        return true;
    }

    public SubK clone() {
        SubK sk = new SubK(this.getParent(), true);
        for (Variable t : this.getTerms()) {
            Variable tt = new Variable("");
            tt.setBind(t.getBind());
            sk.addVariable(tt);
        }
        return sk;
    }
}
