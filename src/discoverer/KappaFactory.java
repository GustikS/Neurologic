package discoverer;

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
        if (kappaMap.containsKey(name))
            return kappaMap.get(name);

        Kappa k = new Kappa(name, WeightInitializator.init());
        kappaMap.put(name, k);
        return k;
    }

    public int getNumberOfKappas() { return kappaMap.size(); }
}
