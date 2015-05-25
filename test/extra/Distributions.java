/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package extra;

import discoverer.construction.network.WeightInitializator;
import discoverer.global.Global;
import java.util.Random;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Gusta
 */
public class Distributions {

    @Test
    public void longTail() {
        Global.setRg(new Random(2));
        for (int i = 0; i < 1000; i++) {
            double t = WeightInitializator.longTail();
            System.out.println(t);
        }
    }
    
    @Ignore
    public void uniform() {
        Global.setRg(new Random(2));
        for (int i = 0; i < 1000; i++) {
            double t = WeightInitializator.uniform();
            System.out.println(t);
        }
    }
}
