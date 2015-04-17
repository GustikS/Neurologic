package discoverer;

/**
 * Grounded lambda
 */
public class SubL extends SubKL {
    private Lambda parent;

    public SubL(Lambda l) { parent = l; }

    @Override
    public Lambda getParent() { return parent; }

    @Override
    public String toString() {
        String s = parent.toString();
        for (Terminal v: super.getTermsList()) {
            s += v;
            s += ",";
        }
        return s;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        hash = 31 * hash + parent.hashCode();
        for (Terminal term: termsList) {
            Integer bind = term.getBind();
            if (bind != null)
                hash = 31 * hash + bind.hashCode();
        }

        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof SubL)) return false;

        SubL sl = (SubL) o;

        if (sl.getParent() != this.getParent()) return false;

        for (int i = 0; i < getTermsList().size(); i++) {
            Integer bind1 = this.getTerms().get(i).getBind();
            Integer bind2 = sl.getTerms().get(i).getBind();
            if (bind1 == null && bind2 != null) return false;
            if (bind1 != null && bind2 == null) return false;
            if (bind1 == null && bind2 == null) continue;
            if (!bind1.equals(bind2)) return false;
        }

        return true;
    }

    public SubL clone() {
        SubL sl = new SubL(this.getParent());
        for (Terminal t: this.getTerms()) {
            Terminal tt = new Terminal("");
            tt.setBind(t.getBind());
            sl.addVariable(tt);
        }
        return sl;
    }
}
