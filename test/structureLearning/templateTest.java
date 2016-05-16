/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package structureLearning;

import discoverer.GroundedDataset;
import discoverer.construction.template.Kappa;
import discoverer.drawing.Dotter;
import discoverer.drawing.GroundDotter;
import discoverer.grounding.network.GroundKL;
import discoverer.grounding.network.GroundKappa;
import discoverer.learning.Results;
import discoverer.learning.Sample;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;

/**
 *
 * @author Gusta
 */
public class templateTest {

    public static void main(String[] args) {
        templateTest bf = new templateTest();
        bf.trainTestLRNN();
    }

    @Test
    public void initLRNN() {
        StructureLearning sli = new StructureLearning();
        String arguments = "-e ../in/muta/test/train -test ../in/muta/test/test -r ../in/muta/test/template.txt";
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

    @Test
    public void trainTestLRNN() {
        StructureLearning sli = new StructureLearning();
        String arguments = "-e ../in/muta/test/train -test ../in/muta/test/test -r ../in/muta/test/template.txt";
        GroundedDataset initDataset = sli.init(arguments);

        List<Sample> train = initDataset.sampleSplitter.getTrain();
        List<Sample> test = initDataset.sampleSplitter.getTest();

        initDataset.sampleSplitter.samples = train; //train on training only

        Dotter.draw(initDataset.template, "liftedAfterInit");

        Results trainResults = sli.train(initDataset, 1000, 100, 1);

        System.out.println("train error: " + trainResults.training.getError());
        System.out.println("train mse: " + trainResults.training.getMse());
        Dotter.draw(initDataset.template, "liftedAfterTraining");

        Results testResults = sli.test(initDataset.template, test, trainResults);
        
        System.out.println("test error: " + testResults.testing.getError());
        System.out.println("test mse: " + testResults.testing.getMse());
    }
}
