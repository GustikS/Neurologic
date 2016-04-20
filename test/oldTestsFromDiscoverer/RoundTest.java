package oldTestsFromDiscoverer;

import discoverer.learning.Weights;
import discoverer.construction.network.rules.KappaRule;
import discoverer.construction.template.Kappa;
import discoverer.construction.TemplateFactory;
import discoverer.construction.ExampleFactory;
import discoverer.construction.example.Example;
import discoverer.construction.template.MolecularTemplate;
import discoverer.global.Global;
import discoverer.grounding.evaluation.Evaluator;
import discoverer.grounding.evaluation.GroundedTemplate;
import discoverer.grounding.Grounder;
import extras.BackpropGroundKappa;
import java.util.*;
import org.junit.*;

public class RoundTest {

    private Map<Example, GroundedTemplate> roundStore = new HashMap<Example, GroundedTemplate>();
    private static final boolean debugEnabled = true;

    @Before
    public void initRandom() {
        Global.setRg(new Random(1L));
    }

    @Test
    public void test() {
        String[] rules = {
            "l21(X) :- atom(X,cl), atom(X,cl).",
            "l22(X) :- atom(X,br).",
            "l23(X) :- atom(X,na).",
            "l24(X) :- atom(X,f).",
            "0.1 k21(X) :- l21(X).",
            "0.1 k22(X) :- l21(X).",
            "0.1 k22(X) :- l22(X).",
            "0.1 k22(X) :- l23(X).",
            "0.1 k23(X) :- l23(X).",
            "0.1 k23(X) :- l24(X).",
            "l11(X) :- k21(X), k22(Y).",
            "l12(X) :- k21(X), k22(Y).",
            "l13(X) :- k22(X), k23(Y).",
            "0.1 k11(X) :- l11(X).",
            "0.1 k11(X) :- l12(X).",
            "0.9 k12(X) :- l11(X).",
            "0.10 k12(X) :- l12(X).",
            "0.11 k12(X) :- l13(X).",
            "0.12 k13(X) :- l11(X).",
            "0.13 k13(X) :- l12(X).",
            "0.14 k13(X) :- l13(X).",};

        String[] ex = {
            "1.0 b(a,b), b(b,c), b(c,a), b(c,d), b(c,e), atom(a,c), atom(b,c), atom(c,c), atom(d,cl), atom(d,br).",
            "1.0 blb(a,b).",
            "1.0 b(a,b), b(b,c), b(c,a), b(c,d), b(c,e), atom(a,c), atom(b,c), atom(c,c), atom(d,cl), atom(d,br).",
            "1.0 b(a,b), b(b,c), b(c,a), b(c,d), b(c,e), atom(a,c), atom(b,c), atom(c,c), atom(d,cl), atom(d,br).",};

        TemplateFactory nf = new TemplateFactory();
        MolecularTemplate last = nf.construct(rules);

        ExampleFactory eFactory = new ExampleFactory();
        for (int i = 0; i < ex.length; i++) {
            Example e = eFactory.construct(ex[i]);
            GroundedTemplate b = Grounder.groundTemplate(last.last, e);
            if (b == null) {
                b = new GroundedTemplate(-1);
            }
            roundStore.put(e, b);
            System.out.println("Original output #" + i + "\t" + b.valMax);
        }

        while (true) {
            for (Map.Entry<Example, GroundedTemplate> entry : roundStore.entrySet()) {
                Example e = entry.getKey();
                GroundedTemplate b = Grounder.groundTemplate(last.last, e);
                if (b == null) {
                    b = new GroundedTemplate(-1);
                }
                roundStore.put(e, b);
                System.out.println("New subs #" + "\t" + b.valMax);
            }

            for (int i = 0; i < 5; i++) {
                for (Map.Entry<Example, GroundedTemplate> entry : roundStore.entrySet()) {
                    Example e = entry.getKey();
                    GroundedTemplate b = entry.getValue();
                    if (b == null) {
                        continue;
                    }

                    Weights w = BackpropGroundKappa.getNewWeights(b, e, Global.batchMode.NO, 0.05);

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
                }
            }
            break;
        }
    }
}
