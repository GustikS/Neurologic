package discoverer.construction.template.rules;

import discoverer.construction.Variable;
import discoverer.construction.template.rules.SubKL;
import discoverer.global.Global;
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

    public Set<Variable> allVars;

    public Rule() {
        unbound = new HashSet<>();
        usedTerms = new HashSet<>();
        allVars = new HashSet<>();
    }
    
    public abstract String toFullString();

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
     * @return returns the OLD bindings! - or null if the unification fails!
     */
    public boolean ruleHeadUnification(List<Variable> vars) {
        if (vars == null || vars.isEmpty()) {
            return true;  //there is nothing to unify
        }

        SubKL head = getHead();
        if (Global.debugEnabled && head.termsList.size() != vars.size()) {
            return false; //TODO, this shouldn't happen
        }

        //and start new binding
        for (int i = 0; i < vars.size(); i++) {
            Variable headVar = head.getTerm(i);
            Variable newVar = vars.get(i);
            if (newVar.isBind() && !headVar.isBind()) { //regular binding
                bind(headVar, newVar.getBind());
            } else if (!newVar.isBind() && headVar.isBind()) {  //reverse binding! - this can only happend in forwardchecker! (unbound queries)
                bind(newVar, headVar.getBind());
            } else if (newVar.isBind() && headVar.isBind() && newVar.getBind() != headVar.getBind()) {   //different binds - not unificable!
                return false;
            } //else do nothing - both binds are same (either bound or unbound)
        }
        return true;   //return the old bindings!
    }
    
    /**
     * we do not ask what was the previous binding and force the new one
     * @param vars 
     */
    public void forceRuleHeadUnification(List<Variable> vars) {
        if (vars == null || vars.isEmpty()) {
            return;
        }

        SubKL head = getHead();
        if (head.termsList.isEmpty()) {
            return; //TODO, this shouldn't happen
        }

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
     * like a rule head unification, but this always succeeds as it enforces the
     * input binding onto the head literal (e.g. for restoration purpose)
     *
     * @param binding
     */
    public void forceRuleUnification(int[] binding) {
        int i = 0;
        for (Variable var : allVars) {
            if (binding[i] == -1) {
                unbind(var);
            } else {
                bind(var, binding[i]);
            }
            i++;
        }
    }

    public int[] getAllVariableBindings() {
        int[] bindings = new int[allVars.size()];
        int i = 0;
        for (Variable var : allVars) {
            bindings[i++] = var.getBind();
        }
        return bindings;
    }

    /**
     * unbind all of head's Terms(Terminals) TODO - dangerous, do not do...use
     * setHeadsBinding instead!
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

    public abstract SubKL getHead();

    public abstract Rule getUnbindClone();

}
