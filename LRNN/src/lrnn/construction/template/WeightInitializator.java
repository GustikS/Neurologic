package lrnn.construction.template;

import lrnn.global.Global;

/**
 * Initializator for weights in graph
 */
public class WeightInitializator {

    //private static Random rg = Global.getRg();

    public static final double getWeight() {
        //double rand = (rg.nextDouble() / 10) - 0.05;
        //double rand = (rg.nextDouble()) - 0.5;
        //double rand = 0.1;
        switch (Global.getWeightInit()) {
            case handmade:
                return getHandMade();
            case longtail:
                return longTail();
            case uniform:
                return uniform();
            case constant:
                return constant();
            default:
                throw new AssertionError();
        }
    }

    private static double constant() {
        return 0.1;
    }

    public static double getHandMade() {
        return Global.getRandomDouble() > 0.1 ? 0.1 : 0.9;
    }

    /**
     * produces power-law distribution from a uniform distribution from
     * Global.rg
     *
     * @return
     */
    public static double longTail() {
        double power = 50;
        double x0 = 0;
        double x1 = 10;
        double y = Global.getRandomDouble();
        double x = x1 - (Math.pow(((Math.pow(x1, (power + 1)) - Math.pow(x0, (power + 1))) * y + Math.pow(x0, (power + 1))), (1 / (power + 1))));
        return x;
    }

    public static double uniform() {
        double range = 2;
        double offset = -0.5;
        return range * (Global.getRandomDouble() + offset);
    }
}
