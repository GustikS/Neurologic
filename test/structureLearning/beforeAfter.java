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
        bf.tuesdayCrash();
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

    public void cycleLRNN() {
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

        //regrounding with an altered template (new rule + changed weights)
        String[] newTemplate2 = {
            "mother(C,M) :- parent(C,M),female(M).",
            "father(C,F) :- parent(C,F),male(F).",
            "bisex(C,B):- female(C), female(B)",
            "0.0 res :- mother(C,M).",
            "0.0 res :- father(C,F).",
            "0.5 res :- bisex(C,B).",
            "finalLambda :- res."
        };

        sli.reGroundMe(initDataset, newTemplate2);
        Dotter.draw(initDataset.template, "liftedAfterTrainRegTrainReg");
        i = 0;
        for (Sample sam : initDataset.samples) {
            GroundDotter.drawAVG(sam.getBall(), "groundLogicAfterTrainRegTrainReg" + i);
            GroundDotter.drawNeural(sam.neuralNetwork, "groundNeuralAfterTrainRegTrainReg" + i++, initDataset.template.sharedWeights);
        }

        //train weights and draw
        trainResults = sli.train(initDataset, 10, 10, 1);
        Dotter.draw(initDataset.template, "liftedAfterTrainRegTrainRegTrain");
        i = 0;
        for (Sample sam : initDataset.sampleSplitter.samples) {
            GroundDotter.drawAVG(sam.getBall(), "groundLogicAfterTrainRegTrainRegTrain" + i);
            GroundDotter.drawNeural(sam.neuralNetwork, "groundNeuralAfterTrainRegTrainRegTrain" + i++, initDataset.template.sharedWeights);
        }
    }

    public void cycleLRNN2() {
        StructureLearning sli = new StructureLearning();
        String arguments = "-r ./test/structureLearning/rules.txt -e ./test/structureLearning/examples2.txt";
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
            "person4L(X) :- male(X).",
            "person4L(X) :- female(X).",
            "0.0 person5K(X) :- person4L(X).",
            "person6L(X) :- person5K(X).",
            "0.0 person7K(X) :- person6L(X).",
            "person8L(X) :- person7K(X).",
            "0.0 person9K(X) :- person8L(X).",
            "personL(X) :- person9K(X).",
            "person11L(X) :- person5K(X).",
            "0.0 finalKappa(X) :- personL(X).",
            "0.0 finalKappa(X) :- male(X).",
            "0.0 finalKappa(X) :- person6L(X).",
            "0.0 finalKappa(X) :- person11L(X).",
            "finalLambda :- finalKappa(X)."
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

        //regrounding with an altered template (new rule + changed weights)
        String[] newTemplate2 = {
            "person4L(X) :- male(X).",
            "person4L(X) :- female(X).",
            "0.0 person5K(X) :- person4L(X).",
            "person6L(X) :- person5K(X).",
            "0.0 person7K(X) :- person6L(X).",
            "person8L(X) :- person7K(X).",
            "0.0 person9K(X) :- person8L(X).",
            "personL(X) :- person9K(X).",
            "person11L(X) :- person5K(X).",
            "0.0 finalKappa(X) :- personL(X).",
            "0.0 finalKappa(X) :- female(X).",
            "0.0 finalKappa(X) :- male(X).",
            "0.0 finalKappa(X) :- person6L(X).",
            "0.0 finalKappa(X) :- person4L(X).",
            "0.0 finalKappa(X) :- person11L(X).",
            "finalLambda :- finalKappa(X)."
        };

        sli.reGroundMe(initDataset, newTemplate2);
        Dotter.draw(initDataset.template, "liftedAfterTrainRegTrainReg");
        i = 0;
        for (Sample sam : initDataset.samples) {
            GroundDotter.drawAVG(sam.getBall(), "groundLogicAfterTrainRegTrainReg" + i);
            GroundDotter.drawNeural(sam.neuralNetwork, "groundNeuralAfterTrainRegTrainReg" + i++, initDataset.template.sharedWeights);
        }

        //train weights and draw
        trainResults = sli.train(initDataset, 10, 10, 1);
        Dotter.draw(initDataset.template, "liftedAfterTrainRegTrainRegTrain");
        i = 0;
        for (Sample sam : initDataset.sampleSplitter.samples) {
            GroundDotter.drawAVG(sam.getBall(), "groundLogicAfterTrainRegTrainRegTrain" + i);
            GroundDotter.drawNeural(sam.neuralNetwork, "groundNeuralAfterTrainRegTrainRegTrain" + i++, initDataset.template.sharedWeights);
        }
    }

    public void cycleLRNN3() {
        StructureLearning sli = new StructureLearning();
        String arguments = "-r ./test/structureLearning/rules.txt -e ./test/structureLearning/examples2.txt";
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
            "0.0 apAtom5(X0) :- female(X0).",
            "0.0 apAtom5(X0) :- male(X0).",
            "0.0 apAtom7(X0) :- female(X0).",
            "0.0 apAtom7(X0) :- male(X0).",
            "0.0 apAtom8(X0,X1) :- sibling(X0,X1).",
            "apAtom65Lambda(X0) :- apAtom7(X0).",
            "apAtom66Lambda(X0) :- apAtom8(X0,X1).",
            "apAtom67Lambda(X1) :- apAtom8(X0,X1).",
            "apAtom68Lambda(X0) :- apAtom5(X0).",
            "0.0 finalKappa(X0) :- apAtom65Lambda(X0).",
            "0.0 finalKappa(X0) :- apAtom66Lambda(X0).",
            "0.0 finalKappa(X0) :- apAtom67Lambda(X0).",
            "0.0 finalKappa(X0) :- apAtom68Lambda(X0).",
            "finalLambda :- finalKappa(X)."
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

        //regrounding with an altered template (new rule + changed weights)
        String[] newTemplate2 = {
            "person4L(X) :- male(X).",
            "person4L(X) :- female(X).",
            "0.0 person5K(X) :- person4L(X).",
            "person6L(X) :- person5K(X).",
            "0.0 person7K(X) :- person6L(X).",
            "person8L(X) :- person7K(X).",
            "0.0 person9K(X) :- person8L(X).",
            "personL(X) :- person9K(X).",
            "person11L(X) :- person5K(X).",
            "0.0 finalKappa(X) :- personL(X).",
            "0.0 finalKappa(X) :- female(X).",
            "0.0 finalKappa(X) :- male(X).",
            "0.0 finalKappa(X) :- person6L(X).",
            "0.0 finalKappa(X) :- person4L(X).",
            "0.0 finalKappa(X) :- person11L(X).",
            "finalLambda :- finalKappa(X)."
        };

        sli.reGroundMe(initDataset, newTemplate2);
        Dotter.draw(initDataset.template, "liftedAfterTrainRegTrainReg");
        i = 0;
        for (Sample sam : initDataset.samples) {
            GroundDotter.drawAVG(sam.getBall(), "groundLogicAfterTrainRegTrainReg" + i);
            GroundDotter.drawNeural(sam.neuralNetwork, "groundNeuralAfterTrainRegTrainReg" + i++, initDataset.template.sharedWeights);
        }

        //train weights and draw
        trainResults = sli.train(initDataset, 10, 10, 1);
        Dotter.draw(initDataset.template, "liftedAfterTrainRegTrainRegTrain");
        i = 0;
        for (Sample sam : initDataset.sampleSplitter.samples) {
            GroundDotter.drawAVG(sam.getBall(), "groundLogicAfterTrainRegTrainRegTrain" + i);
            GroundDotter.drawNeural(sam.neuralNetwork, "groundNeuralAfterTrainRegTrainRegTrain" + i++, initDataset.template.sharedWeights);
        }
    }

    @Test
    public void tuesdayCrash() {
        StructureLearning sli = new StructureLearning();
        String arguments = "-r ./test/structureLearning/tuesdayRules.txt -e ./test/structureLearning/tuesdayExamples.txt";
        GroundedDataset initDataset = sli.init(arguments);

        Dotter.draw(initDataset.template, "liftedAfterInit");
        int i = 0;
        for (Sample sam : initDataset.sampleSplitter.samples) {
            GroundDotter.drawAVG(sam.getBall(), "groundLogicAfterInit" + i);
            GroundDotter.drawNeural(sam.neuralNetwork, "groundNeuralAfterInit" + i++, initDataset.template.sharedWeights);
        }

        Results trainResults = sli.train(initDataset, 10, 10, 1);

        Dotter.draw(initDataset.template, "liftedAfterTrain");
        i = 0;
        for (Sample sam : initDataset.sampleSplitter.samples) {
            GroundDotter.drawAVG(sam.getBall(), "groundLogicAfterTrain" + i);
            GroundDotter.drawNeural(sam.neuralNetwork, "groundNeuralAfterTrain" + i++, initDataset.template.sharedWeights);
        }

        // first grounding & grounding & learning
        String[] firstTemplate = {
            "0.0 apAtom4(X0) :- female(X0).",
            "0.0 apAtom4(X0) :- male(X0).",
            "0.0 apAtom5(X0) :- female(X0).",
            "0.0 apAtom5(X0) :- male(X0).",
            "apAtom5Lambda(X0) :- apAtom4(X0).",
            "0.0 finalKappa(X0) :- apAtom5Lambda(X0).",
            "finalLambda :- finalKappa(X)."
        };

        GroundedDataset newDataset = sli.reGroundMe(initDataset, firstTemplate);
        Dotter.draw(newDataset.template, "liftedAfterTrainReg");
        i = 0;
        for (Sample sam : newDataset.sampleSplitter.samples) {
            GroundDotter.drawAVG(sam.getBall(), "groundLogicAfterTrainReg" + i);
            GroundDotter.drawNeural(sam.neuralNetwork, "groundNeuralAfterTrainReg" + i++, newDataset.template.sharedWeights);
        }
        sli.train(newDataset, 10, 10, 0);
        sli.reGroundMe(newDataset, firstTemplate);
        Dotter.draw(newDataset.template, "liftedAfterTrainRegTrain");
        i = 0;
        for (Sample sam : newDataset.sampleSplitter.samples) {
            GroundDotter.drawAVG(sam.getBall(), "groundLogicAfterTrainRegTrain" + i);
            GroundDotter.drawNeural(sam.neuralNetwork, "groundNeuralAfterTrainRegTrain" + i++, newDataset.template.sharedWeights);
        }
        System.out.println("error: " + trainResults.training.getError());
        System.out.println("dispersion: " + trainResults.training.getDispersion());
        System.out.println("mse: " + trainResults.training.getMse());
    }
}
