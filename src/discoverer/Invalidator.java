package discoverer;

/**
 * Invalidator for edges
 */
public class Invalidator {
    public static void invalidate(KL kl) {
        if (kl instanceof Kappa)
            invalidate((Kappa) kl);
        else
            invalidate((Lambda) kl);
    }

    private static void invalidate(Kappa k) {
        if (k.isElement())
            return;

        k.setWeight(WeightInitializator.init());
        for (KappaRule kr: k.getRules())
            invalidate(kr);
    }

    private static void invalidate(KappaRule kr) {
        kr.setWeight(WeightInitializator.init());
        invalidate(kr.getBody().getParent());
    }

    private static void invalidate(Lambda l) {
        for (SubK sk: l.getRule().getBody())
            invalidate(sk.getParent());
    }
}
