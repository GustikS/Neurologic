/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ida.utils.treeliker.csp;

/**
 *
 * @author ondra
 */
public abstract class UnaryConstraint {
    
    private static int hashCodeCounter = 0;
    
    private int hashCode = -1;
    
    private CSPVariable variableA;
    
    public UnaryConstraint(CSPVariable variableA){
        this.variableA = variableA;
        this.hashCode = hashCodeCounter++;
    }
    
    public CSPVariable variableA(){
        return variableA;
    }
    
    /**
     * 
     * @return true of the domain has been reduced by the filtering
     */
    public abstract boolean filterDomainOfVariableA();
    
    @Override
    public String toString(){
        return "UnaryConstraint["+this.variableA+"]";
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
