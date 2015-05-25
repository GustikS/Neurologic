package discoverer;

import discoverer.construction.example.Example;
import discoverer.global.Global;
import discoverer.global.Glogger;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Splitter for performing n-fold crossval
 */
public class ExampleSplitter {

    public int foldCount;
    public int testFold = 0;
    private List<List<Example>> folds;

    /**
     * stratified split of examples(same #positive examples) for k-fold
     * cross-validation
     *
     * @param k
     * @param ex
     */
    public ExampleSplitter(int k, List<Example> ex) {
        folds = new ArrayList<List<Example>>();

        List<Example> positives = getPositives(ex);
        List<Example> negatives = getNegatives(ex);

        int foldLen = (int) Math.floor((double) ex.size() / k);
        //repaired fold count - extra fold for remaining samples
        foldCount = k;
        //foldCount = (int) Math.floor((double) ex.size() / foldLen);
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

            Collections.shuffle(fold, Global.getRg());
            folds.add(fold);
        }

        //distribute the last corrupted fold
        if (folds.size() > k) {
            int i = 0;
            for (Example negative : folds.get(k)) {
                folds.get(i++).add(negative);
            }
            folds.remove(k);
        }

        if (Global.isOutputFolds()) {
            Glogger.createDir("folds");
            int i = 1;
            for (List<Example> fold : folds) {
                try {
                    int a = 0;
                    BufferedWriter pw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("folds/fold" + i++), "utf-8"));
                    for (Example exa : fold) {
                        pw.write(a++ + " : " + exa.hash + "\n");
                        pw.flush();
                    }
                } catch (FileNotFoundException ex1) {
                    Logger.getLogger(ExampleSplitter.class.getName()).log(Level.SEVERE, null, ex1);
                } catch (UnsupportedEncodingException ex1) {
                    Logger.getLogger(ExampleSplitter.class.getName()).log(Level.SEVERE, null, ex1);
                } catch (IOException ex1) {
                    Logger.getLogger(ExampleSplitter.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }
    }

    private List<Example> getPositives(List<Example> ex) {
        List<Example> positives = new ArrayList<Example>();
        for (Example e : ex) {
            if (e.getExpectedValue() == 1) {
                positives.add(e);
            }
        }

        return positives;
    }

    private List<Example> getNegatives(List<Example> ex) {
        List<Example> negatives = new ArrayList<Example>();
        for (Example e : ex) {
            if (e.getExpectedValue() == 0) {
                negatives.add(e);
            }
        }

        return negatives;
    }

    public boolean hasNext() {
        return testFold < foldCount;
    }

    public void next() {
        testFold++;
    }

    public List<Example> getTrain() {
        List<Example> tmp = new ArrayList<Example>();
        int i = 0;
        for (List<Example> fold : folds) {
            if (i++ != testFold || foldCount == 1) {    //or just a training set (that shouldnt cause anything in crossval)
                tmp.addAll(fold);
            }
        }

        Collections.shuffle(tmp, Global.getRg());
        return tmp;
    }

    public List<Example> getTest() {
        return folds.get(testFold);
    }
}
