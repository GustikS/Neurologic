/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ida.utils.treeliker.csp;

import ida.utils.Cache;
import ida.utils.collections.IntegerSet;

/**
 *
 * @author Ondra
 */
public class TabularUnaryConstraint extends UnaryConstraint {
    
    private TabularUnaryConstraintDefinition ucd;
    
    private static Cache<IntegerSet,IntegerSet> cache = new Cache<IntegerSet, IntegerSet>();
    
    public TabularUnaryConstraint(CSPVariable scope, TabularUnaryConstraintDefinition ucd){
        super(scope);
        this.ucd = ucd;
    }
    
    
    /**
     * 
     * @return true of the domain has been reduced by the filtering
     */
    @Override
    public boolean filterDomainOfVariableA(){
        int oldSize = variableA().domain().size();
        IntegerSet filtered = IntegerSet.intersection(variableA().domain(), ucd.admissibleValues());
        IntegerSet cached = cache.get(filtered);
        if (cached == null){
            cache.put(filtered, filtered);
        } else {
            filtered = cached;
        }
        variableA().setDomain(filtered);
        int newSize = variableA().domain().size();
        return oldSize > newSize;
    }
    
    @Override
    public String toString(){
        return "TabularUnaryConstraint["+variableA()+", definition: "+ucd+"]";
    }
    
    public static void clearCache(){
        cache.clear();
    }
}
