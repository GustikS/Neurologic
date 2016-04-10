package discoverer.construction.network.rules;

import discoverer.construction.Variable;
import discoverer.construction.template.Kappa;
import discoverer.construction.network.rules.SubL;
import discoverer.construction.network.rules.SubK;
import discoverer.construction.template.WeightInitializator;
import java.io.Serializable;
import java.util.Objects;

/**
 * Kappa clause - THIS IS NOT A REAL RULE!! (just a construct to accommodate
 * disjunction)
 */
public class KappaRule extends Rule implements Serializable {

    private double weight;    //I don't want this to be a public object anymore
    public double step;
    //private Double gradient;
    public double deltaW;
    private SubK head;
    private SubL body;
    private boolean drawn;
    private String string;

    @Override
    public String toString() {
        return head.toString() + " :- " + body.toString() + ".";
    }

    public String toFullString() {
        return String.format("%.15f", getWeight()) + " " + head.toString() + " :- " + body.toString() + ".";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof KappaRule)) {
            return false;
        }

        KappaRule kr = (KappaRule) o;

        if (this.toString().equals(kr.toString())) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.head);
        hash = 83 * hash + Objects.hashCode(this.body);
        return hash;
    }

    public KappaRule(double w) {
        //step = 0.01;
        setWeight(w != 0 ? w : WeightInitializator.getWeight());
        drawn = false;
    }

    public boolean isDrawn() {
        return drawn;
    }

    public void setDrawn(boolean b) {
        drawn = b;
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
    
    public void setHead(SubK h) {
        head = h;
    }

    public void setBody(SubL b) {
        body = b;
        for (Variable t : b.getTermsList()) {
            if (!t.isBind()) {
                unbound.add(t);
            }
        }
    }

    public SubL getBody() {
        return body;
    }

    public double getWeight() {
        return weight;
    }

    public final void setWeight(double d) {
        weight = d;
    }

    public SubK getHead() {
        return head;
    }

    @Override
    /**
     * return nex unbind variable - all dummy variables goes first if any,
     * otherwise just get a random next variable
     */
    public Variable getNextUnbound() {
        for (Variable var : unbound) {
            if (var.isDummy()) {
                return var;
            }
        }

        return super.unbound.iterator().next();
    }
}
