package discoverer.learning.backprop.functions;

public class Activations {

    public static final double kappaActivation(double x) {
        return sigmoid(x);
        //return identity(x);
    }

    public static final double kappaActivationDerived(double x) {
        return sigmoidDerived(x);
        //return identityDerived(x);
    }

    public static final double lambdaActivation(double x) {
        //return identity(x);
        return sigmoid(x);   //test-mode
    }

    public static final double lambdaActivationDerived(double x) {
        //return identityDerived(x);
        return sigmoidDerived(x);
    }

    public static final double identity(double x) {
        return x;
    }

    public static final double identityDerived(double x) {
        return 1;
    }

    public static final double sigmoid(double x) {
        return (1 / (1 + Math.pow(Math.E, -x)));
    }

    public static final double sigmoidDerived(double x) {
        double sx = sigmoid(x);
        return sx * (1 - sx);
    }
}
