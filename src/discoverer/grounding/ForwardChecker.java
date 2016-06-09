package discoverer.grounding;

import discoverer.construction.template.Kappa;
import discoverer.construction.template.rules.KappaRule;
import discoverer.construction.template.Lambda;
import discoverer.construction.template.rules.LambdaRule;
import discoverer.construction.template.rules.Rule;
import discoverer.construction.template.rules.SubK;
import discoverer.construction.template.rules.SubL;
import discoverer.construction.Variable;
import discoverer.construction.example.Example;
import discoverer.construction.template.KL;
import discoverer.construction.template.rules.SubKL;
import discoverer.global.Global;
import discoverer.global.Glogger;
import java.util.*;

/**
 * Forward checking in kl-network
 */
public class ForwardChecker {

    private Example example;
    private HashMap<SubKL, Boolean> cache = new HashMap<>();
    public HashSet<KL> openSet = new HashSet<>();

    private final boolean cacheEnabled = Global.isCacheEnabled();
    private final boolean debugEnabled = Global.isDebugEnabled();
    public int runs = 0;

    public int exnum = 0;

    public void printRuns() {
        Glogger.info(exnum++ + " example -> #forward checker runs:(" + runs + ")");
        runs = 0;
    }

    public void clear() {
        cache.clear();
        openSet.clear();
    }

    public boolean shouldContinue(Rule r, Example e) {
        runs++;
        if (example != e) {
            example = e;

            if (cacheEnabled) {
                clear();
            }
        }

        if (debugEnabled) {
            System.out.print("[ForwardChecker]\t" + r + "\t-->\t usedTerms: " + r.usedTerms + "\t-->\t solveVars: " + r.unbound);
        }

        boolean ret = r instanceof KappaRule ? check((KappaRule) r, null) : check((LambdaRule) r, null);

        if (debugEnabled) {
            System.out.print("" + "\t-->\t" + ret + '\n');
        }

        return ret;
    }

    /**
     * this is too time-consuming, probably the type-checking!
     *
     * @param o
     * @return
     */
    public boolean check(SubK o) {
        if (!cacheEnabled) {
            return checkCompute(o);
        }

        Boolean b;
        b = cache.get(o);
        if (b == null) {
            b = checkCompute(o);
            SubK sk = o;
            cache.put(sk.clone(), b);
        }

        return b;
    }

    public boolean check(SubL o) {
        if (!cacheEnabled) {
            return checkCompute(o);
        }

        Boolean b;
        b = cache.get(o);
        if (b == null) {
            b = checkCompute(o);
            SubL sl = o;
            cache.put(sl.clone(), b);
        }
        return b;
    }

    private boolean checkCompute(SubK sk) {

        if (sk.isElement()) {
            return example.contains(sk);
        }

        if (openSet.contains(sk.getParent())) {
            /*
            KL[] toArray = openSet.toArray(new KL[openSet.size()]);
            for (int i = 0; i < toArray.length; i++) {
                System.out.println(toArray[0].hashCode() == toArray[i].hashCode() && toArray[0].equals(toArray[i]));
            }
            */
            return true;    //we are in a recursive cycle here! -> return true because we actually do not know (and thus cannot prune)
        }
        openSet.add(sk.getParent());

        boolean check = check(sk.getParent(), sk.getTerms());
        
        openSet.remove(sk.getParent());
        
        return check;
    }

    private boolean checkCompute(SubL sl) { 

        if (sl.isElement()) {
            return example.contains(sl);
        }

        if (openSet.contains(sl.getParent())) {
            return true;    //we are in a recursive cycle here! -> return true because we actually do not know (and thus cannot prune)
        }
        openSet.add(sl.getParent());

        boolean check = check(sl.getParent(), sl.getTerms());
        
        openSet.remove(sl.getParent());
        
        return check;
    }

    private boolean check(Kappa k, List<Variable> vars) {
        for (KappaRule kr : k.getRules()) {
            if (check(kr, vars)) {
                return true;
            }
        }

        return false;
    }

    private boolean check(Lambda l, List<Variable> vars) {
        return check(l.getRule(), vars);
    }

    private boolean check(KappaRule kr, List<Variable> vars) {
        kr.ruleHeadUnification(vars);
        SubL sl = kr.getBody();
        boolean ret = check(sl);
        if (vars != null) {
            kr.unbindHead();
        }
        return ret;
    }

    private boolean check(LambdaRule lr, List<Variable> vars) {

        lr.ruleHeadUnification(vars);
        Variable lastBindedTerm = lr.getLastBindedVar();

        boolean ret = lastBindedTerm == null ? checkAll(lr) : checkConstrainedToVar(lr, lastBindedTerm);

        if (vars != null) {
            lr.unbindHead();
        }

        return ret;
    }

    private boolean checkConstrainedToVar(LambdaRule lr, Variable lastBinded) {
        for (SubK sk : lr.getBody()) {
            if (sk.contains(lastBinded) && !check(sk)) {
                return false;
            }
        }

        return true;
    }

    private boolean checkAll(LambdaRule lr) {
        for (SubK sk : lr.getBody()) {
            if (!check(sk)) {
                return false;
            }
        }
        return true;
    }
}