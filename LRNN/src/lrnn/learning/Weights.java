package lrnn.learning;

import lrnn.construction.template.KL;
import lrnn.construction.template.rules.KappaRule;
import lrnn.construction.template.Kappa;
import lrnn.global.Tuple;
import lrnn.grounding.network.GroundKL;

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
        weights = new LinkedHashMap<>();
        updates = new LinkedList<>();

        diffs = new HashMap<>();
        addedWeightsCount = new HashMap<>();
    }

    public Weights compareTo(Weights w) {
        Weights res = new Weights();
        for (Map.Entry<Object, Double> wt : w.weights.entrySet()) {
            if (weights.containsKey(wt.getKey())) {
                if ((wt.getValue() - weights.get(wt.getKey())) < 0.000000000001) {
                    //ok
                } else {
                    res.getWeights().put(wt.getKey(), wt.getValue() - weights.get(wt.getKey()));
                }
            } else {
                res.getWeights().put(wt.getKey(), 1000.0);
            }
        }
        return res;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Object, Double> w : weights.entrySet()) {
            sb.append(w.getKey()).append(" += ").append(w.getValue()).append("\n");
        }
        return sb.toString();
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
        updates.add(new Tuple<Object, Double>(kr, d));
        if (weights.containsKey(kr)) {
            weights.put(kr, weights.get(kr) + d);
            addedWeightsCount.put(kr, addedWeightsCount.get(kr) + 1);
        } else {
            weights.put(kr, d);
            addedWeightsCount.put(kr, 1);
        }
    }

    public void addW(KL k, Double d) {
        updates.add(new Tuple<Object, Double>(k, d));
        if (weights.containsKey(k)) {
            weights.put(k, weights.get(k) + d);
            addedWeightsCount.put(k, addedWeightsCount.get(k) + 1);
        } else {
            weights.put(k, d);
            addedWeightsCount.put(k, 1);
        }
    }

    public void addW(GroundKL k, double d) {
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
