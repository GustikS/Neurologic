package extras;

import discoverer.grounding.evaluation.GroundedTemplate;
import discoverer.construction.example.Example;
import discoverer.grounding.network.GroundKappa;
import discoverer.grounding.network.GroundLambda;
import discoverer.construction.template.Kappa;
import discoverer.construction.template.rules.KappaRule;
import discoverer.global.Global;
import discoverer.learning.functions.Activations;
import discoverer.global.Tuple;
import discoverer.learning.Weights;
import java.util.HashSet;
import java.util.Set;

public class BackpropKappa {

    private static Set<Kappa> activeKappas = new HashSet<Kappa>();
    private static Weights weights = new Weights();

    /**
     * Method for computing new weights with modified backpropagation
     *
     * @param b ball representing the network
     * @param e given example
     * @param batch batch mode?
     * @param learnRate learn rate
     * @return modified weights
     */
    public static Weights getNewWeights(GroundedTemplate b, Example e, boolean batch, double learnRate) {
        Object o = b.getLast();
        activeKappas.clear();
        weights.clear();
        if (o == null) {
            return weights;
        }

        //double base = -2 * (e.getExpectedValue() - b.val);
        double base = -1 * (e.getExpectedValue() - b.valMax);

        for (KappaRule kr : b.getActiveRules()) {
            activeKappas.add(kr.getHead().getParent());
            double weightDerivation;
            if (o instanceof GroundKappa) {
                weightDerivation = kappaDerivative((GroundKappa) o, (Object) kr);
            } else {
                weightDerivation = lambdaDerivative((GroundLambda) o, (Object) kr);
            }
            double gradient = base * weightDerivation;
            double weight;
            if (batch) //weight = Rprop.computeWeight(gradient, kr);
            {
                weight = -learnRate * gradient;
            } else {
                weight = -learnRate * gradient;
            }
            //double weight = - 0.1 * gradient;
            weights.addW(kr, weight);
        }

        for (Kappa k : activeKappas) {
            double weightDerivation;
            if (o instanceof GroundKappa) {
                weightDerivation = kappaDerivative((GroundKappa) o, (Object) k);
            } else {
                weightDerivation = lambdaDerivative((GroundLambda) o, (Object) k);
            }
            double gradient = base * weightDerivation;
            double weight;
            if (batch) //weight = Rprop.computeWeight(gradient, k);
            {
                weight = -learnRate * gradient;
            } else {
                weight = -learnRate * gradient;
            }
            //double weight = - 0.1 * gradient;
            weights.addD(k, weight);
        }

        return weights;
    }

    /**
     * Compute derivative for kappa node
     *
     * @param gk ground kappa node
     * @param kr kappa-clause rule
     * @return derivative of kappa node
     */
    public static double kappaDerivative(GroundKappa gk, Object kr) {
        if (gk.isElement()) {
            return 0.0;
        }

        double result = firstPartKappaDerivative(gk);

        if (kr instanceof Kappa) {
            if (gk.getGeneral() == kr) {
                return result;
            }
        } else {
            for (Tuple<GroundLambda, KappaRule> t : gk.getDisjuncts()) {
                if (t.y == kr) {
                    return result * t.x.getValue();
                }
            }
        }

        double secondPart = 0;
        for (Tuple<GroundLambda, KappaRule> t : gk.getDisjuncts()) {
            secondPart += t.y.getWeight() * lambdaDerivative(t.x, kr);
        }

        return result * secondPart;
    }

    /**
     * Compute derivative for lambda node
     *
     * @param gl ground lambda node
     * @param kr kappa-clause rule
     * @return derivative of lambda node
     */
    public static double lambdaDerivative(GroundLambda gl, Object kr) {
        double result = firstPartLambdaDerivative(gl);

        double secondPart = 0;
        for (GroundKappa gk : gl.getConjuncts()) {
            secondPart += kappaDerivative(gk, kr);
        }

        return result * secondPart;
    }

    /**
     * Compute first part of derivative for kappa node
     *
     * @param gk ground kappa node
     * @return derivative of first part of kappa node
     */
    public static double firstPartKappaDerivative(GroundKappa gk) {
        double result = gk.getGeneral().getOffset();
        for (Tuple<GroundLambda, KappaRule> t : gk.getDisjuncts()) {
            result += t.x.getValue() * t.y.getWeight();
        }

//        result = Activations.kappaActivationDerived(result);
        return result;
    }

    /**
     * Compute first part of derivative for lambda node
     *
     * @param gl ground lambda node
     * @return derivative of first part of lambda node
     */
    public static double firstPartLambdaDerivative(GroundLambda gl) {
        double result = gl.getGeneral().getOffset();
        for (GroundKappa gk : gl.getConjuncts()) {
            result += gk.getValue();
        }

//        result = Activations.lambdaActivationDerived(result);
        return result;
    }
}
