package discoverer;

public class Backpropagation2 {
    private static Weights weights = new Weights();

    /** Method for computing new weights with modified backpropagation 2nd version
     *
     * @param b ball representing the network
     * @param e given example
     * @param batch batch mode?
     * @param learnRate learn rate
     * @return modified weights
     */
    public static Weights getNewWeights(Ball b, Example e, Batch batch, double learnRate) {
        weights.clear();
        Object o = b.getLast();

        for (GroundKappa gk: GroundKappaGetter.getAllGroundKappas(b)) {
            backpropLines(o, gk, b, e, batch, learnRate);
        }
        return weights;
    }

    private static void backpropLines(Object o, GroundKappa gk, Ball b, Example e, Batch batch, double learnRate) {
        for (Tuple<GroundLambda, KappaRule> t: gk.getDisjuncts()) {
            Triple triple = new Triple(gk, t.x, t.y);
            tripleLearn(o, triple, b, e, batch, learnRate);
        }
        soloLearn(o, gk, b, e, batch, learnRate);
    }

    private static void soloLearn(Object o, GroundKappa gk, Ball b, Example e, Batch batch, double learnRate) {
        double weightDerivation;
        if (o instanceof GroundKappa)
            weightDerivation = kappaDerivative((GroundKappa) o, (Object) gk);
        else
            weightDerivation = lambdaDerivative((GroundLambda) o, (Object) gk);

        double base = -2 * (e.getExpectedValue() - b.val);

        double gradient = base * weightDerivation;
        double weight;
        if (batch == Batch.YES)
            //weight = Rprop.computeWeight(gradient, kr);
            weight = - learnRate * gradient;
        else
            weight =  - learnRate * gradient;
        //double weight = - 0.1 * gradient;
        //System.out.println(gk.getGeneral() + "\t-> " + weight);
        weights.addW(gk.getGeneral(), weight);
    }

    private static void tripleLearn(Object o, Triple t, Ball b, Example e, Batch batch, double learnRate) {
        double weightDerivation;
        if (o instanceof GroundKappa)
            weightDerivation = kappaDerivative((GroundKappa) o, (Object) t);
        else
            weightDerivation = lambdaDerivative((GroundLambda) o, (Object) t);

        double base = -2 * (e.getExpectedValue() - b.val);

        double gradient = base * weightDerivation;
        double weight;
        if (batch == Batch.YES)
            //weight = Rprop.computeWeight(gradient, kr);
            weight = - learnRate * gradient;
        else
            weight =  - learnRate * gradient;
        //double weight = - 0.1 * gradient;
        //System.out.println(t.kr + "(" + t.gk + ")" + "\t-> " + weight);
        weights.addW(t.kr, weight);
    }

    private static double kappaDerivative(GroundKappa gk, Object kr) {
        if (gk.isElement())
            return 0.0;

        double result = firstPartKappaDerivative(gk);

        if (kr instanceof GroundKappa) {
            if (gk == kr)
                return result;
        } else {
            for (Tuple<GroundLambda, KappaRule> t: gk.getDisjuncts()) {
                Triple triple = (Triple) kr;
                if (t.x == triple.gl && t.y == triple.kr && gk == triple.gk) {
                    //System.out.println(t.x + " = " + t.x.getValue());
                    return result * t.x.getValue();
                }
            }
        }

        double secondPart = 0;
        for (Tuple<GroundLambda, KappaRule> t: gk.getDisjuncts())
            secondPart += t.y.getWeight() * lambdaDerivative(t.x, kr);

        return result * secondPart;
    }

    private static double lambdaDerivative(GroundLambda gl, Object kr) {
        double result = firstPartLambdaDerivative(gl);

        double secondPart = 0;
        for (GroundKappa gk: gl.getConjuncts())
            secondPart += kappaDerivative(gk, kr);

        return result * secondPart;
    }


    private static double firstPartKappaDerivative(GroundKappa gk) {
        double result = gk.getGeneral().getWeight();
        for (Tuple<GroundLambda, KappaRule> t: gk.getDisjuncts())
            result += t.x.getValue() * t.y.getWeight();

        result = Sigmoid.sigmoidDerived(result);
        return result;
    }

    private static double firstPartLambdaDerivative(GroundLambda gl) {
        double result = gl.getGeneral().getInitialW();
        for (GroundKappa gk: gl.getConjuncts())
            result += gk.getValue();

        result = Sigmoid.sigmoidDerived(result);
        return result;
    }
}
