package discoverer.grounding;

import discoverer.construction.TemplateFactory;
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

    private static final boolean cacheEnabled = Global.isCacheEnabled();
    private static final boolean debugEnabled = Global.isDebugEnabled();
    public int runs = 0;

    public int exnum = 0;
    private static final boolean templateConstansts = Global.templateConstants;
    private static final boolean recursion = Global.recursion;

    public void setupForNewExample(Example e) {
        if (example != e) {
            example = e;

            if (cacheEnabled) {
                clear();
            }
        }
        runs = 0;
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

            if (sk.getParent().special) {
                return TemplateFactory.specialPredicatesMap.get(sk.getParent()).isTrue(Grounder.getBindingsNames(example, sk.getTerms()));
            }

            return example.contains(sk);
        }

        if (recursion) {
            if (openLiteralSet.contains(sk.getParent())) {
                return true;    //we are in a recursive cycle here! -> return true because we actually do not know (and thus cannot prune)
            }
            openLiteralSet.add(sk.getParent());
        }

        boolean check = check(sk.getParent(), sk.getTerms());

        if (recursion) {
            openLiteralSet.remove(sk.getParent());
        }

        return check;
    }

    private boolean checkCompute(SubL sl) {

        if (sl.isElement()) {

            if (sl.getParent().special) {
                return TemplateFactory.specialPredicatesMap.get(sl.getParent()).evaluate(Grounder.getBindingsNames(example, sl.getTerms())) != 0;
            }

            return example.contains(sl);
        }

        if (recursion) {
            if (openLiteralSet.contains(sl.getParent())) {
                return true;    //we are in a recursive cycle here! -> return true because we actually do not know (and thus cannot prune)
            }
            openLiteralSet.add(sl.getParent());
        }

        boolean check = check(sl.getParent(), sl.getTerms());

        if (recursion) {
            openLiteralSet.remove(sl.getParent());
        }

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
        int[] bindingBefore = null;
        boolean unificationSuccess = false;
        if (templateConstansts) {
            bindingBefore = kr.getAllVariableBindings();
            unificationSuccess = kr.ruleHeadUnification(vars);
        } else {
            kr.forceRuleHeadUnification(vars);
        }
        if (templateConstansts && !unificationSuccess) {
            kr.forceRuleUnification(bindingBefore);
            return false;
        }

        SubL sl = kr.getBody();
        boolean ret = check(sl);

        if (templateConstansts) {
            kr.forceRuleUnification(bindingBefore);
        } else if (vars != null && !vars.isEmpty()) {
            kr.unbindHead();     //rebind even if the vars are null because the binding could have come from bellow now!
        }

        return ret;
    }

    private boolean check(LambdaRule lr, List<Variable> vars) {
        int[] bindingBefore = null;
        boolean unificationSuccess = false;
        if (templateConstansts) {
            bindingBefore = lr.getAllVariableBindings();
            unificationSuccess = lr.ruleHeadUnification(vars);
        } else {
            lr.forceRuleHeadUnification(vars);
        }
        if (templateConstansts && !unificationSuccess) {
            lr.forceRuleUnification(bindingBefore);
            return false;
        }

        Variable lastBindedTerm = lr.getLastBindedVar();
        boolean ret = lastBindedTerm == null ? checkAll(lr) : checkConstrainedToVar(lr, lastBindedTerm);

        if (templateConstansts) {
            lr.forceRuleUnification(bindingBefore);
        } else if (vars != null && !vars.isEmpty()) {
            lr.unbindHead();     //rebind even if the vars are null because the binding could have come from bellow now!
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
