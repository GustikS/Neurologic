package discoverer.grounding.evaluation;

import discoverer.grounding.network.GroundKL;
import discoverer.grounding.network.GroundLambda;
import discoverer.construction.network.rules.KappaRule;
import discoverer.learning.backprop.functions.Sigmoid;
import java.util.HashSet;
import java.util.Set;

/* object for representing the result of maximal substitution */
public class Ball {

    public Double val;  //the value is always synchronized with the value of the Object last
    public Double valAvg;
    private GroundKL last;    // GroundLambda or GroundKappa literal(new superClass)
    private Set<GroundLambda> lastAvg;
    private Set<KappaRule> activeRules;

    public Ball() {
        val = 0.0;
        valAvg = 0.0;
        activeRules = new HashSet<KappaRule>();
        lastAvg = new HashSet<>();
    }

    public Ball(double d) {
        this();
        val = d;
        valAvg = d;
    }

    @Override
    public String toString() {
        return "val: " + val.toString() + ", avgVal: " + valAvg.toString();
    }

    public GroundKL getLast() {
        return last;
    }

    public void setLast(GroundKL l) {
        last = l;
    }

    public void weightItWith(double w) {
        val *= w;
    }

    public void weightAvgWith(double w) {
        setValAvg((Double) (getValAvg() * w));
    }

    public void sigmoid() {
        val = Sigmoid.sigmoid(val);
    }

    public void sigmoidAvg() {
        valAvg = Sigmoid.sigmoid(valAvg);
    }

    public void add(Ball b) {
        val += b.val;
        //activeRules.addAll(b.getActiveRules());
    }

    public void addAvg(Ball b) {
        valAvg += b.valAvg;
        //activeRules.addAll(b.getActiveRules());
    }

    @Override
    public Ball clone() {
        Ball clone = new Ball();
        clone.setLast(last);
        clone.val = val;
        clone.activeRules = activeRules;
        //--here
        clone.valAvg = valAvg;

        return clone;
    }

    public void addActiveRule(KappaRule kr) {
        activeRules.add(kr);
    }

    public Set<KappaRule> getActiveRules() {
        return activeRules;
    }

    /**
     * @return the lastAvg
     */
    public Set<GroundLambda> getLastAvg() {
        return lastAvg;
    }

    /**
     * @param lastAvg the lastAvg to set
     */
    public void setLastAvg(Set<GroundLambda> lastAvg) {
        this.lastAvg = lastAvg;
    }

    public void addGroundRule(GroundLambda r) {
        /*if (lastAvg == null) {
         lastAvg = new HashSet<>();
         }*/
        //if (r != null) {
        lastAvg.add(r);
        //}
    }

    /**
     * @return the valAvg
     */
    public Double getValAvg() {
        return valAvg;
    }

    public void setValAvg(Double val) {
        valAvg = val;    //omg
    }

    public void addLastAvg(Set<GroundLambda> gls) {
        /*if (lastAvg == null){
         lastAvg = new HashSet<>();
         }*/
        lastAvg.addAll(gls);
    }
}
