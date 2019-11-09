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
public class TabularUnaryConstraintDefinition {
    
    private IntegerSet values;
    
    public TabularUnaryConstraintDefinition(IntegerSet values){
        this.values = values;
    }

    public IntegerSet admissibleValues(){
        return values;
    }
}
