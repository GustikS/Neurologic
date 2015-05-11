/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package extra;

import discoverer.Learner;
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
            Learner learn = new Learner();
            double t = learn.learnDecay(i, 0);
            System.out.println(t);
        }
    }

}
