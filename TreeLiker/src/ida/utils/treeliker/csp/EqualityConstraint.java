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
public class EqualityConstraint extends BinaryConstraint {

    private static Cache<IntegerSet,IntegerSet> cache = new Cache<IntegerSet,IntegerSet>();
    
    public EqualityConstraint(CSPVariable a, CSPVariable b){
        super(a, b);
    }
    
    @Override
    public boolean filterDomainOfVariableA() {
        int originalSize = this.variableA().domain().size();
        IntegerSet filtered = IntegerSet.intersection(this.variableA().domain(), this.variableB().domain());
        IntegerSet cached = cache.get(filtered);
        if (cached == null){
            cache.put(filtered, filtered);
        } else {
            filtered = cached;
        }
        this.variableA().setDomain(filtered);
        return originalSize > this.variableA().domain().size();
    }
    
    public static void clearCache(){
        cache.clear();
    }
}
