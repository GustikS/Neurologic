package examples;

import discoverer.construction.TemplateFactory;
import discoverer.construction.template.KL;
import discoverer.construction.ExampleFactory;
import discoverer.construction.example.Example;
import discoverer.construction.template.MolecularTemplate;
import discoverer.drawing.Dotter;
import discoverer.drawing.GroundDotter;
import discoverer.global.Global;
import discoverer.grounding.evaluation.GroundedTemplate;
import discoverer.grounding.Grounder;
import discoverer.grounding.network.groundNetwork.GroundNetwork;
import discoverer.grounding.network.groundNetwork.GroundNeuron;
import discoverer.learning.Sample;
import java.util.*;
import static org.junit.Assert.*;
import org.junit.*;

public class People {

    public static void main(String[] args) {
        peopleTest2();
    }

    @Test
    public static void peopleTest() {
        Global.setLambdaActivation(Global.activationSet.sig);
        Global.setKappaActivation(Global.activationSet.sig);
        Global.setWeightInit(Global.weightInitSet.handmade);
        Global.setDebugEnabled(true);
        Global.setRg(new Random(1));

        String[] rules = {
            "mother(C,M):-parent(C,M),female(M).",
            "father(C,F):-parent(C,F),male(F).",
            "1.0 res :- mother(C,M).",
            "0.6 res :- father(C,F)."
        };

        String example = "1.0 male(bob),female(alice),parent(bob,alice),parent(eve,alice),parent(bob,george),male(george).";

        TemplateFactory nf = new TemplateFactory();
        MolecularTemplate last = (MolecularTemplate) nf.construct(rules);

        ExampleFactory ef = new ExampleFactory();
        Example e = ef.construct(example);

        Grounder grounder = new Grounder();
        GroundedTemplate b = grounder.groundTemplate(last.last, e);

        Dotter.draw(last.last, "people");
        GroundDotter.drawMax(b, "peopleGroundMax");
        GroundDotter.drawAVG(b, "peopleGroundAvg");
    }

    @Test
    public static void peopleTest2() {
        Global.setLambdaActivation(Global.activationSet.sig);
        Global.setKappaActivation(Global.activationSet.sig);
        Global.setWeightInit(Global.weightInitSet.handmade);
        Global.setAvg();
        Global.setDebugEnabled(true);
        Global.setRg(new Random(1));

        String[] rules = {
            "sibling(X,Y) :- male(X),male(Y).",
            "0.0 finalKappa(X) :- sibling(X,Y).",
            "finalLambda :- finalKappa(A)."
        };

        String example = "1.0 male(adam),male(sid).";

        TemplateFactory nf = new TemplateFactory();
        MolecularTemplate template = (MolecularTemplate) nf.construct(rules);

        ExampleFactory ef = new ExampleFactory();
        Example e = ef.construct(example);

        Grounder grounder = new Grounder();
        GroundedTemplate b = grounder.groundTemplate(template.last, e);

        Dotter.draw(template.last, "people2");
        GroundDotter.drawMax(b, "peopleGroundMax2");
        GroundDotter.drawAVG(b, "peopleGroundAvg2");

    }
}
