package discoverer;

import java.util.*;

/**
 * One line with rule
 */
public abstract class Rule {
    protected Set<Terminal> unbound;
    private Terminal lastBindedVar;

    public Rule() {
        unbound = new HashSet<Terminal>();
    }

    public Terminal getLastBindedVar() {
        return lastBindedVar;
    }

    public void setLastBindedVar(Terminal term) {
        lastBindedVar = term;
    }

    public void consumeVars(List<Terminal> vars) {
        if (vars == null || vars.size() == 0)
            return;

        SubKL head = getHead();
        for (int i = 0; i < vars.size(); i++) {
            Terminal var = head.getTerm(i);
            Terminal boundedVar = vars.get(i);
            if (boundedVar.isBind()) {
                int c = boundedVar.getBind();
                bind(var, c);
            } else {
                unbind(var);
            }
        }
    }

    public void unconsumeVars() {
        SubKL head = getHead();
        for (Terminal v: head.getTerms())
            unbind(v);
    }

    public void bind(Terminal var, int c) {
        var.setBind(c);
        unbound.remove(var);
    }

    public void unbind(Terminal var) {
        var.unBind();
        unbound.add(var);
    }

    //public Terminal getNextUnbound() { return unbound.iterator().next(); }
    public abstract Terminal getNextUnbound();

    public boolean isBound() { return unbound.isEmpty(); }
    protected abstract SubKL getHead();
}
