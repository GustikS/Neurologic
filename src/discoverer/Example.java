package discoverer;

import java.util.*;

/**
 * lk-sample representation
 */
public class Example {
    private double expVal;
    private Chunks chunks;
    private Map<Integer, List<Integer>> possibleChunks;
    private int constCount;

    public Example(Double exp, Map<Integer, List<Integer>> map) {
        expVal = exp;
        chunks = new Chunks();
        possibleChunks = map;
    }

    public void setConstCount(Integer i) { constCount = i; }
    public int getConstCount() { return constCount; }
    public double getExpectedValue() { return expVal; }
    public void addChunk(int[] numeral) { chunks.insert(numeral); }
    public Map<Integer, List<Integer>> getPossibleChunks() { return possibleChunks; }
    public Chunks getChunks() { return chunks; }

    public boolean containsLiteral(Kappa k) {
        return possibleChunks.containsKey(k.getId());
    }

    public Boolean containsNG(SubK sk) {
        int len = sk.getTerms().size() + 1;
        List<Integer> ia2 = new ArrayList<Integer>(len);
        ia2.add(sk.getId());

        for (int i = 1; i < len; i++) {
            Terminal t = sk.getTerm(i-1);
            int x = t.isBind() ? t.getBind() : -1;
            ia2.add(x);
        }

        return contains2NG(ia2);
    }

    public Boolean contains(SubK sk) {
        int len = sk.getTerms().size() + 1;
        int[] ia = new int[len];

        ia[0] = sk.getId();
        for (int i = 1; i < ia.length; i++) {
            Terminal t = sk.getTerm(i-1);
            ia[i] = t.isBind() ? t.getBind() : -1;
        }

        return contains2(ia);
    }

    private boolean contains2NG(List<Integer> literal) {
        if (!possibleChunks.containsKey(literal.get(0)))
            return false;

        for (Integer possibleLiteral: possibleChunks.get(literal.get(0))) {
            literal.set(0, possibleLiteral);
            if (chunks.containsNG(literal))
                return true;
        }
        return false;
    }

    private boolean contains2(int[] literal) {
        if (!possibleChunks.containsKey(literal[0]))
            return false;

        for (Integer possibleLiteral: possibleChunks.get(literal[0])) {
            literal[0] = possibleLiteral;
            if (chunks.contains(literal))
                return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return Double.toString(expVal);
    }
}
