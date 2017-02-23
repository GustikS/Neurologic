package lrnn.learning;

import lrnn.construction.example.Example;
import lrnn.grounding.evaluation.GroundedTemplate;
import lrnn.grounding.network.groundNetwork.GroundNetwork;
import java.io.Serializable;

/**
 * one grounded network for example
 */
public class Sample implements Serializable {

    public int position;
    public double targetValue;

    //these are empty in the memoryLight version
    private Example example = null;
    private GroundedTemplate ball = null;

    //fast version
    public GroundNetwork neuralNetwork;

    public Sample(Example e, double target){
        this.example = e;
        this.targetValue = target;
    }

    public Sample(GroundNetwork gn, double target) {
        neuralNetwork = gn;
        targetValue = target;
    }

    public Sample(Example e, GroundedTemplate b) {
        example = e;
        ball = b;
    }

    public Sample(GroundedTemplate proof, double targetVal) {
        this.ball = proof;
        this.targetValue = targetVal;
    }

    public Example getExample() {
        return example;
    }

    public GroundedTemplate getBall() {
        return ball;
    }

    public void setBall(GroundedTemplate b) {
        ball = b;
    }

    public void setExample(Example e) {
        example = e;
    }

    public void makeMeSmall() {
        setExample(null);
        setBall(null);
    }

    public void deleteNeurons() {
        neuralNetwork = null;
    }
}
