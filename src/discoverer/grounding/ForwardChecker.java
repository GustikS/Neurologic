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
    public HashSet<KL> openLiteralSet = new HashSet<>();

    private final boolean cacheEnabled = Global.isCacheEnabled();
    private final boolean debugEnabled = Global.isDebugEnabled();
    public int runs = 0;

    public int exnum = 0;

    public void setupForNewExample(Example e) {
        if (example != e) {
            example = e;

            if (cacheEnabled) {
                clear();
            }
        }
    }

    public void printRuns() {
        Glogger.info(exnum++ + " example -> #forward checker runs:(" + runs + ")");
        runs = 0;
    }

    public void clear() {
        cache.clear();
        openLiteralSet.clear();
    }

    public boolean shouldContinue(Rule r) {
        runs++;

        if (debugEnabled) {
            System.out.print("[ForwardChecker]: rule\t" + r + "\t-->\t usedTerms: " + Grounder.getBindingsNames(example, r.usedTerms) + "\t-->\t unboundVars: " + r.unbound);
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

        if (openLiteralSet.contains(sk.getParent())) {
            /*
            KL[] toArray = openSet.toArray(new KL[openSet.size()]);
            for (int i = 0; i < toArray.length; i++) {
                System.out.println(toArray[0].hashCode() == toArray[i].hashCode() && toArray[0].equals(toArray[i]));
            }
             */
            return true;    //we are in a recursive cycle here! -> return true because we actually do not know (and thus cannot prune)
        }
        openLiteralSet.add(sk.getParent());

        boolean check = check(sk.getParent(), sk.getTerms());

        boolean remove = openLiteralSet.remove(sk.getParent());

        return check;
    }

    private boolean checkCompute(SubL sl) {

        if (sl.isElement()) {
            return example.contains(sl);
        }

        if (openLiteralSet.contains(sl.getParent())) {
            return true;    //we are in a recursive cycle here! -> return true because we actually do not know (and thus cannot prune)
        }
        openLiteralSet.add(sl.getParent());

        boolean check = check(sl.getParent(), sl.getTerms());

        boolean remove = openLiteralSet.remove(sl.getParent());

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
        int[] bindingBefore = kr.getAllVariableBindings();
        boolean unificationSuccess = kr.ruleHeadUnification(vars);
        if (!unificationSuccess) {
            kr.forceRuleUnification(bindingBefore);
            return false;
        }

        SubL sl = kr.getBody();
        boolean ret = check(sl);

        //if (vars != null && !vars.isEmpty()) {
        kr.forceRuleUnification(bindingBefore);     //rebind even if the vars are null because the binding could have come from bellow now!
        //}
        return ret;
    }

    private boolean check(LambdaRule lr, List<Variable> vars) {
        int[] bindingBefore = lr.getAllVariableBindings();
        boolean unificationSuccess = lr.ruleHeadUnification(vars);
        if (!unificationSuccess) {
            lr.forceRuleUnification(bindingBefore);
            return false;
        }

        Variable lastBindedTerm = lr.getLastBindedVar();
        boolean ret = lastBindedTerm == null ? checkAll(lr) : checkConstrainedToVar(lr, lastBindedTerm);

        //if (vars != null && !vars.isEmpty()) {
        lr.forceRuleUnification(bindingBefore);
        //}
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
