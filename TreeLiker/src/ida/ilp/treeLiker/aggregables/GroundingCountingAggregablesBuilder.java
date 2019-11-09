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
 * Implementation of interface AggregablesBuilder for building Aggregables with GroundingCountingAggregable objects
 * @author Ondra
 */
public class GroundingCountingAggregablesBuilder implements AggregablesBuilder {
    
    private static GroundingCountingAggregablesBuilder theOne = new GroundingCountingAggregablesBuilder();
    
    private GroundingCountingAggregablesBuilder(){}
    
    /**
     * Singleton-method
     * @return
     */
    public static GroundingCountingAggregablesBuilder construct(){
        return theOne;
    }
    
    public Aggregables construct(PredicateDefinition def, IntegerSet literalIDs, Example example) {
        Aggregables aggregables = new Aggregables(literalIDs.size());
        for (int i = 0; i < literalIDs.size(); i++){
            aggregables.add(GroundingCountingAggregable.one());
        }
        return aggregables;
//        return AggregablesFilledWithOneElement.construct(GroundingCountingAggregable.one(), literalIDs.size());
    }
    
}
