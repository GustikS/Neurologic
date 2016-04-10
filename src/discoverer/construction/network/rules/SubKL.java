package discoverer.construction.network.rules;

import discoverer.construction.Variable;
import discoverer.construction.template.KL;
import java.io.Serializable;
import java.util.*;

/**
 * partially grounded kappa or lambda, basically the only thing this class
 * provides is the termList
 */
public abstract class SubKL implements Serializable {

    protected List<Variable> termsList;

    public abstract Integer getId();

    public List<Variable> getTermsList() {
        return termsList;
    }

    public SubKL() {
        termsList = new ArrayList<Variable>();
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

    public KL getParent() {
        return null;
    }

    public boolean contains(Variable term) {
        return termsList.contains(term);
    }

}
