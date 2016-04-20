package LRNN;

import discoverer.grounding.evaluation.GroundedTemplate;
import discoverer.grounding.evaluation.Evaluator;
import discoverer.construction.example.Example;
import discoverer.construction.ExampleFactory;
import discoverer.global.Global;
import discoverer.grounding.Grounder;
import discoverer.construction.template.KL;
import discoverer.construction.TemplateFactory;
import discoverer.construction.template.MolecularTemplate;
import java.util.*;
import static org.junit.Assert.*;
import org.junit.*;

public class SolveEvalAVGTest {

    @Before
    public void initRandom() {
        Global.setRg(new Random(1L));
    }

    @Test
    public void testTrivial() {
        Grounder grounder = new Grounder();
        System.out.println("--test - trivial--");
        String[] rules = {
            "nodeLambda_1(X) :- node1(X).",
            "nodeLambda_2(X) :- node2(X).",
            "nodeLambda_3(X) :- node3(X).",
            "edgeLambda_1(Y,Z) :- edge1(Y,Z).",
            "edgeLambda_2(Y,Z) :- edge2(Y,Z).",
            "edgeLambda_3(Y,Z) :- edge3(Y,Z).",
            "0.5 nodeKappa_1(X) :- nodeLambda_1(X).",
            "1.0 nodeKappa_1(X) :- nodeLambda_3(X)",
            "0.5 nodeKappa_2(X) :- nodeLambda_2(X).",
            "0.5 nodeKappa_3(X) :- nodeLambda_3(X).",
            "0.5 edgeKappa_1(Y,Z) :- edgeLambda_1(Y,Z).",
            "0.5 edgeKappa_2(Y,Z) :- edgeLambda_2(Y,Z).",
            "0.5 edgeKappa_3(Y,Z) :- edgeLambda_3(Y,Z).",
            "finalLambda(DMY) :- nodeKappa_1(X), edgeKappa_1(X,Y), nodeKappa_2(Y).", //X=a1,Y=a2
            "0.5 finalKappa :- finalLambda(DMY)."};
        //we have 2 interpretations of the pattern now, node1(a1)-node2(a2) or node3(a1)-node2(a2), but they are just sumed up i both versions
        String[] ex = {
            "1.0 node1(a1), edge1(a1,a2), node2(a2), edge2(a2,a3), node3(a1)",
            "1.0 node1(b1), edge1(b1,b2), node2(b2), edge3(b1,b3), node3(b3)",
            "1.0 node1(a1), edge1(a1,a2), node2(a2)",
            "0.0 node1(c1), edge1(c1,c2), node3(c2)",
            "0.0 node1(d1), edge2(d1,d2), node2(d2)",
            "0.0 node1(c1), edge1(c1,c2), node3(c2), edge1(c2,c2)"
        };

        TemplateFactory nf = new TemplateFactory();
        MolecularTemplate last = (MolecularTemplate) nf.construct(rules);

        for (int i = 0; i < ex.length; i++) {
            ExampleFactory eFactory = new ExampleFactory();
            Example e = eFactory.construct(ex[i]);

            GroundedTemplate bb = null;
            Double dd = null;

            bb = grounder.groundTemplate(last.last, e);
            //assertEquals("Output", 0.6428072668247082, bb.val, 0);
            System.out.println("avg Ball: " + bb.valAvg);
            System.out.println("val Ball: " + bb.valMax);
            //assertEquals("Output", 0.6428072668247082, dd, 0);
            double avg = Evaluator.evaluateAvg(bb);
            double val = Evaluator.evaluateMax(bb);
            System.out.println("EvaluatorAVG: " + avg);
            System.out.println("Evaluator: " + Evaluator.evaluateMax(bb));
            assertEquals(bb.valAvg - bb.valMax, 0, 0.00000000000000000001);
            assertEquals(avg - val, 0, 0.00000000000000000001);
        }
        System.out.println("-------------------");
    }

    @Test
    public void testBasic() {
        Grounder grounder = new Grounder();
        System.out.println("--test-basic--");
        String[] rules = {
            "l1(X) :- atom(d,X), atom(d,X).",
            "l2(X) :- atom(X,X).",
            "1.0 k(X) :- l2(X).",
            "0.5 k(X) :- l1(X).",
            "0.5 k(X) :- l1(X).",};

        String[] ex = {"1.0 atom(z,c), atom(d,cl), atom(d,br).",};

        TemplateFactory nf = new TemplateFactory();
        MolecularTemplate last = (MolecularTemplate) nf.construct(rules);

        ExampleFactory eFactory = new ExampleFactory();
        Example e = eFactory.construct(ex[0]);

        GroundedTemplate bb = grounder.groundTemplate(last.last, e);
        //assertEquals("Output", 0.6428072668247082, bb.val, 0);
        System.out.println(bb.valMax);
        //System.out.println(bb.getLast());
        System.out.println(Evaluator.evaluateMax(bb));
        //bb = Grounder.solve(last, e);
        System.out.println(bb.valAvg);
        System.out.println(Evaluator.evaluateAvg(bb));
        double avg = Evaluator.evaluateAvg(bb);
        double val = Evaluator.evaluateMax(bb);
        System.out.println("EvaluatorAVG: " + avg);
        System.out.println("Evaluator: " + val);
        assertEquals(bb.valAvg - bb.valMax, 0, 0.00000000000000000001);
        assertEquals(avg - val, 0, 0.00000000000000000001);
        System.out.println("---------------");
    }

    @Test
    public void testDifferentAvg() {
        Grounder grounder = new Grounder();
        System.out.println("--test-differentAVG--");
        String[] rules = {
            "nodeLambda_1(X) :- node1(X).",
            "nodeLambda_2(X) :- node2(X).",
            "nodeLambda_3(X) :- node3(X).",
            "edgeLambda_1(Y,Z) :- edge1(Y,Z).",
            "edgeLambda_2(Y,Z) :- edge2(Y,Z).",
            "edgeLambda_3(Y,Z) :- edge3(Y,Z).",
            "0.000000000000005 nodeKappa_1(X) :- nodeLambda_1(X).",
            "100000000000000.0 nodeKappa_1(X) :- nodeLambda_3(X).",
            "0.5 nodeKappa_2(X) :- nodeLambda_2(X).",
            "0.5 nodeKappa_3(X) :- nodeLambda_3(X).",
            "0.5 edgeKappa_1(Y,Z) :- edgeLambda_1(Y,Z).",
            "0.5 edgeKappa_2(Y,Z) :- edgeLambda_2(Y,Z).",
            "0.5 edgeKappa_3(Y,Z) :- edgeLambda_3(Y,Z).",
            //this - 3rd layer(Lambda) is the first interesting layer where averaging can make a difference
            "finalLambda(X,Y) :- nodeKappa_1(X), edgeKappa_1(X,Y), nodeKappa_2(Y).", //X=a1,Y=a2 | 
            "0.5 finalKappa :- finalLambda(X,Y)."};
        // now we have two genuine different patterns to make a difference, node1(a1)-node2(a2) or node3(a3)-node2(a2)
        String[] ex = {
            "1.0 node1(a1), edge1(a1,a2), node2(a2), edge1(a3,a2), node3(a3)",
            "1.0 node1(a1), edge1(a1,a2), node2(a2), edge1(a3,a2), node3(a3)",
            "1.0 node1(a1), edge1(a1,a2), node2(a2), edge1(a3,a2), node3(a3)",
            "0.0 node1(c1), edge1(c1,c2), node3(c2)",
            "0.0 node1(d1), edge2(d1,d2), node2(d2)",
            "0.0 node1(c1), edge1(c1,c2), node3(c2), edge1(c2,c2)"
        };

        TemplateFactory nf = new TemplateFactory();
        MolecularTemplate last = (MolecularTemplate) nf.construct(rules);
        for (int i = 0; i < ex.length; i++) {

            ExampleFactory eFactory = new ExampleFactory();
            Example e = eFactory.construct(ex[i]);

            GroundedTemplate bb = grounder.groundTemplate(last.last, e);
            //assertEquals("Output", 0.6428072668247082, bb.val, 0);
            System.out.println("val:" + bb.valMax);
            //System.out.println(bb.getLast());
            System.out.println(Evaluator.evaluateMax(bb));
            //bb = Grounder.solve(last, e);
            System.out.println("valAVG:" + bb.valAvg);
            System.out.println(Evaluator.evaluateAvg(bb));

            if (bb.valMax != -1) {
                assertFalse(Math.abs(bb.valAvg - bb.valMax) < 0.000000000000001);
            } else {
                assertEquals(bb.valAvg, bb.valMax);
            }
        }
        System.out.println("---------------");
    }

    @Test
    public void testMain() {
        Grounder grounder = new Grounder();
        String[] rules = {
            "l21(X) :- atom(X,cl).",
            "l22(X) :- atom(X,br).",
            "0.1 k21(X) :- l21(X).",
            "0.1 k22(X) :- l21(X).",
            "0.1 k22(X) :- l22(X).",
            "final(X) :- k22(X).",};
        //0.6843863582571729
        //0.6843863582571729
        //0.6590875535191826
        //0.6590875535191826
        //0.7246309752556929
        //0.7246309752556929

        String[] ex = {"1.0 b(a,b), b(b,c), b(c,a), b(c,d), b(c,e), atom(a,c), atom(b,c), atom(c,c), atom(d,cl), atom(d,br).",};

        TemplateFactory nf = new TemplateFactory();
        MolecularTemplate last = (MolecularTemplate) nf.construct(rules);

        ExampleFactory eFactory = new ExampleFactory();
        Example e = eFactory.construct(ex[0]);

        GroundedTemplate bb = grounder.groundTemplate(last.last, e);
        System.out.println(bb.valMax);
        //assertEquals("Output", 0.6428072668247082, bb.val, 0);

        Double dd = Evaluator.evaluateMax(bb);
        System.out.println(dd);
        //assertEquals("Output", 0.6428072668247082, dd, 0);
        //
        bb = grounder.groundTemplate(last.last, e);
        System.out.println(bb.valMax);

        dd = Evaluator.evaluateMax(bb);
        System.out.println(dd);
        //assertEquals("Output", 0.6428072668247082, dd, 0);

    }

    @Test
    public void testMain2() {
        Grounder grounder = new Grounder();
        String[] rules = {
            "l21(X) :- atom(X,cl), atom(X,cl).",
            "l22(X) :- atom(X,br)., atom(cl,cl)",
            "l23(X) :- atom(X,na).",
            "l24(X) :- atom(X,f).",
            "1.1 k21(X) :- l21(X).",
            "1.1 k22(X) :- l21(X).",
            "1.1 k22(X) :- l22(X).",
            "1.1 k22(X) :- l23(X).",
            "1.1 k23(X) :- l23(X).",
            "1.1 k23(X) :- l24(X).",
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
            "0.14 k13(X) :- l13(X).",
            "final(X) :- k11(X).",
            "final(X) :- k12(X).",
            "final(X) :- k13(X).",};

        String[] ex = {"1.0 b(a,b), b(b,c), b(c,a), b(c,d), b(c,e), atom(a,c), atom(b,c), atom(c,c), atom(d,cl), atom(d,br).",};
        //String[] ex = { "1.0 atom(d,cl), atom(d,br).", };

        TemplateFactory nf = new TemplateFactory();
        MolecularTemplate last = (MolecularTemplate) nf.construct(rules);

        ExampleFactory eFactory = new ExampleFactory();
        Example e = eFactory.construct(ex[0]);

        GroundedTemplate bb = grounder.groundTemplate(last.last, e);
        System.out.println(bb.valMax);
        System.out.println(Evaluator.evaluateMax(bb));

        bb = grounder.groundTemplate(last.last, e);
        System.out.println(bb.valMax);
        System.out.println(Evaluator.evaluateMax(bb));

        //System.out.println(bb.getActiveRules());
        //Backpropagation.changeWeights(bb, e);
        //assertEquals("Output", 0.6792981617480565, bb.val, 0);
//        bb = Solvator.solve(last,e);
//        System.out.println(bb.val);
//        System.out.println(Evaluator.evaluate(bb));
//        //assertEquals("Output", 0.6792981617480565, bb.val, 0);
//        bb = Solvator.solve(last,e);
//        System.out.println(bb.val);
//        System.out.println(Evaluator.evaluate(bb));
//        //assertEquals("Output", 0.6792981617480565, bb.val, 0);
//        bb = Solvator.solve(last,e);
//        System.out.println(bb.val);
//        System.out.println(Evaluator.evaluate(bb));
//        //assertEquals("Output", 0.6792981617480565, bb.val, 0);
//        bb = Solvator.solve(last,e);
//        System.out.println(bb.val);
//        System.out.println(Evaluator.evaluate(bb));
//        //assertEquals("Output", 0.6792981617480565, bb.val, 0);
//        bb = Solvator.solve(last,e);
//        System.out.println(bb.val);
//        System.out.println(Evaluator.evaluate(bb));
//        //assertEquals("Output", 0.6792981617480565, bb.val, 0);
//        bb = Solvator.solve(last,e);
//        System.out.println(bb.val);
//        System.out.println(Evaluator.evaluate(bb));
//        //assertEquals("Output", 0.6792981617480565, bb.val, 0);
        /*
         *       for (Map.Entry<KL, Double> ee: bb.getOutputs().entrySet()) {
         *           System.out.println(ee.getKey() + " --> " + ee.getValue());
         *       }
         *
         *       System.out.println(bb.getActives());
         *
         *       System.out.println(bb.val);
         */
        //Double dd = Evaluator.eval(last,bb);
        //System.out.println(dd);
        //System.out.println(dd + " -- " + bb.getValue());
        //assertEquals("Output", 0.6792981617480565, dd, 0);
    }

    @Ignore
    public void testMain3() {
        Grounder grounder = new Grounder();
        String[] rules = {"l21(X) :- atom(X,cl), atom(X,cl).",};

        String[] ex = {"1.0 b(a,b), b(b,c), b(c,a), b(c,d), b(c,e), atom(a,c), atom(b,c), atom(c,c), atom(d,cl), atom(d,br).",};

        TemplateFactory nf = new TemplateFactory();
        MolecularTemplate last = (MolecularTemplate) nf.construct(rules);

        ExampleFactory eFactory = new ExampleFactory();
        Example e = eFactory.construct(ex[0]);

        GroundedTemplate bb = grounder.groundTemplate(last.last, e);
        //assertEquals("Output", 0.7310585786300049, bb.val, 0);
        System.out.println(bb.valMax);

        Double dd = Evaluator.evaluateMax(bb);
        //assertEquals("Output", 0.7310585786300049, dd, 0);
        System.out.println(bb.valMax);
    }

    @Ignore
    public void testMain4() {
        Grounder grounder = new Grounder();
        String[] rules = {"l21(X) :- ato(X,nEvErExiStEdConStanT).",};

        String[] ex = {"1.0 b(a,b), b(b,c), b(c,a), b(c,d), b(c,e), atom(a,c), atom(b,c), atom(c,c), atom(d,cl), atom(d,br).",};

        TemplateFactory nf = new TemplateFactory();
        MolecularTemplate last = (MolecularTemplate) nf.construct(rules);

        ExampleFactory eFactory = new ExampleFactory();
        Example e = eFactory.construct(ex[0]);

        GroundedTemplate bb = grounder.groundTemplate(last.last, e);
        assertEquals("Output", 0.0, bb.valMax, 0);
        System.out.println(bb.valMax);

        Double dd = Evaluator.evaluateMax(bb);
        assertEquals("Output", 0.0, dd, 0);
        System.out.println(dd);
    }

    @Ignore
    public void testMain5() {
        Grounder grounder = new Grounder();
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
            "l11(QQ) :- k21(X), k22(Y).",
            "l12(QQ) :- k21(X), k22(Y).",
            "l13(QQ) :- k22(X), k23(Y).",
            "0.1 k11(QQ) :- l11(Q11).",};

        String[] ex = {"1.0 b(a,b), b(b,c), b(c,a), b(c,d), b(c,e), atom(a,c), atom(b,c), atom(c,c), atom(d,cl), atom(d,br).",};
        TemplateFactory nf = new TemplateFactory();
        MolecularTemplate last = (MolecularTemplate) nf.construct(rules);

        ExampleFactory eFactory = new ExampleFactory();
        Example e = eFactory.construct(ex[0]);

        GroundedTemplate bb = grounder.groundTemplate(last.last, e);
        //assertEquals("Output", 0.6792981617480565, bb.val, 0);
//
//        for (Map.Entry<KL, Double> ee : bb.getActiveRules().entrySet()) {
//            System.out.println(ee.getKey() + " --> " + ee.getValue());
//        }

        //System.out.println(bb.getActives());
        System.out.println(bb.valMax);

        Double dd = Evaluator.evaluateMax(bb);
        System.out.println(dd + " -- " + bb.valMax);
        //assertEquals("Output", 0.6792981617480565, dd, 0);
    }
}
