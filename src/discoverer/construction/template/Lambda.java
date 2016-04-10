package discoverer.construction.template;

import discoverer.construction.network.rules.LambdaRule;
import discoverer.global.Global;
import java.io.Serializable;

/**
 * Lambda node = rule neuron
 */
public class Lambda extends KL implements Serializable {

    private LambdaRule rule;
    private double offset;
    private Integer id;

    public Lambda(String name) {
        super(name);
    }

    public Lambda() {
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
        setOffset(-rule.getBodyLen() + Global.getInitLambdaAdaptiveOffset() + 0.0);
        //initialW = 0.0;
    }

    public boolean isElement() {
        return rule == null;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer i) {
        id = i;
    }

    public boolean hasId() {
        return id != null;
    }
}
