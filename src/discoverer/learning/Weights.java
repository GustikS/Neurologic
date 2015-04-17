package discoverer.learning;

import discoverer.construction.network.rules.KappaRule;
import discoverer.construction.network.Kappa;
import discoverer.global.Tuple;
import java.util.*;

/**
 * Weights in graph
 */
public class Weights {

    private Map<Object, Double> weights;
    private List<Tuple<Object, Double>> updates;
    private Map<Object, Double> diffs;
    private Map<Object, Integer> addedWeightsCount; //number of particular edge-weight updates

    public Weights() {
        weights = new HashMap<Object, Double>();
        updates = new LinkedList<>();

        diffs = new HashMap<Object, Double>();
        addedWeightsCount = new HashMap<Object, Integer>();
    }

    public void clear() {
        weights.clear();
        updates.clear();

        diffs.clear();
        addedWeightsCount.clear();
    }

    /**
     * kapparule weight update
     *
     * @param kr
     * @param d
     */
    public void addW(KappaRule kr, Double d) {
        updates.add(new Tuple<>(kr, d));
        if (weights.containsKey(kr)) {
            weights.put(kr, weights.get(kr) + d);
            addedWeightsCount.put(kr, addedWeightsCount.get(kr) + 1);
        } else {
            weights.put(kr, d);
            addedWeightsCount.put(kr, 1);
        }
    }

    public void addW(Kappa k, Double d) {
        updates.add(new Tuple<>(k, d));
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

    public Map<Object, Double> getDiffs() {
        return diffs;
    }

    public double getWeight(Object o) {
        return weights.get(o);
    }

    public double getDiffs(Object o) {
        return diffs.get(o);
    }
}
