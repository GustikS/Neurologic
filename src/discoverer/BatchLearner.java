package discoverer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BatchLearner {
    private Map<Object, Double> weightAccumulator;

    public BatchLearner() {
        weightAccumulator = new HashMap<Object, Double>();
    }

    /** Method for handling the first learning run
     *
     * @param examples examples
     * @param last output node
     * @return list with balls from first run
     */
    public List<RoundElement> firstRun(List<Example> examples, KL last) {
        List<RoundElement> roundStore = new ArrayList<RoundElement>();
        for (Example e: examples) {
            Ball b = Solvator.solve(last,e);
            roundStore.add(new RoundElement(e,b));
        }

        return roundStore;
    }

    /** Method for refreshig weights in net
     *
     * @param w weights to refresh
     */
    public void refreshWeights(Weights w) {
        for (Map.Entry<Object, Double> entryWeights: w.getWeights().entrySet()) {
            Object o = entryWeights.getKey();
            Double newWeight = entryWeights.getValue();
            if (o instanceof Kappa) {
                Kappa k = (Kappa) o;
                try {
                    weightAccumulator.put(k, weightAccumulator.get(k) + newWeight);
                } catch (Exception exception) {
                    weightAccumulator.put(k, k.getWeight());
                }
            } else {
                KappaRule kr = (KappaRule) o;
                try {
                    weightAccumulator.put(kr, weightAccumulator.get(kr) + newWeight);
                } catch (Exception exception) {
                    weightAccumulator.put(kr, kr.getWeight());
                }
            }
        }
    }

    /** Method for running a computation
     *
     * @param last output node
     * @param exmaples examples
     * @param learningSteps how many backprops
     * @param learningEpochs how many resubstitutions
     * @param learnRate lrate
     * @param restartCount experimental restarted strategy
     */
    public double solve(KL last, List<Example> examples, int learningSteps, int learningEpochs, int restartCount, double learnRate) {
        List<RoundElement> roundStore = firstRun(examples, last);

        Results results = null;
        for (int a = 0; a < restartCount; a++) {
            System.out.println("Restart: " + a);
            for (int x = 0; x < learningEpochs; x++) {
                for (int i = 0; i < learningSteps; i++) {
                    results = new Results();
                    weightAccumulator.clear();
                    System.out.println("\t\t:::::::::::::: New Round :::::::::::::");
                    for (RoundElement result: roundStore) {
                        Example e = result.getExample();
                        Ball b = result.getBall();
                        Weights w = Backpropagation2.getNewWeights(b, e, Batch.YES, learnRate);
                        refreshWeights(w);
                        GroundInvalidator.invalidate(b);
                        b.val = Evaluator.evaluate(b);
                        results.add(new Result(b.val, e.getExpectedValue()));
                        System.out.println("New output for example with expected value " + e + ":\t" + b.val);
                    }
                    System.out.println("Learning error =\t" + results.getLearningError() + " (maj: " + results.getMajorityClass() + ")" + " (th: " + results.getThreshold() + ")");

                    for (Map.Entry<Object, Double> entr: weightAccumulator.entrySet()) {
                        Object o = entr.getKey();
                        double dr1 = entr.getValue();
                        if (o instanceof KappaRule) {
                            KappaRule kr = (KappaRule) o;
                            kr.setWeight(dr1);
                        } else {
                            Kappa k = (Kappa) o;
                            k.setWeight(dr1);
                        }
                    }
                }

                results = new Results();
                for (RoundElement roundElement: roundStore) {
                    Example e = roundElement.getExample();
                    Ball b = Solvator.solve(last,e);
                    roundElement.setBall(b);
                    results.add(new Result(b.val, e.getExpectedValue()));
                    System.out.println("Chance to resubstitute, output for\t" + e + "\t->\t" + b.val);

                    for (Map.Entry<Object, Double> entr: weightAccumulator.entrySet()) {
                        Object o = entr.getKey();
                        if (o instanceof KappaRule) {
                            KappaRule kr = (KappaRule) o;
                            kr.eraseGradient();
                            kr.step = 0.001;
                        } else {
                            Kappa k = (Kappa) o;
                            k.eraseGradient();
                            k.step = 0.001;
                        }
                    }
                }

                System.out.println("Learning error after resubstition =\t" + results.getLearningError() + " (maj: " + results.getMajorityClass() + ")" + " (th: " + results.getThreshold() + ")" +" (disp: " + results.getDispersion() + ")");
                double le = results.getLearningError();
                double th = results.getThreshold();
                double disp = results.getDispersion();

                if (Saver.isBetterThenBest(le, th, disp)) {
                    Saver.save(last, le, th, disp);
                }

                //Dotter.draw(last);
            }
            Invalidator.invalidate(last);
        }

        Saver.load();
        System.out.println("Saver loaded the best one");
        results = new Results();
        for (RoundElement roundElement: roundStore) {
            Example e = roundElement.getExample();
            Ball b = Solvator.solve(last,e);
            roundElement.setBall(b);
            results.add(new Result(b.val, e.getExpectedValue()));
            System.out.println("Substitution:\t" + e + "->\t" + b.val);
        }
        System.out.println("Saved Learning error after resubstition =\t" + results.getLearningError() + " (maj: " + results.getMajorityClass() + ")" + " (th: " + results.getThreshold() + ")" +" (disp: " + results.getDispersion() + ")");

        return results.getThreshold();
    }
}
