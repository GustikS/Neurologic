package discoverer.learning;

import discoverer.construction.example.Example;
import discoverer.grounding.evaluation.GroundedTemplate;
import java.io.Serializable;

/**
 * one grounded network for example
 */
public class Sample implements Serializable{
    public int position;
    private Example example;
    private GroundedTemplate ball;

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
}
