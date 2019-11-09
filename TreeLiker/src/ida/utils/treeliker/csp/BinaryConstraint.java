/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ida.utils.treeliker.csp;

/**
 *
 * @author Ondra
 */
public abstract class BinaryConstraint<T> {
    
    private static int hashCodeCounter = 0;
    
    private int hashCode = -1;
    
    private CSPVariable<T> a, b;
    
    public BinaryConstraint(CSPVariable<T> a, CSPVariable<T> b){
        this.a = a;
        this.b = b;
        this.hashCode = hashCodeCounter++;
    }
    
    /**
     * 
     * @return true of the domain has been reduced by the filtering
     */
    public abstract boolean filterDomainOfVariableA();
    
    public CSPVariable variableA(){
        return this.a;
    }
    
    public CSPVariable variableB(){
        return this.b;
    }
    
    @Override
    public String toString(){
        return "BinaryConstraint["+this.a+", "+this.b+"]";
    }
    
    @Override
    public boolean equals(Object o){
        return o == this;
    }
    
    @Override
    public int hashCode(){
        return this.hashCode;
    }
}
