package discoverer;

import java.util.*;

/**
 * Weights in graph
 */
public class Weights {
    private Map<Object, Double> weights;
    private Map<Object, Double> diffs;
    private Map<Object, Integer> addedWeightsCount;

    public Weights() {
        weights = new HashMap<Object, Double>();
        diffs = new HashMap<Object, Double>();
        addedWeightsCount = new HashMap<Object, Integer>();
    }

    public void clear() {
        weights.clear();
        diffs.clear();
        addedWeightsCount.clear();
    }

    public void addW(KappaRule kr, Double d) {
        if (weights.containsKey(kr)) {
            weights.put(kr, weights.get(kr) + d);
            addedWeightsCount.put(kr, addedWeightsCount.get(kr) + 1);
        } else {
            weights.put(kr, d);
            addedWeightsCount.put(kr, 1);
        }
    }

    public void addW(Kappa k, Double d) {
        if (weights.containsKey(k)) {
            weights.put(k, weights.get(k) + d);
            addedWeightsCount.put(k, addedWeightsCount.get(k) + 1);
        } else {
            weights.put(k, d);
            addedWeightsCount.put(k, 1);
        }
    }

    public void addD(KappaRule kr, Double d) {
        diffs.put(kr, d);
    }

    public void addD(Kappa k, Double d) {
        diffs.put(k, d);
    }

    public Map<Object, Double> getWeights() {
        /*
         *for (Map.Entry<Object, Integer> e: addedWeightsCount.entrySet()) {
         *    Object key = e.getKey();
         *    Integer count = e.getValue();
         *    double w = weights.get(key);
         *    w /= count;
         *    weights.put(key, w);
         *}
         */

        return weights;
    }
    public Map<Object, Double> getDiffs() { return diffs; }
    public double getWeight(Object o) { return weights.get(o); }
    public double getDiffs(Object o) { return diffs.get(o); }
}
