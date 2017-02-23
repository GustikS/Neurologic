package lrnn.construction.template;

import lrnn.construction.template.rules.LambdaRule;
import lrnn.global.Global;
import java.io.Serializable;

/**
 * Lambda node = rule neuron
 */
public class Lambda extends KL implements Serializable {

    private LambdaRule rule;

    public Lambda(String name) {
        super(name);
    }

    public Lambda() {
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

}