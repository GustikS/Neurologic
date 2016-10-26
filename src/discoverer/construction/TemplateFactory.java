package discoverer.construction;

import discoverer.construction.template.rules.LambdaRule;
import discoverer.construction.template.rules.KappaRule;
import discoverer.construction.template.rules.SubL;
import discoverer.construction.template.rules.SubK;
import discoverer.construction.template.KL;
import discoverer.construction.template.Kappa;
import discoverer.construction.template.KappaFactory;
import discoverer.construction.template.Lambda;
import discoverer.construction.template.LambdaFactory;
import discoverer.construction.template.LiftedTemplate;
import discoverer.construction.template.MolecularTemplate;
import discoverer.construction.template.NLPtemplate;
import discoverer.construction.template.rules.Rule;
import discoverer.construction.template.rules.SubKL;
import discoverer.construction.template.specialPredicates.NotEqualPredicate;
import discoverer.construction.template.specialPredicates.SimilarityPredicate;
import discoverer.construction.template.specialPredicates.SpecialPredicate;
import discoverer.global.Global;
import discoverer.global.Glogger;
import discoverer.learning.functions.Activations;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Factory for whole network
 */
public class TemplateFactory {

    public KappaFactory kFactory = new KappaFactory();
    public LambdaFactory lFactory = new LambdaFactory();
    public VariableFactory vFactory = new VariableFactory();

    public LinkedList<Rule> templateRules = new LinkedList<>();

    public static Map<String, SpecialPredicate> specialPredicateNames;
    public static Map<KL, SpecialPredicate> specialPredicatesMap;
    
    public static Map<String,KL> predicatesByName;

    public TemplateFactory() {
        ConstantFactory.clearConstantFactory();

        specialPredicateNames = new HashMap<>();
        if (Global.specialPredicates) {
            specialPredicateNames.put("@similar/2", new SimilarityPredicate("@similar"));
            specialPredicateNames.put("@neq/2", new NotEqualPredicate("@neq"));
            specialPredicatesMap = new HashMap<>();
        }
        
        predicatesByName = new HashMap<>();
    }

    public List<Rule> getRules() {
        return templateRules;
    }

    /**
     * creates network from rules, returns last row KL node as output
     * (kappa-lambda superclass)
     *
     * @param rules
     * @return
     */
    public LiftedTemplate construct(String[] rules) {
        KL kl = null;
        HashMap<String, String> activations = new HashMap<>();
        HashMap<String, Double> offsets = new HashMap<>();

        if (Global.isCheckback()) {
            for (int i = 0; i < 42; i++) {
                Global.getRandomDouble();   //to synchronize initizalization of new template with lambda elements
            }
        }

        for (int x = 0; x < rules.length; x++) {

            if (rules[x].contains(":-")) {  //this is a rule..
                String[][] tokens = Parser.parseRule(rules[x]);
                boolean isLambdaLine = tokens[0][0].isEmpty();
                //the self handling of each K/L rule, adding it to the base

                kl = isLambdaLine ? handleLambdaLine(tokens, rules[x]) : handleKappaLine(tokens, rules[x]);
                predicatesByName.put(kl.name, kl);
                vFactory.clear();   //scope of variables is one line only!
            } else if (rules[x].contains("[")) {    //this is activation function specification
                String[] split = rules[x].split(" ");
                activations.put(split[0].trim(), split[1].substring(split[1].indexOf("[") + 1, split[1].indexOf("]")));
            } else if (rules[x].contains(".")) {    //this is offset for Kappa (actually i Lambda) specification
                String[] split = rules[x].split(" ");
                try {
                    offsets.put(split[0].trim(), Double.parseDouble(split[1].trim()));
                } catch (Exception ex) {
                    Glogger.err("Couldn't parse offset from " + rules[x]);
                }
            }
        }

        kl.dropout = -1;    //do never drop the last element of the network!! (otherwise there is no network :))

        if (Global.isKappaAdaptiveOffset()) {
            for (Kappa kappa : kFactory.getKappas()) {
                kappa.initOffset();
            }
        }
        //setup offsets if specified for some literals
        for (Map.Entry<String, Double> ent : offsets.entrySet()) {
            predicatesByName.get(ent.getKey()).offset = ent.getValue();
        }
        //setup activation functions - new feature!
        for (Map.Entry<String, String> ent : activations.entrySet()) {
            predicatesByName.get(ent.getKey()).activation = Activations.translate(ent.getValue());
        }

        //setup network
        LiftedTemplate network;
        if (Global.molecularTemplates) {
            network = new MolecularTemplate(kl);  //a wrapper for the last KL literal
        } else if (Global.NLPtemplate) {
            network = new NLPtemplate(kl, predicatesByName, templateRules, new HashSet<>(kFactory.getKappas()));
        } else {
            network = new LiftedTemplate(kl);
        }
        return network;
    }

    public Variable constructTerm(String s) {
        boolean isVariable = s.matches("^[A-Z].*");

        return isVariable ? vFactory.construct(s) : ConstantFactory.construct(s);
    }

    /**
     * takes a lambda row token string (conjunction with no weights) and
     * <p>
     * adds lambda node to lambda-Factory
     * <p>
     * creates grounded Lambda node sl
     * <p>
     * adds every head variable as Terminal to variable-Factory
     * <p>
     * creates LambdaRule with sl as head<p>
     * <p>
     * creates Kappa node and its grounding for every literal in body<p>
     * for each variable in the literal adds corresponding Terminal's as
     * variable's to the Kappa grounding<p>
     * <p>
     * adds every body literal with all its variables to the rule sets current
     * rule to this Lambda (head) node (has just this one)
     *
     * @param tokens
     * @return
     */
    private Lambda handleLambdaLine(String[][] tokens, String original) {
        Lambda l = lFactory.construct(tokens[1][0]);
        SubL sl = new SubL(l, true);
        for (int i = 1; i < tokens[1].length; i++) {
            Variable v = constructTerm(tokens[1][i]);
            sl.addVariable(v);
        }
        LambdaRule lr = new LambdaRule();
        lr.addHead(sl);
        templateRules.addFirst(lr);

        for (int i = 2; i < tokens.length; i++) {
            Kappa k = kFactory.construct(tokens[i][0]);
            predicatesByName.put(k.name, k);
            SubK sk = new SubK(k, false);
            for (int j = 1; j < tokens[i].length; j++) {
                Variable t = constructTerm(tokens[i][j]);
                sk.addVariable(t);
            }
            lr.addBodyConjunct(sk);
        }

        l.setRule(lr);
        lr.originalName = original;
        return l;
    }

    /**
     * takes a Kappa row token string (disjunction with weights) and
     * <p>
     * adds Kappa node to kappa-Factory
     * <p>
     * creates grounded Kappa node sk
     * <p>
     * adds every head variable as Terminal to variable-Factory
     * <p>
     * creates KappaRule with sk as head<p>
     * <p>
     * adds KappaRule to list kappaRules
     * <p>
     * creates Lambda node and its grounding for every literal in body<p>
     * for each variable in the literal adds corresponding Terminal's as
     * variable's to the Lambda grounding<p>
     * <p>
     * sets the last body literal (there should be only one for Kappa rule line)
     * with its variables to the rule adds current rule to this Kappa (head)
     * node (may have more)
     *
     * @param tokens
     * @return
     */
    private Kappa handleKappaLine(String[][] tokens, String original) {
        Double w = Double.parseDouble(tokens[0][0].replace(",", "."));
        Kappa k = kFactory.construct(tokens[1][0]);
        SubK sk = new SubK(k, true);
        for (int i = 1; i < tokens[1].length; i++) {
            Variable v = constructTerm(tokens[1][i]);
            sk.addVariable(v);
        }
        KappaRule kr = new KappaRule(w);
        kr.setHead(sk);
        templateRules.addFirst(kr);

        for (int i = 2; i < tokens.length; i++) {
            Lambda l = lFactory.construct(tokens[i][0]);
            predicatesByName.put(l.name, l);
            SubL sl = new SubL(l, false);
            for (int j = 1; j < tokens[i].length; j++) {
                Variable t = constructTerm(tokens[i][j]);
                sl.addVariable(t);
            }
            kr.setBody(sl);
        }

        k.addRule(kr);
        kr.originalName = original;
        return k;
    }

    public List<SubKL> constructFacts(String facts) {
        facts = facts.trim().substring(facts.indexOf(" "), facts.length());
        String[][] tokens = Parser.parseQuery(facts);
        vFactory.clear();
        List<SubKL> skls = new LinkedList<>();
        for (String[] token : tokens) {
            Lambda l = lFactory.construct(token[0]);
            SubL sl = new SubL(l, true);
            for (int i = 1; i < token.length; i++) {
                Variable v = constructTerm(token[i]);
                sl.addVariable(v);
            }
            skls.add(sl);
        }
        return skls;
    }

    public void printWeights() {
        System.out.println("-----------------offsets-------------");
        int i = 0;
        for (Kappa kappa : kFactory.getKappas()) {
            System.out.println(i++ + " -> " + kappa.offset);
        }
        System.out.println("----------------ruleweights--------------");
        i = 0;
        for (Rule rule : templateRules) {
            if (rule instanceof KappaRule) {
                System.out.println(i++ + " -> " + ((KappaRule) rule).getWeight());
            }
        }

    }

    /**
     * mergeElements saved network with a new one - replace some with pretrained
     * weights
     *
     * @param network
     * @param savedNet
     * @return
     */
    public KL merge(KL network, KL savedNet) {
        return null;
    }

    public void clearVarFactory() {
        vFactory.clear();
    }

}
