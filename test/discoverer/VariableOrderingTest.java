package discoverer;

import java.util.*;
import static org.junit.Assert.*;
import org.junit.*;

public class VariableOrderingTest {
    @Before
    public void initRandom() {
        Global.rg = new Random(1L);
    }

    @Test
    public void test() {
        String[] rules = {
            "l21(X) :- atom(X,Y), atom(Z,cl), atom(Q,a,R,c,d).",
        };

        NetFactory nf = new NetFactory();
        Lambda l = (Lambda) nf.construct(rules);
        LambdaRule lr = l.getRule();

        Terminal t1 = lr.getNextUnbound(); lr.unbound.remove(t1);
        Terminal t2 = lr.getNextUnbound(); lr.unbound.remove(t2);
        Terminal t3 = lr.getNextUnbound(); lr.unbound.remove(t3);

        String var1 = t1.getName();
        String var2 = t2.getName();
        String var3 = t3.getName();

        assertTrue( ( var1.equals("R") && var2.equals("Q") ) ||
                    ( var1.equals("Q") && var2.equals("R") ) );

        assertTrue(var3.equals("Z"));
    }

}
