package lrnn.construction.template;

import lrnn.construction.TemplateFactory;
import lrnn.construction.template.specialPredicates.SpecialPredicate;
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
     * creates new Lambda node from a String name, or return an existing one
     * with the name
     *
     * @param name
     * @return
     */
    public Lambda construct(String name) {
        if (lambdaMap.containsKey(name)) {
            return lambdaMap.get(name);
        }
        
        Lambda l = new Lambda(name);
        lambdaMap.put(name, l);
        
        SpecialPredicate special;
        if ((special = TemplateFactory.specialPredicateNames.get(name)) != null){
            l.special = true;
            TemplateFactory.specialPredicatesMap.put(l, special);
        }
        
        return l;
    }
}
