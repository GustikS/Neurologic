/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package extra;

import discoverer.learning.learners.Learning;
import discoverer.construction.network.WeightInitializator;
import org.junit.Test;

/**
 *
 * @author Gusta
 */
public class LearnDecay {

    @Test
    public void longTail() {
        for (int i = 0; i < 10000; i++) {
            Learning learn = new Learning();
            double t = learn.learnDecay(i, 0);
            System.out.println(t);
        }
    }

}
