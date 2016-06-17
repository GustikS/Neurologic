package discoverer.construction.template;

import discoverer.construction.TemplateFactory;
import discoverer.construction.template.Kappa;
import discoverer.construction.template.specialPredicates.SpecialPredicate;
import java.util.*;

/**
 * Factory for kappa nodes
 */
public class KappaFactory {

    private Map<String, Kappa> kappaMap;

    public KappaFactory() {
        kappaMap = new HashMap<String, Kappa>();
    }

    public Kappa construct(String name) {
        if (kappaMap.containsKey(name)) {
            return kappaMap.get(name);
        }

        Kappa k = new Kappa(name);
        kappaMap.put(name, k);

        SpecialPredicate special;
        if ((special = TemplateFactory.specialPredicateNames.get(name)) != null) {
            k.special = true;
            TemplateFactory.specialPredicatesMap.put(k, special);
        }

        return k;
    }

    public int getNumberOfKappas() {
        return kappaMap.size();
    }

    public Collection<Kappa> getKappas() {
        return kappaMap.values();
    }
}
