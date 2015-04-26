package discoverer.learning.backprop.functions;

import discoverer.global.Global;

public class Activations {

    public static Global.activationSet lambda = Global.lambdaActivation;
    public static Global.activationSet kappa = Global.kappaActivation;

    public static final double kappaActivation(double x) {
        switch (kappa) {
            case sig:
                return sigmoid(x);
            case id:
                return identity(x);
            default:
                throw new AssertionError();
        }
    }

    public static final double kappaActivationDerived(double x) {
        switch (kappa) {
            case sig:
                return sigmoidDerived(x);
            case id:
                return identityDerived(x);
            default:
                throw new AssertionError();
        }
    }

    public static final double lambdaActivation(double x) {
        switch (lambda) {
            case sig:
                return sigmoid(x);
            case id:
                return identity(x);
            default:
                throw new AssertionError();
        }
    }

    public static final double lambdaActivationDerived(double x) {
        switch (lambda) {
            case sig:
                return sigmoidDerived(x);
            case id:
                return identityDerived(x);
            default:
                throw new AssertionError();
        }
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
