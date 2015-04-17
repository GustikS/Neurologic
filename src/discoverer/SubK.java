package discoverer;

/**
 * Grounded kappa
 */
public class SubK extends SubKL {
    private Kappa parent;

    public SubK(Kappa k, boolean isHead) {
        parent = k;
        if (!isHead && isElement() && !parent.hasId()) {
            parent.setId(ElementMapper.map(parent.getName()));
        }
    }

    @Override
    public Kappa getParent() { return parent; }

    @Override
    public String toString() {
        String s = parent.toString();
        for (Terminal v: super.getTermsList()) {
            s += v;
            s += ",";
        }
        return s;
    }

    public boolean isElement() { return parent.isElement(); }
    public Integer getId() { return parent.getId(); }

    public int countScoreFor(Terminal var) {
        boolean present = false;
        int score = 0;

        for (Terminal t: termsList) {
            if (var == t) present = true;
            if (t.isBind()) score += 1;
        }

        return present ? score : 0;
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
        if (!(o instanceof SubK)) return false;

        SubK sk = (SubK) o;

        if (sk.getParent() != this.getParent()) return false;

        for (int i = 0; i < getTermsList().size(); i++) {
            Integer bind1 = this.getTerms().get(i).getBind();
            Integer bind2 = sk.getTerms().get(i).getBind();
            if (bind1 == null && bind2 != null) return false;
            if (bind1 != null && bind2 == null) return false;
            if (bind1 == null && bind2 == null) continue;
            if (!bind1.equals(bind2)) return false;
        }

        return true;
    }

    public SubK clone() {
        SubK sk = new SubK(this.getParent(), true);
        for (Terminal t: this.getTerms()) {
            Terminal tt = new Terminal("");
            tt.setBind(t.getBind());
            sk.addVariable(tt);
        }
        return sk;
    }
}
