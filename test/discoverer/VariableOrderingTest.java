package discoverer;

import discoverer.construction.Variable;
import discoverer.construction.network.rules.LambdaRule;
import discoverer.construction.TemplateFactory;
import discoverer.construction.template.Lambda;
import discoverer.construction.template.MolecularTemplate;
import discoverer.global.Global;
import java.util.*;
import static org.junit.Assert.*;
import org.junit.*;

public class VariableOrderingTest {
    @Before
    public void initRandom() {
        Global.setRg(new Random(1L));
    }

    @Test
    public void test() {
        String[] rules = {
            "l21(X) :- atom(X,Y), atom(Z,cl), atom(Q,a,R,c,d).",
        };

        TemplateFactory nf = new TemplateFactory();
        MolecularTemplate net = nf.construct(rules);
        Lambda l = (Lambda) net.last;
        LambdaRule lr = l.getRule();

        Variable t1 = lr.getNextUnbound(); lr.unbound.remove(t1);
        Variable t2 = lr.getNextUnbound(); lr.unbound.remove(t2);
        Variable t3 = lr.getNextUnbound(); lr.unbound.remove(t3);

        String var1 = t1.getName();
        String var2 = t2.getName();
        String var3 = t3.getName();

        assertTrue( ( var1.equals("R") && var2.equals("Q") ) ||
                    ( var1.equals("Q") && var2.equals("R") ) );

        assertTrue(var3.equals("Z"));
    }

}
