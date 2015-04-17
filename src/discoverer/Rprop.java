package discoverer;

/**
 * Experimental!
 * Try to perform RPROP in learning, BAD RESULTS!
 */
public class Rprop {
    private static final double etaPos = 1.3;
    private static final double etaNeg = 0.5;
    private static final double maxStep = 10;
    private static final double minStep = 1E-6;

    public static double computeWeight(double gradient, KappaRule kr) {
        if (kr.gradientIsNull()) {
            kr.setGradient(gradient);
            kr.deltaW = - Math.signum(gradient) * kr.step;
            return kr.deltaW;
        }

        double change = kr.getGradient() * gradient;
        double weight = 0;

        if (change > 0) {
            kr.step = Math.min(kr.step * etaPos, maxStep);
            kr.deltaW = - Math.signum(gradient) * kr.step;
            weight = kr.deltaW;
            kr.setGradient(gradient);
        } else if (change < 0) {
            kr.step = Math.max(kr.step * etaNeg, minStep);
            kr.setGradient(0);
            weight = - kr.deltaW;
        } else {
            kr.deltaW = - Math.signum(gradient) * kr.step;
            weight = kr.deltaW;
            kr.setGradient(gradient);
        }

        return weight;
    }

    public static double computeWeight(double gradient, Kappa k) {
        if (k.gradientIsNull()) {
            k.setGradient(gradient);
            k.deltaW = - Math.signum(gradient) * k.step;
            return k.deltaW;
        }

        double change = k.getGradient() * gradient;
        double newW = 0;

        if (change > 0) {
            k.step = Math.min(k.step * etaPos, maxStep);
            k.deltaW = - Math.signum(gradient) * k.step;
            newW = k.deltaW;
            k.setGradient(gradient);
        } else if (change < 0) {
            k.step = Math.max(k.step * etaNeg, minStep);
            k.setGradient(0);
            newW = - k.deltaW;
        } else {
            k.deltaW = - Math.signum(gradient) * k.deltaW;
            newW = k.deltaW;
            k.setGradient(gradient);
        }

        return newW;
    }
}
