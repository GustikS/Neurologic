package extras;

import discoverer.grounding.evaluation.Ball;
import discoverer.construction.example.Example;
import discoverer.global.Global;
import discoverer.grounding.network.GroundKL;
import discoverer.grounding.network.GroundKappa;
import discoverer.grounding.network.GroundLambda;
import discoverer.construction.network.KL;
import discoverer.construction.network.Kappa;
import discoverer.construction.network.rules.KappaRule;
import discoverer.construction.network.Lambda;
import discoverer.construction.network.rules.LambdaRule;
import discoverer.construction.network.rules.Rule;
import discoverer.construction.network.rules.SubK;
import discoverer.construction.network.rules.SubL;
import discoverer.construction.Terminal;
import discoverer.grounding.ForwardChecker;
import discoverer.learning.backprop.functions.Activations;
import java.util.List;
import java.util.WeakHashMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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
            if (cache == null) {
                cache = new HashMap<Object, Ball>();
            } else {
                cache.clear();
            }
        }
    }

    /**
     * solves maximal substitution of network induced by example e w.r.t. K/L
     * node kl
     *
     * @param kl
     * @param e
     * @return
     */
    public static Ball solve(KL kl, Example e) {
        if (debugEnabled) {
            System.out.println("Entering to solve\t" + kl);
        }

        example = e;
        prepareCache();

        Ball b = kl instanceof Kappa ? solve2((Kappa) kl, null) : solve2((Lambda) kl, null);    //always Kappa only...first literal is without variables(ignoring them)

        ForwardChecker.printRuns();
        //ForwardChecker.clear();

        return b;
    }

    /**
     * recursive substitution solver for Kappa node, given some bounded
     * variables
     * <p>
     * computes the result of disjunctive rule -> ball
     *
     * @param k
     * @param vars
     * @return
     */
    public static Ball solve2(Kappa k, List<Terminal> vars) {
        if (debugEnabled) {
            System.out.println("Solve\t" + k + "\tvariables\t" + vars);
        }

        Ball b = new Ball();
        GroundKappa gk = new GroundKappa(k, vars);

        for (KappaRule r : k.getRules()) {
            Ball tmp = solve(r, vars);
            if (tmp.valMax == 0) { // 0= non-entailed or entailed with 0 weight?
                continue;
            }

            double w = r.weight;

            // HERE I need to get back a list of GroundLambda
            //- calculate the average, weight it and add
            Set<GroundLambda> lastAvg = tmp.getLastAvg();   //this set of GroundLambda must have already been evaluated(the have value set) through solve2(Lambda..)
            gk.addDisjunctAvg(lastAvg, r);
            tmp.setValAvg(GroundKL.getAvgValFrom(lastAvg));
            tmp.weightAvgWith(w);
            b.addAvg(tmp);
            //b.addActiveRule(r);
            //---
            //---previous max.subst. part of code
            GroundLambda t = (GroundLambda) tmp.getLast();
            gk.addDisjunct(t, r);
            tmp.weightItWith(w);    //weight the disjunct
            b.add(tmp);         //summing disjuncts' contributions
            //---
            b.addActiveRule(r);
        }

        if (b.valMax != 0) {   //what if the rules sum up to 0?

            b.valMax += k.offset;  //node offset
            b.valMax = Activations.kappaActivation(b.valMax);    // + sigmoid
            //---
            b.valAvg += k.offset;
            b.valAvg = Activations.kappaActivation(b.valAvg);
        }

        gk.setValue(b.valMax);
        //--
        gk.setValueAvg(b.valAvg);

        b.setLast(gk);
        //System.out.println(k + "\t->\t" + b.val);
        return b;
    }

    /**
     * recursive substitution solver for Lambda node
     * <p>
     * computes the result of the respective conjunctive rule -> ball
     *
     * @param l
     * @param vars
     * @return
     */
    public static Ball solve2(Lambda l, List<Terminal> vars) {
        if (debugEnabled) {
            System.out.println("Solve\t" + l + "\tvariables\t" + vars);
        }

        Ball b = solve(l.getRule(), vars);      //there is only one rule for lambda node

        // HERE I need to get back a list of GroundKappa groundings(weighted), but
        // but the results is a Set of the same groundings of this Lambda(same rule's head with different bodies) with sumed up conjuncts values
        //- calculate the average
        Set<GroundLambda> lastAvg = b.getLastAvg();
        GroundLambda gl = (GroundLambda) b.getLast();

        if (b.valMax != 0) {   //this should always be true for entailed body of kappas
            b.valMax += l.getOffset();    //add offset = -1*number_of_conjuncts
            b.valMax = Activations.lambdaActivation(b.valMax);
            //---calculate the average value
            b.setValAvg(GroundKL.getAvgValFrom(lastAvg));
            b.valAvg += l.getOffset();
            b.valAvg = Activations.lambdaActivation(b.valAvg);
        }

        if (gl != null) {
            gl.setValue(b.valMax);
            //---
            gl.setValueAvg(b.valAvg);
            gl.addConjuctsAvgFrom(lastAvg); //extracting all the groundings of conjuncts into a hashmap (weighted occurrence)
        }

        //System.out.println(l + "\t->\t" + b.val);
        return b;
    }

    /**
     * solving ONE (lambda/kappa) rule - consuming variables and binding rule r
     *
     * @param r
     * @param vars
     * @return
     */
    public static Ball solve(Rule r, List<Terminal> vars) {
        if (debugEnabled) {
            System.out.println("Solve\t" + r + "\tvariables\t" + vars);
        }

        r.consumeVars(vars);    //head -> body variable binding/unification
        r.setLastBindedVar(null);

        //-------------------
        Ball b = new Ball();
        b.setLastAvg(new HashSet());    //we will assemble the average on the level of rules(bodies)
        return bindAll(r, b);  //extensive combination binding of unbound variables
    }

    /**
     * At first - iteratively binding variables in rule r to all possible
     * constants from actual example e
     * <p>
     * Then - running solveBound on a particular combination of
     * constant->variables binding
     * <p>
     * returning the best found ball
     *
     * <p>
     * NOTE - when calling this(except the finalKappa) the rule's head is always
     * fully bound
     *
     * @param r
     * @param best
     * @return
     */
    public static Ball bindAll(Rule r, Ball best) {
        if (debugEnabled) {
            System.out.println("BindingAll\t" + r);
        }

        if (forwardCheckEnabled && !ForwardChecker.shouldContinue(r, example)) {
            return new Ball();
        }

        if (r.isBound()) {  //no more unbound variables
            Ball solvedBound = solveBound(r, best); // all variables are bound -> dispatch
            //HERE
            best.addGroundRule((GroundLambda) solvedBound.getLast());
            //----here
            return solvedBound;
        }

        Terminal toBind = r.getNextUnbound();   //get next unbound variable
        for (int i = 0; i < example.getConstCount(); i++) { // all possible bindings of variables in rule r
            if (debugEnabled) {
                System.out.println("\t toBind=" + toBind + " -> " + (i + 1) + " of " + example.getConstCount());
            }
            r.bind(toBind, i);
            r.setLastBindedVar(toBind);
            Ball b = bindAll(r, best);  //and bind the rest recursively

            //------------
            if (b.valMax >= best.valMax) {    //if this binding of current toBind variable to i-th constant is best so far
                b.addLastAvg(best.getLastAvg()); //we need to keep all the so-far found solutions for averaging
                best = b;   //replace it
            }
            //----------
            r.unbind(toBind);
            if (toBind.isDummy()) {     //dummy = no bindings in body = can be arbitrary
                if (debugEnabled) {
                    System.out.println("Is dummy, skipping." + toBind);
                }
                break;
            }
        }

        return best;
    }

//------------------------------------------------------ bounded - ground nodes solving from here
    /**
     * solving a rule with all variables in it bounded
     *
     * @param r
     * @param best
     * @return
     */
    public static Ball solveBound(Rule r, Ball best) {
        if (debugEnabled) {
            System.out.println("Dispatching solving bound\t" + r);
        }

        if (r instanceof KappaRule) {
            return solveBoundKR((KappaRule) r);
        } else {
            return solveBoundLR((LambdaRule) r, best);
        }
    }

    /**
     * bound kappa rule - not really a rule (only one literal in body) = solve
     * the body
     *
     * @param kr
     * @return
     */
    public static Ball solveBoundKR(KappaRule kr) {
        if (debugEnabled) {
            System.out.println("Solving bound\t" + kr);
        }

        SubL body = kr.getBody();
        Ball b = cachedSolve(body);

        return b;
    }

    /**
     * solve bounded Lambda rule - solve all conjuncts in the body and sum them
     * - no other computation<p>
     * (subtracting the length is by setting initial weight in Lambda.setRule())
     *
     * @param lr
     * @param best
     * @return
     */
    public static Ball solveBoundLR(LambdaRule lr, Ball best) {
        if (debugEnabled) {
            System.out.println("Solving bound\t" + lr);
        }

        GroundLambda gl = new GroundLambda(lr.getHead().getParent(), lr.getHead().getTerms());

        Ball out = new Ball();
        boolean cancel = false;
        int i = 1;
        for (SubK sk : lr.getBody()) {
            Ball tmp = cachedSolve(sk);
            if (tmp.valMax == 0.0) {
                cancel = true;      //if one fails - the whole conjunction has no solution -> cancel
                return new Ball();  //skip the rest of conjuncts and return empty result
            }

            gl.addConjunct((GroundKappa) tmp.getLast());
            out.add(tmp);   //sum the conjuncts in the Ball out
            out.addAvg(tmp);

            //-------------------------
            double upperBound = out.valMax + lr.getBodyLen() - i;
            if (best.valMax >= upperBound) {
                //HERE - no pruning
                //    return new Ball();          //pruning if this solution is necesarily worse!!
            }
            //-------------------------

            /*
             *if (best.val >= out.val + upperBound(lr, i))
             *    return new Ball();
             */
            i++;
        }

        if (cancel) {
            out.valMax = 0.0;  //delete the result if some middle conjunct has failed
        }
        //HERE set intermediate value to this GroundLambda(only sumation, no offset and sigmoid)
        //it will be later averaged and evaluated in solve2(Lambda...)
        //gl.setValue(out.val); //should not be necessary
        gl.setValueAvg(out.valAvg);

        out.setLast(gl);

        return out;
    }

    /**
     * compute grounded lambda or kappa node
     *
     * @param o
     * @return
     */
    public static Ball solve(Object o) {
        if (o instanceof SubK) {
            SubK sk = (SubK) o;
            return solve((SubK) o);
        } else {
            return solve((SubL) o);
        }
    }

    /**
     * computing grounded kappa node - binding to example literals
     * <p>
     * or dispatching to kappa-node solver solve2 if not element(=no rules)
     *
     * @param sk
     * @return
     */
    public static Ball solve(SubK sk) {
        if (debugEnabled) {
            System.out.println("Computing\t" + sk);
        }

        Kappa parent = sk.getParent();

        Ball b;
        if (parent.isElement()) {   //= literal with no rules
            double val = example.contains(sk) ? 1.0 : 0.0;          //assigning values from example - ignoring example value?
            if (debugEnabled) {
                System.out.println(sk + " is found: " + val);
            }
            b = new Ball(val);
            GroundKappa gk = new GroundKappa(sk.getParent(), sk.getTerms());        //this GroundKappa is with no Sigmoid
            gk.setValue(val);
            b.setLast(gk);
            //HERE - (end of recursion here(binding to an example)) starting the recursion tree with both val and valAvg set up
            b.valAvg = val;
            gk.setValueAvg(val);
        } else {
            b = solve2(parent, sk.getTerms());
        }

        return b;
    }

    /**
     * computing grounded lambda - just dispatching bounded variables to lambda
     * node solver solve2
     *
     * @param sl
     * @return
     */
    public static Ball solve(SubL sl) {
        if (debugEnabled) {
            System.out.println("Computing\t" + sl);
        }

        Lambda parent = sl.getParent();
        return solve2(parent, sl.getTerms());
    }

    /**
     * check cache for grounded literals or go to solve -> creates acyclic
     * structure instead of a tree!!
     * <p>
     * is the same grounding o the literal has been solved before return the
     * corresponding Ball(with previously created GroundKapp/Lambda as the last
     * Object)
     *
     * @param o
     * @return
     */
    public static Ball cachedSolve(Object o) {
        if (!cacheEnabled) {
            return solve(o);
        }

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
}
