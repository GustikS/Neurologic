package oldTestsFromDiscoverer;

import discoverer.construction.TemplateFactory;
import discoverer.construction.template.KL;
import discoverer.construction.ExampleFactory;
import discoverer.construction.example.Example;
import discoverer.construction.template.MolecularTemplate;
import discoverer.drawing.Dotter;
import discoverer.drawing.GroundDotter;
import discoverer.global.FileToStringList;
import discoverer.global.Global;
import discoverer.grounding.evaluation.GroundedTemplate;
import discoverer.grounding.Grounder;
import java.util.*;
import static org.junit.Assert.*;
import org.junit.*;

public class DrawTest {

    @Test
    public void stringTest() {
        Global.setLambdaActivation(Global.activationSet.sig);
        Global.setKappaActivation(Global.activationSet.sig);
        Global.setWeightInit(Global.weightInitSet.handmade);
        Global.setRg(new Random(1));
        String[] rules = FileToStringList.convert("in/strings/easy-rules.txt", Integer.MAX_VALUE);
        
        TemplateFactory nf = new TemplateFactory();
        MolecularTemplate last = nf.construct(rules);

        Dotter.draw(last.last, "strings");
    }
    
    @Test
    public void test1() {
        Global.setLambdaActivation(Global.activationSet.sig);
        Global.setKappaActivation(Global.activationSet.sig);
        Global.setWeightInit(Global.weightInitSet.handmade);
        Global.setRg(new Random(1));
        //String[] rules = FileToStringListJava6.convert("../data/rules_2_2.txt", Integer.MAX_VALUE);
        String[] rules = {
            "l21(X) :- atom(X,cl), atom(X,cl).",
            "l22(X) :- atom(X,br), atom(cl,cl).",
            "l23(X) :- atom(X,na).",
            "l24(X) :- atom(X,f).",
            "1.12 k21(X) :- l21(X).",
            "1.13 k22(X) :- l21(X).",
            "1.545 k22(DMY) :- l21(X), l22(X).",
            "1.116 k22(X) :- l22(X).",
            "1.12 k22(X) :- l23(X).",
            "1.135 k23(X) :- l23(X).",
            "1.144 k23(X) :- l24(X).",
            "l11(X) :- k21(X), k22(Y).",
            "l12(X) :- k21(X), k22(Y).",
            "l13(X) :- k22(X), k23(Y).",
            "0.1 k11(X) :- l11(X).",
            "0.1 k11(X) :- l12(X).",
            "0.9 k12(X) :- l11(X).",
            "0.102 k12(X) :- l12(X).",
            "0.11 k12(X) :- l13(X).",
            "0.12 k13(X) :- l11(X).",
            "0.13 k13(X) :- l12(X).",
            "0.14 k13(X) :- l13(X).",
            "final(X) :- k11(X).",};

        TemplateFactory nf = new TemplateFactory();
        MolecularTemplate last = nf.construct(rules);

        Dotter.draw(last.last, "chemieee");
    }

    @Test
    public void blueTest() {
        Global.setLambdaActivation(Global.activationSet.sig);
        Global.setKappaActivation(Global.activationSet.sig);
        Global.setWeightInit(Global.weightInitSet.handmade);
        Global.setRg(new Random(1));
        String[] rules = {
            "lg(X) :- light_green(X)",
            "dg(X) :- dark_green(X)",
            "d(X) :- dark(X)",
            "r(X) :- red(X)",
            "db(X) :- dark_blue(X)",
            "lb(X) :- light_blue(X)",
            "0.4 blue(X) :- lg(X).",
            "0.9 blue(X) :- db(X).",
            "0.6 blue(X) :- dg(X).",
            "0.7 blue(X) :- d(X).",
            "0.5 blue(X) :- r(X).",
            "0.8 blue(X) :- lb(X).",
            "blueist(DMY) :- edge(1,B), edge(B,C), edge(C,4), blue(B), blue(C).",
            "1.0 output(DMY) :- blueist(DMY2)."
        };

        String ex = "1.0 edge(1,6),edge(6,5),edge(5,4),edge(1,2),edge(2,3),edge(3,4),"
                + "light_green(1),dark_green(2),dark(3),red(4),dark_blue(5),light_blue(6).";
        TemplateFactory nf = new TemplateFactory();
        MolecularTemplate last = nf.construct(rules);

        ExampleFactory ef = new ExampleFactory();
        Example e = ef.construct(ex);

        GroundedTemplate b = Grounder.groundTemplate(last.last, e);

        Dotter.draw(last.last, "modra3");
        GroundDotter.drawMax(b, "modraGround3");
        GroundDotter.drawAVG(b, "modraAVG3");
    }

}
