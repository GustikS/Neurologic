/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ida.utils.treeliker.csp;

import ida.utils.Cache;
import ida.utils.collections.IntegerSet;
import ida.utils.collections.TupleSet;

/**
 *
 * @author Ondra
 */
public class TabularBinaryConstraint<T> extends BinaryConstraint<T> {
    
    private TabularBinaryConstraintDefinition tcd;
    
    private static Cache<IntegerSet,IntegerSet> cache = new Cache<IntegerSet, IntegerSet>();
    
    public TabularBinaryConstraint(CSPVariable<T> a, CSPVariable<T> b, TabularBinaryConstraintDefinition tcd){
        super(a, b);
        this.tcd = tcd;
    }
    
    @Override
    public boolean filterDomainOfVariableA(){
        CSPVariable a = this.variableA();
        CSPVariable b = this.variableB();
        TupleSet filteredDomainOfConstraint = this.tcd.admissibleValues().select(1, b.domain());
        int oldSize = a.domain().size();
        IntegerSet filtered = IntegerSet.intersection(a.domain(), filteredDomainOfConstraint.column(0));
        IntegerSet cached = cache.get(filtered);
        if (cached == null){
            cache.put(filtered, filtered);
        } else {
            filtered = cached;
        }
        a.setDomain(filtered);
        int newSize = a.domain().size();
        return oldSize > newSize;
    }
    
    @Override
    public String toString(){
        return "BinaryConstraint["+this.variableA()+", "+this.variableB()+", definition: "+this.tcd+"]";
    }
    
    public static void clearCache(){
        cache.clear();
    }
    
}
