/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NLP;

import discoverer.GroundedDataset;
import discoverer.construction.template.Kappa;
import discoverer.drawing.Dotter;
import discoverer.drawing.GroundDotter;
import discoverer.global.Global;
import discoverer.grounding.network.GroundKL;
import discoverer.grounding.network.GroundKappa;
import discoverer.learning.Sample;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import structureLearning.StructureLearning;
import structureLearning.beforeAfter;

/**
 *
 * @author Gusta
 */
public class Recursion {
    
    public static void main(String[] args) {
        Recursion bf = new Recursion();
        bf.initLRNN();
    }

    @Test
    public void initLRNN() {
        Global.debugEnabled = false;
        Global.drawing = true;
        StructureLearning sli = new StructureLearning();
        String arguments = "-r ../in/recursion/template.txt -e ../in/recursion/sample.txt";
        GroundedDataset dataset = sli.init(arguments);
        Dotter.draw(dataset.template, "liftedAfterInit");
        int i = 0;
        Set<Kappa> allKappas = new HashSet<>();
        for (Sample sam : dataset.sampleSplitter.samples) {
            for (GroundKL kl : sam.getBall().groundNeurons) {
                if (kl instanceof GroundKappa) {
                    GroundKappa kl2 = (GroundKappa) kl;
                    allKappas.add(kl2.getGeneral());
                }
            }
            GroundDotter.drawAVG(sam.getBall(), "groundLogicAfterInit" + i);
            GroundDotter.drawNeural(sam.neuralNetwork, "groundNeuralAfterInit" + i++, dataset.template.sharedWeights);
        }
    }
}
