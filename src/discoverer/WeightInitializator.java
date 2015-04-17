package discoverer;

import java.util.Random;

/**
 * Initializator for weights in graph
 */
public class WeightInitializator {
    private static Random rg = Global.rg;

    public static final double init() {
        //double rand = (rg.nextDouble() / 10) - 0.05;
        //double rand = (rg.nextDouble()) - 0.5;
        //double rand = 0.1;
        double rand = rg.nextDouble() > 0.1 ? 0.1 : 0.9;
        //double rand = 0.3;
        return rand;
    }
}
