/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer.construction.template;

import discoverer.construction.Variable;
import discoverer.construction.example.Example;
import discoverer.construction.template.rules.Rule;
import discoverer.construction.template.rules.SubKL;
import discoverer.drawing.GroundDotter;
import discoverer.global.Global;
import discoverer.global.Glogger;
import discoverer.grounding.Grounder;
import discoverer.grounding.evaluation.Evaluator;
import discoverer.grounding.evaluation.GroundedTemplate;
import discoverer.grounding.evaluation.struct.GroundNetworkParser;
import discoverer.grounding.network.GroundKL;
import discoverer.learning.Weights;
import discoverer.learning.backprop.BackpropDown;
import discoverer.learning.backprop.BackpropDownAvg;
import discoverer.learning.learners.Learning;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Gusta
 */
public class NLPtemplate extends LiftedTemplate {

    public Learning learning = new Learning();
    public Grounder prover = learning.grounder;

    public Map<String, KL> KLs = new HashMap<>();

    boolean clearingCache = true;

    /**
     * create new template based on input facts and rules, basically creating a
     * neural deductive ontology from input A-Box and T-box
     *
     * @param iFacts
     * @param iRules
     * @param iQueries
     */
    public NLPtemplate(KL ikl, Map<String, KL> klNames, List<Rule> irules) {
        last = ikl;
        KLs = klNames;
        //+ maybe extract rules as well
        for (Rule irule : irules) {
            rules.add(irule);
        }
        //interesting - maybe now the cache may stay and be reused over many queries, because the fact structures stay the same? (if there are no new facts comming)
        prover.prepareCache();
    }

    /**
     * answer a query with an output value given the facts
     *
     * @param query
     * @param vars
     * @param facts
     * @return
     */
    public GroundedTemplate query(KL target, List<Variable> vars, Example facts) {
        prover.example = facts;

        if (clearingCache) {
            prover.prepareCache();
        }
        prover.forwardChecker.setupForNewExample(facts);
        SubKL skl = prover.addOpenAtom(target, vars);
        GroundedTemplate answer = target instanceof Kappa ? prover.solveKappaGivenVars((Kappa) target, vars) : prover.solveLambdaGivenVars((Lambda) target, vars);

        prover.removeOpenAtom(skl);

        prover.forwardChecker.printRuns();
        if (answer == null) {
            Glogger.err("Warning, unentailed query by the template!: ");
            return new GroundedTemplate(Global.getFalseAtomValue());
        }
        answer.constantNames = facts.constantNames;

        GroundedTemplate b = answer;
        Set<GroundKL> groundKLs = null;
        if (Global.getGrounding() == Global.groundingSet.avg) {
            groundKLs = GroundNetworkParser.parseAVG(b);
        } else if (Global.getGrounding() == Global.groundingSet.max) {
            groundKLs = GroundNetworkParser.parseMAX(b);
        }
        b.loadGroundNeurons(groundKLs);   //store all ground L/K in an array for fast and simple operations instead of DFS for each simple pass
        //b.groundNeurons.addAll(GroundNetworkParser.elements);  //no, do not add the fact neurons as their values will get invalidated then

        return answer;
    }

    public void updateWeights(GroundedTemplate proof, double targetVal) {
        Weights newWeights;
        if (Global.getGrounding().equals(Global.groundingSet.avg)) {
            newWeights = BackpropDownAvg.getNewWeights(proof, targetVal);
        } else {
            newWeights = BackpropDown.getNewWeights(proof, targetVal);
        }
        learning.refreshWeights(newWeights);
    }

    public double evaluateProof(GroundedTemplate proof) {
        if (Global.getGrounding().equals(Global.groundingSet.avg)) {
            proof.valAvg = Evaluator.evaluateAvg(proof);  //forward propagation
            return proof.valAvg;
        } else {
            proof.valMax = Evaluator.evaluateMax(proof);  //forward propagation
            return proof.valMax;
        }
    }

    public GroundedTemplate evaluate(Example facts) {
        return prover.groundTemplate(last, facts);
    }

}
