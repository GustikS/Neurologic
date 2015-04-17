package discoverer;

/**
 * Result object
 */
public class Result implements Comparable<Result> {
    private double expected;
    private double actual;

    public Result(double act, double exp) {
        expected = exp;
        actual = act;
    }

    public double getActual() {
        return actual;
    }

    public double getExpected() {
        return expected;
    }

    @Override
    public int compareTo(Result r) {
        return Double.compare(actual, r.getActual());
    }
}
