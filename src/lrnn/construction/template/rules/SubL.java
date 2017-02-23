package lrnn.construction.template.rules;

import lrnn.construction.ElementMapper;
import lrnn.construction.Variable;
import lrnn.construction.template.Lambda;
import java.io.Serializable;

/**
 * partially grounded lambda
 */
public class SubL extends SubKL implements Serializable {

    private Lambda parent;

    public SubL(Lambda l, boolean isHead) {
        parent = l;
        if (!isHead && isElement() && !parent.hasId()) {
            parent.setId(ElementMapper.map(parent.getName()));
        }
    }

    @Override
    public Lambda getParent() {
        return parent;
    }

    public final boolean isElement() {
        return getParent().isElement();
    }

    @Override
    public SubL clone() {
        SubL sl = new SubL(this.getParent(), true);
        for (Variable t : this.getTerms()) {
            Variable tt = new Variable(t.name);
            tt.setBind(t.getBind());
            sl.addVariable(tt);
        }
        return sl;
    }
}
