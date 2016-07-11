/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer.grounding;

import discoverer.construction.ConstantFactory;
import discoverer.construction.Parser;
import discoverer.construction.TemplateFactory;
import discoverer.construction.Variable;
import discoverer.construction.template.KL;
import discoverer.construction.template.Kappa;
import discoverer.construction.template.Lambda;
import discoverer.construction.template.rules.KappaRule;
import discoverer.construction.template.rules.Rule;
import discoverer.construction.template.rules.SubK;
import discoverer.construction.template.rules.SubKL;
import discoverer.construction.template.rules.SubL;
import discoverer.global.Glogger;
import discoverer.global.Tuple;
import discoverer.grounding.network.GroundKL;
import discoverer.grounding.network.GroundKappa;
import discoverer.grounding.network.GroundLambda;
import ida.ilp.logic.Clause;
import ida.ilp.logic.Literal;
import ida.ilp.logic.LogicUtils;
import ida.ilp.logic.Term;
import ida.ilp.logic.io.PrologParser;
import ida.ilp.logic.subsumption.Matching;
import ida.utils.tuples.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import supertweety.lrnn.grounder.BottomUpGrounder;

/**
 *
 * @author Gusta
 */
public class BottomUpConnector {

    Set<Literal> herbrandModel;
    Map<Rule, List<List<Literal>>> groundRuleMap;

    boolean caching = true;
    HashMap<Literal, GroundKL> cache;
    HashSet<Literal> openAtomSet;
    int recursiveLoopCount;
    private LinkedHashMap<Literal, Integer> recursiveLoops;

    String[] extraRules = new String[]{"similar(X,X)"};

    public static void main(String[] args) {
        String[] rules = new String[]{
            "0.96875 holdsK(S,P,O) :- holdsL1(S,P,O)",
            "1.0 similarK1s(A,B) :- similar(A,B)",
            "1.0 similarK1p(A,B) :- similar(A,B)",
            "1.0 similarK1o(A,B) :- similar(A,B)",
            "holdsL1(S,P,O) :- similarK1s(S,concept:mammal:tiger),similarK1p(P,concept:animalistypeofanimal),similarK1o(O,concept:mammal:cats)",
            "similar(X,X)"
        };
        String[] facts = new String[]{"nic(dummy)"};
        Set<String> allConstants = new HashSet<>();
        allConstants.add("concept:mammal:tiger");
        allConstants.add("concept:animalistypeofanimal");
        allConstants.add("concept:mammal:cats");
        /*
        Set<Literal> herbrandModel = getHerbrandModel(rules, facts, allConstants);
        for (Literal literal : herbrandModel) {
            System.out.println(literal);
        }
         */    }

    public GroundKL getGroundLRNN(List<Rule> rules, String facts, String query) {
        cache = new HashMap<>();
        openAtomSet = new HashSet<>();
        recursiveLoopCount = 0;
        recursiveLoops = new LinkedHashMap<>();

        Map<Literal, Map<Rule, List<List<Literal>>>> head2Tails = new HashMap<>();
        if (groundRuleMap == null) {
            groundRuleMap = getGroundRules(rules, facts, ConstantFactory.getConstMap().keySet());
        }
        for (Map.Entry<Rule, List<List<Literal>>> ent : groundRuleMap.entrySet()) {
            for (List<Literal> clause : ent.getValue()) {
                Literal head = null;
                List<Literal> body = new LinkedList<>();
                for (Literal lit : clause) {
                    if (lit.isNegated()) {
                        body.add(lit.negation());
                    } else {
                        head = lit;
                    }
                }
                Map<Rule, List<List<Literal>>> rule2groundings = head2Tails.get(head);
                if (rule2groundings == null) {  //there is no such literal as a key
                    Map<Rule, List<List<Literal>>> ruleWithGroundings = new HashMap<>();
                    List<List<Literal>> bodies = new ArrayList<>();
                    bodies.add(body);
                    ruleWithGroundings.put(ent.getKey(), bodies);
                    head2Tails.put(head, ruleWithGroundings);
                } else if (rule2groundings.get(ent.getKey()) == null) { //the literal has no such Rule
                    List<List<Literal>> bodies = new ArrayList<>();
                    bodies.add(body);
                    rule2groundings.put(ent.getKey(), bodies);
                } else {    // just add the new ground rule to the list of ground rules for the respective Rule of the respective literal
                    rule2groundings.get(ent.getKey()).add(body);
                }
            }
        }

        Literal start = null;
        for (Literal head : head2Tails.keySet()) {
            if (head.toString().replaceAll(" ", "").equals(query)) {
                start = head;
            }
        }
        GroundKL output = createGroundLRNN(start, head2Tails);
        return output;
    }

    private GroundKL createGroundLRNN(Literal top, Map<Literal, Map<Rule, List<List<Literal>>>> head2Tails) {
        int storedRecursiveLoopCount = recursiveLoopCount;

        KL kl = TemplateFactory.predicatesByName.get(top.predicate() + "/" + top.arity());
        if (openAtomSet.contains(top)) {
            Integer recCount = recursiveLoops.get(top);
            if (recCount == null) {
                recursiveLoops.put(top, 1);
            } else {
                recursiveLoops.put(top, recCount + 1);
            }
            recursiveLoopCount++;
            return null;
        } else {
            openAtomSet.add(top);
        }

        List<Variable> terms = new ArrayList<>();
        for (Term term : top.arguments()) {
            Variable var = ConstantFactory.construct(term.name());
            terms.add(var);
        }

        Map<Rule, List<List<Literal>>> rule2Bodies = head2Tails.get(top);

        GroundKL gkl = null;
        if (kl instanceof Kappa) {
            gkl = new GroundKappa((Kappa) kl, terms);
            List<Tuple<HashSet<GroundLambda>, KappaRule>> allDisjuncts = new ArrayList<>();
            if (rule2Bodies == null) {
                gkl.setValueAvg(1.0);
                openAtomSet.remove(top);
                Integer removed = recursiveLoops.remove(top);
                if (removed != null) {
                    recursiveLoopCount -= removed;
                }
                return gkl;
            }
            for (Map.Entry<Rule, List<List<Literal>>> ent : rule2Bodies.entrySet()) {
                KappaRule kr = (KappaRule) ent.getKey();
                HashSet<GroundLambda> grbodi = new HashSet<>();
                for (List<Literal> grbody : ent.getValue()) {
                    for (Literal literal : grbody) {    //KappaRule is flat - just one literal in the body
                        GroundKL saved = cache.get(literal);
                        if (saved != null) {
                            grbodi.add((GroundLambda) saved);
                            continue;
                        }
                        GroundLambda solved = (GroundLambda) createGroundLRNN(literal, head2Tails);
                        if (solved != null) {
                            grbodi.add(solved);
                        } else if (caching && (storedRecursiveLoopCount == recursiveLoopCount && solved == null)) {
                            cache.put(literal, solved);
                        }
                    }
                }
                if (!grbodi.isEmpty()) {
                    allDisjuncts.add(new Tuple(grbodi, kr));
                }
            }
            if (!allDisjuncts.isEmpty()) {
                ((GroundKappa) gkl).setDisjunctsAvg(allDisjuncts);
            } else {
                openAtomSet.remove(top);
                Integer removed = recursiveLoops.remove(top);
                if (removed != null) {
                    recursiveLoopCount -= removed;
                }
                return null;
            }
        } else {
            gkl = new GroundLambda((Lambda) kl, terms);
            if (rule2Bodies == null) {
                gkl.setValueAvg(0.5);
                openAtomSet.remove(top);
                Integer removed = recursiveLoops.remove(top);
                if (removed != null) {
                    recursiveLoopCount -= removed;
                }
                return gkl;
            }
            HashMap<GroundKappa, Integer> allConjuncts = new HashMap<>();

            int count = 0;
            for (Map.Entry<Rule, List<List<Literal>>> ent : rule2Bodies.entrySet()) {   //GroundLambda has just one LambdaRule

                for (List<Literal> grbody : ent.getValue()) {   //all the ground bodies become flattened in the compressed representation
                    count++;
                    List<GroundKappa> body = new ArrayList<>(grbody.size());
                    for (Literal literal : grbody) {
                        GroundKL saved = cache.get(literal);
                        if (saved != null) {
                            body.add((GroundKappa) saved);
                            continue;
                        }
                        GroundKappa solved = (GroundKappa) createGroundLRNN(literal, head2Tails);
                        if (solved != null) {
                            body.add(solved);
                        } else {
                            if (caching && (storedRecursiveLoopCount == recursiveLoopCount && solved == null)) {
                                cache.put(literal, solved);
                            }
                            body = null;
                            break;
                        }
                    }
                    if (body != null) {
                        for (GroundKappa gk : body) {
                            Integer get = allConjuncts.get(gk);
                            if (get != null) {
                                allConjuncts.put(gk, get + 1);
                            } else {
                                allConjuncts.put(gk, 1);
                            }
                        }
                    }
                }
            }
            if (!allConjuncts.isEmpty()) {
                ((GroundLambda) gkl).setConjunctsAvg(allConjuncts);
                ((GroundLambda) gkl).setConjunctsCountForAvg(count);
            } else {
                openAtomSet.remove(top);
                Integer removed = recursiveLoops.remove(top);
                if (removed != null) {
                    recursiveLoopCount -= removed;
                }
                return null;
            }
        }
        openAtomSet.remove(top);
        Integer removed = recursiveLoops.remove(top);
        if (removed != null) {
            recursiveLoopCount -= removed;
        }
        return gkl;
    }

    /**
     * requires ConstantFactory to be load up and filled with all constant names
     * as well as TemplateFactory.predicatesByName
     *
     * @param rules
     * @param facts
     * @return
     */
    public Set<SubKL> getLRNNcache(List<Rule> rules, String facts) {
        Set<SubKL> cache = new HashSet<>();
        if (herbrandModel == null) {
            herbrandModel = getHerbrandModel(rules, facts, ConstantFactory.getConstMap().keySet());
        }
        for (Literal literal : herbrandModel) {
            if (literal.predicate().equals("exists")) {
                continue;
            }
            KL kl = TemplateFactory.predicatesByName.get(literal.predicate() + "/" + literal.arity());
            SubKL subkl;
            if (kl instanceof Kappa) {
                subkl = new SubK((Kappa) kl, true);
            } else {
                subkl = new SubL((Lambda) kl, true);
            }
            for (Term term : literal.arguments()) {
                Variable var = ConstantFactory.construct(term.name());
                subkl.addVariable(var);
            }
            cache.add(subkl);
        }
        return cache;
    }

    public Set<Literal> getHerbrandModel(List<Rule> rules, String facts, Set<String> allConstants) {
        Pair<List<Clause>, Clause> clauseRepresentation = getClauseRepresentationFromLRNNStrings(rules, facts, allConstants);

        return getHerbrandModel(clauseRepresentation.r, clauseRepresentation.s);
    }

    public Map<Rule, List<List<Literal>>> getGroundRules(List<Rule> rules, String facts, Set<String> allConstants) {
        Pair<List<Clause>, Clause> clauseRepresentation = getClauseRepresentationFromLRNNStrings(rules, facts, allConstants);

        if (herbrandModel == null) {
            herbrandModel = getHerbrandModel(clauseRepresentation.r, clauseRepresentation.s);
        }

        return getGroundRules(herbrandModel, rules, clauseRepresentation.r);
    }

    public Map<Rule, List<List<Literal>>> getGroundRules(Set<Literal> herbrand, List<Rule> rules, List<Clause> clauses) {
        Clause herbrandBase = new Clause(herbrand);
        Map<Rule, List<List<Literal>>> groundRules = new LinkedHashMap<>();
        Matching m = new Matching();
        if (rules.size() != clauses.size()) {
            Glogger.err("warning - mismatch in size matching of rule and clause lists: " + rules.size() + " vs " + clauses.size());
        }
        for (int i = 0; i < rules.size(); i++) {
            List<List<Literal>> grRules = new ArrayList<>();
            groundRules.put(rules.get(i), grRules);
            Pair<Term[], List<Term[]>> pair = m.allSubstitutions(removeNegationsFromClause(clauses.get(i)), herbrandBase);
            for (Term[] substitution : pair.s) {

                //Clause grRule = LogicUtils.substitute(clauses.get(i), pair.r, substitution);
                List<Literal> lits = getMySubstitutions(clauses.get(i), pair.r, substitution);

                grRules.add(lits);
            }
        }
        return groundRules;
    }

    public Set<Literal> getHerbrandModel(List<Clause> irules, Clause groundFacts) {
        List<Clause> rules = new ArrayList<>(irules);
        for (Literal l : groundFacts.literals()) {
            rules.add(new Clause(l));
        }

        Set<Literal> herbrand = null;
        long t1 = System.nanoTime();

        BottomUpGrounder bug = new BottomUpGrounder();
        herbrand = bug.herbrandModel(rules);

        long t2 = System.nanoTime();
        Glogger.process("Herbrand model creation time: " + (t2 - t1) / 1e6 + "ms");

        return herbrand;

    }

    private Clause removeNegationsFromClause(Clause rule) {
        LinkedHashSet<Literal> literals = new LinkedHashSet<>();
        for (Literal lit : rule.literals()) {
            if (lit.isNegated()) {
                lit = lit.negation();
            }
            literals.add(lit);
        }
        return new Clause(literals);
    }

    public Clause getClauseFromRuleString(String line) {
        int weightLen = Parser.getWeightLen(line);
        Pair<List<Literal>, List<Literal>> rule = PrologParser.parseLine(line.substring(weightLen));
        List<Literal> literals = new ArrayList<>();
        literals.addAll(rule.r);
        if (rule.s != null) {
            for (Literal literal : rule.s) {
                literals.add(literal.negation());
            }
        }
        return new Clause(literals);
    }

    private Pair<List<Clause>, Clause> getClauseRepresentationFromLRNNStrings(List<Rule> rules, String facts, Set<String> allConstants) {
        List<Clause> clauses = new ArrayList<>();
        for (Rule rule : rules) {
            Clause clause = getClauseFromRuleString(rule.toString());
            clauses.add(clause);
        }

        for (String extra : extraRules) {
            Glogger.process("adding extra rule: " + extra);
            Clause clause = getClauseFromRuleString(extra);
            clauses.add(clause);
        }

        StringBuilder sb = new StringBuilder();
        for (String constant : allConstants) {
            sb.append("exists(").append(constant).append("),");
        }
        sb.append(facts);

        Clause ground = Clause.parse(sb.toString());
        return new Pair<>(clauses, ground);
    }

    private int containsLiteralCount(String rule, String literalName) {
        Pattern p = Pattern.compile(literalName + "\\(");
        Matcher m = p.matcher(rule);
        int count = 0;
        while (m.find()) {
            count += 1;
        }
        return count;
    }

    private List<Literal> getMySubstitutions(Clause c, Term[] a, Term[] b) {
        Map<Term, Term> substitution = new HashMap<>();
        for (int i = 0; i < a.length; i++) {
            substitution.put(a[i], b[i]);
        }

        List<Literal> literals = new ArrayList<Literal>();
        for (Literal l : c.literals()) {
            Literal cl = l.copy();
            for (int j = 0; j < l.arity(); j++) {
                if (substitution.containsKey(l.get(j))) {
                    cl.set(substitution.get(l.get(j)), j);
                }
            }
            literals.add(cl);
        }
        return literals;
    }
}
