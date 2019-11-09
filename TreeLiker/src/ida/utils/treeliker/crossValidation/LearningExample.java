/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ida.utils.treeliker.crossValidation;

/**
 *
 * @author Ondra
 */
public interface LearningExample<T> {

    public String classification();

    public T attribute(String name);

}
