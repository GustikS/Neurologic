package discoverer;

import java.util.HashSet;
import java.util.Set;

/* object for representing the result of maximal substitution */
public class Ball {
    public Double val;
    private Object last;
    private Set<KappaRule> activeRules;

    public Ball() {
        val = 0.0;
        activeRules = new HashSet<KappaRule>();
    }

    public Ball(double d) {
        this();
        val = d;
    }

    @Override
    public String toString() {
        return val.toString();
    }

    public Object getLast() {
        return last;
    }

    public void setLast(Object l) {
        last = l;
    }

    public void weightItWith(double w) {
        val *= w;
    }

    public void sigmoid() {
        val = Sigmoid.sigmoid(val);
    }

    public void add(Ball b) {
        val += b.val;
        activeRules.addAll(b.getActiveRules());
    }

    public Ball clone() {
        Ball clone = new Ball();
        clone.setLast(last);
        clone.val = val;
        clone.activeRules = activeRules;

        return clone;
    }

    public void addActiveRule(KappaRule kr) {
        activeRules.add(kr);
    }

    public Set<KappaRule> getActiveRules() {
        return activeRules;
    }
}
