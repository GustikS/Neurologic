package discoverer;

import java.util.List;
import java.util.WeakHashMap;
import java.util.HashMap;

/**
 * Maximal substitution solver
 */
public class Solvator {
    private static final boolean forwardCheckEnabled = Global.forwardCheckEnabled;
    private static final boolean cacheEnabled = Global.cacheEnabled;
    private static final boolean debugEnabled = Global.debugEnabled;

    private static Example example;
    private static HashMap<Object, Ball> cache;

    private static void prepareCache() {
        if (cacheEnabled) {
            if (cache == null)
                cache = new HashMap<Object, Ball>();
            else
                cache.clear();
        }
    }

    public static Ball solve(KL kl, Example e) {
        if (debugEnabled) System.out.println("Entering to solve\t" + kl);

        example = e;
        prepareCache();

        Ball b = kl instanceof Kappa ? solve2((Kappa) kl, null) : solve2((Lambda) kl, null);

        ForwardChecker.printRuns();
        //ForwardChecker.clear();

        return b;
    }

    public static Ball solve2(Kappa k, List<Terminal> vars) {
        if (debugEnabled) System.out.println("Solve\t" + k + "\tvariables\t" + vars);

        Ball b = new Ball();
        GroundKappa gk = new GroundKappa(k, vars);

        for (KappaRule r: k.getRules()) {
            Ball tmp = solve(r, vars);
            if (tmp.val == 0)
                continue;

            double w = r.weight;

            GroundLambda t = (GroundLambda) tmp.getLast();
            gk.addDisjunct(t, r);
            tmp.weightItWith(w);
            b.add(tmp);
            b.addActiveRule(r);
        }

        if (b.val != 0) {
            b.val += k.weight;
            b.sigmoid();
        }

        gk.setValue(b.val);
        b.setLast(gk);

        //System.out.println(k + "\t->\t" + b.val);
        return b;
    }

    public static Ball solve2(Lambda l, List<Terminal> vars) {
        if (debugEnabled) System.out.println("Solve\t" + l + "\tvariables\t" + vars);

        Ball b = solve(l.getRule(), vars);
        if (b.val != 0) {
            b.val += l.initialW;
            b.sigmoid();
        }
        GroundLambda gl = (GroundLambda) b.getLast();
        if (gl != null)
            gl.setValue(b.val);

        //System.out.println(l + "\t->\t" + b.val);
        return b;
    }

    public static Ball solve(Rule r, List<Terminal> vars) {
        if (debugEnabled) System.out.println("Solve\t" + r + "\tvariables\t" + vars);

        r.consumeVars(vars);
        r.setLastBindedVar(null);

        return bindAll(r, new Ball());
    }

    public static Ball bindAll(Rule r, Ball best) {
        if (debugEnabled) System.out.println("BindingAll\t" + r);

        if (forwardCheckEnabled && !ForwardChecker.shouldContinue(r, example))
            return new Ball();

        if (r.isBound()) {
            return solveBound(r, best);
        }

        Terminal toBind = r.getNextUnbound();
        for (int i = 0; i < example.getConstCount(); i++) {
            r.bind(toBind, i);
            r.setLastBindedVar(toBind);
            Ball b = bindAll(r, best);
            if (b.val >= best.val)
                best = b;
            r.unbind(toBind);
            if (toBind.isDummy()) {
                //System.out.println("Is dummy, skipping." + toBind);
                break;
            }
        }

        return best;
    }

    public static Ball solveBound(Rule r, Ball best) {
        if (debugEnabled) System.out.println("Dispatching solving bound\t" + r);

        if (r instanceof KappaRule)
            return solveBoundKR((KappaRule) r);
        else
            return solveBoundLR((LambdaRule) r, best);
    }

    public static Ball solveBoundKR(KappaRule kr) {
        if (debugEnabled) System.out.println("Solving bound\t" + kr);

        SubL body = kr.getBody();
        Ball b = cachedSolve(body);

        return b;
    }

    /*
     *private static double upperBound(LambdaRule lr, int index) {
     *    int i = 0;
     *    double est = 0.0;
     *    for (SubK sk: lr.getBody()) {
     *        if (index <= i) {
     *            est += Estimator.estimate(sk.getParent(), example);
     *        }
     *        i++;
     *    }
     *    return est;
     *}
     */

    public static Ball solveBoundLR(LambdaRule lr, Ball best) {
        if (debugEnabled) System.out.println("Solving bound\t" + lr);

        GroundLambda gl = new GroundLambda(lr.getHead().getParent(), lr.getHead().getTerms());

        Ball out = new Ball();
        boolean cancel = false;
        int i = 1;
        for (SubK sk: lr.getBody()) {
            Ball tmp = cachedSolve(sk);
            if (tmp.val == 0.0) {
                cancel = true;
                return new Ball();
            }


            gl.addConjunct((GroundKappa) tmp.getLast());
            out.add(tmp);

            double upperBound = out.val + lr.getBodyLen() - i;
            if (best.val >= upperBound) {
                return new Ball();
            }

            /*
             *if (best.val >= out.val + upperBound(lr, i))
             *    return new Ball();
             */
            i++;
        }

        if (cancel)
            out.val = 0.0;

        out.setLast(gl);

        return out;
    }

    public static Ball solve(SubK sk) {
        if (debugEnabled) System.out.println("Computing\t" + sk);

        Kappa parent = sk.getParent();

        Ball b;
        if (parent.isElement()) {
            double val = example.contains(sk) ? 1.0 : 0.0;
            b = new Ball(val);
            GroundKappa gk = new GroundKappa(sk.getParent(), sk.getTerms());
            gk.setValue(val);
            b.setLast(gk);
        } else {
            b = solve2(parent, sk.getTerms());
        }

        return b;
    }

    public static Ball solve(Object o) {
        if (o instanceof SubK) {
            SubK sk = (SubK) o;
            return solve((SubK) o);
        } else {
            return solve((SubL) o);
        }
    }

/*
 *    public static Ball cachedSolve(Object o) {
 *        if (!cacheEnabled)
 *            return solve(o);
 *
 *        SubOutput so;
 *        if (o instanceof SubK)
 *            so = new SubOutput((SubK) o);
 *        else
 *            so = new SubOutput((SubL) o);
 *
 *        Ball b = cache.get(so);
 *        if (b == null) {
 *            b = solve(o);
 *            cache.put(so, b);
 *        }
 *
 *        return b.clone();
 *    }
 */

    public static Ball cachedSolve(Object o) {
        if (!cacheEnabled)
            return solve(o);

        Ball b;
        if (o instanceof SubK) {
            SubK sk = (SubK) o;
            b = cache.get((SubK) o);
            if (b == null) {
                b = solve(o);
                SubK s = (SubK) o;
                cache.put(s.clone(), b);
            }
        } else {
            b = cache.get((SubL) o);
            if (b == null) {
                b = solve(o);
                SubL s = (SubL) o;
                cache.put(s.clone(), b);
            }
        }

        return b.clone();
    }

    public static Ball solve(SubL sl) {
        if (debugEnabled) System.out.println("Computing\t" + sl);

        Lambda parent = sl.getParent();
        return solve2(parent, sl.getTerms());
    }
}
