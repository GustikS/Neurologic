package discoverer.grounding.network;

import discoverer.construction.network.Kappa;
import discoverer.construction.network.rules.KappaRule;
import discoverer.construction.Terminal;
import discoverer.global.Tuple;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Grounded kappa node = atom neuron
 */
public class GroundKappa extends GroundKL {

    private Kappa general;

    //change this into static arrays for performance!!
    
    private List<Tuple<GroundLambda, KappaRule>> disjuncts;
    private List<Tuple<HashSet<GroundLambda>, KappaRule>> disjunctsAvg;
    //the hashset average value could be pre-calculated for speedup
    
    //public GroundLambda[] atoms;
    //public KappaRule[] rules;

    @Override
    public GroundKappa cloneMe() {
        GroundKappa gk = new GroundKappa(general);
        gk.disjuncts.addAll(disjuncts);
        gk.disjunctsAvg.addAll(disjunctsAvg);
        gk.setTermList(getTermList());
        return gk;
    }

    public GroundKappa(Kappa k) {
        super();
        general = k;
        disjuncts = new ArrayList<>();
        disjunctsAvg = new ArrayList<>();
    }

    /**
     * term list is full and final = this Kappa is really fully grounded at the
     * time of creation
     *
     * @param k
     * @param terms
     */
    public GroundKappa(Kappa k, List<Terminal> terms) {
        super(terms);
        general = k;
        disjuncts = new ArrayList<Tuple<GroundLambda, KappaRule>>();
        disjunctsAvg = new ArrayList<>();
    }

    public void addDisjunct(GroundLambda gl, KappaRule kr) {
        Tuple<GroundLambda, KappaRule> t;
        t = new Tuple<GroundLambda, KappaRule>(gl, kr);
        disjuncts.add(t);
    }

    public void addDisjunctAvg(Set<GroundLambda> gl, KappaRule kr) {
        Tuple<HashSet<GroundLambda>, KappaRule> t;
        t = new Tuple<>((HashSet<GroundLambda>) gl, kr);
        getDisjunctsAvg().add(t);
    }

    public boolean isElement() {
        return disjuncts.isEmpty() && disjunctsAvg.isEmpty();
    }

    /**
     * return all disjuncts(rule right sides) for this Kappa
     *
     * @return
     */
    public List<Tuple<GroundLambda, KappaRule>> getDisjuncts() {
        return disjuncts;
    }

    public Kappa getGeneral() {
        return general;
    }

    @Override
    public String toString() {
        String s = general.getName() + "(";
        for (Integer i : getTermList()) {
            s += i + ",";
        }
        s = s.substring(0, s.length() - 1);
        s += ")#" + getId();
        return s;
    }
    public String toString(HashMap<Integer,String> constNames) {
        String s = general.getName() + "(";
        for (Integer i : getTermList()) {
            s += constNames.get(i) + ",";
        }
        s = s.substring(0, s.length() - 1);
        s += ")#" + getId();
        return s;
    }

    /**
     * @return the disjunctsAvg
     */
    public List<Tuple<HashSet<GroundLambda>, KappaRule>> getDisjunctsAvg() {
        return disjunctsAvg;
    }

    /**
     * @param disjunctsAvg the disjunctsAvg to set
     */
    public void setDisjunctsAvg(List<Tuple<HashSet<GroundLambda>, KappaRule>> disjunctsAvg) {
        this.disjunctsAvg = disjunctsAvg;
    }

    /*
    @Override
    public void transform2Arrays() {
        atoms = new GroundLambda[disjunctsAvg.size()];
        rules = new KappaRule[disjunctsAvg.size()];
        int i =0;
        for (Tuple<HashSet<GroundLambda>, KappaRule> disjunct : disjunctsAvg) {
            atoms[i] = disjunct.x.iterator().next();
            rules[i++] = disjunct.y;
        }
    }
    */
}
