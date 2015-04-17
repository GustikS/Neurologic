package discoverer;

import static org.junit.Assert.*;
import org.junit.*;

public class NetFactoryTest {
    @Test
    public void test() {
        String[] rules = {
            "l21(X) :- bond(tr000_4, tr000_2, cl, c, 1), atom(tr000, tr000_4).",
            "l22(X) :- bond(tr001_20, tr001_7, c, c, 1).",
            "l23(X) :- bond(tr028_5, tr028_6, c, cl, 1).",
            "l24(X) :- bond(tr028_4, tr028_3, br, c, 1).",

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
            "0.14 k13(X) :- l13(X).",
        };

        NetFactory nf = new NetFactory();
        Kappa last = (Kappa) nf.construct(rules);

        System.out.println("id:\t" + last.id);

        System.out.println("rules v");
        for (Rule r: last.getRules())
            System.out.println(r);

        System.out.println("Constants");
        System.out.println(ConstantFactory.getConstCount());
        System.out.println(ConstantFactory.getConstMap());

        System.out.println("Literals");
        System.out.println(ElementMapper.getElCount());
        System.out.println(ElementMapper.getElMap());

        assertEquals("Number of different constants", 13, ConstantFactory.getConstCount(), 0);
        assertEquals("Number of different literals", 2, ElementMapper.getElCount(), 0);

        assertEquals("Number of different constants(map length)", ConstantFactory.getConstMap().size(), ConstantFactory.getConstCount(), 0);
        assertEquals("Number of different literals(map length)", ElementMapper.getElMap().size(), ElementMapper.getElCount(), 0);
    }

}
