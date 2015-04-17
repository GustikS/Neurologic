/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Gusta
 */
public class BackpropDown {

    private static Weights weights = new Weights(); //storing intermediate weight updates(Kappa + Double tuple updates)
    static double learnRate = 0.1;

    public static Weights getNewWeights(Ball b, Example e, Batch batch, double learnRat) {
        learnRate = learnRat;
        weights.clear();
        GroundKL o = b.getLast(); //final Kappa node(assuming Kappa output only anyway)
        if (o == null) {
            return weights;
        }

        double baseDerivative = /*-1**/ (e.getExpectedValue() - b.val);  //output error-level derivative

        if (o instanceof GroundKappa) {
            derive((GroundKappa) o, baseDerivative);
        } else {
            derive((GroundLambda) o, baseDerivative);
        }

        return weights;
    }

    private static void derive(GroundKappa gk, double derivative) {
        if (Global.debugEnabled) {
            System.out.println("deriving: " + gk);
        }

        if (gk.isElement()) {
            return; //we do not update the weights for example atoms (but it would be possible and might be interesting)
        }

        gk.addGroundParentDerivative(derivative);   //aggregating(summing) the derivative from ground parent nodes
        gk.incrGroundParentsChecked();

        double myDerivative = 0;

        if (gk.getGroundParentsChecked() == gk.getGroundParents()) { //all parents checked
            double firstDerivative = firstPartKappaDerivative(gk);
            myDerivative = gk.getGroundParentDerivative() * firstDerivative;
            weights.addW(gk.getGeneral(), learnRate * myDerivative);   //updating offset weight (it's inner derivative is just 1, so no more computations needed)

            for (Tuple<GroundLambda, KappaRule> tup : gk.getDisjuncts()) {
                weights.addW(tup.y, learnRate * myDerivative * tup.x.getValue());    //updating Kappa-rule's weight (it's inner derivative is just the value of corresponding GroundLambda)
                derive(tup.x, myDerivative * tup.y.getWeight());    //dive into solving the corresponding GroundLambda
            }
        }
    }

    private static void derive(GroundLambda gl, double derivative) {
        if (Global.debugEnabled) {
            System.out.println("deriving: " + gl);
        }
        gl.addGroundParentDerivative(derivative);
        gl.incrGroundParentsChecked();

        if (gl.getGroundParentsChecked() == gl.getGroundParents()) {    //all parent's derivatives evaluated
            double firstDerivative = firstPartLambdaDerivative(gl);
            for (GroundKappa gk : gl.getConjuncts()) {
                derive(gk, gl.getGroundParentDerivative() * firstDerivative);
            }
        }

    }

//-----------------------the actual-level derivative(no recursion) based on GroundKappa/Lambda's output value(within a derived Sigmoid)
    private static double firstPartKappaDerivative(GroundKappa gk) {
        double result = gk.getGeneral().getWeight();
        for (Tuple<GroundLambda, KappaRule> t : gk.getDisjuncts()) {
            result += t.x.getValue() * t.y.getWeight();     //we need to sum it up again because the value we have is after sigmoid
        }
        result = Sigmoid.sigmoidDerived(result);    //and we need to feed it through a DERIVED sigmoid
        return result;
    }

    private static double firstPartLambdaDerivative(GroundLambda gl) {
        double result = gl.getGeneral().getInitialW();
        for (GroundKappa gk : gl.getConjuncts()) {
            result += gk.getValue();
        }
        result = Sigmoid.sigmoidDerived(result);
        return result;
    }
}
