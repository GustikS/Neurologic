package discoverer.crossvalidation;

import discoverer.construction.example.Example;
import discoverer.global.Global;
import discoverer.global.Glogger;
import discoverer.learning.Sample;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Splitter for performing stratified n-fold crossval
 */
public class SampleSplitter implements Serializable {

    public int foldCount;
    public int testFold = 0;
    public final List<List<Sample>> folds;
    public final List<Sample> samples;

    public SampleSplitter(List<Sample> train, List<Sample> test) {
        folds = new ArrayList<>();
        folds.add(train);
        folds.add(test);
        testFold = 1;
        foldCount = 2; //should be checked
        samples = new ArrayList<>(train.size() + test.size());
        samples.addAll(train);
        samples.addAll(test);
    }

    /**
     * stratified split of examples(same #positive examples) for k-fold
     * cross-validation
     *
     * @param k
     * @param ex
     */
    public SampleSplitter(int k, List<Sample> ex) {
        numberSamples(ex);
        folds = new ArrayList<>();
        samples = new ArrayList<>();
        samples.addAll(ex);

        List<Sample> positives = getPositives(ex);
        List<Sample> negatives = getNegatives(ex);

        int foldLen = (int) Math.floor((double) ex.size() / k);
        //repaired fold count - extra fold for remaining samples
        foldCount = k;
        //foldCount = (int) Math.floor((double) ex.size() / foldLen);
        int positivesInFold = (int) Math.ceil((double) positives.size() / ex.size() * foldLen);

        int n = 0;
        int p = 0;

        while (n < negatives.size() || p < positives.size()) {
            List<Sample> fold = new ArrayList<>();
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
            for (Sample negative : folds.get(k)) {
                List<Sample> ff = folds.get(i++); //problem with retypeing
                ff.add(negative);
            }
            folds.remove(k);
        }

        if (Global.isOutputFolds()) {
            outputSplits();
        }
    }

    final void outputSplits() {
        Glogger.createDir("folds");
        int i = 1;
        for (List<Sample> fold : folds) {
            try {
                BufferedWriter pw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("folds/fold" + i++), "utf-8"));
                for (Sample exa : fold) {
                    pw.write(exa.position + " : " + exa.getExample().hash + "\n");
                    pw.flush();
                }
            } catch (FileNotFoundException ex1) {
                Logger.getLogger(SampleSplitter.class.getName()).log(Level.SEVERE, null, ex1);
            } catch (UnsupportedEncodingException ex1) {
                Logger.getLogger(SampleSplitter.class.getName()).log(Level.SEVERE, null, ex1);
            } catch (IOException ex1) {
                Logger.getLogger(SampleSplitter.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    private List<Sample> getPositives(List<Sample> ex) {
        List<Sample> positives = new ArrayList<>();
        for (Sample e : ex) {
            if (e.getExample().getExpectedValue() == 1) {
                positives.add(e);
            }
        }

        return positives;
    }

    private List<Sample> getNegatives(List<Sample> ex) {
        List<Sample> negatives = new ArrayList<>();
        for (Sample e : ex) {
            if (e.getExample().getExpectedValue() == 0) {
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

    public List<Sample> getTrain() {
        List<Sample> tmp = new ArrayList<Sample>();
        int i = 0;
        for (List<Sample> fold : folds) {
            if (i++ != testFold || foldCount == 1) {    //or just a training set (that shouldnt cause anything in crossval)
                tmp.addAll(fold);
            }
        }

        Collections.shuffle(tmp, Global.getRg());
        return tmp;
    }

    public List<Sample> getTest() {
        return folds.get(testFold);
    }

    private void numberSamples(List<Sample> ex) {
        if (Global.isOutputFolds()) {
            Glogger.process("---------------------------whole sample set------------------------------");
        }
        for (int i = 0; i < ex.size(); i++) {
            ex.get(i).position = i;
            if (Global.isOutputFolds()) {
                Glogger.info("sample " + i + " : " + ex.get(i).toString());
            }
        }
    }
}
