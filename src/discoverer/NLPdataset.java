/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer;

import discoverer.construction.ConstantFactory;
import discoverer.construction.ExampleFactory;
import discoverer.construction.Parser;
import static discoverer.construction.Parser.getWeightLen;
import discoverer.construction.TemplateFactory;
import discoverer.construction.Variable;
import discoverer.construction.example.Example;
import discoverer.construction.template.KL;
import discoverer.construction.template.Kappa;
import discoverer.construction.template.Lambda;
import static discoverer.construction.template.LightTemplate.weightFolder;
import discoverer.construction.template.NLPtemplate;
import discoverer.construction.template.rules.KappaRule;
import discoverer.construction.template.rules.Rule;
import discoverer.construction.template.rules.SubK;
import discoverer.construction.template.rules.SubKL;
import discoverer.construction.template.rules.SubL;
import discoverer.drawing.Dotter;
import discoverer.drawing.GroundDotter;
import discoverer.global.Global;
import discoverer.global.Glogger;
import discoverer.grounding.BottomUpConnector;
import discoverer.grounding.evaluation.GroundedTemplate;
import discoverer.grounding.network.GroundKL;
import discoverer.learning.Saver;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gusta
 */
public class NLPdataset extends Main {

    TemplateFactory templateFactory;
    NLPtemplate template;

    public HashMap<String, Integer> constantNames2Id;
    //facts
    public static Example facts;

    private boolean exportCache = false;

    public static void main(String[] args) {
        Glogger.resultsDir = "./results/";
        //setup all parameters and load all the necessary input files
        List<String[]> inputs = setupFromArguments(args);
        //create logger for all messages within the program
        Glogger.init();

        if (inputs.size() < 3) {
            Glogger.err("Missing input files!");
            return;
        }

        String[] queries = inputs.get(0);
        String[] facts = inputs.get(1);
        String[] rules = inputs.get(2);
        //String[] pretrainedRules = inputs.get(3);

        //create ground networks dataset
        NLPdataset dataset = new NLPdataset(facts, rules);

        //start learning
        String[] results = dataset.learnOn(queries);
        //dataset.evaluate();
        dataset.export(results, "NLP");
    }

    private NLPdataset(String[] iFacts, String[] iRules) {
        Global.weightFolder = "./weights/";
        weightFolder = "./weights/";
        Dotter.outPath = "./images/";

        Global.NLPtemplate = true;
        Global.molecularTemplates = false;
        Global.weightedFacts = true;

        Global.templateConstants = true;
        Global.recursion = true;

        //Global.alldiff = true;
        //Global.embeddings = true;
        Global.cacheEnabled = true;

        templateFactory = new TemplateFactory();
        template = (NLPtemplate) templateFactory.construct(iRules);

        if (Global.embeddings) {
            ConstantFactory.loadEmbeddings("./in/embeddings.csv");     //TO change
        }

        //contruct a fact store = actually like a one huge example graph
        ExampleFactory eFactory = new ExampleFactory();
        double[] weights = new double[iFacts.length];
        StringBuilder sb = new StringBuilder("1.0 ");
        for (int i = 0; i < iFacts.length; i++) {
            int expLen = getWeightLen(iFacts[i]);
            if (expLen > 0) {
                weights[i] = Double.parseDouble(iFacts[i].substring(0, expLen));
            } else {
                weights[i] = 1;
            }
            sb.append(iFacts[i].substring(expLen, iFacts[i].length())).append(",");
        }
        sb.replace(sb.length() - 1, sb.length(), ".");

        facts = eFactory.construct(sb.toString());

        if (Global.weightedFacts) {
            facts.setWeightedFacts(weights, templateFactory.constructFacts(sb.toString()));
        }

        for (Map.Entry<String, Integer> ent : ExampleFactory.getConstMap().entrySet()) {
            ConstantFactory.construct(ent.getKey());
        }

        template.constantNames = facts.constantNames;

        if (Global.drawing) {
            Dotter.draw(template.KLs.values(), "initNLPtemplate");
        }
        
        String query = "story(th)";
        BottomUpConnector btmup = new BottomUpConnector();
        GroundKL groundLRNN = btmup.getGroundLRNN(templateFactory.getRules(), facts.hash.substring(0, facts.hash.lastIndexOf(".")), query);
        GroundedTemplate b = new GroundedTemplate();
        b.constantNames = template.constantNames;
        b.setLast(groundLRNN);
        template.evaluateProof(b);
        GroundDotter.draw(b, "bottomUP");
        System.out.println("nakresleno");
        
    }

    private void evaluate() {
        GroundedTemplate proof = template.evaluate(facts);
        GroundDotter.draw(proof, "eval");
    }

    private String[] learnOn(String[] queries) {
        int i = 0;
        String[] results = new String[queries.length];
        for (String query : queries) {
            Double targetValue = null;

            int wLen = Parser.getWeightLen(query);
            if (wLen > 0) {
                targetValue = Double.parseDouble(query.substring(0, wLen));
            }

            String[][] queryTokens = Parser.parseQuery(query.substring(wLen, query.length()));
            String signature = queryTokens[0][0];
            KL target = template.KLs.get(signature);

            templateFactory.clearVarFactory();
            List<Variable> vars = new ArrayList<>();
            for (int j = 1; j < queryTokens[0].length; j++) {
                Variable t = templateFactory.constructTerm(queryTokens[0][j]);
                vars.add(t);
            }

            GroundedTemplate proof = template.query(target, vars, facts);
            double res = proof.valMax;
            if (Global.drawing) {
                GroundDotter.draw(proof, i + "beforeLearning_" + query.substring(wLen, query.length()));
            }
            if (targetValue != null) {
                template.updateWeights(proof, targetValue);

                if (Global.drawing) {
                    GroundDotter.draw(proof, i + "afterLearning_" + query.substring(wLen, query.length()));
                }
                res = template.evaluateProof(proof);
                if (Global.drawing) {
                    GroundDotter.draw(proof, i + "afterEvaluation_" + query.substring(wLen, query.length()));
                }
            }
            results[i++] = res + " <- " + query;
            Glogger.out(results[i - 1]);
        }
        return results;
    }

    public void export(String[] results, String destination) {
        if (Global.embeddings) {
            ConstantFactory.exportEmbeddings(destination);
        }
        template.exportTemplate(destination);
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(weightFolder + destination + "-facts.w"), "utf-8"));
            for (GroundKL gkl : facts.storedFacts.values()) {
                double val;
                if (Global.getGrounding() == Global.groundingSet.avg) {
                    val = gkl.getValueAvg();
                } else {
                    val = gkl.getValue();
                }
                StringBuilder sb = new StringBuilder(val + "  " + gkl.getGeneral().getPredicateName() + "(");
                for (int i : gkl.getTermList()) {
                    sb.append(facts.constantNames.get(i)).append(",");
                }
                sb.replace(sb.length() - 1, sb.length(), ")\n");
                writer.write(sb.toString());
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Saver.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Saver.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(Saver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(weightFolder + destination + "-answers.w"), "utf-8"));
            for (String result : results) {
                writer.write(result + "\n");
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Saver.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Saver.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(Saver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (exportCache && Global.isCacheEnabled()) {
            try {
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(weightFolder + destination + "-cache.w"), "utf-8"));
                HashMap<SubKL, GroundedTemplate> cache = template.prover.getCache();
                for (Map.Entry<SubKL, GroundedTemplate> ent : cache.entrySet()) {
                    if (ent.getValue() == null) {
                        continue;
                    }
                    ent.getValue().constantNames = facts.constantNames;
                    //System.out.println(ent.getValue() + " : " + ent.getValue().getLast().toString(template.constantNames));
                    writer.write(ent.getValue() + " : " + ent.getValue().getLast().toString(template.constantNames) + "\n");
                    GroundDotter.draw(ent.getValue(), ent.getKey().getParent().name);
                }
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(Saver.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Saver.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    writer.close();
                } catch (IOException ex) {
                    Logger.getLogger(Saver.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
