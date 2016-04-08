package discoverer.construction.network.rules;

import discoverer.construction.Terminal;
import discoverer.construction.template.KL;
import java.io.Serializable;
import java.util.*;

/**
 * partially grounded kappa or lambda
 */
public abstract class SubKL implements Serializable {

    protected List<Terminal> termsList;

    public abstract Integer getId();

    public List<Terminal> getTermsList() {
        return termsList;
    }

    public SubKL() {
        termsList = new ArrayList<Terminal>();
    }

    public Terminal getTerm(int i) {
        return termsList.get(i);
    }

    public List<Terminal> getTerms() {
        return termsList;
    }

    public void addVariable(Terminal t) {
        termsList.add(t);
    }

    public KL getParent() {
        return null;
    }

    public boolean contains(Terminal term) {
        return termsList.contains(term);
    }

}
