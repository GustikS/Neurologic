package discoverer.learning;

import discoverer.construction.example.Example;
import discoverer.grounding.evaluation.GroundedTemplate;
import discoverer.grounding.network.groundNetwork.GroundNetwork;
import java.io.Serializable;

/**
 * one grounded network for example
 */
public class Sample implements Serializable {

    public int position;

    private Example example = null;
    private GroundedTemplate ball = null;

    //fast version
    public double targetValue;
    public GroundNetwork neuralNetwork;

    public Sample(GroundNetwork gn, double target) {
        neuralNetwork = gn;
        targetValue = target;
    }

    public Sample(Example e, GroundedTemplate b) {
        example = e;
        ball = b;
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
}
