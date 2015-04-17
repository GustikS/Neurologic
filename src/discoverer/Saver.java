package discoverer;

import java.util.HashSet;
import java.util.Set;

/**
 * Saver for the best learned network so far
 */
public class Saver {
    private static Set<Tuple<KappaRule, Double>> save = new HashSet<Tuple<KappaRule, Double>>();
    private static Set<Tuple<Kappa, Double>> saveK = new HashSet<Tuple<Kappa, Double>>();
    private static Double learnError, threshold, dispersion;

    public static void save(KL network, double le, double th, double disp) {
        save.clear();
        saveK.clear();
        learnError = le;
        threshold = th;
        dispersion = disp;
        if (network instanceof Kappa)
            save((Kappa) network);
        else
            save((Lambda) network);
    }

    private static void save(Kappa k) {
        if (k.isElement())
            return;

        saveK.add(new Tuple<Kappa, Double>(k, k.getWeight()));
        for (KappaRule kr: k.getRules()) {
            Tuple<KappaRule, Double> t = new Tuple<KappaRule, Double>(kr, kr.getWeight());
            save.add(t);
            save(kr.getBody().getParent());
        }
    }

    private static void save(Lambda l) {
        for (SubK sk: l.getRule().getBody()) {
            save(sk.getParent());
        }
    }

    public static void load() {
        for (Tuple<KappaRule, Double> t: save)
            t.x.setWeight(t.y);

        for (Tuple<Kappa, Double> t: saveK)
            t.x.setWeight(t.y);

        System.out.println("Loading: " + learnError + ", " + threshold);
        learnError = null;
        threshold = null;
        dispersion = null;
    }

    public static boolean isBetterThenBest(double le, double th, double disp) {
        if (learnError == null) return true;
        if (learnError >= le) return true;
        return false;
        /*
         *if (learnError > le) return true;
         *if (learnError < le) return false;
         *if (dispersion > disp) return false;
         *if (dispersion < disp) return true;
         *if (Math.abs(threshold - 0.5) <= Math.abs(th - 0.5)) return false;
         *return true;
         */
    }
}
