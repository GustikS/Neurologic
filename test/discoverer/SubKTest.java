package discoverer;

import static org.junit.Assert.*;

import org.junit.Test;

public class SubKTest {

    @Test
    public void test1() {
        Kappa k = new Kappa("test", 0.5);
        SubK sk1 = new SubK(k, true);
        Terminal t1 = new Terminal("A", 1); Terminal t2 = new Terminal("B", 2); Terminal t3 = new Terminal("C", -1);
        sk1.addVariable(t1); sk1.addVariable(t2); sk1.addVariable(t3);

        SubK sk2 = new SubK(k, true);
        Terminal tt1 = new Terminal("A", 1); Terminal tt2 = new Terminal("B", 2); Terminal tt3 = new Terminal("C", -1);
        sk2.addVariable(tt1); sk2.addVariable(tt2); sk2.addVariable(tt3);

        assertEquals(sk1, sk2);
    }

    @Test
    public void test2() {
        Kappa k = new Kappa("test", 0.5);
        SubK sk1 = new SubK(k, true);
        Terminal t1 = new Terminal("A", 2); Terminal t2 = new Terminal("B", 2); Terminal t3 = new Terminal("C", -1);
        sk1.addVariable(t1); sk1.addVariable(t2); sk1.addVariable(t3);

        SubK sk2 = new SubK(k, true);
        Terminal tt1 = new Terminal("A", 1); Terminal tt2 = new Terminal("B", 2); Terminal tt3 = new Terminal("C", -1);
        sk2.addVariable(tt1); sk2.addVariable(tt2); sk2.addVariable(tt3);

        assertFalse(sk1.equals(sk2));
    }
}
