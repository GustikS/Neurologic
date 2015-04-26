package discoverer.construction.network;

import discoverer.global.Global;
import java.util.Random;

/**
 * Initializator for weights in graph
 */
public class WeightInitializator {

    private static Random rg = Global.rg;

    public static final double getWeight() {
        //double rand = (rg.nextDouble() / 10) - 0.05;
        //double rand = (rg.nextDouble()) - 0.5;
        //double rand = 0.1;
        switch (Global.weightInit) {
            case handmade:
                return getHandMade();
            case longtail:
                return longTail();
            default:
                throw new AssertionError();
        }
    }

    public static double getHandMade() {
        return rg.nextDouble() > 0.1 ? 0.1 : 0.9;
    }

    /**
     * produces power-law distribution from a uniform distribution from
     * Global.rg
     *
     * @return
     */
    public static double longTail() {
        double power = 10;
        double x0 = 0;
        double x1 = 2;
        double y = Global.rg.nextDouble();
        double x = x1 - (Math.pow(((Math.pow(x1, (power + 1)) - Math.pow(x0, (power + 1))) * y + Math.pow(x0, (power + 1))), (1 / (power + 1))));
        return x;
    }
}
