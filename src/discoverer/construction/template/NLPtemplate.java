/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer.construction.template;

import discoverer.NLPdataset;
import discoverer.construction.Parser;
import discoverer.construction.Variable;
import discoverer.construction.example.Example;
import static discoverer.construction.template.LightTemplate.weightFolder;
import discoverer.construction.template.rules.KappaRule;
import discoverer.construction.template.rules.LambdaRule;
import discoverer.construction.template.rules.Rule;
import discoverer.construction.template.rules.SubKL;
import discoverer.drawing.GroundDotter;
import discoverer.global.Global;
import discoverer.global.Glogger;
import discoverer.grounding.BottomUpConnector;
import discoverer.grounding.Grounder;
import discoverer.grounding.evaluation.Evaluator;
import discoverer.grounding.evaluation.GroundedTemplate;
import discoverer.grounding.evaluation.struct.GroundNetworkParser;
import discoverer.grounding.network.GroundKL;
import discoverer.grounding.network.GroundLambda;
import discoverer.learning.Saver;
import discoverer.learning.Weights;
import discoverer.learning.backprop.BackpropDown;
import discoverer.learning.backprop.BackpropDownAvg;
import discoverer.learning.learners.Learning;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gusta
 */
public class NLPtemplate extends LiftedTemplate {
    
    public LinkedHashSet<Rule> commonRules;
    public HashMap<String, HashSet<Rule>> relevantRules;
    
    public Learning learning = new Learning();
    public Grounder prover;
    
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
    public NLPtemplate(KL ikl, Map<String, KL> klNames, List<Rule> irules, Set<Kappa> ikappas) {
        kappas = ikappas;
        last = ikl;
        KLs = klNames;
        //+ maybe extract rules as well
        for (Rule irule : irules) {
            rules.add(irule);
        }
        //interesting - maybe now the cache may stay and be reused over many queries, because the fact structures stay the same? (if there are no new facts comming)
        if (Global.bottomUp) {
            prover = new BottomUpConnector();
        } else {
            prover = learning.grounder;
            prover.prepareCache();
        }
    }

    /**
     * answer a query with an output value given the facts
     *
     * @param query
     * @param vars
     * @param facts
     * @return
     */
    public GroundedTemplate queryTopDown(KL target, List<Variable> vars, Example facts) {
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
    
    public GroundedTemplate queryBottomUp(Example facts, String query) {
        List<Rule> relRules = new ArrayList<>(rules);
        if (NLPdataset.predicateInvention) {
            relRules = selectRelevantRuleSubset(query);
        }
        GroundKL groundLRNN = ((BottomUpConnector) prover).getGroundLRNN(relRules, facts, query);
        GroundedTemplate b = new GroundedTemplate();
        b.constantNames = constantNames;
        b.setLast(groundLRNN);
        evaluateProof(b);
        Set<GroundKL> groundKLs = null;
        if (Global.getGrounding() == Global.groundingSet.avg) {
            groundKLs = GroundNetworkParser.parseAVG(b);
        } else if (Global.getGrounding() == Global.groundingSet.max) {
            groundKLs = GroundNetworkParser.parseMAX(b);
        }
        b.loadGroundNeurons(groundKLs);   //store all ground L/K in an array for fast and simple operations instead of DFS for each simple pass
        return b;
    }
    
    public void updateWeights(GroundedTemplate proof, double targetVal) {
        Weights newWeights;
        if (Global.getGrounding().equals(Global.groundingSet.avg)) {
            newWeights = BackpropDownAvg.getNewWeights(proof, targetVal);
        } else {
            newWeights = BackpropDown.getNewWeights(proof, targetVal);
        }
        Iterator<Object> iterator = newWeights.getWeights().keySet().iterator();
        while (iterator.hasNext()) {
            Object w = iterator.next();
            if (w.toString().startsWith("holdsK(A,B,C) :- holdsLrek") || w.toString().equals("holdsK/3") || w.toString().startsWith("similar(")) {
                iterator.remove();
            }
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
    
    @Override
    public void exportWeightMatrix(String destination) {
        LinkedHashMap<String, LinkedHashMap<String, Double>> weights = new LinkedHashMap<>();
        LinkedHashSet<String> cols = new LinkedHashSet<>();
        //creating weight matrix
        for (Rule rule : rules) {
            if (rule instanceof LambdaRule || rule.toString().contains("holdsL(A,generalizations,C)")) {
                continue;
            }
            KappaRule kr = (KappaRule) rule;
            
            if (kr.getBody().getParent().getRule().getBody().get(0).isElement()) {
                String row = null;
                String col = null;
                if (kr.getBody().getParent().getRule().getBody().get(1).getTerm(1).getName().equals("generalizations")) {
                    row = kr.getBody().getParent().getRule().getBody().get(0).getTerm(1).getName();
                    col = kr.getBody().getParent().getRule().getBody().get(2).getTerm(1).getName();
                } else if (kr.getBody().getParent().getRule().getBody().get(1).getTerm(1).getName().equals("hasFeature")) {
                    row = kr.getBody().getParent().getRule().getBody().get(2).getTerm(1).getName();
                    col = kr.getBody().getParent().getRule().getBody().get(0).getTerm(1).getName();
                }
                LinkedHashMap<String, Double> getRow = weights.get(row);
                if (getRow == null) {
                    getRow = new LinkedHashMap<>();
                }
                getRow.put(col, kr.getWeight());
                weights.put(row, getRow);
                cols.add(col);
            }
        }
        
        BufferedWriter export = null;
        try {
            export = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(weightFolder + destination + "-nlp-weightMatrix.csv"), "utf-8"));
            export.write("weights;");
            for (String col : cols) {
                export.write(col + ";");
            }
            export.write("\n");
            for (String row : weights.keySet()) {
                export.write(row + ";");
                LinkedHashMap<String, Double> getRow = weights.get(row);
                for (String col : cols) {
                    if (getRow.get(col) != null) {
                        export.write(getRow.get(col).toString() + ";");
                    } else {
                        export.write("NA" + ";");
                    }
                }
                export.write("\n");
            }
            export.write("\n");
            export.close();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Saver.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Saver.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                export.close();
            } catch (IOException ex) {
                Logger.getLogger(Saver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void exportValueMatrix(String destination, Collection<GroundKL> cache) {
        LinkedHashMap<String, LinkedHashMap<String, Double>> weights = new LinkedHashMap<>();
        LinkedHashSet<String> cols = new LinkedHashSet<>();
        //creating weight matrix
        for (GroundKL gkl : cache) {
            if (gkl == null) {
                continue;
            }
            if (gkl instanceof GroundLambda) {
                continue;
            }
            if (!gkl.toString().startsWith("holdsK(")) {
                continue;
            }
            if (!constantNames.get(gkl.getTermList()[1]).equals("generalizations")) {
                continue;
            }
            String row = constantNames.get(gkl.getTermList()[0]);
            String col = constantNames.get(gkl.getTermList()[2]);
            
            LinkedHashMap<String, Double> getRow = weights.get(row);
            if (getRow == null) {
                getRow = new LinkedHashMap<>();
            }
            getRow.put(col, gkl.getValueAvg());
            weights.put(row, getRow);
            cols.add(col);
        }

        //ordering----
        LinkedHashSet<String> orderedCols = new LinkedHashSet<>();
        for (String col : cols) {
            if (weights.containsKey(col)) {
                orderedCols.add(col);
            }
        }
        LinkedHashMap<String, LinkedHashMap<String, Double>> orderedWeights = new LinkedHashMap<>();
        for (String cls : orderedCols) {
            orderedWeights.put(cls, weights.get(cls));
        }
        orderedWeights.putAll(weights);
        
        for (String col : cols) {
            orderedCols.add(col);
        }

        //--
        BufferedWriter export = null;
        try {
            export = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(weightFolder + destination + "-nlp-valueMatrix.csv"), "utf-8"));
            export.write("values;");
            for (String col : orderedCols) {
                export.write(col + ";");
            }
            export.write("\n");
            for (String row : orderedWeights.keySet()) {
                export.write(row + ";");
                LinkedHashMap<String, Double> getRow = orderedWeights.get(row);
                for (String col : orderedCols) {
                    if (getRow.get(col) != null) {
                        export.write(getRow.get(col).toString() + ";");
                    } else {
                        export.write("NA" + ";");
                    }
                }
                export.write("\n");
            }
            export.write("\n");
            export.close();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Saver.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Saver.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                export.close();
            } catch (IOException ex) {
                Logger.getLogger(Saver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private List<Rule> selectRelevantRuleSubset(String query) {
        List relRules = new ArrayList();
        String[] parseLiteral = Parser.parseLiteral(query);
        for (int i = 1; i < parseLiteral.length; i++) {
            if (!parseLiteral[i].equals("hasFeature")) {
                relRules.addAll(relevantRules.get(parseLiteral[i]));
            }
        }
        relRules.addAll(commonRules);
        return relRules;
    }
    
    public HashMap<String, HashSet<Rule>> createRelevantRules(List<Rule> rules) {
        HashMap<String, HashSet<Rule>> kappaConnections = new HashMap<>();
        HashMap<String, HashSet<Rule>> relevantRules = new HashMap<>();
        rules.stream().forEach((Rule rule) -> {
            String[][] parseRule = Parser.parseRule(rule.toFullString());
            for (int j = 1; j < parseRule.length; j++) {    //skip the weight
                String[] strings = parseRule[j];
                String headPredicate = strings[0];
                HashSet<Rule> get = kappaConnections.get(headPredicate);
                if (get == null) {
                    get = new HashSet<>();
                    kappaConnections.put(headPredicate, get);
                }
                get.add(rule);
                for (int i = 1; i < strings.length; i++) {
                    if (!strings[i].isEmpty() && !strings[i].equals("is") && Character.isLowerCase(strings[i].charAt(0))) {
                        HashSet<Rule> relr = relevantRules.get(strings[i]);
                        if (relr == null) {
                            relr = new HashSet<>();
                            relevantRules.put(strings[i], relr);
                        }
                        relr.add(rule);
                    }
                }
            }
        });
        //add connected kappas
        rules.stream().forEach((Rule rule) -> {
            String[][] parseRule = Parser.parseRule(rule.toFullString());
            String headPredicate = parseRule[1][0];
            for (int j = 1; j < parseRule.length; j++) {
                String[] strings = parseRule[j];
                for (int i = 1; i < strings.length; i++) {
                    if (!strings[i].isEmpty() && !strings[i].equals("is") && Character.isLowerCase(strings[i].charAt(0))) {
                        HashSet<Rule> relr = relevantRules.get(strings[i]);
                        relr.addAll(kappaConnections.get(headPredicate));
                    }
                }
            }
        });
        return relevantRules;
    }
}
