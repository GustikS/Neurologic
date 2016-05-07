package oldTestsFromDiscoverer;

import discoverer.construction.Variable;
import discoverer.construction.template.rules.SubK;
import discoverer.construction.template.Kappa;
import static org.junit.Assert.*;

import org.junit.Test;

public class SubKTest {

    @Test
    public void test1() {
        Kappa k = new Kappa("test", 0.5);
        SubK sk1 = new SubK(k, true);
        Variable t1 = new Variable("A", 1); Variable t2 = new Variable("B", 2); Variable t3 = new Variable("C", -1);
        sk1.addVariable(t1); sk1.addVariable(t2); sk1.addVariable(t3);

        SubK sk2 = new SubK(k, true);
        Variable tt1 = new Variable("A", 1); Variable tt2 = new Variable("B", 2); Variable tt3 = new Variable("C", -1);
        sk2.addVariable(tt1); sk2.addVariable(tt2); sk2.addVariable(tt3);

        assertEquals(sk1, sk2);
    }

    @Test
    public void test2() {
        Kappa k = new Kappa("test", 0.5);
        SubK sk1 = new SubK(k, true);
        Variable t1 = new Variable("A", 2); Variable t2 = new Variable("B", 2); Variable t3 = new Variable("C", -1);
        sk1.addVariable(t1); sk1.addVariable(t2); sk1.addVariable(t3);

        SubK sk2 = new SubK(k, true);
        Variable tt1 = new Variable("A", 1); Variable tt2 = new Variable("B", 2); Variable tt3 = new Variable("C", -1);
        sk2.addVariable(tt1); sk2.addVariable(tt2); sk2.addVariable(tt3);

        assertFalse(sk1.equals(sk2));
    }
}
