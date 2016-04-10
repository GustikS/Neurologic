package discoverer.construction.network.rules;

import discoverer.construction.ElementMapper;
import discoverer.construction.Variable;
import discoverer.construction.template.Lambda;
import java.io.Serializable;

/**
 * partially grounded lambda
 */
public class SubL extends SubKL implements Serializable {

    private Lambda parent;

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

    public SubL(Lambda l, boolean isHead) {
        parent = l;
        if (!isHead && isElement() && !parent.hasId()) {
            parent.setId(ElementMapper.map(parent.getName()));
        }
    }

    @Override
    public Lambda getParent() {
        return parent;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        hash = 31 * hash + parent.hashCode();
        for (Variable term : termsList) {
            int bind = term.getBind();
            if (bind != -1) {
                hash = 31 * hash + bind;
            }
        }
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof SubL)) {
            return false;
        }

        SubL sl = (SubL) o;

        if (sl.getParent() != this.getParent()) {
            return false;
        }

        for (int i = 0; i < getTermsList().size(); i++) {
            int bind1 = this.getTerms().get(i).getBind();
            int bind2 = sl.getTerms().get(i).getBind();
            if (bind1 == -1 && bind2 != -1) {
                return false;
            }
            if (bind1 != -1 && bind2 == -1) {
                return false;
            }
            if (bind1 == -1 && bind2 == -1) {
                continue;
            }
            if (bind1 != bind2) {
                return false;
            }
        }

        return true;
    }

    public SubL clone() {
        SubL sl = new SubL(this.getParent(), true);
        for (Variable t : this.getTerms()) {
            Variable tt = new Variable("");
            tt.setBind(t.getBind());
            sl.addVariable(tt);
        }
        return sl;
    }

    public final boolean isElement() {
        return parent.isElement();
    }

    public Integer getId() {
        return parent.getId();
    }
}
