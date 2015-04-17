package discoverer;

import java.util.*;


/**
 * Kappa node
 */
public class Kappa extends KL {
    private List<KappaRule> rules;
    public double weight;
    public double step;
    public double deltaW;
    private Double gradient;
    private Integer id;

    public Kappa(String name, Double d) {
        super(name);
        rules = new ArrayList<KappaRule>();
        weight = d;
        step = 0.01;
    }

    public double getGradient() { return gradient; }
    public boolean gradientIsNull() { return gradient == null; }
    public void setGradient(double g) { gradient = g; }
    public void eraseGradient() { gradient = null; }

    public void addRule(KappaRule kr) { rules.add(kr); }
    public void setWeight(double d)     { weight = d; }
    public void setId(Integer i)      { id = i; }
    public double getWeight()           { return weight; }
    public boolean isElement()        { return rules.isEmpty(); }
    public Integer getId()                { return id; }
    public boolean hasId()            { return id != null; }
    public List<KappaRule> getRules()  { return rules; }
}
