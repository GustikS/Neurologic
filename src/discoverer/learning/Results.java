package discoverer.learning;

import discoverer.global.Global;
import discoverer.global.Glogger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Collection of results
 */
public class Results {

    private List<Result> results;
    private LearnStep actual;
    public ArrayList<LearnStep> past;
    int history = Global.history;

    public boolean convergence() {
        if (past.size() > 0) {
            int last = past.size() >= history ? history : past.size();
            last = past.size() - last;
            if ((past.get(last).sum100 / past.get(last).count100 - actual.sum100 / actual.count100) < Global.convergenceLimit) {
                Glogger.info("converged: " + past.get(last).sum100 / past.get(last).count100 + " - " + actual.sum100 / actual.count100 + " < " + Global.convergenceLimit);
                if (past.size() > history) {
                    return true;
                }
            }
            Glogger.info("not yet converged: " + past.get(last).sum100 / past.get(last).count100 + " - " + actual.sum100 / actual.count100 + " > " + Global.convergenceLimit);
        }
        return false;
    }

    public Results() {
        actual = new LearnStep();
        results = new ArrayList<Result>();
        past = new ArrayList<>(Global.cumMaxSteps);
    }

    public void clear() {
        actual = new LearnStep();
        results.clear();
    }

    public void add(Result result) {
        results.add(result);
    }

    public List<Result> get() {
        return results;
    }

    public double getThreshold() {
        if (actual.getThresh() == null) {
            compute();
        }
        return actual.getThresh();
    }

    public double getLearningError() {
        if (actual.getError() == null) {
            compute();
        }
        return actual.getError();
    }

    public double getMajorityClass() {
        if (actual.getMajorityErr() == null) {
            compute();
        }
        return actual.getMajorityErr();
    }

    public double getDispersion() {
        if (actual.getDispersion() == null) {
            compute();
        }
        return actual.getDispersion();
    }

    private void compute() {
        Collections.sort(results); //iterate in ascending order

        int bad = 0;
        for (Result current : results) {    //counting of negative exs.
            if (current.getExpected() == 0) {
                bad++;
            }
        }
        actual.setMajorityErr((Double) (double) bad / results.size());  //majorityErr = negative/all (there's majority of positive, or will be flipped)

        int bestBad = bad;
        int zeroes = bad;
        int ones = results.size() - bad;    //count of positive exs.
        double zeroSum = 0.0;
        double oneSum = 0.0;
        Result bestResult = null;
        Result nextResult = null;
        int i = 1;
        for (Result current : results) {    //searching for best threshold separation

            if (current.getExpected() == 1) {   //count positive/negative
                oneSum += current.getActual();  //sum their values
                bad += 1;
            } else {
                zeroSum += current.getActual();
                bad -= 1;
            }

            //deciding the threshold:
            if (bestBad > bad) {
                bestResult = current;
                bestBad = bad;
                if (i < results.size()) {
                    nextResult = results.get(i);
                } else {
                    nextResult = bestResult;
                }
            }
            i++;
        }

        actual.setDispersion((Double) Math.abs((zeroSum / zeroes) - (oneSum / ones)));

        actual.setError((Double) (double) bestBad / results.size());    //?what

        if (bestResult != null) {
            double left = bestResult.getActual();
            double right = nextResult.getActual();
            actual.setThresh((Double) left + (right - left) / 2);
        } else {
            actual.setThresh((Double) 0.0);
        }

        actual.setMajorityErr(actual.getMajorityErr() > 0.5 ? 1 - actual.getMajorityErr() : actual.getMajorityErr());   //correct flip of majority

        //---------update running avg for past---------
        actual.sum100 = actual.getError();
        actual.count100 = 1;
        if (past.size() > 0) {
            actual.count100 = past.size() + 1;
            actual.sum100 += past.get(past.size() - 1).sum100;
        }
        if (past.size() >= history) {
            actual.count100 = history;
            actual.sum100 -= past.get(past.size() - history).getError();
        }

        Glogger.info(actual.toString());
        //convergence();
        testcheck();
        past.add(actual);
    }

    void testcheck() {
        double error = 0;
        for (Result res : results) {
            double clas = res.getActual() > actual.getThresh() ? 1.0 : 0.0;
            //Glogger.info("Classified -> " + clas + " Expected -> " + example.getExpectedValue() + " Out -> " + ballValue + " Thresh -> " + res.getThreshold());
            if (clas != res.getExpected()) {
                error += 1.0;
            }
        }
        double err = error / results.size();
        Glogger.info("Fold Train error calculated : " + err);
    }
}
