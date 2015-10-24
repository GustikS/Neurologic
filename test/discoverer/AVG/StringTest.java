/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer.AVG;

import discoverer.construction.ExampleFactory;
import discoverer.construction.NetworkFactory;
import discoverer.construction.example.Example;
import discoverer.construction.network.Kappa;
import discoverer.construction.network.Network;
import discoverer.construction.network.rules.KappaRule;
import discoverer.drawing.Dotter;
import discoverer.drawing.GroundDotter;
import discoverer.global.FileToStringListJava6;
import discoverer.global.Global;
import discoverer.grounding.Grounder;
import discoverer.grounding.evaluation.Ball;
import discoverer.grounding.evaluation.Evaluator;
import discoverer.grounding.evaluation.struct.GroundNetworkParser;
import discoverer.learning.Weights;
import discoverer.learning.backprop.BackpropDown;
import extras.BackpropGroundKappa;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.junit.Test;

/**
 *
 * @author Gusta
 */
public class StringTest {

    private Map<Example, Ball> roundStore = new HashMap<Example, Ball>();
    private static final boolean debugEnabled = true;

    @Test
    public void stringTest() {
        Global.setLambdaActivation(Global.activationSet.id);
        Global.setKappaActivation(Global.activationSet.id);
        Global.setWeightInit(Global.weightInitSet.handmade);
        Global.setRg(new Random(1));
        String[] rules = FileToStringListJava6.convert("in/strings/easy-rules.txt", Integer.MAX_VALUE);

        NetworkFactory nf = new NetworkFactory();
        Network net = nf.construct(rules);

        Dotter.draw(net.last, "strings");

        ExampleFactory eFactory = new ExampleFactory();
        String[] examples = FileToStringListJava6.convert("in/strings/easy-examples.txt", Integer.MAX_VALUE);
        Example e = eFactory.construct(examples[0]);

        Ball b = Grounder.solve(net.last, e);

        GroundDotter.draw(b, "string_ground");
    }

    @Test
    public void learntest() {
        Global.setLambdaActivation(Global.activationSet.sig);
        Global.setKappaActivation(Global.activationSet.sig);
        Global.setWeightInit(Global.weightInitSet.handmade);
        Global.setRg(new Random(1));
        String[] rules = FileToStringListJava6.convert("in/strings/easy-rules.txt", Integer.MAX_VALUE);

        NetworkFactory nf = new NetworkFactory();
        Network net = nf.construct(rules);

        Dotter.draw(net.last, "strings");

        ExampleFactory eFactory = new ExampleFactory();
        String[] ex = FileToStringListJava6.convert("in/strings/easy-examples.txt", Integer.MAX_VALUE);
        
        for (int i = 0; i < ex.length; i++) {
            Example e = eFactory.construct(ex[i]);
            Ball b = Grounder.solve(net.last, e);
            if (b == null) {
                b = new Ball(-1);
            }
            GroundNetworkParser.parseMAX(b);
            roundStore.put(e, b);
            System.out.println("Original output #" + i + "\t" + b.valMax);
        }

        while (true) {
            for (Map.Entry<Example, Ball> entry : roundStore.entrySet()) {
                Example e = entry.getKey();
                Ball b = Grounder.solve(net.last, e);
                if (b == null) {
                    b = new Ball(-1);
                }
                roundStore.put(e, b);
                System.out.println("New subs #" + "\t" + b.valMax);
            }
            
            for (int i = 0; i < 5; i++) {
                for (Map.Entry<Example, Ball> entry : roundStore.entrySet()) {
                    Example e = entry.getKey();
                    Ball b = entry.getValue();
                    if (b == null) {
                        continue;
                    }

                    Weights w = BackpropDown.getNewWeights(b, e);

                    for (Map.Entry<Object, Double> t : w.getWeights().entrySet()) {
                        Object o = t.getKey();
                        if (o instanceof KappaRule) {
                            KappaRule kr = (KappaRule) o;
                            if (debugEnabled) {
                                System.out.println("Changing rule weights\t" + kr + "\t" + kr.getWeight() + "\t->\t" + t.getValue() + "\t(" + (kr.getWeight() - t.getValue()) + ")");
                            }
                            kr.setWeight(t.getValue());
                        } else {
                            Kappa kap = (Kappa) o;
                            if (debugEnabled) {
                                System.out.println("Changing bias weights\t\t" + kap + "\t" + kap.getOffset() + "\t->\t" + t.getValue() + "\t(" + (kap.offset - t.getValue()) + ")");
                            }
                            kap.setOffset(t.getValue());
                        }
                    }

                    double out = Evaluator.evaluateMax(b);
                    b.valMax = out;
                    System.out.println("Learned output:\t" + out);
                    
                    GroundDotter.draw(b, "string_ground" + i);
                }
            }
            break;
        }
    }
}