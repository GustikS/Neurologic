package discoverer;

/**
 * Results for one round in n-fold
 */
public class RoundElement {
    private Example example;
    private Ball ball;

    public RoundElement(Example e, Ball b) {
        example = e;
        ball = b;
    }

    public Example getExample() {
        return example;
    }

    public Ball getBall() {
        return ball;
    }

    public void setBall(Ball b) {
        ball = b;
    }
}
