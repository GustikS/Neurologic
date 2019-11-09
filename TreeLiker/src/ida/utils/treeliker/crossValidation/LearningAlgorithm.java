/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ida.utils.treeliker.crossValidation;

import java.util.Collection;
/**
 *
 * @author Ondra
 */
public interface LearningAlgorithm {

    public void train(Collection<LearningExample> examples);

    public String classify(LearningExample example);

}
