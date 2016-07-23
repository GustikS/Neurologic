package discoverer.construction.template;

import discoverer.construction.template.rules.KappaRule;
import discoverer.global.Global;
import java.io.Serializable;
import java.util.*;

/**
 * Kappa node = atom neuron
 */
public class Kappa extends KL implements Serializable {

    private List<KappaRule> rules;
    public double step;
    public double deltaW;
    //private Double gradient;

    public Kappa(String name) {
        super(name);
        rules = new ArrayList<KappaRule>();
        step = 0.01;
        if (!Global.isKappaAdaptiveOffset()) {
            initOffset();   //HERE and in the netfactory CHANGE to make possible for adaptive initialization
        }
        if (Global.KappaFixedZeroOffset) {
            offset = 0.0;
        }
    }

    public Kappa() {
    }

    public Kappa(String name, Double d) {
        super(name);
        rules = new ArrayList<KappaRule>();
        offset = d;
        step = 0.01;
    }

    public void initOffset() {
        if (Global.getInitKappaAdaptiveOffset() > 0) {
            offset = 1 / (2 * (Global.getRandomDouble() - 0.5) * Global.getInitKappaAdaptiveOffset() * rules.size());
        } else {
            offset = WeightInitializator.getWeight();
        }
    }

    /*
    public double getGradient() {
        return gradient;
    }

    public boolean gradientIsNull() {
        return gradient == null;
    }

    public void setGradient(double g) {
        gradient = g;
    }

    public void eraseGradient() {
        gradient = null;
    }
     */
    public void addRule(KappaRule kr) {
        rules.add(kr);
    }

    public boolean isElement() {
        return rules.isEmpty();
    }

    public List<KappaRule> getRules() {
        return rules;
    }
}
