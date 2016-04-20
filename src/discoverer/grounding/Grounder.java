package discoverer.grounding;

import discoverer.grounding.evaluation.GroundedTemplate;
import discoverer.construction.example.Example;
import discoverer.global.Global;
import discoverer.grounding.network.GroundKL;
import discoverer.grounding.network.GroundKappa;
import discoverer.grounding.network.GroundLambda;
import discoverer.construction.template.KL;
import discoverer.construction.template.Kappa;
import discoverer.construction.network.rules.KappaRule;
import discoverer.construction.template.Lambda;
import discoverer.construction.network.rules.LambdaRule;
import discoverer.construction.network.rules.Rule;
import discoverer.construction.network.rules.SubK;
import discoverer.construction.network.rules.SubL;
import discoverer.construction.Variable;
import discoverer.global.Glogger;
import discoverer.grounding.evaluation.Evaluator;
import discoverer.learning.functions.Activations;
import java.awt.Desktop;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * substitution solver
 */
public class Grounder {

    private final boolean forwardCheckEnabled = Global.isForwardCheckEnabled();
    private final boolean pruning = Global.isPruning();
    private final boolean cacheEnabled = Global.isCacheEnabled();
    private final boolean debugEnabled = Global.isDebugEnabled();

    private Example example;
    private HashMap<Object, GroundedTemplate> cache;
    
    public ForwardChecker forwardChecker = new ForwardChecker();

    private void prepareCache() {
        if (cacheEnabled) {
            if (cache == null) {
                cache = new HashMap<Object, GroundedTemplate>();
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
    public GroundedTemplate groundTemplate(KL kl, Example e) {
        //return Solvator.solve(kl, e);
        if (debugEnabled) {
            System.out.println("Entering to solve\t" + kl);
        }

        example = e;
        prepareCache();

        GroundedTemplate b = kl instanceof Kappa ? solveKappaGivenVars((Kappa) kl, null) : solveLambdaGivenVars((Lambda) kl, null);    //always Kappa only...first literal is without variables(ignoring them)

        //forwardChecker.printRuns();

        if (b == null) {
            Glogger.err("Warning, unentailed example by the template!" + e.hash);
            return new GroundedTemplate(Global.getFalseAtomValue());
        }
        
        b.constantNames = e.constantNames;
        
        //ForwardChecker.clear();
        return b;   //warning - now can return null if the given KL program and Example e have no grounded solution! //replaced with -1 empty GroundedTemplate
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
    public GroundedTemplate solveKappaGivenVars(Kappa k, List<Variable> vars) {
        if (debugEnabled) {
            System.out.println("Solve literal\t" + k + "\twith variables\t" + vars);
        }

        GroundedTemplate b = new GroundedTemplate();
        GroundKappa gk = new GroundKappa(k, vars);

        List<Double> inputsMax = new ArrayList<>();
        List<Double> inputsAvg = new ArrayList<>();
        boolean cancel = true;
        for (KappaRule r : k.getRules()) {
            GroundedTemplate tmp = headMatching(r, vars);  //tmp.val is consisten but valAvg is not, needs to be computed here
            if (tmp == null || tmp.getLast() == null) { //only if there is no true grounding found for this rule then skip
                continue;
            }

            cancel = false;
            double w = r.getWeight();

            // HERE I need to get back a list of GroundLambda
            //- calculate the average, weight it and add
            Set<GroundLambda> lastAvg = tmp.getLastAvg();   //this set of GroundLambda must have already been evaluated(the have value set) through solve2(Lambda..)
            gk.addDisjunctAvg(lastAvg, r);
            tmp.setValAvg(Evaluator.getAvgValFrom(lastAvg));
            tmp.weightAvgWith(w);
            b.addAvg(tmp);
            //b.addActiveRule(r);   //we omit active rules completely in the new version
            //---
            //---previous max.subst. part of code
            GroundLambda t = (GroundLambda) tmp.getLast();
            gk.addDisjunct(t, r);
            tmp.weightItWith(w);    //weight the disjunct
            b.addMax(tmp);         //summing disjuncts' contributions
            //---
            //b.addActiveRule(r);
            inputsMax.add(tmp.valMax);
            inputsAvg.add(tmp.valAvg);
        }
        if (cancel) {
            return null;    //if I didn't find any of this Kappa's disjuncts(with the vars given), there is nothing to return!
        }

        //b.valMax += k.offset;  //node offset
        //b.valMax = Activations.kappaActivation(b.valMax);    // + sigmoid
        b.valMax = Activations.kappaActivation(inputsMax, k.offset);
        //---
        //b.valAvg += k.offset;
        //b.valAvg = Activations.kappaActivation(b.valAvg);
        b.valAvg = Activations.kappaActivation(inputsAvg, k.offset);

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
    public GroundedTemplate solveLambdaGivenVars(Lambda l, List<Variable> vars) {
        if (debugEnabled) {
            System.out.println("Solve lambda\t" + l + "\tvariables\t" + vars);
        }

        GroundedTemplate b = headMatching(l.getRule(), vars);      //there is only one rule for lambda node

        if (b == null || b.getLast() == null) {
            return null;    //if I didn't solve this Lambda's rule(+vars), there is nothing to send!
        }

        // HERE I need to get back a list of GroundKappa groundings(weighted), but
        // but the results is a Set of the same groundings of this Lambda(same rule's head with different bodies) with sumed up conjuncts values
        //- calculate the average
        Set<GroundLambda> lastAvg = b.getLastAvg(); //this shouldn't be empty now
        GroundLambda gl = (GroundLambda) b.getLast();   //as well as this

        //the body should also be non-zero now
        //b.valMax += l.getOffset();    //add offset = -1*number_of_conjuncts
        //b.valMax = Activations.lambdaActivation(b.valMax);
        List<Double> inputs = new ArrayList<>();
        for (GroundKappa inp : gl.getConjuncts()) {
            inputs.add(inp.getValue());
        }
        b.valMax = Activations.lambdaActivation(inputs, l.getOffset());
        //---calculate the average value
        gl.addConjuctsAvgFrom(lastAvg); //extracting all the groundings of conjuncts into a hashmap (weighted occurrence) - this is important as adding
        //b.setValAvg(GroundKL.getAvgValFrom(lastAvg));
        //b.valAvg += l.getOffset();
        //b.valAvg = Activations.lambdaActivation(b.valAvg);
        inputs = new ArrayList<>();
        for (Map.Entry<GroundKappa, Integer> inp : gl.getConjunctsAvg().entrySet()) {
            inputs.add(inp.getValue() * inp.getKey().getValueAvg() / gl.getConjunctsCountForAvg());
        }
        b.valAvg = Activations.lambdaActivation(inputs, l.getOffset());

        gl.setValue(b.valMax);
        //---
        gl.setValueAvg(b.valAvg);

        if (debugEnabled) {
            System.out.println(l + "\t->\t" + b.valAvg);
        }

        return b;
    }

    /**
     * solving ONE (lambda/kappa) rule - consuming variables and binding rule r
     *
     * @param r
     * @param vars - variables binded from the above call (to be unified with the head)
     * @return
     */
    public GroundedTemplate headMatching(Rule r, List<Variable> vars) {
        if (debugEnabled) {
            System.out.println("Solving rule\t" + r + " with \tvariables\t" + vars);
        }

        r.headUnification(vars);    //head of rule variable binding/unification/matching from previous line/call
        r.setLastBindedVar(null);   //

        //-------------------//we will assemble the average on the level of rules(bodies)
        return bindAllVarsInRule(r, new GroundedTemplate());  //extensive combination binding of unbound variables
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
    public GroundedTemplate bindAllVarsInRule(Rule r, GroundedTemplate best) {
        if (debugEnabled) {
            System.out.println("BindingAll variables in \t" + r);
        }

        if (forwardCheckEnabled && !forwardChecker.shouldContinue(r, example)) {
            //return new GroundedTemplate();
            return null;
        }

        if (r.isBound()) {  //no more unbound variables
            GroundedTemplate solvedBound = solveBound(r, best); // all variables are bound -> dispatch this concrete grounding (the bind variables(Term) information is in SubKL-termsList)
            //HERE  - is the aggregation of all groundings
            if (solvedBound != null) {
                best.addGroundRule((GroundLambda) solvedBound.getLast());   //the final node is either one GroundLambda for a KappaRule's body, or one GroundLamda for LambdaRule's head(with sumed up body as value)
                solvedBound.addLastAvg(best.getLastAvg());
                //solvedBound.setValAvg(best.getValAvg());
            }            //----here                                                  //i.e. it's always a sumation of the one grounded rule's body value(s)
            return solvedBound;
        }

        Variable toBind = r.getNextUnbound();   //get next best(score) unbound variable for the rule
        for (int i = 0; i < example.getConstCount(); i++) { // all possible bindings of variables in rule r
            if (debugEnabled) {
                System.out.println("\t toBind = " + toBind + " -> trying constant " + example.constantNames.get(i) + " of total " + example.getConstCount() + " in the example");
            }
            if (Global.alldiff && r.usedTerms.contains(i)) {
                 continue; //ALLDIFF functionality - that's it :)
            }
            r.bind(toBind, i);
            r.setLastBindedVar(toBind);
            GroundedTemplate b = bindAllVarsInRule(r, best);  //and bind the rest recursively

            //------------this is not really pruning, just holding the best so far GroundedTemplate.val(doesn't hurt the average grounding agregation)
            if ((b != null) && ((best.valMax == null) || (b.valMax >= best.valMax))) {    //if this binding of current toBind variable to i-th constant is best so far
                b.addLastAvg(best.getLastAvg()); //we need to keep all the so-far found solutions for averaging (the best GroundedTemplate should already contain all of them so "adding" shouldn't be necessary, just setting)
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
    public GroundedTemplate solveBound(Rule r, GroundedTemplate best) {
        if (debugEnabled) {
            System.out.println("Dispatching solving bound\t" + r);
        }

        if (r instanceof KappaRule) {
            return solveBoundKR((KappaRule) r);
        } else {
            return solveGroundedLR((LambdaRule) r, best);
        }
    }

    /**
     * bound kappa rule - not really a rule (only one literal in body) = solve
     * the body
     *
     * @param kr
     * @return
     */
    public GroundedTemplate solveBoundKR(KappaRule kr) {
        if (debugEnabled) {
            System.out.println("Solving bound\t" + kr);
        }

        SubL body = kr.getBody();
        GroundedTemplate b = cachedSolveGroundLiteral(body);

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
    public GroundedTemplate solveGroundedLR(LambdaRule lr, GroundedTemplate best) {
        if (debugEnabled) {
            System.out.println("Solving grounded\t" + lr);
        }

        GroundLambda gl = new GroundLambda(lr.getHead().getParent(), lr.getHead().getTerms());

        GroundedTemplate out = new GroundedTemplate();
        int i = 1;
        for (SubK sk : lr.getBody()) {
            GroundedTemplate tmp = cachedSolveGroundLiteral(sk); //go solve one of body's ground kappa literal SubK
            if (tmp == null) {   //HERE - now I only care if I got this grounding entailed/confirmed
                return null;  //if I didn't I have no information to send (not just setting value to 0)
            }

            gl.addConjunct((GroundKappa) tmp.getLast());
            //gl.addConjunctAvg((GroundKappa) tmp.getLast());  //also add to avg - but shouldn't be necessary - corrected this is actually wrong (would be sumed up twice if best)

            out.addMax(tmp);   //sum the conjuncts in the GroundedTemplate out
            out.addAvg(tmp);

            //-------------------------
            if (pruning) {
                double upperBound = out.valMax + lr.getBodyLen() - i;
                if (best.valMax != null && best.valMax >= upperBound) {
                    //HERE - pruning
                    return null;          //pruning if this solution is necesarily worse - this must be off in the avg variant!!
                }
            }
            //-------------------------

            /*
             *if (best.val >= out.val + upperBound(lr, i))
             *    return new GroundedTemplate();
             */
            i++;
        }

        //HERE set intermediate value to this GroundLambda(only sumation, no offset and sigmoid)
        //it will be later averaged and evaluated in solve2(Lambda...)
        gl.setValue(out.valMax); //should not be necessary
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
    public GroundedTemplate solve(Object o) {
        if (o instanceof SubK) {
            return solveGroundKappa((SubK) o);
        } else {
            return solveGroundLambda((SubL) o);
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
    public GroundedTemplate solveGroundKappa(SubK sk) {
        if (debugEnabled) {
            System.out.println("Computing\t" + sk);
        }

        Kappa parent = sk.getParent();

        GroundedTemplate b;
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

            b = new GroundedTemplate(val);
            GroundKappa gk = new GroundKappa(sk.getParent(), sk.getTerms());        //this GroundKappa is with no Sigmoid
            gk.setValue(val);
            b.setLast(gk);
            //HERE - (end of recursion here(binding to an example)) starting the recursion tree with both val and valAvg set up
            b.valAvg = val;
            gk.setValueAvg(val);
        } else {
            b = solveKappaGivenVars(parent, sk.getTerms());
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
    public GroundedTemplate solveGroundLambda(SubL sl) {
        if (debugEnabled) {
            System.out.println("Computing\t" + sl);
        }

        Lambda parent = sl.getParent();
        GroundedTemplate b;
        if (parent.isElement()) {   //= literal with no rules
            double val;
            if (example.contains(sl)) {
                val = 0.5;    //assigning values from example - ignoring example value?
            } else {
                return null;    //HERE - this is different, if this ground kappa SubK is not entailed return false/null, not just a zero value
            }

            if (debugEnabled) {
                System.out.println(sl + " is found: " + val);
            }

            b = new GroundedTemplate(val);
            GroundLambda gl = new GroundLambda(sl.getParent(), sl.getTerms());        //this GroundKappa is with no Sigmoid
            gl.setValue(val);
            b.setLast(gl);
            //HERE - (end of recursion here(binding to an example)) starting the recursion tree with both val and valAvg set up
            b.valAvg = val;
            gl.setValueAvg(val);
            return b;
        } else {
            return solveLambdaGivenVars(parent, sl.getTerms());   //go solve Lambda parent of this ground lambda literal
        }
    }

    /**
     * check cache for grounded literals or go to solve -> creates acyclic
     * structure instead of a tree!!
     * <p>
     * is the same grounding o the literal has been solved before return the
     * corresponding GroundedTemplate(with previously created GroundKapp/Lambda
     * as the last Object)
     *
     * @param o
     * @return
     */
    public GroundedTemplate cachedSolveGroundLiteral(Object o) {
        if (!cacheEnabled) {
            return solve(o);
        }

        GroundedTemplate b;
        if (o instanceof SubK) {
            SubK sk = (SubK) o;
            if (cache.containsKey(sk)) {
                b = cache.get(sk);
            } else {
                b = solveGroundKappa(sk);   //go solve one ground kappa literal SubK
                cache.put(sk.clone(), b);    //I'll save it even if it's null (so as to remember it is not entailed)
            }
        } else {
            SubL sl = (SubL) o;
            if (cache.containsKey(sl)) {
                b = cache.get(sl);
            } else {
                b = solveGroundLambda(sl);   //if the solved lambda grounding is false I store it anyway
                cache.put(sl.clone(), b);    //we store this (false) SubL with corresponding (empty) ball
            }
        }
        if (b != null) {
            return b.clone();
        }
        return null;
    }

//a question - how does a GroundedTemplate of an empty/false substitution look like?    
    /*
     *    public static GroundedTemplate cachedSolve(Object o) {
     *        if (!cacheEnabled)
     *            return solve(o);
     *
     *        SubOutput so;
     *        if (o instanceof SubK)
     *            so = new SubOutput((SubK) o);
     *        else
     *            so = new SubOutput((SubL) o);
     *
     *        GroundedTemplate b = cache.get(so);
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
