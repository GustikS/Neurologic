package oldTestsFromDiscoverer;

import discoverer.construction.TemplateFactory;
import discoverer.construction.template.KL;
import discoverer.construction.ExampleFactory;
import discoverer.construction.example.Example;
import discoverer.construction.template.MolecularTemplate;
import discoverer.global.FileToStringList;
import discoverer.global.Global;
import discoverer.grounding.evaluation.GroundedTemplate;
import discoverer.grounding.Grounder;
import java.util.*;
import static org.junit.Assert.*;
import org.junit.*;

public class SolveTest {
    private MolecularTemplate network;

    @Before
    public void initRandom() {
        Global.setRg(new Random(1L));
        String[] rules = FileToStringList.convert("../in/muta2/rules", Integer.MAX_VALUE);
        TemplateFactory nf = new TemplateFactory();
        network = nf.construct(rules);
    }

    @Test
    public void test1() {
        Global.setRg(new Random(1L));
        String[] ex = { "1.0 bond(tr105_10, tr105_8, 689), c(tr105_10), c(tr105_8), 7(689), bond(tr105_8, tr105_10, 689), bond(tr105_14, tr105_4, 690), h(tr105_14), c(tr105_4), 1(690), bond(tr105_4, tr105_14, 690), bond(tr105_8, tr105_6, 691), c(tr105_6), 7(691), bond(tr105_6, tr105_8, 691), bond(tr105_7, tr105_6, 692), n(tr105_7), 1(692), bond(tr105_6, tr105_7, 692), bond(tr105_9, tr105_8, 693), c(tr105_9), 1(693), bond(tr105_8, tr105_9, 693), bond(tr105_19, tr105_10, 694), h(tr105_19), 1(694), bond(tr105_10, tr105_19, 694), bond(tr105_13, tr105_2, 695), h(tr105_13), c(tr105_2), 1(695), bond(tr105_2, tr105_13, 695), bond(tr105_11, tr105_2, 696), h(tr105_11), 1(696), bond(tr105_2, tr105_11, 696), bond(tr105_12, tr105_2, 697), h(tr105_12), 1(697), bond(tr105_2, tr105_12, 697), bond(tr105_4, tr105_3, 698), c(tr105_3), 7(698), bond(tr105_3, tr105_4, 698), bond(tr105_5, tr105_4, 699), c(tr105_5), 7(699), bond(tr105_4, tr105_5, 699), bond(tr105_6, tr105_5, 700), 7(700), bond(tr105_5, tr105_6, 700), bond(tr105_18, tr105_9, 701), h(tr105_18), 1(701), bond(tr105_9, tr105_18, 701), bond(tr105_16, tr105_9, 702), h(tr105_16), 1(702), bond(tr105_9, tr105_16, 702), bond(tr105_17, tr105_9, 703), h(tr105_17), 1(703), bond(tr105_9, tr105_17, 703), bond(tr105_21, tr105_7, 704), h(tr105_21), 1(704), bond(tr105_7, tr105_21, 704), bond(tr105_20, tr105_7, 705), h(tr105_20), 1(705), bond(tr105_7, tr105_20, 705), bond(tr105_15, tr105_5, 706), h(tr105_15), 1(706), bond(tr105_5, tr105_15, 706), bond(tr105_3, tr105_1, 707), o(tr105_1), 1(707), bond(tr105_1, tr105_3, 707), bond(tr105_2, tr105_1, 708), 1(708), bond(tr105_1, tr105_2, 708), bond(tr105_10, tr105_3, 709), 7(709), bond(tr105_3, tr105_10, 709).", };
        ExampleFactory eFactory = new ExampleFactory();
        Example e = eFactory.construct(ex[0]);
        print(e, network.last);

        String[] ex2 = { "1.0 bond(tr000_4, tr000_2, 0), cl(tr000_4), c(tr000_2), 1(0), bond(tr000_2, tr000_4, 0), bond(tr000_5, tr000_2, 1), h(tr000_5), 1(1), bond(tr000_2, tr000_5, 1), bond(tr000_3, tr000_2, 2), cl(tr000_3), 1(2), bond(tr000_2, tr000_3, 2), bond(tr000_2, tr000_1, 3), cl(tr000_1), 1(3), bond(tr000_1, tr000_2, 3).", };
        Example e2 = eFactory.construct(ex2[0]);
        print(e, network.last);
        print(e, network.last);
        print(e, network.last);
        print(e2, network.last);
        print(e, network.last);
        print(e2, network.last);
/*
 *        print(e2, network);
 *
 *        print(e, network);
 *        print(e2, network);
 *
 *        print(e, network);
 *        print(e2, network);
 */
    }

    @Ignore
    public void test2() {
        String[] rules = FileToStringList.convert("../in/muta2/rules", Integer.MAX_VALUE);
        String[] ex = { "1.0 bond(tr000_4, tr000_2, 0), cl(tr000_4), c(tr000_2), 1(0), bond(tr000_2, tr000_4, 0), bond(tr000_5, tr000_2, 1), h(tr000_5), 1(1), bond(tr000_2, tr000_5, 1), bond(tr000_3, tr000_2, 2), cl(tr000_3), 1(2), bond(tr000_2, tr000_3, 2), bond(tr000_2, tr000_1, 3), cl(tr000_1), 1(3), bond(tr000_1, tr000_2, 3).", };
        TemplateFactory nf = new TemplateFactory();
        MolecularTemplate network = nf.construct(rules);
        ExampleFactory eFactory = new ExampleFactory();
        Example e = eFactory.construct(ex[0]);
        print(e, network.last);
    }

    private void print(Example e, KL network) {
        GroundedTemplate b = Grounder.groundTemplate(network, e);
        System.out.println(b.valMax);

/*
 *        b = Solvator.solve(network, e);
 *        System.out.println(b.val);
 *
 *        b = Solvator.solve(network, e);
 *        System.out.println(b.val);
 *
 *        b = Solvator.solve(network, e);
 *        System.out.println(b.val);
 *
 *        double d = Evaluator.evaluate(b);
 *        System.out.println(d);
 *
 *        d = Evaluator.evaluate(b);
 *        System.out.println(d);
 *
 *        b = Solvator.solve(network, e);
 *        System.out.println(b.val);
 *
 *        d = Evaluator.evaluate(b);
 *        System.out.println(d);
 */
        System.out.println("--------------------");
    }
}
