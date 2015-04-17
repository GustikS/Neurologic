package discoverer;

import java.util.List;
import java.util.WeakHashMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Maximal substitution solver
 */
public class Grounder {

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
        //return Solvator.solve(kl, e);
        if (debugEnabled) {
            System.out.println("Entering to solve\t" + kl);
        }

        example = e;
        prepareCache();

        Ball b = kl instanceof Kappa ? solve2((Kappa) kl, null) : solve2((Lambda) kl, null);    //always Kappa only...first literal is without variables(ignoring them)

        if (b == null) {
            return new Ball(Global.falseAtomValue);
        }

        ForwardChecker.printRuns();
        //ForwardChecker.clear();

        return b;   //warning - now can return null if the given KL program and Example e have no grounded solution! //replaced with -1 empty Ball
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

        boolean cancel = true;
        for (KappaRule r : k.getRules()) {
            Ball tmp = solve(r, vars);  //tmp.val is consisten but valAvg is not, needs to be computed here
            if (tmp == null || tmp.getLast() == null) { //only if there is no true grounding found for this rule then skip
                continue;
            }

            cancel = false;
            double w = r.weight;

            // HERE I need to get back a list of GroundLambda
            //- calculate the average, weight it and add
            Set<GroundLambda> lastAvg = tmp.getLastAvg();   //this set of GroundLambda must have already been evaluated(the have value set) through solve2(Lambda..)
            gk.addDisjunctAvg(lastAvg, r);
            tmp.setValAvg(GroundKL.getAvgValFrom(lastAvg));
            tmp.weightAvgWith(w);
            b.addAvg(tmp);
            //b.addActiveRule(r);   //we omit active rules completely in the new version
            //---
            //---previous max.subst. part of code
            GroundLambda t = (GroundLambda) tmp.getLast();
            gk.addDisjunct(t, r);
            tmp.weightItWith(w);    //weight the disjunct
            b.add(tmp);         //summing disjuncts' contributions
            //---
            //b.addActiveRule(r);
        }
        if (cancel) {
            return null;    //if I didn't find any of this Kappa's disjuncts(with the vars given), there is nothing to return!
        }

        b.val += k.weight;  //node offset
        b.sigmoid();    // + sigmoid
        //---
        b.valAvg += k.weight;
        b.sigmoidAvg();

        gk.setValue(b.val);
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

        if (b == null || b.getLast() == null) {
            return null;    //if I didn't solve this Lambda's rule(+vars), there is nothing to send!
        }

        // HERE I need to get back a list of GroundKappa groundings(weighted), but
        // but the results is a Set of the same groundings of this Lambda(same rule's head with different bodies) with sumed up conjuncts values
        //- calculate the average
        Set<GroundLambda> lastAvg = b.getLastAvg(); //this shouldn't be empty now
        GroundLambda gl = (GroundLambda) b.getLast();   //as well as this

        //the body should also be non-zero now
        b.val += l.initialW;    //add offset = -1*number_of_conjuncts
        b.sigmoid();
        //---calculate the average value
        b.setValAvg(GroundKL.getAvgValFrom(lastAvg));
        b.valAvg += l.initialW;
        b.sigmoidAvg();

        gl.setValue(b.val);
        //---
        gl.setValueAvg(b.valAvg);
        gl.addConjuctsAvgFrom(lastAvg); //extracting all the groundings of conjuncts into a hashmap (weighted occurrence) - this is important as adding

        if (Global.debugEnabled) {
            System.out.println(l + "\t->\t" + b.valAvg);
        }

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

        //-------------------//we will assemble the average on the level of rules(bodies)
        return bindAll(r, new Ball());  //extensive combination binding of unbound variables
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
     * NOTE - when calling this(except the no-variables finalKappa) the rule's
     * head is always fully bound
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
            //return new Ball();
            return null;
        }

        if (r.isBound()) {  //no more unbound variables
            Ball solvedBound = solveBound(r, best); // all variables are bound -> dispatch this concrete grounding (the bind variables(Term) information is in SubKL-termsList)
            //HERE  - is the aggregation of all groundings
            if (solvedBound != null) {
                best.addGroundRule((GroundLambda) solvedBound.getLast());   //the final node is either one GroundLambda for a KappaRule's body, or one GroundLamda for LambdaRule's head(with sumed up body as value)
                solvedBound.addLastAvg(best.getLastAvg());
                //solvedBound.setValAvg(best.getValAvg());
            }            //----here                                                  //i.e. it's always a sumation of the one grounded rule's body value(s)
            return solvedBound;
        }

        Terminal toBind = r.getNextUnbound();   //get next unbound variable from all body literals
        for (int i = 0; i < example.getConstCount(); i++) { // all possible bindings of variables in rule r
            if (debugEnabled) {
                System.out.println("\t toBind=" + toBind + " -> " + (i + 1) + " of " + example.getConstCount());
            }
            r.bind(toBind, i);
            r.setLastBindedVar(toBind);
            Ball b = bindAll(r, best);  //and bind the rest recursively

            //------------this is not really pruning, just holding the best so far Ball.val(doesn't hurt the average grounding agregation)
            if (b != null && b.val >= best.val) {    //if this binding of current toBind variable to i-th constant is best so far
                b.addLastAvg(best.getLastAvg()); //we need to keep all the so-far found solutions for averaging (the best Ball should already contain all of them so "adding" shouldn't be necessary, just setting)
                b.setValAvg(best.getValAvg());  //not necessary, we do not work with the ball's average value, it is aggregated from the last GroundLambdas
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
        return best;    //the valAvg is not consistent at this state
    }

//------------------------------------------------------ bounded - ground rule/nodes solving from here ---------------------------------------------------------
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
        int i = 1;
        for (SubK sk : lr.getBody()) {
            Ball tmp = cachedSolve(sk); //go solve one of body's ground kappa literal SubK
            if (tmp == null) {   //HERE - now I only care if I got this grounding entailed/confirmed
                return null;  //if I didn't I have no information to send (not just setting value to 0)
            }

            gl.addConjunct((GroundKappa) tmp.getLast());
            //gl.addConjunctAvg((GroundKappa) tmp.getLast());  //also add to avg - but shouldn't be necessary - corrected this is actually wrong (would be sumed up twice if best)

            out.add(tmp);   //sum the conjuncts in the Ball out
            out.addAvg(tmp);

            //-------------------------
            if (Global.pruning) {
                double upperBound = out.val + lr.getBodyLen() - i;
                if (best.val >= upperBound) {
                    //HERE - pruning
                    return null;          //pruning if this solution is necesarily worse - this must be off in the avg variant!!
                }
            }
            //-------------------------

            /*
             *if (best.val >= out.val + upperBound(lr, i))
             *    return new Ball();
             */
            i++;
        }

        //HERE set intermediate value to this GroundLambda(only sumation, no offset and sigmoid)
        //it will be later averaged and evaluated in solve2(Lambda...)
        gl.setValue(out.val); //should not be necessary
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
            double val;
            if (example.contains(sk)) {
                val = 1.0;    //assigning values from example - ignoring example value?
            } else {
                return null;    //HERE - this is different, if this ground kappa SubK is not entailed return false/null, not just a zero value
            }

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
        return solve2(parent, sl.getTerms());   //go solve Lambda parent of this ground lambda literal
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
            if (cache.containsKey(sk)) {
                b = cache.get(sk);
            } else {
                b = solve(sk);   //go solve one ground kappa literal SubK
                cache.put(sk.clone(), b);    //I'll save it even if it's null (so as to remember it is not entailed)
            }
        } else {
            SubL sl = (SubL) o;
            if (cache.containsKey(sl)) {
                b = cache.get(sl);
            } else {
                b = solve(sl);   //if the solved lambda grounding is false I store it anyway
                cache.put(sl.clone(), b);    //we store this (false) SubL with corresponding (empty) ball
            }
        }
        if (b != null) {
            return b.clone();
        }
        return null;
    }

//a question - how does a Ball of an empty/false substitution look like?    
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
