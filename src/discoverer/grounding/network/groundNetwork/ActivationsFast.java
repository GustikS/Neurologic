/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer.grounding.network.groundNetwork;

import discoverer.global.Global;
import java.util.List;

/**
 *
 * @author Gusta
 */
public final class ActivationsFast {

    public static final Global.activationSet lambda = Global.getLambdaActivation();
    public static final Global.activationSet kappa = Global.getKappaActivation();
    public static final Global.groundingSet aggregation = Global.getGrounding();

    private static final double switchMe(Global.activationSet literal, double[] inputs, double offset) throws AssertionError {
        switch (literal) {
            case sig:
                for (double input : inputs) {
                    offset += input;
                }
                return sigmoid(offset);
            case id:
                for (double input : inputs) {
                    offset += input;
                }
                return identity(offset);
            case relu:
                for (double input : inputs) {
                    offset += input;
                }
                return relu(offset);
            case softmax:
                return softMax(inputs, offset);
            default:
                throw new AssertionError();
        }
    }

    private static final double switchMeDerived(Global.activationSet literal, double[] inputs, double offset) throws AssertionError {
        switch (literal) {
            case sig:
                for (double input : inputs) {
                    offset += input;
                }
                return sigmoidDerived(offset);
            case id:
                for (double input : inputs) {
                    offset += input;
                }
                return identityDerived(offset);
            case relu:
                for (double input : inputs) {
                    offset += input;
                }
                return reluDerived(offset);
            case softmax:
                return softMaxDerived(inputs, offset);
            default:
                throw new AssertionError();
        }
    }

    private static final double switchMe(Global.activationSet literal, double input, double offset) throws AssertionError {
        switch (literal) {
            case sig:
                return sigmoid(offset + input);
            case id:
                return identity(offset + input);
            case relu:
                return relu(offset + input);
            default:
                throw new AssertionError();
        }
    }

    private static final double switchMeDerived(Global.activationSet literal, double input, double offset) throws AssertionError {
        switch (literal) {
            case sig:
                return sigmoidDerived(offset + input);
            case id:
                return identityDerived(offset + input);
            case relu:
                return reluDerived(offset + input);
            default:
                throw new AssertionError();
        }
    }

    private static final double switchMe(Global.activationSet literal, double input) throws AssertionError {
        switch (literal) {
            case sig:
                return sigmoid(input);
            case id:
                return identity(input);
            case relu:
                return relu(input);
            default:
                throw new AssertionError();
        }
    }

    private static final double switchMeDerived(Global.activationSet literal, double input) throws AssertionError {
        switch (literal) {
            case sig:
                return sigmoidDerived(input);
            case id:
                return identityDerived(input);
            case relu:
                return reluDerived(input);
            default:
                throw new AssertionError();
        }
    }

    public static final double kappaActivation(double[] inputs, double offset) {
        return switchMe(kappa, inputs, offset);
    }

    public static final double kappaActivationDerived(double[] inputs, double offset) {
        return switchMeDerived(kappa, inputs, offset);
    }

    public static final double lambdaActivation(double[] inputs, double offset) {
        return switchMe(lambda, inputs, offset);
    }

    public static final double lambdaActivationDerived(double[] inputs, double offset) {
        return switchMeDerived(lambda, inputs, offset);
    }

    public static final double kappaActivation(double input, double offset) {
        return switchMe(kappa, input, offset);
    }

    public static final double kappaActivationDerived(double input, double offset) {
        return switchMeDerived(kappa, input, offset);
    }

    public static final double lambdaActivation(double input, double offset) {
        return switchMe(lambda, input, offset);
    }

    public static final double lambdaActivationDerived(double input, double offset) {
        return switchMeDerived(lambda, input, offset);
    }

    public static final double kappaActivation(double input) {
        return switchMe(kappa, input);
    }

    public static final double kappaActivationDerived(double input) {
        return switchMeDerived(kappa, input);
    }

    public static final double lambdaActivation(double input) {
        return switchMe(lambda, input);
    }

    public static final double lambdaActivationDerived(double input) {
        return switchMeDerived(lambda, input);
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

    public static final double softMax(double[] inputs, double offset) {
        return 0;
    }

    public static final double softMaxDerived(double[] inputs, double offset) {
        return 0;
    }

    public static final double aggregation(double[] inputs) {
        switch (aggregation) {
            case avg:
                double avg = 0;
                for (double input : inputs) {
                    avg += input;
                }
                return avg / inputs.length;
            case max:
                double max = Double.MIN_VALUE;
                for (double input : inputs) {
                    if (input > max) {
                        max = input;
                    }
                }
                return max;
            default:
                throw new AssertionError();
        }
    }

    public static final double aggregationDerived(double[] inputs) {
        switch (aggregation) {
            case avg:
                return (1.0 / inputs.length);
            case max:
                return 1;
            default:
                throw new AssertionError();
        }
    }

    public static final double aggregationDerived(int input) {
        switch (aggregation) {
            case avg:
                return (1.0 / input);
            case max:
                return 1;
            default:
                throw new AssertionError();
        }
    }

    public static final int getMaximumIndex(double[] inputs) {
        double max = Double.MIN_VALUE;
        int index = 0;
        for (int i = 0; i < inputs.length; i++) {
            if (inputs[i] > max) {
                max = inputs[i];
                index = i;
            }
        }
        return index;
    }
}
