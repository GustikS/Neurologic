package discoverer.learning;

import discoverer.construction.template.KL;
import discoverer.construction.template.Kappa;
import discoverer.construction.template.rules.KappaRule;
import discoverer.construction.template.Lambda;
import discoverer.construction.template.MolecularTemplate;
import discoverer.construction.template.rules.SubK;
import discoverer.construction.template.WeightInitializator;

/**
 * Invalidator for edge weights
 */
public class Invalidator {
    public static void invalidate(MolecularTemplate net) {
        KL kl = net.last;   //now after introducing MolecularTemplate-class this recursion could be shrotcuted by going throug arraylist of rules only
        
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
