package oldTestsFromDiscoverer;

import discoverer.construction.TemplateFactory;
import discoverer.construction.template.KL;
import discoverer.construction.ExampleFactory;
import discoverer.construction.example.Example;
import discoverer.construction.template.MolecularTemplate;
import discoverer.global.Global;
import discoverer.grounding.evaluation.Evaluator;
import discoverer.grounding.evaluation.GroundedTemplate;
import discoverer.grounding.Grounder;
import java.util.*;
import static org.junit.Assert.*;
import org.junit.*;

public class SolveAndEvalTest {

    @Before
    public void initRandom() {
        Global.setRg(new Random(1L));
    }

    @Ignore
    public void test01() {
        String[] rules = {
            "atomLambda_1(X) :- o(X).",
            "atomLambda_2(X) :- n(X).",
            "atomLambda_3(X) :- f(X).",
            "atomLambda_4(X) :- p(X).",
            "atomLambda_5(X) :- sn(X).",
            "atomLambda_6(X) :- br(X).",
            "atomLambda_7(X) :- cu(X).",
            "atomLambda_8(X) :- c(X).",
            "atomLambda_9(X) :- k(X).",
            "atomLambda_10(X) :- cl(X).",
            "atomLambda_11(X) :- i(X).",
            "atomLambda_12(X) :- na(X).",
            "atomLambda_13(X) :- ca(X).",
            "atomLambda_14(X) :- in(X).",
            "atomLambda_15(X) :- h(X).",
            "atomLambda_16(X) :- zn(X).",
            "atomLambda_17(X) :- pb(X).",
            "atomLambda_18(X) :- s(X).",
            "atomLambda_19(X) :- ba(X).",
            "bondLambda_1(X) :- 2(X).",
            "bondLambda_2(X) :- 3(X).",
            "bondLambda_3(X) :- 7(X).",
            "bondLambda_4(X) :- 1(X).",
            "0.1 atomKappa_1(X) :- atomLambda_1(X).",
            "0.1 atomKappa_1(X) :- atomLambda_2(X).",
            "0.1 atomKappa_1(X) :- atomLambda_3(X).",
            "0.1 atomKappa_1(X) :- atomLambda_4(X).",
            "0.1 atomKappa_1(X) :- atomLambda_5(X).",
            "0.1 atomKappa_1(X) :- atomLambda_6(X).",
            "0.1 atomKappa_1(X) :- atomLambda_7(X).",
            "0.1 atomKappa_1(X) :- atomLambda_8(X).",
            "0.1 atomKappa_1(X) :- atomLambda_9(X).",
            "0.1 atomKappa_1(X) :- atomLambda_10(X).",
            "0.1 atomKappa_1(X) :- atomLambda_11(X).",
            "0.1 atomKappa_1(X) :- atomLambda_12(X).",
            "0.1 atomKappa_1(X) :- atomLambda_13(X).",
            "0.1 atomKappa_1(X) :- atomLambda_14(X).",
            "0.1 atomKappa_1(X) :- atomLambda_15(X).",
            "0.1 atomKappa_1(X) :- atomLambda_16(X).",
            "0.1 atomKappa_1(X) :- atomLambda_17(X).",
            "0.1 atomKappa_1(X) :- atomLambda_18(X).",
            "0.1 atomKappa_1(X) :- atomLambda_19(X).",
            "0.1 atomKappa_2(X) :- atomLambda_1(X).",
            "0.1 atomKappa_2(X) :- atomLambda_2(X).",
            "0.1 atomKappa_2(X) :- atomLambda_3(X).",
            "0.1 atomKappa_2(X) :- atomLambda_4(X).",
            "0.1 atomKappa_2(X) :- atomLambda_5(X).",
            "0.1 atomKappa_2(X) :- atomLambda_6(X).",
            "0.1 atomKappa_2(X) :- atomLambda_7(X).",
            "0.1 atomKappa_2(X) :- atomLambda_8(X).",
            "0.1 atomKappa_2(X) :- atomLambda_9(X).",
            "0.1 atomKappa_2(X) :- atomLambda_10(X).",
            "0.1 atomKappa_2(X) :- atomLambda_11(X).",
            "0.1 atomKappa_2(X) :- atomLambda_12(X).",
            "0.1 atomKappa_2(X) :- atomLambda_13(X).",
            "0.1 atomKappa_2(X) :- atomLambda_14(X).",
            "0.1 atomKappa_2(X) :- atomLambda_15(X).",
            "0.1 atomKappa_2(X) :- atomLambda_16(X).",
            "0.1 atomKappa_2(X) :- atomLambda_17(X).",
            "0.1 atomKappa_2(X) :- atomLambda_18(X).",
            "0.1 atomKappa_2(X) :- atomLambda_19(X).",
            "0.1 atomKappa_3(X) :- atomLambda_1(X).",
            "0.1 atomKappa_3(X) :- atomLambda_2(X).",
            "0.1 atomKappa_3(X) :- atomLambda_3(X).",
            "0.1 atomKappa_3(X) :- atomLambda_4(X).",
            "0.1 atomKappa_3(X) :- atomLambda_5(X).",
            "0.1 atomKappa_3(X) :- atomLambda_6(X).",
            "0.1 atomKappa_3(X) :- atomLambda_7(X).",
            "0.1 atomKappa_3(X) :- atomLambda_8(X).",
            "0.1 atomKappa_3(X) :- atomLambda_9(X).",
            "0.1 atomKappa_3(X) :- atomLambda_10(X).",
            "0.1 atomKappa_3(X) :- atomLambda_11(X).",
            "0.1 atomKappa_3(X) :- atomLambda_12(X).",
            "0.1 atomKappa_3(X) :- atomLambda_13(X).",
            "0.1 atomKappa_3(X) :- atomLambda_14(X).",
            "0.1 atomKappa_3(X) :- atomLambda_15(X).",
            "0.1 atomKappa_3(X) :- atomLambda_16(X).",
            "0.1 atomKappa_3(X) :- atomLambda_17(X).",
            "0.1 atomKappa_3(X) :- atomLambda_18(X).",
            "0.1 atomKappa_3(X) :- atomLambda_19(X).",
            "0.1 atomKappa_4(X) :- atomLambda_1(X).",
            "0.1 atomKappa_4(X) :- atomLambda_2(X).",
            "0.1 atomKappa_4(X) :- atomLambda_3(X).",
            "0.1 atomKappa_4(X) :- atomLambda_4(X).",
            "0.1 atomKappa_4(X) :- atomLambda_5(X).",
            "0.1 atomKappa_4(X) :- atomLambda_6(X).",
            "0.1 atomKappa_4(X) :- atomLambda_7(X).",
            "0.1 atomKappa_4(X) :- atomLambda_8(X).",
            "0.1 atomKappa_4(X) :- atomLambda_9(X).",
            "0.1 atomKappa_4(X) :- atomLambda_10(X).",
            "0.1 atomKappa_4(X) :- atomLambda_11(X).",
            "0.1 atomKappa_4(X) :- atomLambda_12(X).",
            "0.1 atomKappa_4(X) :- atomLambda_13(X).",
            "0.1 atomKappa_4(X) :- atomLambda_14(X).",
            "0.1 atomKappa_4(X) :- atomLambda_15(X).",
            "0.1 atomKappa_4(X) :- atomLambda_16(X).",
            "0.1 atomKappa_4(X) :- atomLambda_17(X).",
            "0.1 atomKappa_4(X) :- atomLambda_18(X).",
            "0.1 atomKappa_4(X) :- atomLambda_19(X).",
            "0.1 bondKappa_1(X) :- bondLambda_1(X).",
            "0.1 bondKappa_1(X) :- bondLambda_2(X).",
            "0.1 bondKappa_1(X) :- bondLambda_3(X).",
            "0.1 bondKappa_1(X) :- bondLambda_4(X).",
            "0.1 bondKappa_2(X) :- bondLambda_1(X).",
            "0.1 bondKappa_2(X) :- bondLambda_2(X).",
            "0.1 bondKappa_2(X) :- bondLambda_3(X).",
            "0.1 bondKappa_2(X) :- bondLambda_4(X).",
            "0.1 bondKappa_3(X) :- bondLambda_1(X).",
            "0.1 bondKappa_3(X) :- bondLambda_2(X).",
            "0.1 bondKappa_3(X) :- bondLambda_3(X).",
            "0.1 bondKappa_3(X) :- bondLambda_4(X).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            "lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_1(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_2(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_3(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_4(Y).",
            //"lambda(D3) :- atomKappa_4(X), bond(X,Y,B1), bondKappa_3(B1), atomKappa_4(Y), bond(Y,Z,B2), bondKappa_3(B2), atomKappa_4(Y).",

            "1.0 finalKappa(D1) :- lambda(D2).",};

        String[] ex = {"1.0 bond(tr105_10, tr105_8, 689), c(tr105_10), c(tr105_8), 7(689), bond(tr105_8, tr105_10, 689), bond(tr105_14, tr105_4, 690), h(tr105_14), c(tr105_4), 1(690), bond(tr105_4, tr105_14, 690), bond(tr105_8, tr105_6, 691), c(tr105_6), 7(691), bond(tr105_6, tr105_8, 691), bond(tr105_7, tr105_6, 692), n(tr105_7), 1(692), bond(tr105_6, tr105_7, 692), bond(tr105_9, tr105_8, 693), c(tr105_9), 1(693), bond(tr105_8, tr105_9, 693), bond(tr105_19, tr105_10, 694), h(tr105_19), 1(694), bond(tr105_10, tr105_19, 694), bond(tr105_13, tr105_2, 695), h(tr105_13), c(tr105_2), 1(695), bond(tr105_2, tr105_13, 695), bond(tr105_11, tr105_2, 696), h(tr105_11), 1(696), bond(tr105_2, tr105_11, 696), bond(tr105_12, tr105_2, 697), h(tr105_12), 1(697), bond(tr105_2, tr105_12, 697), bond(tr105_4, tr105_3, 698), c(tr105_3), 7(698), bond(tr105_3, tr105_4, 698), bond(tr105_5, tr105_4, 699), c(tr105_5), 7(699), bond(tr105_4, tr105_5, 699), bond(tr105_6, tr105_5, 700), 7(700), bond(tr105_5, tr105_6, 700), bond(tr105_18, tr105_9, 701), h(tr105_18), 1(701), bond(tr105_9, tr105_18, 701), bond(tr105_16, tr105_9, 702), h(tr105_16), 1(702), bond(tr105_9, tr105_16, 702), bond(tr105_17, tr105_9, 703), h(tr105_17), 1(703), bond(tr105_9, tr105_17, 703), bond(tr105_21, tr105_7, 704), h(tr105_21), 1(704), bond(tr105_7, tr105_21, 704), bond(tr105_20, tr105_7, 705), h(tr105_20), 1(705), bond(tr105_7, tr105_20, 705), bond(tr105_15, tr105_5, 706), h(tr105_15), 1(706), bond(tr105_5, tr105_15, 706), bond(tr105_3, tr105_1, 707), o(tr105_1), 1(707), bond(tr105_1, tr105_3, 707), bond(tr105_2, tr105_1, 708), 1(708), bond(tr105_1, tr105_2, 708), bond(tr105_10, tr105_3, 709), 7(709), bond(tr105_3, tr105_10, 709).",};
        //String[] ex = { "1.0 bond(tr000_4, tr000_2, 0), cl(tr000_4), c(tr000_2), 1(0), bond(tr000_2, tr000_4, 0), bond(tr000_5, tr000_2, 1), h(tr000_5), 1(1), bond(tr000_2, tr000_5, 1), bond(tr000_3, tr000_2, 2), cl(tr000_3), 1(2), bond(tr000_2, tr000_3, 2), bond(tr000_2, tr000_1, 3), cl(tr000_1), 1(3), bond(tr000_1, tr000_2, 3).", };
        //OUT  =  .7246309752556929

        TemplateFactory nf = new TemplateFactory();
        MolecularTemplate last = nf.construct(rules);

        ExampleFactory eFactory = new ExampleFactory();
        Example e = eFactory.construct(ex[0]);

        GroundedTemplate bb = null;
        Double dd = null;
        //while (true) {
        bb = Grounder.groundTemplate(last.last, e);
        //assertEquals("Output", 0.6428072668247082, bb.val, 0);
        System.out.println(bb.valMax);
        //assertEquals("Output", 0.6428072668247082, dd, 0);
        System.out.println(Evaluator.evaluateMax(bb));
            //System.out.println(bb.getActiveRules());
        //Backpropagation.changeWeights(bb, e);

        //while (true) {
        //bb = Solvator.solve(last,e);
        //assertEquals("Output", 0.6428072668247082, bb.val, 0);
        //System.out.println(bb.val);
        //System.out.println(bb.getElOutputs());
        //System.out.println(bb.getOutputs());
        //}
        //}
        //dd = Evaluator.eval(last,bb);
        //assertEquals("Output", 0.6428072668247082, dd, 0);
        //System.out.println(dd);
        //bb = Solvator.solve(last,e);
        //System.out.println(bb.val);
        //dd = Evaluator.eval(last,bb);
        //System.out.println(dd);
    }

    @Ignore
    public void test000() {
        String[] rules = {
            "atomLambda_1(X) :- o(X).",
            "atomLambda_2(X) :- n(X).",
            "atomLambda_3(X) :- f(X).",
            "atomLambda_4(X) :- p(X).",
            "atomLambda_5(X) :- sn(X).",
            "atomLambda_6(X) :- br(X).",
            "atomLambda_7(X) :- cu(X).",
            "atomLambda_8(X) :- c(X).",
            "atomLambda_9(X) :- k(X).",
            "atomLambda_10(X) :- cl(X).",
            "atomLambda_11(X) :- i(X).",
            "atomLambda_12(X) :- na(X).",
            "atomLambda_13(X) :- ca(X).",
            "atomLambda_14(X) :- in(X).",
            "atomLambda_15(X) :- h(X).",
            "atomLambda_16(X) :- zn(X).",
            "atomLambda_17(X) :- pb(X).",
            "atomLambda_18(X) :- s(X).",
            "atomLambda_19(X) :- ba(X).",
            "bondLambda_1(X) :- 2(X).",
            "bondLambda_2(X) :- 3(X).",
            "bondLambda_3(X) :- 7(X).",
            "bondLambda_4(X) :- 1(X).",
            "0.0 bondKappa_1(X) :-bondLambda_1(X).",
            "0.0 bondKappa_1(X) :-bondLambda_2(X).",
            "0.0 bondKappa_1(X) :-bondLambda_3(X).",
            "0.0 bondKappa_1(X) :-bondLambda_4(X).",
            "0.0 bondKappa_2(X) :-bondLambda_1(X).",
            "0.0 bondKappa_2(X) :-bondLambda_2(X).",
            "0.0 bondKappa_2(X) :-bondLambda_3(X).",
            "0.0 bondKappa_2(X) :-bondLambda_4(X).",
            "0.0 bondKappa_3(X) :-bondLambda_1(X).",
            "0.0 bondKappa_3(X) :-bondLambda_2(X).",
            "0.0 bondKappa_3(X) :-bondLambda_3(X).",
            "0.0 bondKappa_3(X) :-bondLambda_4(X).",
            "0.0 atomKappa_1(X) :- atomLambda_1(X).",
            "0.0 atomKappa_1(X) :- atomLambda_2(X).",
            "0.0 atomKappa_1(X) :- atomLambda_3(X).",
            "0.0 atomKappa_1(X) :- atomLambda_4(X).",
            "0.0 atomKappa_1(X) :- atomLambda_5(X).",
            "0.0 atomKappa_1(X) :- atomLambda_6(X).",
            "0.0 atomKappa_1(X) :- atomLambda_7(X).",
            "0.0 atomKappa_1(X) :- atomLambda_8(X).",
            "0.0 atomKappa_1(X) :- atomLambda_9(X).",
            "0.0 atomKappa_1(X) :- atomLambda_10(X).",
            "0.0 atomKappa_1(X) :- atomLambda_11(X).",
            "0.0 atomKappa_1(X) :- atomLambda_12(X).",
            "0.0 atomKappa_1(X) :- atomLambda_13(X).",
            "0.0 atomKappa_1(X) :- atomLambda_14(X).",
            "0.0 atomKappa_1(X) :- atomLambda_15(X).",
            "0.0 atomKappa_1(X) :- atomLambda_16(X).",
            "0.0 atomKappa_1(X) :- atomLambda_17(X).",
            "0.0 atomKappa_1(X) :- atomLambda_18(X).",
            "0.0 atomKappa_1(X) :- atomLambda_19(X).",
            "0.0 atomKappa_2(X) :- atomLambda_1(X).",
            "0.0 atomKappa_2(X) :- atomLambda_2(X).",
            "0.0 atomKappa_2(X) :- atomLambda_3(X).",
            "0.0 atomKappa_2(X) :- atomLambda_4(X).",
            "0.0 atomKappa_2(X) :- atomLambda_5(X).",
            "0.0 atomKappa_2(X) :- atomLambda_6(X).",
            "0.0 atomKappa_2(X) :- atomLambda_7(X).",
            "0.0 atomKappa_2(X) :- atomLambda_8(X).",
            "0.0 atomKappa_2(X) :- atomLambda_9(X).",
            "0.0 atomKappa_2(X) :- atomLambda_10(X).",
            "0.0 atomKappa_2(X) :- atomLambda_11(X).",
            "0.0 atomKappa_2(X) :- atomLambda_12(X).",
            "0.0 atomKappa_2(X) :- atomLambda_13(X).",
            "0.0 atomKappa_2(X) :- atomLambda_14(X).",
            "0.0 atomKappa_2(X) :- atomLambda_15(X).",
            "0.0 atomKappa_2(X) :- atomLambda_16(X).",
            "0.0 atomKappa_2(X) :- atomLambda_17(X).",
            "0.0 atomKappa_2(X) :- atomLambda_18(X).",
            "0.0 atomKappa_2(X) :- atomLambda_19(X).",
            "0.0 atomKappa_3(X) :- atomLambda_1(X).",
            "0.0 atomKappa_3(X) :- atomLambda_2(X).",
            "0.0 atomKappa_3(X) :- atomLambda_3(X).",
            "0.0 atomKappa_3(X) :- atomLambda_4(X).",
            "0.0 atomKappa_3(X) :- atomLambda_5(X).",
            "0.0 atomKappa_3(X) :- atomLambda_6(X).",
            "0.0 atomKappa_3(X) :- atomLambda_7(X).",
            "0.0 atomKappa_3(X) :- atomLambda_8(X).",
            "0.0 atomKappa_3(X) :- atomLambda_9(X).",
            "0.0 atomKappa_3(X) :- atomLambda_10(X).",
            "0.0 atomKappa_3(X) :- atomLambda_11(X).",
            "0.0 atomKappa_3(X) :- atomLambda_12(X).",
            "0.0 atomKappa_3(X) :- atomLambda_13(X).",
            "0.0 atomKappa_3(X) :- atomLambda_14(X).",
            "0.0 atomKappa_3(X) :- atomLambda_15(X).",
            "0.0 atomKappa_3(X) :- atomLambda_16(X).",
            "0.0 atomKappa_3(X) :- atomLambda_17(X).",
            "0.0 atomKappa_3(X) :- atomLambda_18(X).",
            "0.0 atomKappa_3(X) :- atomLambda_19(X).",
            "lambda_0(D) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            "lambda_1(D) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            "lambda_2(D) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            "lambda_3(D) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            "lambda_4(D) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            "lambda_5(D) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            "lambda_6(D) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            "lambda_7(D) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            "lambda_8(D) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            "lambda_9(D) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            "lambda_10(D) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            "lambda_11(D) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            "lambda_12(D) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            "lambda_13(D) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            "lambda_14(D) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            "lambda_15(D) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            "lambda_16(D) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            "lambda_17(D) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            "lambda_18(D) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            "lambda_19(D) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            "lambda_20(D) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            "lambda_21(D) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            "lambda_22(D) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            "lambda_23(D) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            "lambda_24(D) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            "lambda_25(D) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            "lambda_26(D) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            "lambda_27(D) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            "lambda_28(D) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            "lambda_29(D) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            "lambda_30(D) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            "lambda_31(D) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            "lambda_32(D) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            "lambda_33(D) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            "lambda_34(D) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            "lambda_35(D) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            "lambda_36(D) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            "lambda_37(D) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            "lambda_38(D) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            "lambda_39(D) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            "lambda_40(D) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            "lambda_41(D) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            "lambda_42(D) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            "lambda_43(D) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            "lambda_44(D) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            "lambda_45(D) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            "lambda_46(D) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            "lambda_47(D) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            "lambda_48(D) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            "lambda_49(D) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            "lambda_50(D) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            "lambda_51(D) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            "lambda_52(D) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            "lambda_53(D) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            "lambda_54(D) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            "lambda_55(D) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            "lambda_56(D) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            "lambda_57(D) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            "lambda_58(D) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            "lambda_59(D) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            "lambda_60(D) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            "lambda_61(D) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            "lambda_62(D) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            "lambda_63(D) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            "lambda_64(D) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            "lambda_65(D) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            "lambda_66(D) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            "lambda_67(D) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            "lambda_68(D) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            "lambda_69(D) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            "lambda_70(D) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            "lambda_71(D) :- atomKappa_2(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            "lambda_72(D) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            "lambda_73(D) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            "lambda_74(D) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            "lambda_75(D) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            "lambda_76(D) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            "lambda_77(D) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            "lambda_78(D) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            "lambda_79(D) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            "lambda_80(D) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            "lambda_81(D) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            "lambda_82(D) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            "lambda_83(D) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            "lambda_84(D) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            "lambda_85(D) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            "lambda_86(D) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            "lambda_87(D) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            "lambda_88(D) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            "lambda_89(D) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            "lambda_90(D) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            "lambda_91(D) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            "lambda_92(D) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            "lambda_93(D) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            "lambda_94(D) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            "lambda_95(D) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_2(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            "lambda_96(D) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            "lambda_97(D) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            "lambda_98(D) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).",
            "lambda_99(D) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_1(Y).",
            "lambda_100(D) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            "lambda_101(D) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            "lambda_102(D) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_2(Y).",
            "lambda_103(D) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_2(Y).",
            "lambda_104(D) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            "lambda_105(D) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_1(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            "lambda_106(D) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_3(Y).",
            "lambda_107(D) :- atomKappa_3(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_3(Y), bond(Y,Z,B2), bondKappa_2(B2), atomKappa_3(Y).",
            "0.0 finalKappa(D1) :- lambda_0(DD).",
            "0.0 finalKappa(D1) :- lambda_1(DD).",
            "0.0 finalKappa(D1) :- lambda_2(DD).",
            "0.0 finalKappa(D1) :- lambda_3(DD).",
            "0.0 finalKappa(D1) :- lambda_4(DD).",
            "0.0 finalKappa(D1) :- lambda_5(DD).",
            "0.0 finalKappa(D1) :- lambda_6(DD).",
            "0.0 finalKappa(D1) :- lambda_7(DD).",
            "0.0 finalKappa(D1) :- lambda_8(DD).",
            "0.0 finalKappa(D1) :- lambda_9(DD).",
            "0.0 finalKappa(D1) :- lambda_10(DD).",
            "0.0 finalKappa(D1) :- lambda_11(DD).",
            "0.0 finalKappa(D1) :- lambda_12(DD).",
            "0.0 finalKappa(D1) :- lambda_13(DD).",
            "0.0 finalKappa(D1) :- lambda_14(DD).",
            "0.0 finalKappa(D1) :- lambda_15(DD).",
            "0.0 finalKappa(D1) :- lambda_16(DD).",
            "0.0 finalKappa(D1) :- lambda_17(DD).",
            "0.0 finalKappa(D1) :- lambda_18(DD).",
            "0.0 finalKappa(D1) :- lambda_19(DD).",
            "0.0 finalKappa(D1) :- lambda_20(DD).",
            "0.0 finalKappa(D1) :- lambda_21(DD).",
            "0.0 finalKappa(D1) :- lambda_22(DD).",
            "0.0 finalKappa(D1) :- lambda_23(DD).",
            "0.0 finalKappa(D1) :- lambda_24(DD).",
            "0.0 finalKappa(D1) :- lambda_25(DD).",
            "0.0 finalKappa(D1) :- lambda_26(DD).",
            "0.0 finalKappa(D1) :- lambda_27(DD).",
            "0.0 finalKappa(D1) :- lambda_28(DD).",
            "0.0 finalKappa(D1) :- lambda_29(DD).",
            "0.0 finalKappa(D1) :- lambda_30(DD).",
            "0.0 finalKappa(D1) :- lambda_31(DD).",
            "0.0 finalKappa(D1) :- lambda_32(DD).",
            "0.0 finalKappa(D1) :- lambda_33(DD).",
            "0.0 finalKappa(D1) :- lambda_34(DD).",
            "0.0 finalKappa(D1) :- lambda_35(DD).",
            "0.0 finalKappa(D1) :- lambda_36(DD).",
            "0.0 finalKappa(D1) :- lambda_37(DD).",
            "0.0 finalKappa(D1) :- lambda_38(DD).",
            "0.0 finalKappa(D1) :- lambda_39(DD).",
            "0.0 finalKappa(D1) :- lambda_40(DD).",
            "0.0 finalKappa(D1) :- lambda_41(DD).",
            "0.0 finalKappa(D1) :- lambda_42(DD).",
            "0.0 finalKappa(D1) :- lambda_43(DD).",
            "0.0 finalKappa(D1) :- lambda_44(DD).",
            "0.0 finalKappa(D1) :- lambda_45(DD).",
            "0.0 finalKappa(D1) :- lambda_46(DD).",
            "0.0 finalKappa(D1) :- lambda_47(DD).",
            "0.0 finalKappa(D1) :- lambda_48(DD).",
            "0.0 finalKappa(D1) :- lambda_49(DD).",
            "0.0 finalKappa(D1) :- lambda_50(DD).",
            "0.0 finalKappa(D1) :- lambda_51(DD).",
            "0.0 finalKappa(D1) :- lambda_52(DD).",
            "0.0 finalKappa(D1) :- lambda_53(DD).",
            "0.0 finalKappa(D1) :- lambda_54(DD).",
            "0.0 finalKappa(D1) :- lambda_55(DD).",
            "0.0 finalKappa(D1) :- lambda_56(DD).",
            "0.0 finalKappa(D1) :- lambda_57(DD).",
            "0.0 finalKappa(D1) :- lambda_58(DD).",
            "0.0 finalKappa(D1) :- lambda_59(DD).",
            "0.0 finalKappa(D1) :- lambda_60(DD).",
            "0.0 finalKappa(D1) :- lambda_61(DD).",
            "0.0 finalKappa(D1) :- lambda_62(DD).",
            "0.0 finalKappa(D1) :- lambda_63(DD).",
            "0.0 finalKappa(D1) :- lambda_64(DD).",
            "0.0 finalKappa(D1) :- lambda_65(DD).",
            "0.0 finalKappa(D1) :- lambda_66(DD).",
            "0.0 finalKappa(D1) :- lambda_67(DD).",
            "0.0 finalKappa(D1) :- lambda_68(DD).",
            "0.0 finalKappa(D1) :- lambda_69(DD).",
            "0.0 finalKappa(D1) :- lambda_70(DD).",
            "0.0 finalKappa(D1) :- lambda_71(DD).",
            "0.0 finalKappa(D1) :- lambda_72(DD).",
            "0.0 finalKappa(D1) :- lambda_73(DD).",
            "0.0 finalKappa(D1) :- lambda_74(DD).",
            "0.0 finalKappa(D1) :- lambda_75(DD).",
            "0.0 finalKappa(D1) :- lambda_76(DD).",
            "0.0 finalKappa(D1) :- lambda_77(DD).",
            "0.0 finalKappa(D1) :- lambda_78(DD).",
            "0.0 finalKappa(D1) :- lambda_79(DD).",
            "0.0 finalKappa(D1) :- lambda_80(DD).",
            "0.0 finalKappa(D1) :- lambda_81(DD).",
            "0.0 finalKappa(D1) :- lambda_82(DD).",
            "0.0 finalKappa(D1) :- lambda_83(DD).",
            "0.0 finalKappa(D1) :- lambda_84(DD).",
            "0.0 finalKappa(D1) :- lambda_85(DD).",
            "0.0 finalKappa(D1) :- lambda_86(DD).",
            "0.0 finalKappa(D1) :- lambda_87(DD).",
            "0.0 finalKappa(D1) :- lambda_88(DD).",
            "0.0 finalKappa(D1) :- lambda_89(DD).",
            "0.0 finalKappa(D1) :- lambda_90(DD).",
            "0.0 finalKappa(D1) :- lambda_91(DD).",
            "0.0 finalKappa(D1) :- lambda_92(DD).",
            "0.0 finalKappa(D1) :- lambda_93(DD).",
            "0.0 finalKappa(D1) :- lambda_94(DD).",
            "0.0 finalKappa(D1) :- lambda_95(DD).",
            "0.0 finalKappa(D1) :- lambda_96(DD).",
            "0.0 finalKappa(D1) :- lambda_97(DD).",
            "0.0 finalKappa(D1) :- lambda_98(DD).",
            "0.0 finalKappa(D1) :- lambda_99(DD).",
            "0.0 finalKappa(D1) :- lambda_100(DD).",
            "0.0 finalKappa(D1) :- lambda_101(DD).",
            "0.0 finalKappa(D1) :- lambda_102(DD).",
            "0.0 finalKappa(D1) :- lambda_103(DD).",
            "0.0 finalKappa(D1) :- lambda_104(DD).",
            "0.0 finalKappa(D1) :- lambda_105(DD).",
            "0.0 finalKappa(D1) :- lambda_106(DD).",
            "0.0 finalKappa(D1) :- lambda_107(DD).",};

        String[] ex = {
            "1.0 bond(tr000_4, tr000_2, 0), cl(tr000_4), c(tr000_2), 1(0), bond(tr000_2, tr000_4, 0), bond(tr000_5, tr000_2, 1), h(tr000_5), 1(1), bond(tr000_2, tr000_5, 1), bond(tr000_3, tr000_2, 2), cl(tr000_3), 1(2), bond(tr000_2, tr000_3, 2), bond(tr000_2, tr000_1, 3), cl(tr000_1), 1(3), bond(tr000_1, tr000_2, 3).",};

        TemplateFactory nf = new TemplateFactory();
        MolecularTemplate last = nf.construct(rules);

        ExampleFactory eFactory = new ExampleFactory();
        Example e = eFactory.construct(ex[0]);

        GroundedTemplate bb = Grounder.groundTemplate(last.last, e);
        //assertEquals("Output", 0.6428072668247082, bb.val, 0);
        System.out.println(bb.valMax);
        //System.out.println(bb.getLast());
        System.out.println(Evaluator.evaluateMax(bb));
        //System.out.println(bb.getActiveRules());
        //Backpropagation.changeWeights(bb, e);
        //Double dd = Evaluator.eval(last, bb);
        //assertEquals("Output", 0.6428072668247082, dd, 0);
        //System.out.println(dd);
        //dd = Evaluator.eval(last,bb);
        //assertEquals("Output", 0.6428072668247082, dd, 0);
        //System.out.println(dd);

        //bb = Solvator.solve(last,e);
        //System.out.println(bb.val);
        //dd = Evaluator.eval(last,bb);
        //System.out.println(dd);

        /*
         0.6772272948540424
         0.6772272948540424
         0.6315736437304025
         0.6315736437304025
         0.6315736437304025
         0.6317249660068422
         0.6317249660068422
         0.551918040522789
         0.551918040522789
         */
    }

    @Test
    public void test0() {
        String[] rules = {
            "l1(X) :- atom(d,X), atom(d,X).",
            "l2(X) :- atom(X,X).",
            "1.0 k(X) :- l2(X).",
            "0.5 k(X) :- l1(X).",
            "0.5 k(X) :- l1(X).",};

        String[] ex = {"1.0 atom(z,c), atom(d,cl), atom(d,br).",};

        TemplateFactory nf = new TemplateFactory();
        MolecularTemplate last = nf.construct(rules);

        ExampleFactory eFactory = new ExampleFactory();
        Example e = eFactory.construct(ex[0]);

        GroundedTemplate bb = Grounder.groundTemplate(last.last, e);
        //assertEquals("Output", 0.6428072668247082, bb.val, 0);
        System.out.println(bb.valMax);
        //System.out.println(bb.getLast());
        System.out.println(Evaluator.evaluateMax(bb));
        bb = Grounder.groundTemplate(last.last, e);
        System.out.println(bb.valMax);
        System.out.println(Evaluator.evaluateMax(bb));
        //System.out.println(bb.getActiveRules());
        //Backpropagation.changeWeights(bb, e);
        //Double dd = Evaluator.eval(last, bb);
        //assertEquals("Output", 0.6428072668247082, dd, 0);
        //System.out.println(dd);
        //dd = Evaluator.eval(last,bb);
        //assertEquals("Output", 0.6428072668247082, dd, 0);
        //System.out.println(dd);

        //bb = Solvator.solve(last,e);
        //System.out.println(bb.val);
        //dd = Evaluator.eval(last,bb);
        //System.out.println(dd);

        /*
         0.6772272948540424
         0.6772272948540424
         0.6315736437304025
         0.6315736437304025
         0.6315736437304025
         0.6317249660068422
         0.6317249660068422
         0.551918040522789
         0.551918040522789
         */
    }

    @Test
    public void testMain() {
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
        MolecularTemplate last = nf.construct(rules);

        ExampleFactory eFactory = new ExampleFactory();
        Example e = eFactory.construct(ex[0]);

        GroundedTemplate bb = Grounder.groundTemplate(last.last, e);
        System.out.println(bb.valMax);
        //assertEquals("Output", 0.6428072668247082, bb.val, 0);

        Double dd = Evaluator.evaluateMax(bb);
        System.out.println(dd);
        //assertEquals("Output", 0.6428072668247082, dd, 0);
        //
        bb = Grounder.groundTemplate(last.last, e);
        System.out.println(bb.valMax);

        dd = Evaluator.evaluateMax(bb);
        System.out.println(dd);
        //assertEquals("Output", 0.6428072668247082, dd, 0);

    }

    @Test
    public void testMain2() {
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
        MolecularTemplate last = nf.construct(rules);

        ExampleFactory eFactory = new ExampleFactory();
        Example e = eFactory.construct(ex[0]);

        GroundedTemplate bb = Grounder.groundTemplate(last.last, e);
        System.out.println(bb.valMax);
        System.out.println(Evaluator.evaluateMax(bb));

        bb = Grounder.groundTemplate(last.last, e);
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
        String[] rules = {"l21(X) :- atom(X,cl), atom(X,cl).",};

        String[] ex = {"1.0 b(a,b), b(b,c), b(c,a), b(c,d), b(c,e), atom(a,c), atom(b,c), atom(c,c), atom(d,cl), atom(d,br).",};

        TemplateFactory nf = new TemplateFactory();
        MolecularTemplate last = nf.construct(rules);

        ExampleFactory eFactory = new ExampleFactory();
        Example e = eFactory.construct(ex[0]);

        GroundedTemplate bb = Grounder.groundTemplate(last.last, e);
        //assertEquals("Output", 0.7310585786300049, bb.val, 0);
        System.out.println(bb.valMax);

        Double dd = Evaluator.evaluateMax(bb);
        //assertEquals("Output", 0.7310585786300049, dd, 0);
        System.out.println(bb.valMax);
    }

    @Ignore
    public void testMain4() {
        String[] rules = {"l21(X) :- ato(X,nEvErExiStEdConStanT).",};

        String[] ex = {"1.0 b(a,b), b(b,c), b(c,a), b(c,d), b(c,e), atom(a,c), atom(b,c), atom(c,c), atom(d,cl), atom(d,br).",};

        TemplateFactory nf = new TemplateFactory();
        MolecularTemplate last = nf.construct(rules);

        ExampleFactory eFactory = new ExampleFactory();
        Example e = eFactory.construct(ex[0]);

        GroundedTemplate bb = Grounder.groundTemplate(last.last, e);
        assertEquals("Output", 0.0, bb.valMax, 0);
        System.out.println(bb.valMax);

        Double dd = Evaluator.evaluateMax(bb);
        assertEquals("Output", 0.0, dd, 0);
        System.out.println(dd);
    }

    @Ignore
    public void testMain5() {
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
        MolecularTemplate last = nf.construct(rules);

        ExampleFactory eFactory = new ExampleFactory();
        Example e = eFactory.construct(ex[0]);

        GroundedTemplate bb = Grounder.groundTemplate(last.last, e);
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
