/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ida.ilp.treeLiker.aggregables;

import ida.ilp.treeLiker.Aggregables;
import ida.ilp.treeLiker.AggregablesBuilder;
import ida.ilp.treeLiker.Example;
import ida.ilp.treeLiker.PredicateDefinition;
import ida.utils.collections.IntegerSet;

/**
 * Class for construction of VoidAggregables objects, implementing interface AggregablesBuilder.
 * @author Ondra
 */
public class VoidAggregablesBuilder implements AggregablesBuilder {

    private static VoidAggregablesBuilder theOne = new VoidAggregablesBuilder();
    
    private VoidAggregablesBuilder(){}
    
    /**
     * Singleton method for creating instances of class VoidAggregablesBuilder
     * @return
     */
    public static VoidAggregablesBuilder construct(){
        return theOne;
    }
    
    public Aggregables construct(PredicateDefinition def, IntegerSet literalIDs, Example example) {
        return VoidAggregables.construct(literalIDs.size());
    }
    
}
