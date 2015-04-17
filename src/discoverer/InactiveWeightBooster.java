package discoverer;

/**
 * Experimetal!
 * Boost inactive weights in lk-network
 */
public class InactiveWeightBooster {
    public static void boost(Ball b) {
        for (KappaRule kr: NetFactory.getKappaRules())
            if (!b.getActiveRules().contains(kr))
                kr.increaseWeight(0.5);
    }
}
