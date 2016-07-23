package discoverer.construction.example;

import discoverer.construction.template.Kappa;
import discoverer.construction.template.rules.SubK;
import discoverer.construction.Variable;
import discoverer.construction.template.Lambda;
import discoverer.construction.template.rules.SubKL;
import discoverer.construction.template.rules.SubL;
import discoverer.global.Global;
import discoverer.grounding.network.GroundKL;
import discoverer.grounding.network.GroundKappa;
import discoverer.grounding.network.GroundLambda;
import java.io.Serializable;
import java.util.*;

/**
 * lk-sample representation
 */
public class Example implements Serializable {

    private double targetVal;
    private Chunks chunks;
    //map of existing chunks from other examples
    private Map<Integer, List<Integer>> possibleChunks;
    private int constCount;
    public String hash;
    //new feature - constant IDs to their original names!
    public HashMap<Integer, String> constantNames = new HashMap<>();
    //in case of weighted facts, set of all facts with weights
    public HashMap<SubKL, GroundKL> storedFacts;

    public Example() {
    }

    /**
     * new example from expected value and map of literal occurrences
     *
     * @param expected
     * @param map
     * @param ihash
     */
    public Example(Double expected, Map<Integer, List<Integer>> map, String ihash) {
        targetVal = expected;
        chunks = new Chunks();
        possibleChunks = map;
        hash = ihash;
    }

    public void setConstCount(Integer i) {
        constCount = i;
    }

    public int getConstCount() {
        return constCount;
    }

    public void setExpectedValue(double val) {
        targetVal = val;
    }
    
    public double getExpectedValue() {
        return targetVal;
    }

    public void addChunk(int[] numeral) {
        chunks.insert(numeral);
    }

    public Map<Integer, List<Integer>> getPossibleChunks() {
        return possibleChunks;
    }

    public Chunks getChunks() {
        return chunks;
    }

    public boolean containsLiteral(Kappa k) {
        return possibleChunks.containsKey(k.getId());
    }

    /**
     * not used (?)
     *
     * @param sk
     * @return
     */
    public Boolean containsNG(SubK sk) {
        int len = sk.getTerms().size() + 1;
        List<Integer> ia2 = new ArrayList<Integer>(len);
        ia2.add(sk.getId());

        for (int i = 1; i < len; i++) {
            Variable t = sk.getTerm(i - 1);
            int x = t.isBind() ? t.getBind() : -1;
            ia2.add(x);
        }

        return contains2NG(ia2);
    }

    public Boolean contains(SubKL skl) {
        int len = skl.getTerms().size() + 1;
        int[] ia = new int[len];

        ia[0] = skl.getId();
        for (int i = 1; i < ia.length; i++) {
            Variable t = skl.getTerm(i - 1);
            ia[i] = t.isBind() ? t.getBind() : -1;
        }

        return contains2(ia);
    }

    private boolean contains2NG(List<Integer> literal) {
        if (!possibleChunks.containsKey(literal.get(0))) {
            return false;
        }

        for (Integer possibleLiteral : possibleChunks.get(literal.get(0))) {
            literal.set(0, possibleLiteral);
            if (chunks.containsNG(literal)) {
                return true;
            }
        }
        return false;
    }

    private boolean contains2(int[] literal) {
        if (!possibleChunks.containsKey(literal[0])) {
            return false;
        }

        for (Integer possibleLiteral : possibleChunks.get(literal[0])) {
            literal[0] = possibleLiteral;
            if (chunks.contains(literal)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return Double.toString(targetVal);
    }

    public GroundKL getFact(SubKL skl) {
        return storedFacts.get(skl);
    }

    public void setWeightedFacts(double[] weights, List<SubKL> literals) {
        storedFacts = new LinkedHashMap<>();
        for (int i = 0; i < weights.length; i++) {
            SubKL lit = literals.get(i);
            if (lit instanceof SubL) {
                GroundLambda gl = new GroundLambda((Lambda) lit.getParent(), lit.getTerms());
                gl.setValue(weights[i]);
                gl.setValueAvg(weights[i]);
                storedFacts.put(lit, gl);
            } else {
                GroundKappa gl = new GroundKappa((Kappa) lit.getParent(), lit.getTerms());
                gl.setValue(weights[i]);
                gl.setValueAvg(weights[i]);
                storedFacts.put(lit, gl);
            }
        }
    }
}
