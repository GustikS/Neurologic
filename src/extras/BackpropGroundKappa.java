package extras;

import discoverer.grounding.evaluation.struct.GroundKappaGetter;
import discoverer.grounding.evaluation.Ball;
import discoverer.global.Batch;
import discoverer.construction.example.Example;
import discoverer.grounding.network.GroundKappa;
import discoverer.grounding.network.GroundLambda;
import discoverer.construction.network.rules.KappaRule;
import discoverer.learning.backprop.functions.Activations;
import discoverer.global.Tuple;
import discoverer.learning.Weights;

/**
 * active rules from backprop1 replaced with GroundKappaGetter (they are all
 * ground kappas anyway)
 *
 *
 *
 * sololearn = for (Kappa k: activeKappas) tripple learn = for (KappaRule kr:
 * b.getActiveRules())
 */
public class BackpropGroundKappa {

    private static Weights weights = new Weights();

    /**
     * The methods recursively computes derivatives w.r.t a given GroundKappa by
     * going through the full proof-tree if caching is used then there is only
     * one unique GroundKappa/Lambda for every particular grounding(acyclic
     * structure) (is recursively sums up derivatives from all
     * conjuncts/disjuncts within rules) - shouldn't it work on the level of
     * Kappa then? so there is a lot of redundant computation here
     *
     * @param b ball representing the network
     * @param e given example
     * @param batch batch mode?
     * @param learnRate learn rate
     * @return modified weights
     */
    public static Weights getNewWeights(Ball b, Example e, Batch batch, double learnRate) {
        weights.clear();
        Object o = b.getLast(); //final Kappa node
        if (o == null) {
            return weights;
        }

        for (GroundKappa gk : GroundKappaGetter.getAllGroundKappas(b)) { //gets really Kappas only!
            backpropLines(o, gk, b, e, batch, learnRate);
        }
        return weights;
    }

    /**
     * does trippleLearn for each disjunct of the groundKappa (output) node
     *
     * @param o
     * @param gk
     * @param b
     * @param e
     * @param batch
     * @param learnRate
     */
    private static void backpropLines(Object o, GroundKappa gk, Ball b, Example e, Batch batch, double learnRate) {
        for (Tuple<GroundLambda, KappaRule> t : gk.getDisjuncts()) {
            Triple triple = new Triple(gk, t.x, t.y);
            tripleLearn(o, triple, b, e, batch, learnRate); //derivative of a KAPPA RULE
        }
        soloLearn(o, gk, b, e, batch, learnRate);   //derivative of a KAPPA NODE'S WEIGHT
    }

    /**
     * final output weight update
     *
     * @param o
     * @param gk
     * @param b
     * @param e
     * @param batch
     * @param learnRate
     */
    private static void soloLearn(Object o, GroundKappa gk, Ball b, Example e, Batch batch, double learnRate) {
        double weightDerivation;
        if (o instanceof GroundKappa) {
            weightDerivation = kappaDerivative((GroundKappa) o, (Object) gk);
        } else {
            weightDerivation = lambdaDerivative((GroundLambda) o, (Object) gk);
        }

        //double base = -2 * (e.getExpectedValue() - b.val);
        double base = -1 * (e.getExpectedValue() - b.valMax);

        double gradient = base * weightDerivation;
        double weight;
        if (batch == Batch.YES) //weight = Rprop.computeWeight(gradient, kr);
        {
            weight = -learnRate * gradient;
        } else {
            weight = -learnRate * gradient;
        }
        //double weight = - 0.1 * gradient;
        //System.out.println(gk.getGeneral() + "\t-> " + weight);
        weights.addW(gk.getGeneral(), weight);
    }

    /**
     *
     * @param o the last network's node
     * @param t
     * @param b
     * @param e
     * @param batch
     * @param learnRate
     */
    private static void tripleLearn(Object o, Triple t, Ball b, Example e, Batch batch, double learnRate) {
        double weightDerivation;
        if (o instanceof GroundKappa) {
            weightDerivation = kappaDerivative((GroundKappa) o, (Object) t);
        } else {
            weightDerivation = lambdaDerivative((GroundLambda) o, (Object) t);
        }

        //double base = -2 * (e.getExpectedValue() - b.val);
        double base = -1 * (e.getExpectedValue() - b.valMax);  //base example error derivative

        double gradient = base * weightDerivation;
        double weight;
        if (batch == Batch.YES) //weight = Rprop.computeWeight(gradient, kr);
        {
            weight = -learnRate * gradient;
        } else {
            weight = -learnRate * gradient;
        }
        //double weight = - 0.1 * gradient;
        //System.out.println(t.kr + "(" + t.gk + ")" + "\t-> " + weight);
        weights.addW(t.kr, weight);
    }

    //------------the backpropagation/gradient-descent itself (recursive)
    /**
     * recursively computing gradient updates, starting from a GroundKappa node
     *
     * @param gk - actual output object to compute derivative against
     * @param kr - the weight in partial derivative we look for(denominator)
     * @return
     */
    private static double kappaDerivative(GroundKappa gk, Object kr) {
        if (gk.isElement()) {
            return 0.0;
        }

        double result = firstPartKappaDerivative(gk);

        if (kr instanceof GroundKappa) {
            if (gk == kr) {
                return result;
            }
        } else {
            for (Tuple<GroundLambda, KappaRule> t : gk.getDisjuncts()) { // when gk is an example's element(isElement) (has no disjuncts) this is skipped and we return from recursion
                Triple triple = (Triple) kr;
                if (t.x == triple.gl && t.y == triple.kr && gk == triple.gk) {
                    //System.out.println(t.x + " = " + t.x.getValue());
                    return result * t.x.getValue();
                }
            }
        }

        double secondPart = 0;
        for (Tuple<GroundLambda, KappaRule> t : gk.getDisjuncts()) // when gk is an example's element(isElement) (has no disijuncts) this is skipped and we return from recursion
        {
            secondPart += t.y.getWeight() * lambdaDerivative(t.x, kr);  //summing individual disjuncts's contributions
        }
        return result * secondPart;
    }

    /**
     * recursively computing gradient updates, starting from a GroundLambda node
     *
     * @param gl
     * @param kr
     * @return
     */
    private static double lambdaDerivative(GroundLambda gl, Object kr) {
        double result = firstPartLambdaDerivative(gl);

        double secondPart = 0;
        for (GroundKappa gk : gl.getConjuncts()) {
            secondPart += kappaDerivative(gk, kr);
        }

        return result * secondPart;
    }

//-----------------------the actual-level derivative(no recursion) based on GroundKappa/Lambda's output value(within a derived Sigmoid)
    private static double firstPartKappaDerivative(GroundKappa gk) {
        double result = gk.getGeneral().getOffset();
        for (Tuple<GroundLambda, KappaRule> t : gk.getDisjuncts()) {
            result += t.x.getValue() * t.y.getWeight();     //we need to sum it up again because the value we have is after sigmoid
        }
        result = Activations.kappaActivationDerived(result);    //and we need to feed it through a DERIVED sigmoid
        return result;
    }

    private static double firstPartLambdaDerivative(GroundLambda gl) {
        double result = gl.getGeneral().getOffset();
        for (GroundKappa gk : gl.getConjuncts()) {
            result += gk.getValue();
        }

        result = Activations.lambdaActivationDerived(result);
        return result;
    }
}
