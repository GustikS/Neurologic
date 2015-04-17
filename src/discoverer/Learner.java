package discoverer;

import java.util.*;

/**
 * Perform learning algorithm
 */
public class Learner {
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

    public void refreshWeights(Weights w) {
        for (Map.Entry<Object, Double> entryWeights: w.getWeights().entrySet()) {
            Object o = entryWeights.getKey();
            Double newWeight = entryWeights.getValue();
            if (o instanceof Kappa) {
                Kappa k = (Kappa) o;
                k.setWeight(k.getWeight() + newWeight);
            } else {
                KappaRule kr = (KappaRule) o;
                kr.setWeight(kr.getWeight() + newWeight);
            }
        }
    }

    public double solve(KL last, List<Example> examples, int learningSteps, int learningEpochs, int restartCount, double learnRate) {
        List<RoundElement> roundStore = firstRun(examples, last);

        Results results = null;
        for (int a = 0; a < restartCount; a++) {
            System.out.println("Restart: " + a);
            for (int x = 0; x < learningEpochs; x++) {
                for (int i = 0; i < learningSteps; i++) {
                    Results res = new Results();
                    System.out.println("------------------------");
                    for (RoundElement result: roundStore) {
                        Example e = result.getExample();
                        Ball b = result.getBall();
                        Weights w = Backpropagation2.getNewWeights(b, e, Batch.NO, learnRate);
                        refreshWeights(w);
                        b.val = Evaluator.evaluate(b);
                        res.add(new Result(b.val, e.getExpectedValue()));
                        System.out.println("Weight learning:\t" + e + "->\t" + b.val);
                    }
                    System.out.println("Learning error =\t" + res.getLearningError() + " (maj: " + res.getMajorityClass() + ")" + " (disp: " + res.getThreshold() + ")");
                }

                results = new Results();
                for (RoundElement roundElement: roundStore) {
                    Example e = roundElement.getExample();
                    Ball b = Solvator.solve(last,e);
                    roundElement.setBall(b);
                    results.add(new Result(b.val, e.getExpectedValue()));
                    System.out.println("Substitution:\t" + e + "->\t" + b.val);
                }
                System.out.println("Learning error after resubstition =\t" + results.getLearningError() + " (maj: " + results.getMajorityClass() + ")" + " (th: " + results.getThreshold() + ")" +" (disp: " + results.getDispersion() + ")");
                double le = results.getLearningError();
                double th = results.getThreshold();
                double disp = results.getDispersion();

                if (Saver.isBetterThenBest(le, th, disp))
                    Saver.save(last, le, th, disp);

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
        System.out.println("Saved Learning error after resubstition =\t" + results.getLearningError() + " (maj: " + results.getMajorityClass() + ")" + " (th: " + results.getThreshold() + ")" +" (th: " + results.getDispersion() + ")");

        return results.getThreshold();
    }
}
