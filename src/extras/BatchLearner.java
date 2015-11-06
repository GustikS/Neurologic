package extras;

import discoverer.construction.example.Example;
import discoverer.construction.network.KL;
import discoverer.construction.network.Kappa;
import discoverer.construction.network.MolecularTemplate;
import discoverer.construction.network.rules.KappaRule;
import discoverer.global.Global;
import discoverer.global.Settings;
import discoverer.grounding.evaluation.GroundedTemplate;
import discoverer.grounding.Grounder;
import discoverer.grounding.evaluation.Evaluator;
import discoverer.learning.Invalidator;
import discoverer.learning.Result;
import discoverer.learning.Results;
import discoverer.learning.Sample;
import discoverer.learning.Saver;
import discoverer.learning.Weights;
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
    public List<Sample> firstRun(List<Example> examples, KL last) {
        List<Sample> roundStore = new ArrayList<Sample>();
        for (Example e: examples) {
            GroundedTemplate b = Grounder.solve(last,e);
            roundStore.add(new Sample(e,b));
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
                    weightAccumulator.put(k, k.getOffset());
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
     * @param roundStore
     * @param exmaples examples
     * @param learningSteps how many backprops
     * @param learningEpochs how many resubstitutions
     * @param learnRate lrate
     * @param restartCount experimental restarted strategy
     */
    public Results solve(MolecularTemplate last, List<Sample> roundStore) {
        
        Results results = null;
        for (int a = 0; a < Settings.restartCount; a++) {
            System.out.println("Restart: " + a);
            for (int x = 0; x < Settings.learningEpochs; x++) {
                for (int i = 0; i < Settings.learningSteps; i++) {
                    results = new Results();
                    weightAccumulator.clear();
                    System.out.println("\t\t:::::::::::::: New Round :::::::::::::");
                    for (Sample result: roundStore) {
                        Example e = result.getExample();
                        GroundedTemplate b = result.getBall();
                        Weights w = BackpropGroundKappa.getNewWeights(b, e, Global.batch.YES, Settings.learnRate);
                        refreshWeights(w);
                        //GroundInvalidator.invalidate(b);
                        b.invalidateNeurons();
                        b.valMax = Evaluator.evaluateMax(b);
                        results.add(new Result(b.valMax, e.getExpectedValue()));
                        System.out.println("New output for example with expected value " + e + ":\t" + b.valMax);
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
                            k.setOffset(dr1);
                        }
                    }
                }

                results = new Results();
                for (Sample roundElement: roundStore) {
                    Example e = roundElement.getExample();
                    GroundedTemplate b = Grounder.solve(last.last,e);
                    roundElement.setBall(b);
                    results.add(new Result(b.valMax, e.getExpectedValue()));
                    System.out.println("Chance to resubstitute, output for\t" + e + "\t->\t" + b.valMax);

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
        for (Sample roundElement: roundStore) {
            Example e = roundElement.getExample();
            GroundedTemplate b = Grounder.solve(last.last,e);
            roundElement.setBall(b);
            results.add(new Result(b.valMax, e.getExpectedValue()));
            System.out.println("Substitution:\t" + e + "->\t" + b.valMax);
        }
        System.out.println("Saved Learning error after resubstition =\t" + results.getLearningError() + " (maj: " + results.getMajorityClass() + ")" + " (th: " + results.getThreshold() + ")" +" (disp: " + results.getDispersion() + ")");

        return results;
    }
}
