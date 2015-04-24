package discoverer.construction.network;

import discoverer.construction.network.rules.LambdaRule;
import discoverer.global.Global;

/**
 * Lambda node
 */
public class Lambda extends KL {

    private LambdaRule rule;
    private double offset;

    public Lambda(String name) {
        super(name);
    }

    public void setOffset(double d) {
        offset = d;
    }

    public double getOffset() {
        return offset;
    }

    public LambdaRule getRule() {
        return rule;
    }

    /**
     * - offset by the number of conjuncts
     *
     * @param lr
     */
    public void setRule(LambdaRule lr) {
        rule = lr;
        setOffset(-rule.getBodyLen() / Global.initLambdaOffsetK + 0.0);
        //initialW = 0.0;
    }
}
