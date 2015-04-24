package discoverer.AVG;

import discoverer.learning.backprop.BackpropDown;
import discoverer.learning.backprop.BackpropDownAvg;
import extras.BackpropGroundKappa;
import discoverer.grounding.evaluation.Ball;
import discoverer.global.Batch;
import discoverer.drawing.Dotter;
import discoverer.grounding.evaluation.Evaluator;
import discoverer.grounding.evaluation.EvaluatorAvg;
import discoverer.construction.example.Example;
import discoverer.construction.ExampleFactory;
import discoverer.global.Global;
import discoverer.drawing.GroundDotter;
import discoverer.grounding.Grounder;
import discoverer.construction.network.KL;
import discoverer.construction.network.Kappa;
import discoverer.construction.network.rules.KappaRule;
import discoverer.construction.NetworkFactory;
import discoverer.grounding.evaluation.struct.ParentCounter;
import discoverer.learning.Weights;
import java.util.*;
import org.junit.*;

public class BackPropAVGTest {

    @Before
    public void initRandom() {
        Global.rg = new Random(1L);
    }

    @Ignore
    public void backpropDownTest() {
        Global.setMax();
        String[] rules = {
            "ll(X) :- b(X,b).",
            "l21(X) :- atom(X,cl), atom(X,cl).",
            "l22(X) :- atom(X,br), atom(X,cl).",
            "l23(X) :- atom(X,na).",
            "l24(X) :- atom(X,f).",
            "1.1 k21(X) :- l21(X).",
            "2.3 k21(X) :- ll(X).",
            "2.5 k21(DMY) :- ll(X).",
            "1.2 k22(X) :- l21(X).",
            "1.3 k22(X) :- l22(X).",
            "1.4 k22(X) :- l23(X).",
            "1.5 k23(X) :- l23(X).",
            "1.6 k23(X) :- l24(X).",
            "l11(X) :- k21(X), k22(Y), k21(Y).",
            "l12(X) :- k21(a), k22(Y).",
            "l13(X) :- k22(X), k23(Y).",
            "0.1 k11(X) :- l11(X).",
            "0.2 k11(X) :- l12(X).",
            "0.9 k12(X) :- l11(X).",
            "0.15 k12(X) :- l12(X).",
            "0.11 k12(X) :- l13(X).",
            "0.12 k13(X) :- l11(X).",
            "0.13 k13(X) :- l12(X).",
            "0.14 k13(X) :- l13(X).",
            "final(X) :- k11(X).",};

        String[] rules2 = {
            "l21(X) :- atom(X,cl), atom(X,cl).",
            "l22(X) :- atom(X,br), atom(X,cl).",
            "l23(X) :- atom(X,na).",
            "l24(X) :- atom(X,f).",
            "lb(X,b) :- b(X,b).",
            "latom(X,c) :- atom(X,c)",
            "1.0 aa(X) :- lb(X,b).",
            "0.5 aa(X) :- latom(X,c)",
            "ll(X) :- aa(X).",
            "1.1 k21(X) :- l21(X).",
            "2.3 k21(X) :- ll(X).",
            "2.5 k21(DMY) :- ll(X).",
            "1.2 k22(X) :- l21(X).",
            "1.3 k22(X) :- l22(X).",
            "1.4 k22(X) :- l23(X).",
            "1.5 k23(X) :- l23(X).",
            "1.6 k23(X) :- l24(X).",
            "l11(X) :- k21(X), k22(Y), k21(Y).",
            "l12(X) :- k21(a), k22(Y).",
            "l13(X) :- k22(X), k23(Y).",
            "0.1 k11(X) :- l11(X).",
            "0.2 k11(X) :- l12(X).",
            "0.9 k12(X) :- l11(X).",
            "0.15 k12(X) :- l12(X).",
            "0.11 k12(X) :- l13(X).",
            "0.12 k13(X) :- l11(X).",
            "0.13 k13(X) :- l12(X).",
            "0.14 k13(X) :- l13(X).",
            "finall(X) :- k11(X).",
            "1.0 final :- finall(X).",};

        String[] ex = {"1.0 b(b,b), b(a,b), b(b,c), b(c,a), b(c,d), b(c,e), atom(a,c), atom(b,c), atom(c,c), atom(d,cl), atom(d,br).",};
        /*
         *String[] rules = FileToStringListJava6.convert("../data/rules_3_3.txt", Integer.MAX_VALUE);
         *String[] ex = { "0.0 bond(tr000_4, tr000_2, 0), cl(tr000_4), c(tr000_2), 1(0), bond(tr000_2, tr000_4, 0), bond(tr000_5, tr000_2, 1), h(tr000_5), 1(1), bond(tr000_2, tr000_5, 1), bond(tr000_3, tr000_2, 2), cl(tr000_3), 1(2), bond(tr000_2, tr000_3, 2), bond(tr000_2, tr000_1, 3), cl(tr000_1), 1(3), bond(tr000_1, tr000_2, 3).", };
         */

        NetworkFactory nf = new NetworkFactory();
        KL last = nf.construct(rules);

        ExampleFactory eFactory = new ExampleFactory();
        Example e = eFactory.construct(ex[0]);

        //Dotter.draw(last);
        //-----------solving
        Ball b = Grounder.solve(last, e);
        //GroundDotter.draw(b);
        //GroundDotter.drawAVG(b, "tavg");
        //------
        System.out.println("val: " + b.valMax);
        System.out.println("avgVal: " + b.valAvg);

        //-------backprop------
        double learnRate = 0.1;
        Dotter.draw(last, "backpr0");
        GroundDotter.draw(b, "backprGround0");
        Weights w;
        //--
        boolean vojta = false;
        if (vojta) {
            w = BackpropGroundKappa.getNewWeights(b, e, Batch.NO, learnRate);
            printUpdate(w);
            System.out.println("vojtaVal: " + Evaluator.evaluate(b));
            Dotter.draw(last, "backprVojta1_");
            GroundDotter.draw(b, "backprgroundVojta");
        } else {
            //--
            ParentCounter.countParents(b);
            w = BackpropDown.getNewWeights(b, e, Batch.NO, learnRate);
            printUpdate(w);
            System.out.println("downval: " + Evaluator.evaluate(b));
            Dotter.draw(last, "backprDown_");
            GroundDotter.draw(b, "backprDownground");
        }

        System.out.println("check:" + Evaluator.evaluate(b));
        b = Grounder.solve(last, e);
        System.out.println("val " + b.valMax);
        System.out.println("eval " + Evaluator.evaluate(b));
        //System.out.println(b.getActiveRules().size());
    }

    @Test
    public void backpropAVGtest() {
        Global.setAvg();
        String[] rules = {
            "ll(X) :- b(X,b).",
            "l21(X) :- atom(X,cl), atom(X,cl).",
            "l22(X) :- atom(X,br), atom(X,cl).",
            "l23(X) :- atom(X,na).",
            "l24(X) :- atom(X,f).",
            "1.1 k21(X) :- l21(X).",
            "2.3 k21(X) :- ll(X).",
            "2.5 k21(X) :- ll(X).",
            "1.2 k22(X) :- l21(X).",
            "1.3 k22(X) :- l22(X).",
            "1.4 k22(X) :- l23(X).",
            "1.5 k23(X) :- l23(X).",
            "1.6 k23(X) :- l24(X).",
            "l11(X) :- k21(X), k22(Y), k21(Y).",
            "l12(X) :- k21(a), k22(Y).",
            "l13(X) :- k22(X), k23(Y).",
            "0.1 k11(X) :- l11(X).",
            "0.2 k11(X) :- l12(X).",
            "0.9 k12(X) :- l11(X).",
            "0.15 k12(X) :- l12(X).",
            "0.11 k12(X) :- l13(X).",
            "0.12 k13(X) :- l11(X).",
            "0.13 k13(X) :- l12(X).",
            "0.14 k13(X) :- l13(X).",
            "final(X) :- k11(X).",};

        String[] rules2 = {
            "l21(X) :- atom(X,cl), atom(X,cl).",
            "l22(X) :- atom(X,br), atom(X,cl).",
            "l23(X) :- atom(X,na).",
            "l24(X) :- atom(X,f).",
            "lb(X,b) :- b(X,b).",
            "latom(X,c) :- atom(X,c)",
            "1.0 aa(X) :- lb(X,b).",
            "0.5 aa(X) :- latom(X,c)",
            "ll(X) :- aa(X).",
            "1.1 k21(X) :- l21(X).",
            "2.3 k21(X) :- ll(X).",
            "2.5 k21(DMY) :- ll(X).",
            "1.2 k22(X) :- l21(X).",
            "1.3 k22(X) :- l22(X).",
            "1.4 k22(X) :- l23(X).",
            "1.5 k23(X) :- l23(X).",
            "1.6 k23(X) :- l24(X).",
            "l11(X) :- k21(X), k22(Y), k21(Y).",
            "l12(X) :- k21(a), k22(Y).",
            "l13(X) :- k22(X), k23(Y).",
            "0.1 k11(X) :- l11(X).",
            "0.2 k11(X) :- l12(X).",
            "0.9 k12(X) :- l11(X).",
            "0.15 k12(X) :- l12(X).",
            "0.11 k12(X) :- l13(X).",
            "0.12 k13(X) :- l11(X).",
            "0.13 k13(X) :- l12(X).",
            "0.14 k13(X) :- l13(X).",
            "finall(DMY2) :- k11(X).",
            "1.0 final :- finall(DMY).",};

        String[] ex = {"1.0 b(b,b), b(a,b), b(b,c), b(c,a), b(c,d), b(c,e), atom(a,c), atom(b,c), atom(c,c), atom(d,cl), atom(d,br).",};
        /*
         *String[] rules = FileToStringListJava6.convert("../data/rules_3_3.txt", Integer.MAX_VALUE);
         *String[] ex = { "0.0 bond(tr000_4, tr000_2, 0), cl(tr000_4), c(tr000_2), 1(0), bond(tr000_2, tr000_4, 0), bond(tr000_5, tr000_2, 1), h(tr000_5), 1(1), bond(tr000_2, tr000_5, 1), bond(tr000_3, tr000_2, 2), cl(tr000_3), 1(2), bond(tr000_2, tr000_3, 2), bond(tr000_2, tr000_1, 3), cl(tr000_1), 1(3), bond(tr000_1, tr000_2, 3).", };
         */

        NetworkFactory nf = new NetworkFactory();
        KL last = nf.construct(rules);

        ExampleFactory eFactory = new ExampleFactory();
        Example e = eFactory.construct(ex[0]);

        Dotter.draw(last,"state0");
        //-----------solving
        Ball b = Grounder.solve(last, e);
        //GroundDotter.draw(b);
        GroundDotter.drawAVG(b, "stateground0");
        //------
        System.out.println("val: " + b.valMax);
        System.out.println("avgVal: " + b.valAvg);
        System.out.println("Avg: " + EvaluatorAvg.evaluate(b));
        System.out.println("val: " + Evaluator.evaluate(b));
        b = Grounder.solve(last, e);
        //GroundDotter.drawAVG(b, "backprGroundAvg0");
        System.out.println("grounder...");
        System.out.println("val: " + b.valMax);
        System.out.println("avgVal: " + b.valAvg);
        System.out.println("val: " + Evaluator.evaluate(b));
        System.out.println("Avg: " + EvaluatorAvg.evaluate(b));
        
        //-------backprop------
        double learnRate = 0.1;
        //Dotter.draw(last, "backpr0");
        //GroundDotter.draw(b, "backprGroundMax0");
        //GroundDotter.drawAVG(b, "backprGroundAvg0");
        Weights w;
        //--
        ParentCounter.countParentsAVG(b);
        w = BackpropDownAvg.getNewWeights(b, e, Batch.NO, learnRate);
        printUpdate(w);
        //---
        System.out.println("downvalAvg: " + EvaluatorAvg.evaluate(b));
        GroundDotter.drawAVG(b, "backprGroundAvg1");
        System.out.println("downvalAvg: " + EvaluatorAvg.evaluate(b));
        //--
        System.out.println("corruptedMax: " + Evaluator.evaluate(b));
        //GroundDotter.draw(b, "backprGroundMax1");
        System.out.println("corruptedMax: " + Evaluator.evaluate(b));

        System.out.println("avg:" + EvaluatorAvg.evaluate(b));
        //Dotter.draw(last, "before");
        System.out.println("grounder..");
        //GroundInvalidator.invalidate(b);
        b = Grounder.solve(last, e);
        Dotter.draw(last, "after");
        System.out.println("avg " + b.valAvg);
        System.out.println("avg " + EvaluatorAvg.evaluate(b));
        GroundDotter.drawAVG(b, "backprGroundAvg2");
        System.out.println("val" + b.valMax);
        System.out.println("val" + Evaluator.evaluate(b));
        System.out.println("grounder...");
        b = Grounder.solve(last, e);
        System.out.println("avg" + b.valAvg);
        System.out.println("val" + b.valMax);
        //System.out.println(b.getActiveRules().size());
        //sigmoid(0.5*2.500012561461216 + 0.10002512292243233)
    }

    public void printUpdate(Weights w) {
        for (Map.Entry<Object, Double> entryWeights : w.getWeights().entrySet()) {
            Object o = entryWeights.getKey();
            Double newWeight = entryWeights.getValue();
            if (o instanceof Kappa) {
                Kappa k = (Kappa) o;
                double old = k.getOffset();
                k.setOffset(k.getOffset() + newWeight);
                System.out.println(k + " : " + old + " + " + Dotter.df.format(newWeight) + "\t->\t" + k.getOffset());
            } else {
                KappaRule kr = (KappaRule) o;
                double old = kr.getWeight();
                kr.setWeight(kr.getWeight() + newWeight);
                System.out.println(kr + " : " + old + " + " + Dotter.df.format(newWeight) + "\t->\t" + kr.getWeight());
            }
        }
    }
}
