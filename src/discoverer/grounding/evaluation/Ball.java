package discoverer.grounding.evaluation;

import discoverer.grounding.evaluation.struct.GroundNetworkParser;
import discoverer.grounding.network.GroundKL;
import discoverer.grounding.network.GroundLambda;
import discoverer.construction.network.rules.KappaRule;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* object for representing the result of maximal substitution */
public class Ball implements Serializable{

    public Double valMax;  //the value is always synchronized with the value of the Object last
    public Double valAvg;
    private GroundKL last;    // GroundLambda or GroundKappa literal(new superClass)
    private Set<GroundLambda> lastAvg; //set of different body-substitutions
    //
    private Set<KappaRule> activeRules;
    //public Set<Double> inputsMax = new HashSet<>();
    //public Set<Double> inputsAvg = new HashSet<>();
    public List<GroundKL> groundNeurons;

    public Ball() {
        valMax = null;
        valAvg = null;
        activeRules = new HashSet<KappaRule>();
        lastAvg = new HashSet<>();
        groundNeurons = new ArrayList<>();
    }

    public Ball(double d) {
        this();
        valMax = d;
        valAvg = d;
    }

    @Override
    public String toString() {
        return "val: " + valMax.toString() + ", avgVal: " + valAvg.toString();
    }

    public GroundKL getLast() {
        return last;
    }

    public void setLast(GroundKL l) {
        last = l;
    }

    public void weightItWith(double w) {
        valMax *= w;
    }

    public void weightAvgWith(double w) {
        setValAvg((Double) (getValAvg() * w));
    }

    public void addMax(Ball b) {
        if (valMax == null) {
            valMax = b.valMax;
        } else {
            valMax += b.valMax;
        }
        /*if (inputsMax == null) {
         inputsMax = new HashSet<>();
         }
         inputsMax.add(b.valMax);*/
        //activeRules.addAll(b.getActiveRules());
    }

    public void addAvg(Ball b) {
        if (valAvg == null) {
            valAvg = b.valAvg;
        } else {
            valAvg += b.valAvg;
        }
        /*if (inputsAvg == null) {
         inputsAvg = new HashSet<>();
         }
         inputsAvg.add(b.valAvg);*/
        //activeRules.addAll(b.getActiveRules());
    }

    @Override
    public Ball clone() {
        Ball clone = new Ball();
        clone.setLast(last);
        clone.valMax = valMax;
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
        lastAvg.addAll(gls);    //too time-consuming!!
    }
    
    public void loadGroundNeurons(Set<GroundKL> neurons){
        for (GroundKL neuron : neurons) {
            
        }
        //GroundNetworkParser.parseMAX(this);
        groundNeurons.clear();
        groundNeurons.addAll(neurons);
    }
    
    public void invalidateNeurons(){
        for (GroundKL gkl : groundNeurons) {
            gkl.invalidate();
        }
    }
}
