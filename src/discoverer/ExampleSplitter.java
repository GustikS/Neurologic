package discoverer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * Splitter for performing n-fold crossval
 */
public class ExampleSplitter {
    private int foldCount;
    private int testFold = 0;
    private List<List<Example>> folds;

    public ExampleSplitter(int k, List<Example> ex) {
        folds = new ArrayList<List<Example>>();

        List<Example> positives = getPositives(ex);
        List<Example> negatives = getNegatives(ex);

        int foldLen = (int) Math.floor((double) ex.size() / k);
        foldCount = (int) Math.ceil((double) ex.size() / foldLen);
        int positivesInFold = (int) Math.ceil((double) positives.size() / ex.size() * foldLen);

        int n = 0;
        int p = 0;

        while (n < negatives.size() || p < positives.size()) {
            List<Example> fold = new ArrayList<Example>();
            for (int pNeeded = 0; pNeeded < positivesInFold && p < positives.size(); pNeeded++) {
                fold.add(positives.get(p++));
            }

            while (fold.size() < foldLen && n < negatives.size()) {
                fold.add(negatives.get(n++));
            }

            Collections.shuffle(fold);
            folds.add(fold);
        }

    }

    private List<Example> getPositives(List<Example> ex) {
        List<Example> positives = new ArrayList<Example>();
        for (Example e: ex) {
            if (e.getExpectedValue() == 1)
                positives.add(e);
        }

        return positives;
    }

    private List<Example> getNegatives(List<Example> ex) {
        List<Example> negatives = new ArrayList<Example>();
        for (Example e: ex) {
            if (e.getExpectedValue() == 0)
                negatives.add(e);
        }

        return negatives;
    }

    public boolean hasNext() {
        return testFold+1 < foldCount;
    }

    public void next() {
        testFold++;
    }

    public List<Example> getTrain() {
        List<Example> tmp = new ArrayList<Example>();
        int i = 0;
        for (List<Example> fold: folds)
            if (i++ != testFold)
                tmp.addAll(fold);

        Collections.shuffle(tmp);
        return tmp;
    }

    public List<Example> getTest() {
        return folds.get(testFold);
    }
}
