package discoverer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Collection of results
 */
public class Results {
    private List<Result> results;
    private Double thresh, error, majorityErr, dispersion;

    public Results() {
        results = new ArrayList<Result>();
    }

    public void clear() {
        thresh = null;
        error = null;
        majorityErr = null;
        dispersion = null;
    }

    public void add(Result result) {
        results.add(result);
    }

    public List<Result> get() {
        return results;
    }

    public double getThreshold() {
        if (thresh == null)
            compute();
        return thresh;
    }

    public double getLearningError() {
        if (error == null)
            compute();
        return error;
    }

    public double getMajorityClass() {
        if (majorityErr == null)
            compute();
        return majorityErr;
    }

    public double getDispersion() {
        if (dispersion == null)
            compute();
        return dispersion;
    }

    private void compute() {
        Collections.sort(results);

        int bad = 0;
        for (Result current: results)
            if (current.getExpected() == 0)
                bad++;
        majorityErr = (double) bad / results.size();

        int bestBad = bad;
        int zeroes = bad;
        int ones = results.size() - bad;
        double zeroSum = 0.0;
        double oneSum = 0.0;
        Result bestResult = null;
        Result nextResult = null;
        int i = 1;
        for (Result current: results) {
            bad = current.getExpected() == 1 ? bad+1 : bad-1;

            if (current.getExpected() == 1)
                oneSum += current.getActual();
            else
                zeroSum += current.getActual();


            if (bestBad > bad) {
                bestResult = current;
                bestBad = bad;
                if (i < results.size())
                    nextResult = results.get(i);
                else
                    nextResult = bestResult;
            }
            i++;
        }
        dispersion = Math.abs((zeroSum / zeroes) - (oneSum / ones));

        error = (double) bestBad / results.size();
        if (bestResult != null) {
            double left = bestResult.getActual();
            double right = nextResult.getActual();
            thresh = left + (right - left) / 2;
        } else {
            thresh = 0.0;
        }
        majorityErr = majorityErr > 0.5 ? 1 - majorityErr : majorityErr;
    }
}
