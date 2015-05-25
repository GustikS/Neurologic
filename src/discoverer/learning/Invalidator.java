package discoverer.learning;

import discoverer.construction.network.KL;
import discoverer.construction.network.Kappa;
import discoverer.construction.network.rules.KappaRule;
import discoverer.construction.network.Lambda;
import discoverer.construction.network.Network;
import discoverer.construction.network.rules.SubK;
import discoverer.construction.network.WeightInitializator;

/**
 * Invalidator for edge weights
 */
public class Invalidator {
    public static void invalidate(Network net) {
        KL kl = net.last;   //now after introducing Network-class this recursion could be shrotcuted by going throug arraylist of rules only
        
        if (kl instanceof Kappa)
            invalidate((Kappa) kl);
        else
            invalidate((Lambda) kl);
    }

    private static void invalidate(Kappa k) {
        if (k.isElement())
            return;

        //k.setWeight(WeightInitializator.init());
        k.initOffset();
        for (KappaRule kr: k.getRules())
            invalidate(kr);
    }

    private static void invalidate(KappaRule kr) {
        kr.setWeight(WeightInitializator.getWeight());
        invalidate(kr.getBody().getParent());
    }

    private static void invalidate(Lambda l) {
        for (SubK sk: l.getRule().getBody())
            invalidate(sk.getParent());
    }
}
