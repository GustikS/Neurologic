package discoverer;

import java.util.*;

/**
 * Forward checking in kl-template
 */
public class ForwardChecker {
    private static Example example;
    private static HashMap<Object, Boolean> cache;

    private static final boolean cacheEnabled = Global.cacheEnabled;
    private static final boolean debugEnabled = Global.debugEnabled;
    private static int runs = 0;

    public static void printRuns() {
        System.out.print("(" + runs + ")");
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
                if (cache == null)
                    cache = new HashMap<Object, Boolean>();
                else
                    cache.clear();
            }
        }

        if (debugEnabled)
            System.out.print("[ForwardChecker]\t" + r + "\t-->\t" + r.unbound);

        boolean ret = r instanceof KappaRule ? check((KappaRule) r, null) : check((LambdaRule) r, null);

        if (debugEnabled)
            System.out.print("" + "\t-->\t" + ret + '\n');

        return ret;
    }

    public static boolean check(Object o) {
        if (!cacheEnabled) {
            if (o instanceof SubK)
                return checkCompute((SubK) o);
            else
                return checkCompute((SubL) o);
        }

        Boolean b;
        if (o instanceof SubK) {
            b = cache.get((SubK) o);
            if (b == null) {
                b = checkCompute((SubK) o);
                SubK sk = (SubK) o;
                cache.put(sk.clone(), b);
            }
        } else {
            b = cache.get((SubL) o);
            if (b == null) {
                b = checkCompute((SubL) o);
                SubL sl = (SubL) o;
                cache.put(sl.clone(), b);
            }
        }

        return b;
    }

    private static boolean checkCompute(SubK sk) {
        if (sk.isElement())
            return example.contains(sk);

        Kappa k = sk.getParent();
        return check(k, sk.getTerms());
    }

    private static boolean checkCompute(SubL sl) {
        return check(sl.getParent(), sl.getTerms());
    }

    private static boolean check(Kappa k, List<Terminal> vars) {
        for (KappaRule kr: k.getRules())
            if (check(kr, vars))
                return true;

        return false;
    }

    private static boolean check(Lambda l, List<Terminal> vars) {
        return check(l.getRule(), vars);
    }

    private static boolean check(KappaRule kr, List<Terminal> vars) {
        kr.consumeVars(vars);
        SubL sl = kr.getBody();
        boolean ret = check(sl);
        if (vars != null)
            kr.unconsumeVars();
        return ret;
    }

    private static boolean checkConstrainedToVar(LambdaRule lr, Terminal lastBinded) {
        for (SubK sk: lr.getBody())
            if (sk.contains(lastBinded) && !check(sk))
                return false;

        return true;
    }

    private static boolean checkAll(LambdaRule lr) {
            for (SubK sk: lr.getBody())
                if (!check(sk))
                    return false;

            return true;
    }

    private static boolean check(LambdaRule lr, List<Terminal> vars) {
        lr.consumeVars(vars);
        Terminal lastBindedTerm = lr.getLastBindedVar();

        boolean ret = lastBindedTerm == null ? checkAll(lr) : checkConstrainedToVar(lr, lastBindedTerm);

        if (vars != null)
            lr.unconsumeVars();

        return ret;
    }
}
