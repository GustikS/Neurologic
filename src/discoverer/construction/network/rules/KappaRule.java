package discoverer.construction.network.rules;

import discoverer.construction.Terminal;
import discoverer.construction.network.Kappa;
import discoverer.construction.network.rules.SubL;
import discoverer.construction.network.rules.SubK;
import discoverer.construction.network.WeightInitializator;
import java.io.Serializable;

/**
 * Kappa clause - THIS IS NOT A REAL RULE!! (just a construct to accommodate disjunction)
 */
public class KappaRule extends Rule implements Serializable {

    private double weight;    //I don't want this to be a public object anymore
    public double step;
    private Double gradient;
    public double deltaW;
    private SubK head;
    private SubL body;
    private boolean drawn;
    
    @Override
    public String toString() {
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

    public void addHead(SubK h) {
        head = h;
    }

    public void setBody(SubL b) {
        body = b;
        for (Terminal t : b.getTermsList()) {
            if (!t.isBind()) {
                unbound.add(t);
            }
        }
    }

    public void increaseWeight(double d) {
        setWeight(getWeight() + d);
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
    public Terminal getNextUnbound() {
        for (Terminal var : unbound) {
            if (var.isDummy()) {
                return var;
            }
        }

        return super.unbound.iterator().next();
    }
}
