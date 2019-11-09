/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ida.utils.treeliker.csp;

import ida.utils.collections.TupleSet;

/**
 *
 * @author Ondra
 */
public class TabularBinaryConstraintDefinition {
    
    private TupleSet values;
    
    public TabularBinaryConstraintDefinition(TupleSet values){
        this.values = values;
    }
    
    public TupleSet admissibleValues(){
        return this.values;
    }
 
    @Override
    public String toString(){
        return "<BinaryConstraintDefinition>\n"+values+"</BinaryConstraintDefinition>";
    }
}
