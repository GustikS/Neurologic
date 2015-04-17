package discoverer;

import java.util.*;

/**
 * Factory for lambda nodes
 */
public class LambdaFactory {
    private Map<String, Lambda> lambdaMap;

    public LambdaFactory() {
        lambdaMap = new HashMap<String, Lambda>();
    }

    public Lambda construct(String name) {
        if (lambdaMap.containsKey(name))
            return lambdaMap.get(name);

        Lambda l = new Lambda(name);
        lambdaMap.put(name, l);
        return l;
    }
}
