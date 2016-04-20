package oldTestsFromDiscoverer;

import discoverer.learning.Weights;
import discoverer.construction.network.rules.KappaRule;
import discoverer.construction.template.Kappa;
import discoverer.construction.TemplateFactory;
import discoverer.construction.template.KL;
import discoverer.construction.ExampleFactory;
import discoverer.construction.example.Example;
import discoverer.global.Batch;
import discoverer.global.Global;
import discoverer.grounding.evaluation.Evaluator;
import discoverer.grounding.evaluation.GroundedTemplate;
import discoverer.grounding.Grounder;
import extras.BackpropGroundKappa;
import java.util.*;
import static org.junit.Assert.*;
import org.junit.*;


public class BackpropTest {

    @Before
    public void initRandom() {
        Global.setRg(new Random(1L));
    }

    @Test
    public void test1() {
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

            "final(X) :- k11(X).",
        };

        String[] ex = { "1.0 b(b,b), b(a,b), b(b,c), b(c,a), b(c,d), b(c,e), atom(a,c), atom(b,c), atom(c,c), atom(d,cl), atom(d,br).", };
        /*
         *String[] rules = FileToStringListJava6.convert("../data/rules_3_3.txt", Integer.MAX_VALUE);
         *String[] ex = { "0.0 bond(tr000_4, tr000_2, 0), cl(tr000_4), c(tr000_2), 1(0), bond(tr000_2, tr000_4, 0), bond(tr000_5, tr000_2, 1), h(tr000_5), 1(1), bond(tr000_2, tr000_5, 1), bond(tr000_3, tr000_2, 2), cl(tr000_3), 1(2), bond(tr000_2, tr000_3, 2), bond(tr000_2, tr000_1, 3), cl(tr000_1), 1(3), bond(tr000_1, tr000_2, 3).", };
         */

        TemplateFactory nf = new TemplateFactory();
        KL last = nf.construct(rules);

        ExampleFactory eFactory = new ExampleFactory();
        Example e = eFactory.construct(ex[0]);

        GroundedTemplate b = Grounder.groundTemplate(last, e);
        System.out.println(b.valMax);
        double learnRate = 0.15;
        Weights w = BackpropGroundKappa.getNewWeights(b, e, Batch.NO, learnRate);
        print(w);
        System.out.println(Evaluator.evaluateMax(b));
        b = Grounder.groundTemplate(last, e);
        System.out.println(b.valMax);
        System.out.println(Evaluator.evaluateMax(b));
        System.out.println(b.getActiveRules().size());
    }

    public void print(Weights w) {
        for (Map.Entry<Object, Double> entryWeights: w.getWeights().entrySet()) {
            Object o = entryWeights.getKey();
            Double newWeight = entryWeights.getValue();
            if (o instanceof Kappa) {
                Kappa k = (Kappa) o;
                double old = k.getOffset();
                k.setOffset(k.getOffset() + newWeight);
                System.out.println(old + "\t->\t" + k.getOffset());
            } else {
                KappaRule kr = (KappaRule) o;
                double old = kr.getWeight();
                kr.setWeight(kr.getWeight() + newWeight);
                System.out.println(old + "\t->\t" + kr.getWeight());
            }
        }
    }
}
