package oldTestsFromDiscoverer;

import discoverer.construction.network.rules.KappaRule;
import discoverer.construction.TemplateFactory;
import discoverer.grounding.network.GroundKappa;
import discoverer.grounding.network.GroundLambda;
import discoverer.construction.ExampleFactory;
import discoverer.construction.example.Example;
import discoverer.construction.template.MolecularTemplate;
import discoverer.global.Tuple;
import discoverer.grounding.evaluation.GroundedTemplate;
import discoverer.grounding.Grounder;
import java.util.ArrayList;
import java.util.List;


import org.junit.Test;

public class RuleCountTest {
    List<KappaRule> store = new ArrayList<KappaRule>();
    @Test
    public void test1() {
        //String[] rules = FileToStringListJava6.convert("../data/rules_3_3.txt", Integer.MAX_VALUE);
        //String[] ex = { "1.0 bond(tr000_4, tr000_2, 0), cl(tr000_4), c(tr000_2), 1(0), bond(tr000_2, tr000_4, 0), bond(tr000_5, tr000_2, 1), h(tr000_5), 1(1), bond(tr000_2, tr000_5, 1), bond(tr000_3, tr000_2, 2), cl(tr000_3), 1(2), bond(tr000_2, tr000_3, 2), bond(tr000_2, tr000_1, 3), cl(tr000_1), 1(3), bond(tr000_1, tr000_2, 3).", };
        String[] rules = {
            "ll(X) :- b(X,b).",
            "l21(X) :- atom(X,cl), atom(X,cl).",
            "l22(X) :- atom(X,br), atom(X,cl).",
            "l23(X) :- atom(X,na).",
            "l24(X) :- atom(X,f).",

            "1.1 k21(X) :- l21(X).",
            "2.3 k21(X) :- ll(X).",
            "1.2 k22(X) :- l21(X).",
            "1.3 k22(X) :- l22(X).",
            "1.4 k22(X) :- l23(X).",
            "1.5 k23(X) :- l23(X).",
            "1.6 k23(X) :- l24(X).",

            "l11(X) :- k21(X), k22(Y), k21(Y).",
            "l12(X) :- k21(X), k22(Y).",
            "l13(X) :- k22(X), k23(Y).",

            "0.1 k11(X) :- l11(X).",
            "0.2 k11(X) :- l12(X).",
            "0.9 k12(X) :- l11(X).",
            "0.10 k12(X) :- l12(X).",
            "0.11 k12(X) :- l13(X).",
            "0.12 k13(X) :- l11(X).",
            "0.13 k13(X) :- l12(X).",
            "0.14 k13(X) :- l13(X).",

            "final(X) :- k11(X).",
        };

        String[] ex = { "1.0 b(a,b), b(b,c), b(c,a), b(c,d), b(c,e), atom(a,c), atom(b,c), atom(c,c), atom(d,cl), atom(d,br).", };
        TemplateFactory nf = new TemplateFactory();
        MolecularTemplate last = nf.construct(rules);

        ExampleFactory eFactory = new ExampleFactory();
        Example e = eFactory.construct(ex[0]);
        GroundedTemplate b = Grounder.groundTemplate(last.last, e);

        getAllKappaRules(b);

        System.out.println(store.size());
    }

    private void getAllKappaRules(GroundedTemplate b) {
        Object o = b.getLast();
        if (o instanceof GroundKappa)
            k((GroundKappa) o);
        else
            k((GroundLambda) o);
    }

    private void k(GroundKappa gk) {
        if (gk.isElement())
            return;

        for (Tuple<GroundLambda, KappaRule> t: gk.getDisjuncts()) {
            store.add(t.y);
            System.out.println(t.y);
            k(t.x);
        }
    }

    private void k(GroundLambda gl) {
        for (GroundKappa gk: gl.getConjuncts()) {
            k(gk);
        }
    }
}
