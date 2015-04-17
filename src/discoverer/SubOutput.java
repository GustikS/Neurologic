package discoverer;

import java.util.Arrays;

/**
 * Output of grounded node
 */
public class SubOutput {
    public String name;
    private int[] binds;
    private int hash;

    public SubOutput(SubK sk) {
        name = sk.getParent().getName();
        int len = sk.getTermsList().size();
        binds = new int[len];
        hash = computeHash();
        for (int i = 0; i < sk.getTerms().size(); i++) {
            Integer b1 = sk.getTerm(i).getBind();
            binds[i] = b1 == null ? -1 : b1;
        }
    }

    public SubOutput(SubL sl) {
        name = sl.getParent().getName();
        int len = sl.getTermsList().size();
        binds = new int[len];
        hash = computeHash();
        for (int i = 0; i < sl.getTerms().size(); i++) {
            Integer b1 = sl.getTerm(i).getBind();
            binds[i] = b1 == null ? -1 : b1;
        }
    }

    private int computeHash() {
        int hash = name.hashCode();
        hash += Arrays.hashCode(binds);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof SubOutput)) return false;

        SubOutput so = (SubOutput) o;

        if (binds.length != so.binds.length) return false;
        if (name != so.name) return false;


        //return Arrays.equals(binds, so.binds);
        for (int i = 0; i < binds.length; i++)
            if (binds[i] != so.binds[i]) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public String toString() {
        String s = name + " ";
        for (int i: binds) {
            s += i;
        }

        return s;
    }
}
