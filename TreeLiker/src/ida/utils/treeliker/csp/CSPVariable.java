/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ida.utils.treeliker.csp;

import ida.utils.collections.IntegerSet;

/**
 *
 * @author Ondra
 */
public class CSPVariable<T> {
    
    private static int hashCodeCounter = 0;
    
    private int hashCode;
    
    private T name;
    
    private IntegerSet domain;
    
    public CSPVariable(T name){
        this.name = name;
        this.hashCode = hashCodeCounter++;
    }
    
    public CSPVariable(T name, IntegerSet domain){
        this(name);
        this.domain = domain;
    }
    
    public T name(){
        return this.name;
    }
    
    public IntegerSet domain(){
        return this.domain;
    }
    
    public void setDomain(IntegerSet domain){
        this.domain = domain;
    }
    
    @Override
    public String toString(){
        return "Variable["+this.name+", domain: "+this.domain+"]";
    }
    
    @Override
    public boolean equals(Object o){
        return this == o;
    }
    
    @Override
    public int hashCode(){
        return hashCode;
    }
}
