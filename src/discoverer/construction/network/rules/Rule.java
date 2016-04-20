package discoverer.construction.network.rules;

import discoverer.construction.Variable;
import discoverer.construction.network.rules.SubKL;
import java.io.Serializable;
import java.util.*;

/**
 * One line with rule
 */
public abstract class Rule implements Serializable {

    public String originalName;
    public Set<Variable> unbound;
    private Variable lastBindedVar;
    public Set<Integer> usedTerms;

    public Rule() {
        unbound = new HashSet<>();
        usedTerms = new HashSet<>();
    }

    /*
    @Override
    public int hashCode(){
        return originalName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Rule other = (Rule) obj;
        if (!Objects.equals(this.originalName, other.originalName)) {
            return false;
        }
        return true;
    }
     */
    public Variable getLastBindedVar() {
        return lastBindedVar;
    }

    public void setLastBindedVar(Variable term) {
        lastBindedVar = term;
    }

    /**
     * unifying/(un)binding of this Rule head's variables/terms and consumed
     * list of variables(must be same size) - this is for going from line to
     * line to find unification (while the name of variables might change)
     *
     * @param vars
     */
    public void headUnification(List<Variable> vars) {
        if (vars == null || vars.isEmpty()) {
            return;
        }

        SubKL head = getHead();
        for (int i = 0; i < vars.size(); i++) {
            Variable var = head.getTerm(i);
            Variable boundedVar = vars.get(i);
            if (boundedVar.isBind()) {
                bind(var, boundedVar.getBind());
            } else {
                unbind(var);
            }
        }
    }

    /**
     * unbind all of head's Terms(Terminals)
     */
    public void unbindHead() {
        SubKL head = getHead();
        for (Variable v : head.getTerms()) {
            unbind(v);
        }
    }

    public void bind(Variable var, int c) {
        if (var.isBind()) {  //we are changing the bind
            usedTerms.remove(var.getBind());
            usedTerms.add(c);
        } else {    //we have new binding
            unbound.remove(var);
            usedTerms.add(c);
        }
        var.setBind(c);
    }

    public void unbind(Variable var) {
        usedTerms.remove(var.getBind());
        var.unBind();
        unbound.add(var);
    }

    //public Terminal getNextUnbound() { return unbound.iterator().next(); }
    public abstract Variable getNextUnbound();

    public boolean isBound() {
        return unbound.isEmpty();
    }

    protected abstract SubKL getHead();
}
