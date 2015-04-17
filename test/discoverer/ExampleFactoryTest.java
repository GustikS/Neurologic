package discoverer;

import org.junit.*;

public class ExampleFactoryTest {

    @Test
    public void test() {
        String[] rules = { // constants = 10
            "l21(X) :- p(1,2),p(a,c),q(a).",
            "l22(X) :- z(a,c),p(9,w).",
            "l23(X) :- e(a,a),p(a,a),q(1,2).",
            "l24(X) :- y(p,i), klokan(zebra,pes).",

            "0.1 k21(X) :- l21(X).",
            "0.1 k22(X) :- l21(X).",
            "0.1 k22(X) :- l22(X).",
        };

        String[] ex = {
            "1.0 p(a,b),p(a,d),p(c,c),q(x).",
            "1.0 k(a,c),e(w,9),p(a,b).",
            "1.0 p(w,9),zzz(z,z).",
        };


        NetFactory nf = new NetFactory();
        KL last = nf.construct(rules);

        ExampleFactory ef = new ExampleFactory();
        System.out.println("ElId(construction)=" + ef.getElId());
        System.out.println("PossId(construction)=" + ef.getPossId());
        Example e1 = ef.construct(ex[0]);
        System.out.println("ElId(ex0)=" + ef.getElId());
        System.out.println("PossId(ex0)=" + ef.getPossId());
        //ef.clear();
        ef = new ExampleFactory();
        System.out.println("ElId(clear)=" + ef.getElId());
        System.out.println("PossId(clear)=" + ef.getPossId());
        Example e2 = ef.construct(ex[1]);
        System.out.println("ElId(ex1)=" + ef.getElId());
        System.out.println("PossId(ex1)=" + ef.getPossId());
        //ef.clear();
        ef = new ExampleFactory();
        System.out.println("ElId(clear)=" + ef.getElId());
        System.out.println("PossId(clear)=" + ef.getPossId());
        Example e3 = ef.construct(ex[2]);
        System.out.println("ElId(ex2)=" + ef.getElId());
        System.out.println("PossId(ex2)=" + ef.getPossId());

        System.out.println(ConstantFactory.getConstCount());
        System.out.println(e1);
        System.out.println(e1.getConstCount());
        System.out.println(e1.getPossibleChunks());
        System.out.println(e1.getChunks());

        System.out.println(ConstantFactory.getConstCount());
        System.out.println(e2);
        System.out.println(e2.getConstCount());
        System.out.println(e2.getPossibleChunks());
        System.out.println(e2.getChunks());

        System.out.println(ConstantFactory.getConstCount());
        System.out.println(e3);
        System.out.println(e3.getConstCount());
        System.out.println(e3.getPossibleChunks());
        System.out.println(e3.getChunks());

    }
}
