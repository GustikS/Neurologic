package discoverer.grounding;

import discoverer.construction.network.Kappa;
import discoverer.construction.network.rules.KappaRule;
import discoverer.construction.network.Lambda;
import discoverer.construction.network.rules.LambdaRule;
import discoverer.construction.network.rules.Rule;
import discoverer.construction.network.rules.SubK;
import discoverer.construction.network.rules.SubL;
import discoverer.construction.Terminal;
import discoverer.construction.example.Example;
import discoverer.construction.network.rules.SubKL;
import discoverer.global.Global;
import discoverer.global.Glogger;
import java.util.*;

/**
 * Forward checking in kl-network
 */
public class ForwardChecker {

    private static Example example;
    private static HashMap<SubKL, Boolean> cache;

    private static final boolean cacheEnabled = Global.isCacheEnabled();
    private static final boolean debugEnabled = Global.isDebugEnabled();
    private static int runs = 0;

    public static int exnum = 0;

    public static void printRuns() {
        Glogger.info(exnum++ + " example -> #forward checker runs:(" + runs + ")");
        runs = 0;
    }

    public static void clear() {
        cache.clear();
    }

    public static boolean shouldContinue(Rule r, Example e) {
        runs++;
        if (example != e) {
            example = e;

            if (cacheEnabled) {
                if (cache == null) {
                    cache = new HashMap<>();
                } else {
                    cache.clear();
                }
            }
        }

        if (debugEnabled) {
            System.out.print("[ForwardChecker]\t" + r + "\t-->\t" + r.unbound);
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
    public static boolean check(SubK o) {
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

    public static boolean check(SubL o) {
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

    private static boolean checkCompute(SubK sk) {
        if (sk.isElement()) {
            return example.contains(sk);
        }

        Kappa k = sk.getParent();
        return check(k, sk.getTerms());
    }

    private static boolean checkCompute(SubL sl) {
        if (sl.isElement()) {
            return example.contains(sl);
        }

        return check(sl.getParent(), sl.getTerms());
    }

    private static boolean check(Kappa k, List<Terminal> vars) {
        for (KappaRule kr : k.getRules()) {
            if (check(kr, vars)) {
                return true;
            }
        }

        return false;
    }

    private static boolean check(Lambda l, List<Terminal> vars) {
        return check(l.getRule(), vars);
    }

    private static boolean check(KappaRule kr, List<Terminal> vars) {
        kr.consumeVars(vars);
        SubL sl = kr.getBody();
        boolean ret = check(sl);
        if (vars != null) {
            kr.unconsumeVars();
        }
        return ret;
    }

    private static boolean checkConstrainedToVar(LambdaRule lr, Terminal lastBinded) {
        for (SubK sk : lr.getBody()) {
            if (sk.contains(lastBinded) && !check(sk)) {
                return false;
            }
        }

        return true;
    }

    private static boolean checkAll(LambdaRule lr) {
        for (SubK sk : lr.getBody()) {
            if (!check(sk)) {
                return false;
            }
        }

        return true;
    }

    private static boolean check(LambdaRule lr, List<Terminal> vars) {

        lr.consumeVars(vars);
        Terminal lastBindedTerm = lr.getLastBindedVar();

        boolean ret = lastBindedTerm == null ? checkAll(lr) : checkConstrainedToVar(lr, lastBindedTerm);

        if (vars != null) {
            lr.unconsumeVars();
        }

        return ret;
    }
}
