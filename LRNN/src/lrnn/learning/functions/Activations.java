package lrnn.learning.functions;

import lrnn.global.Global;
import lrnn.global.Glogger;

import java.util.List;

public class Activations {

    public static Global.activationSet lambda = Global.getLambdaActivation();
    public static Global.activationSet kappa = Global.getKappaActivation();

    private static double switchMe(Global.activationSet literal, List<Double> inputs, double offset) throws AssertionError {
        switch (literal) {
            case sig:
                for (Double input : inputs) {
                    offset += input;
                }
                return sigmoid(offset);
            case id:
                for (Double input : inputs) {
                    offset += input;
                }
                return identity(offset);
            case relu:
                for (Double input : inputs) {
                    offset += input;
                }
                return relu(offset);
            case softmax:
                return softMax(inputs, offset);
            default:
                throw new AssertionError();
        }
    }

    private static double switchMeDerived(Global.activationSet literal, List<Double> inputs, double offset) throws AssertionError {
        switch (literal) {
            case sig:
                for (Double input : inputs) {
                    offset += input;
                }
                return sigmoidDerived(offset);
            case id:
                for (Double input : inputs) {
                    offset += input;
                }
                return identityDerived(offset);
            case relu:
                for (Double input : inputs) {
                    offset += input;
                }
                return reluDerived(offset);
            case softmax:
                return softMaxDerived(inputs, offset);
            default:
                throw new AssertionError();
        }
    }

    public static final double kappaActivation(List<Double> inputs, double offset) {
        return switchMe(kappa, inputs, offset);
    }

    public static final double kappaActivationDerived(List<Double> inputs, double offset) {
        return switchMeDerived(kappa, inputs, offset);
    }

    public static final double lambdaActivation(List<Double> inputs, double offset) {
        return switchMe(lambda, inputs, offset);
    }

    public static final double lambdaActivationDerived(List<Double> inputs, double offset) {
        return switchMeDerived(lambda, inputs, offset);
    }

    public static final double identity(double x) {
        return x;
    }

    public static final double identityDerived(double x) {
        return 1;
    }

    public static final double sigmoid(double x) {
        if (x > 100) return 1.0;
        if (x < -100) return 0.0;
        return (1 / (1 + Math.exp(-x)));      //TODO
    }

    public static final double sigmoidDerived(double x) {
        if (x > 100) return 0.0;
        if (x < -100) return 0.0;
        double sx = sigmoid(x);
        return sx * (1 - sx);
    }

    public static final double relu(double x) {
        if (x > 0) {
            return x;
        } else {
            return 0;
        }
    }

    public static final double reluDerived(double x) {
        if (x > 0) {
            return 1;
        } else {
            return 0;
        }
    }

    public static final double softMax(List<Double> inputs, double offset) {
        return 0;
    }

    public static final double softMaxDerived(List<Double> inputs, double offset) {
        return 0;
    }

    public static Global.activationSet translate(String value) {
        switch (value) {
            case "sig":
                return Global.activationSet.sig;
            case "id":
                return Global.activationSet.id;
            case "relu":
                return Global.activationSet.relu;
            case "softmax":
                return Global.activationSet.softmax;
            default:
                Glogger.err("Couldn't recognize activaiton function : " + value);
                return Global.activationSet.sig;
        }
    }
}
