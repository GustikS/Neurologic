package discoverer;

/**
 * Lambda node
 */
public class Lambda extends KL {
    private LambdaRule rule;
    public double initialW;

    public Lambda(String name) { super(name); }

    public void setInitialW(double d) { initialW = d; }
    public double getInitialW()       { return initialW; }
    public LambdaRule getRule()       { return rule; }

    public void setRule(LambdaRule lr) {
        rule = lr;
        initialW = -rule.getBodyLen() + 0.0;
    }
}
