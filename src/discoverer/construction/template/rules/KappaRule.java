package discoverer.construction.template.rules;

import discoverer.construction.Variable;
import discoverer.construction.template.Kappa;
import discoverer.construction.template.rules.SubL;
import discoverer.construction.template.rules.SubK;
import discoverer.construction.template.WeightInitializator;
import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;

/**
 * Kappa clause - THIS IS NOT A REAL RULE!! (just a construct to accommodate
 * disjunction)
 */
public class KappaRule extends Rule implements Serializable {

    //public static LinkedList<KappaRule> counter = new LinkedList<>();
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

        //    counter.add(this);
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
        for (Variable t : h.getTermsList()) {
            if (!t.isBind()) {
                unbound.add(t);
                allVars.add(t);
            }
        }
    }

    public void setBody(SubL b) {
        body = b;
        for (Variable t : b.getTermsList()) {
            if (!t.isBind()) {
                unbound.add(t);
                allVars.add(t);
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

    @Override
    public KappaRule getUnbindClone() {
        KappaRule clone = new KappaRule(this.weight);

        //important that this also sets the unbound list of terms - should be full now, i.e. forget all the bindings!
        //create new unbind body
        SubL sl = new SubL(this.body.getParent(), true);
        for (Variable t : this.body.getTerms()) {
            Variable tt = null;
            if (!clone.unbound.contains(t)) {
                tt = new Variable(t.name);
                clone.unbound.add(tt);
            } else {
                for (Variable var : clone.unbound) {
                    if (t.equals(var)) {
                        tt = var;
                        break;
                    }
                }
            }
            sl.addVariable(tt);
        }
        clone.body = sl;

        //and unbound head
        SubK sk = new SubK(this.head.getParent(), true);
        for (Variable t : this.head.getTerms()) {
            Variable tt = null;
            if (!clone.unbound.contains(t)) {
                tt = new Variable(t.name);
                clone.unbound.add(tt);
            } else {
                for (Variable var : clone.unbound) {
                    if (t.equals(var)) {
                        tt = var;
                        break;
                    }
                }
            }
            sk.addVariable(tt);
        }
        clone.setHead(sk);

        clone.originalName = this.originalName;
        return clone;
    }
}
