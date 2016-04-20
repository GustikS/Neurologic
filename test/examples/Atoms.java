/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package examples;

import discoverer.construction.ExampleFactory;
import discoverer.construction.TemplateFactory;
import discoverer.construction.example.Example;
import discoverer.construction.template.MolecularTemplate;
import discoverer.drawing.Dotter;
import discoverer.drawing.GroundDotter;
import discoverer.global.Global;
import discoverer.grounding.Grounder;
import discoverer.grounding.evaluation.GroundedTemplate;
import java.util.Random;
import org.junit.Test;

/**
 *
 * @author Gusta
 */
public class Atoms {
    public static void main(String[] args) {
        atomsTest();
    }
    
    @Test
    public static void atomsTest() {
        Global.setLambdaActivation(Global.activationSet.sig);
        Global.setKappaActivation(Global.activationSet.sig);
        Global.setWeightInit(Global.weightInitSet.handmade);
        Global.setDebugEnabled(true);
        Global.setRg(new Random(1));
        
        String[] rules = {
            "ll(X) :- bond(X,b).",
            "l21(X) :- atom(X,cl), bond(Z,Y).",
            "l22(X) :- atom(X,br), atom(X,cl).",
            "l23(X) :- atom(X,na), bond(X,Y).",
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

        String example = "1.0 bond(b,b), bond(a,b), bond(b,c), bond(c,a), bond(c,d), bond(c,e), atom(a,c), atom(b,c), atom(c,c), atom(d,cl), atom(d,br).";
        
        TemplateFactory nf = new TemplateFactory();
        MolecularTemplate last = (MolecularTemplate) nf.construct(rules);

        ExampleFactory ef = new ExampleFactory();
        Example e = ef.construct(example);

        Grounder grounder = new Grounder();
        GroundedTemplate b = grounder.groundTemplate(last.last, e);

        Dotter.draw(last.last, "atoms");
        GroundDotter.drawMax(b, "atomsGroundMax");
        GroundDotter.drawAVG(b, "atomsGroundAvg");
    }
}
