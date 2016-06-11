/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer;

import discoverer.construction.ExampleFactory;
import discoverer.construction.Parser;
import discoverer.construction.TemplateFactory;
import discoverer.construction.Variable;
import discoverer.construction.example.Example;
import discoverer.construction.template.KL;
import discoverer.construction.template.LiftedTemplate;
import discoverer.construction.template.NLPtemplate;
import discoverer.drawing.Dotter;
import discoverer.drawing.GroundDotter;
import discoverer.global.Global;
import discoverer.global.Glogger;
import discoverer.grounding.evaluation.GroundedTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Gusta
 */
public class NLPdataset extends Main {

    TemplateFactory templateFactory;
    NLPtemplate template;

    public HashMap<String, Integer> constantNames2Id;
    //facts
    private Example facts;

    public static void main(String[] args) {
        //setup all parameters and load all the necessary input files
        List<String[]> inputs = setupFromArguments(args);
        //create logger for all messages within the program
        Glogger.init();

        String[] queries = inputs.get(0);
        String[] facts = inputs.get(1);
        String[] rules = inputs.get(2);
        //String[] pretrainedRules = inputs.get(3);

        //create ground networks dataset
        NLPdataset dataset = new NLPdataset(facts, rules);

        //start learning
        dataset.learnOn(queries);
    }

    private NLPdataset(String[] iFacts, String[] iRules) {
        //contruct a fact store = actually like a one huge example graph
        ExampleFactory eFactory = new ExampleFactory();
        StringBuilder sb = new StringBuilder("1.0 ");
        for (String fact : iFacts) {
            sb.append(fact).append(",");
        }
        sb.replace(sb.length() - 1, sb.length(), ".");
        facts = eFactory.construct(sb.toString());

        templateFactory = new TemplateFactory();
        template = (NLPtemplate) templateFactory.construct(iRules);

        template.constantNames = facts.constantNames;

        Dotter.draw(template.KLs.values(), "initNLPtemplate");
    }

    private void learnOn(String[] queries) {
        int i = 0;
        for (String query : queries) {
            int wLen = Parser.getWeightLen(query);
            double targetValue = Double.parseDouble(query.substring(0, wLen));
            String[][] queryTokens = Parser.parseQuery(query.substring(wLen, query.length()));
            String signature = queryTokens[0][0];
            KL target = template.KLs.get(signature);

            List<Variable> vars = new ArrayList<>();
            for (int j = 1; j < queryTokens[0].length; j++) {
                Variable t = templateFactory.constructTerm(queryTokens[0][j]);
                vars.add(t);
            }

            GroundedTemplate proof = template.query(target, vars, facts);
            GroundDotter.draw(proof, i + "beforeLearning_" + query.substring(wLen, query.length()));
            template.updateWeights(proof, targetValue);
            GroundDotter.draw(proof, i++ + "afterLearning_" + query.substring(wLen, query.length()));
        }
    }
}
