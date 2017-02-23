package lrnn.construction.template.rules;

import lrnn.construction.Variable;
import lrnn.construction.template.KL;
import java.io.Serializable;
import java.util.*;

/**
 * partially grounded kappa or lambda, basically the only thing this class
 * provides is the termList
 */
public abstract class SubKL implements Serializable {

    protected List<Variable> termsList;

    
    public abstract KL getParent();

    @Override
    public abstract SubKL clone();
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getParent().getPredicateName());
        if (getTermsList().size() > 0) {
            sb.append("(");
            for (Variable v : getTermsList()) {
                sb.append(v);
                sb.append(",");
            }
            sb.replace(sb.length() - 1, sb.length(), ")");
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int hash = 17;
        hash = 31 * hash + getParent().hashCode();
        for (Variable term : termsList) {
            int bind = term.getBind();
            if (bind != -1) {
                hash = 31 * hash + bind;
            }
        }
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof SubKL)) {
            return false;
        }

        SubKL skl = (SubKL) o;

        if (skl.getParent().hashCode() != this.getParent().hashCode()) {
            return false;
        }

        for (int i = 0; i < getTermsList().size(); i++) {
            int bind1 = this.getTerms().get(i).getBind();
            int bind2 = skl.getTerms().get(i).getBind();
            if (bind1 == -1 && bind2 != -1) {
                return false;
            }
            if (bind1 != -1 && bind2 == -1) {
                return false;
            }
            if (bind1 == -1 && bind2 == -1) {
                continue;
            }
            if (bind1 != bind2) {
                return false;
            }
        }

        return true;
    }

    public Integer getId() {
        return getParent().getId();
    }

    public int[] getBinding() {
        int[] bindings = new int[termsList.size()];
        for (int i = 0; i < bindings.length; i++) {
            bindings[i] = termsList.get(i).getBind();
        }
        return bindings;
    }

    public List<Variable> getTermsList() {
        return termsList;
    }

    public SubKL() {
        termsList = new ArrayList<>();
    }

    public Variable getTerm(int i) {
        return termsList.get(i);
    }

    public List<Variable> getTerms() {
        return termsList;
    }

    public void addVariable(Variable t) {
        termsList.add(t);
    }

    public boolean contains(Variable term) {
        return termsList.contains(term);
    }
    
    public void setTerms(List<Variable> terms) {
        termsList = terms;
    }

}
