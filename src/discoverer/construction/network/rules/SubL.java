package discoverer.construction.network.rules;

import discoverer.construction.ElementMapper;
import discoverer.construction.Terminal;
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
            for (Terminal v : getTermsList()) {
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
        for (Terminal term : termsList) {
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
        if (!(o instanceof SubL)) {
            return false;
        }

        SubL sl = (SubL) o;

        if (sl.getParent() != this.getParent()) {
            return false;
        }

        for (int i = 0; i < getTermsList().size(); i++) {
            Integer bind1 = this.getTerms().get(i).getBind();
            Integer bind2 = sl.getTerms().get(i).getBind();
            if (bind1 == null && bind2 != null) {
                return false;
            }
            if (bind1 != null && bind2 == null) {
                return false;
            }
            if (bind1 == null && bind2 == null) {
                continue;
            }
            if (!bind1.equals(bind2)) {
                return false;
            }
        }

        return true;
    }

    public SubL clone() {
        SubL sl = new SubL(this.getParent(), true);
        for (Terminal t : this.getTerms()) {
            Terminal tt = new Terminal("");
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
