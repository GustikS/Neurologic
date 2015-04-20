/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer.learning.backprop;

import discoverer.grounding.evaluation.Ball;
import discoverer.global.Batch;
import discoverer.construction.example.Example;
import discoverer.grounding.network.GroundKL;
import discoverer.grounding.network.GroundKappa;
import discoverer.grounding.network.GroundLambda;
import discoverer.construction.network.rules.KappaRule;
import discoverer.learning.backprop.functions.Activations;
import discoverer.global.Tuple;
import discoverer.learning.Weights;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Gusta
 */
public class BackpropDownAvg {

    private static Weights weights = new Weights(); //storing intermediate weight updates(Kappa + Double tuple updates)

    public static Weights getNewWeights(Ball b, Example e, Batch batch, double learnRate) {
        weights.clear();
        GroundKL o = b.getLast(); //final Kappa node(assuming Kappa output only anyway)
        if (o == null) {
            return weights;
        }

        double baseDerivative = /*(-1)**/ (e.getExpectedValue() - b.valAvg);  //output error-level derivative

        if (o instanceof GroundKappa) {
            derive((GroundKappa) o, learnRate * baseDerivative);
        } else {
            derive((GroundLambda) o, learnRate * baseDerivative);
        }

        return weights;
    }

    private static void derive(GroundKappa gk, double derivative) {

        if (gk.isElement()) {
            return; //we do not update the weights for example atoms (but it would be possible and might be interesting)
        }

        gk.addGroundParentDerivative(derivative);   //aggregating(summing) the derivative from ground parent nodes
        gk.incrGroundParentsChecked();

        double myDerivative = 0;

        if (gk.getGroundParentsChecked() == gk.getGroundParents()) { //all parents checked
            double firstDerivative = firstPartKappaDerivative(gk);
            myDerivative = gk.getGroundParentDerivative() * firstDerivative;
            weights.addW(gk.getGeneral(), myDerivative);   //updating offset weight (it's inner derivative is just 1, so no more computations needed)

            for (Tuple<HashSet<GroundLambda>, KappaRule> tup : gk.getDisjunctsAvg()) {
                weights.addW(tup.y, myDerivative * GroundKL.getAvgValFrom(tup.x));    //updating Kappa-rule's weight (it's inner derivative is just the value of corresponding GroundLambda)
                for (GroundLambda gl : tup.x) {
                    derive(gl, myDerivative * tup.y.getWeight() * (1.0 / tup.x.size()));    //dive into solving the corresponding GroundLambda (they are averaged so each gl provides 1/n value only)
                }
            }
        }
    }

    private static void derive(GroundLambda gl, double derivative) {
        gl.addGroundParentDerivative(derivative);
        gl.incrGroundParentsChecked();

        if (gl.getGroundParentsChecked() == gl.getGroundParents()) {    //all parent's derivatives evaluated
            double firstDerivative = firstPartLambdaDerivative(gl);
            for (Map.Entry<GroundKappa, Integer> wGk : gl.getConjunctsAvg().entrySet()) {
                //each GroundKappa in conjunction is weighted by the occurence, and they are all averaged by the number of body groundings
                derive(wGk.getKey(), gl.getGroundParentDerivative() * firstDerivative * wGk.getValue() * (1.0 / gl.getConjunctsCountForAvg()));
            }
        }
    }

//-----------------------the actual-level derivative(no recursion) based on GroundKappa/Lambda's output value(within a derived Sigmoid)
    private static double firstPartKappaDerivative(GroundKappa gk) {
        double result = gk.getGeneral().getWeight();
        for (Tuple<HashSet<GroundLambda>, KappaRule> t : gk.getDisjunctsAvg()) {
            result += GroundKL.getAvgValFrom(t.x) * t.y.getWeight();     //we need to sum it up again because the value we have is after sigmoid
        }
        result = Activations.kappaActivationDerived(result);    //and we need to feed it through a DERIVED sigmoid
        return result;
    }

    private static double firstPartLambdaDerivative(GroundLambda gl) {
        double result = gl.getGeneral().getInitialW();
        double avg = 0;
        for (Map.Entry<GroundKappa, Integer> gk : gl.getConjunctsAvg().entrySet()) {
            avg += gk.getKey().getValueAvg() * gk.getValue();
        }
        avg /= gl.getConjunctsCountForAvg();    //they are all averaged by the number of body groundings
        result = Activations.lambdaActivationDerived(result + avg);    //
        return result;
    }
}
