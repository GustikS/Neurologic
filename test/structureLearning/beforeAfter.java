/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package structureLearning;

import discoverer.GroundedDataset;
import discoverer.construction.template.rules.KappaRule;
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
public class beforeAfter {

    public static void main(String[] args) {
        beforeAfter bf = new beforeAfter();
        bf.cycleLRNN();
    }

    @Test
    public void initLRNN() {
        StructureLearning sli = new StructureLearning();
        String arguments = "-r ./test/structureLearning/initTemplate.txt -e ./test/structureLearning/examples.txt";
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
    public void regroundLRNN() {
        StructureLearning sli = new StructureLearning();
        String arguments = "-r ./test/structureLearning/initTemplate.txt -e ./test/structureLearning/examples.txt";
        GroundedDataset initDataset = sli.init(arguments);
        Dotter.draw(initDataset.template, "liftedAfterInit");
        int i = 0;
        for (Sample sam : initDataset.sampleSplitter.samples) {
            GroundDotter.drawAVG(sam.getBall(), "groundLogicAfterInit" + i);
            GroundDotter.drawNeural(sam.neuralNetwork, "groundNeuralAfterInit" + i++, initDataset.template.sharedWeights);
        }
        //regrounding with an altered template (new rule + changed weights)
        String[] newTemplate = {
            "mother(C,M):-parent(C,M),female(M).",
            "father(C,F):-parent(C,F),male(F).",
            "bisex(C,B):-parent(C,B)",
            "0.0 res :- mother(C,M).",
            "0.0 res :- father(C,F).",
            "0.5 res :- bisex(C,B).",
            "finalLambda :- res."
        };

        sli.reGroundMe(initDataset, newTemplate);
        Dotter.draw(initDataset.template, "liftedAfterReground");
        i = 0;
        for (Sample sam : initDataset.sampleSplitter.samples) {
            GroundDotter.drawAVG(sam.getBall(), "groundLogicAfterReground" + i);
            GroundDotter.drawNeural(sam.neuralNetwork, "groundNeuralAfterReground" + i++, initDataset.template.sharedWeights);
        }
    }

    @Test
    public void trainLRNN() {
        StructureLearning sli = new StructureLearning();
        String arguments = "-r ./test/structureLearning/initTemplate.txt -e ./test/structureLearning/examples.txt";
        GroundedDataset initDataset = sli.init(arguments);
        
        Dotter.draw(initDataset.template, "liftedAfterInit");
        int i = 0;
        for (Sample sam : initDataset.sampleSplitter.samples) {
            GroundDotter.drawAVG(sam.getBall(), "groundLogicAfterInit" + i);
            GroundDotter.drawNeural(sam.neuralNetwork, "groundNeuralAfterInit" + i++, initDataset.template.sharedWeights);
        }

        //train weights and draw
        Results trainResults = sli.train(initDataset, 10, 10, 1);
        System.out.println("error: " + trainResults.training.getError());
        System.out.println("mse: " + trainResults.training.getMse());
        Dotter.draw(initDataset.template, "liftedAfterTraining");
        i = 0;
        for (Sample sam : initDataset.sampleSplitter.samples) {
            GroundDotter.drawAVG(sam.getBall(), "groundLogicAfterTraining" + i);
            GroundDotter.drawNeural(sam.neuralNetwork, "groundNeuralAfterTraining" + i++, initDataset.template.sharedWeights);
        }

        List<Sample> preparedGroundings = initDataset.prepareGroundings(initDataset.examples, initDataset.template);
        i = 0;
        for (Sample sam : preparedGroundings) {
            GroundDotter.drawAVG(sam.getBall(), "groundLogicAfterTrainingreGrounded" + i);
            GroundDotter.drawNeural(sam.neuralNetwork, "groundNeuralAfterTrainingreGrounded" + i++, initDataset.template.sharedWeights);
        }
    }
    
    public void cycleLRNN(){
        StructureLearning sli = new StructureLearning();
        String arguments = "-r ./test/structureLearning/initTemplate.txt -e ./test/structureLearning/examples.txt";
        GroundedDataset initDataset = sli.init(arguments);
        
        Dotter.draw(initDataset.template, "liftedAfterInit");
        int i = 0;
        for (Sample sam : initDataset.sampleSplitter.samples) {
            GroundDotter.drawAVG(sam.getBall(), "groundLogicAfterInit" + i);
            GroundDotter.drawNeural(sam.neuralNetwork, "groundNeuralAfterInit" + i++, initDataset.template.sharedWeights);
        }

        //train weights and draw
        Results trainResults = sli.train(initDataset, 10, 10, 1);
        System.out.println("error: " + trainResults.training.getError());
        System.out.println("dispersion: " + trainResults.training.getDispersion());
        System.out.println("mse: " + trainResults.training.getMse());
        Dotter.draw(initDataset.template, "liftedAfterTraining");
        i = 0;
        for (Sample sam : initDataset.sampleSplitter.samples) {
            GroundDotter.drawAVG(sam.getBall(), "groundLogicAfterTraining" + i);
            GroundDotter.drawNeural(sam.neuralNetwork, "groundNeuralAfterTraining" + i++, initDataset.template.sharedWeights);
        }
        
        //regrounding with an altered template (new rule + changed weights)
        String[] newTemplate = {
            "mother(C,M):-parent(C,M),female(M).",
            "father(C,F):-parent(C,F),male(F).",
            "bisex(C,B):-parent(C,B)",
            "0.0 res :- mother(C,M).",
            "0.0 res :- father(C,F).",
            "0.5 res :- bisex(C,B).",
            "finalLambda :- res."
        };
        
        sli.reGroundMe(initDataset, newTemplate);
        Dotter.draw(initDataset.template, "liftedAfterTrainReground");
        i = 0;
        for (Sample sam : initDataset.samples) {
            GroundDotter.drawAVG(sam.getBall(), "groundLogicAfterTrainReground" + i);
            GroundDotter.drawNeural(sam.neuralNetwork, "groundNeuralAfterTrainReground" + i++, initDataset.template.sharedWeights);
        }
        
        //train weights and draw
        trainResults = sli.train(initDataset, 10, 10, 1);
        Dotter.draw(initDataset.template, "liftedAfterTrainRegTrain");
        i = 0;
        for (Sample sam : initDataset.sampleSplitter.samples) {
            GroundDotter.drawAVG(sam.getBall(), "groundLogicAfterTrainRegTrain" + i);
            GroundDotter.drawNeural(sam.neuralNetwork, "groundNeuralAfterTrainRegTrain" + i++, initDataset.template.sharedWeights);
        }
         
    }
}
