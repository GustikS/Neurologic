package discoverer.construction.network;

import discoverer.construction.network.Lambda;
import java.util.*;

/**
 * Factory for lambda nodes
 */
public class LambdaFactory {
    private Map<String, Lambda> lambdaMap;

    /**
     * creates new HashMap<String, Lambda>()
     */
    public LambdaFactory() {
        lambdaMap = new HashMap<String, Lambda>();
    }

    /**
     * creates new Lambda node from a String name, or return an existing one with the name
     * @param name
     * @return 
     */
    public Lambda construct(String name) {
        if (lambdaMap.containsKey(name))
            return lambdaMap.get(name);

        Lambda l = new Lambda(name);
        lambdaMap.put(name, l);
        return l;
    }
}
