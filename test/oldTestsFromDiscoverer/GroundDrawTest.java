package oldTestsFromDiscoverer;

import discoverer.construction.TemplateFactory;
import discoverer.construction.template.KL;
import discoverer.construction.ExampleFactory;
import discoverer.construction.example.Example;
import discoverer.construction.template.LiftedTemplate;
import discoverer.construction.template.MolecularTemplate;
import discoverer.drawing.Dotter;
import discoverer.drawing.GroundDotter;
import discoverer.global.FileToStringList;
import discoverer.global.Global;
import discoverer.grounding.evaluation.GroundedTemplate;
import discoverer.grounding.Grounder;
import java.util.Random;
import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

public class GroundDrawTest {
    
    @Test
    public void stringTest() {
        Global.setLambdaActivation(Global.activationSet.id);
        Global.setKappaActivation(Global.activationSet.id);
        Global.setWeightInit(Global.weightInitSet.handmade);
        Global.setRg(new Random(1));
        String[] rules = FileToStringList.convert("in/strings/easy-rules2.txt", Integer.MAX_VALUE);
        
        TemplateFactory nf = new TemplateFactory();
        LiftedTemplate net = nf.construct(rules);

        //Dotter.draw(net.last, "strings");
        
        ExampleFactory eFactory = new ExampleFactory();
        String[] examples = FileToStringList.convert("in/strings/easy-examples.txt", Integer.MAX_VALUE);
        Example e = eFactory.construct(examples[0]);

        GroundedTemplate b = Grounder.groundTemplate(net.last, e);

        GroundDotter.drawMax(b, "string_ground");
    }
    
    @Ignore
    public void test1() {
        //String[] rules = FileToStringListJava6.convert("../data/rules_3_3.txt", Integer.MAX_VALUE);
        //String[] ex = { "1.0 bond(tr000_4, tr000_2, 0), cl(tr000_4), c(tr000_2), 1(0), bond(tr000_2, tr000_4, 0), bond(tr000_5, tr000_2, 1), h(tr000_5), 1(1), bond(tr000_2, tr000_5, 1), bond(tr000_3, tr000_2, 2), cl(tr000_3), 1(2), bond(tr000_2, tr000_3, 2), bond(tr000_2, tr000_1, 3), cl(tr000_1), 1(3), bond(tr000_1, tr000_2, 3).", };
        String[] rules = {
            "ll(X) :- b(X,b).",
            "l21(X) :- atom(X,cl), b(Z,Y).",
            "l22(X) :- atom(X,br), atom(X,cl).",
            "l23(X) :- atom(X,na), b(X,Y).",
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
            "0.10 k12(X) :- l12(X).",
            "0.11 k12(X) :- l13(X).",
            "0.12 k13(X) :- l11(X).",
            "0.13 k13(X) :- l12(X).",
            "0.14 k13(X) :- l13(X).",

            "final(X) :- k11(X).",
        };

        String[] ex = { "1.0 b(b,b), b(a,b), b(b,c), b(c,a), b(c,d), b(c,e), atom(a,c), atom(b,c), atom(c,c), atom(d,cl), atom(d,br).", };
        TemplateFactory nf = new TemplateFactory();
        MolecularTemplate last = nf.construct(rules);

        ExampleFactory eFactory = new ExampleFactory();
        Example e = eFactory.construct(ex[0]);

        GroundedTemplate b = Grounder.groundTemplate(last.last, e);

        Dotter.draw(last.last, b.getActiveRules());
        GroundDotter.draw(b);
    }
    
    @Ignore
    public void test111() {
        //String[] rules = FileToStringListJava6.convert("../data/rules_3_3.txt", Integer.MAX_VALUE);
        //String[] ex = { "1.0 bond(tr000_4, tr000_2, 0), cl(tr000_4), c(tr000_2), 1(0), bond(tr000_2, tr000_4, 0), bond(tr000_5, tr000_2, 1), h(tr000_5), 1(1), bond(tr000_2, tr000_5, 1), bond(tr000_3, tr000_2, 2), cl(tr000_3), 1(2), bond(tr000_2, tr000_3, 2), bond(tr000_2, tr000_1, 3), cl(tr000_1), 1(3), bond(tr000_1, tr000_2, 3).", };
        String[] rules = {
            "l21(X) :- atom(X,cl), b(Z,Y).",
            "l22(X) :- atom(X,br), atom(X,cl).",
            "l23(X) :- atom(X,cl), b(X,Y).",
            "l24(X) :- b(X,b).",

            "1.1 k21(X) :- l21(X).",
            "2.3 k21(X) :- l24(X).",
            "2.5 k24(X) :- l24(X).",
            "1.2 k22(X) :- l21(X).",
            "1.3 k22(X) :- l22(X).",
            "1.4 k22(X) :- l23(X).",
            "1.5 k23(X) :- l23(X).",

            "l11(DMY) :- k21(X), k22(Y), k21(Y).",
            "l12(DMY) :- k21(a), k22(Y), k24(Z).",
            "l13(DMY) :- k22(X), b(X,Y).",
            "l14(DMY) :- k23(X), b(X,Y).",

            "0.1 k11(DMY) :- l11(DMY2).",
            "0.2 k11(DMY) :- l12(DMY2).",
            "0.3 k11(DMY) :- l13(DMY2).",
            "0.4 k11(DMY) :- l14(DMY2).",
        };

        String[] ex = { "1.0 b(b,b), b(a,b), b(b,c), b(c,a), b(c,d), b(c,e), b(d,e), atom(a,c), atom(b,c), atom(c,c), atom(d,cl), atom(d,br).", };
        TemplateFactory nf = new TemplateFactory();
        MolecularTemplate last = nf.construct(rules);

        ExampleFactory eFactory = new ExampleFactory();
        Example e = eFactory.construct(ex[0]);

        GroundedTemplate b = Grounder.groundTemplate(last.last, e);

        Dotter.draw(last.last, b.getActiveRules());
        GroundDotter.draw(b);
    }
}
