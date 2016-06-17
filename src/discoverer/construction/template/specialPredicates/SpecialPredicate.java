/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer.construction.template.specialPredicates;

/**
 *
 * @author Gusta
 */
public abstract class SpecialPredicate {

    String name;
    public double threshold = 0.9;

    public abstract double evaluate(String arg);
    public abstract void update(String args, double gradient);
    
    public boolean isTrue(String arg) {
        return evaluate(arg) > threshold;
    }

}
