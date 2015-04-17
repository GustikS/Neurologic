package discoverer;


/**
 * Kappa clause
 */
public class KappaRule extends Rule {
    public double weight;
    public double step;
    private Double gradient;
    public double deltaW;
    private SubK head;
    private SubL body;
    private boolean drawn;

    @Override
    public String toString() { return head.toString() + ":-" + body.toString(); }

    public KappaRule (double w) {
        step = 0.01;
        weight = w != 0 ? w : WeightInitializator.init();
        drawn = false;
    }

    public boolean isDrawn() {
        return drawn;
    }

    public void setDrawn(boolean b) {
        drawn = b;
    }

    public double getGradient() { return gradient; }
    public boolean gradientIsNull() { return gradient == null; }
    public void setGradient(double g) { gradient = g; }
    public void eraseGradient() { gradient = null; }

    public void addHead(SubK h) { head = h; }

    public void setBody(SubL b) {
        body = b;
        for (Terminal t: b.getTermsList())
            if (!t.isBind())
                unbound.add(t);
    }

    public void increaseWeight(double d) {
        weight += d;
    }

    public SubL getBody() { return body; }

    public double getWeight() { return weight; }
    public void setWeight(double d) { weight = d; }

    protected SubK getHead() { return head; }

    @Override
    public Terminal getNextUnbound() {
        for (Terminal var: unbound)
            if (var.isDummy())
                return var;

        return super.unbound.iterator().next();
    }
}
