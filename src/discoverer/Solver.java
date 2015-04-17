package discoverer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Wrapper for running test with n-fold stratification
 */
public class Solver {
    public void solve(int folds, String[] rules, String[] ex, Batch batch, int steps, int epochs, int restartCount, double learnRate) {
        NetFactory nf = new NetFactory();
        KL network = nf.construct(rules);
        List<Example> examples = createExamples(ex);
        ExampleSplitter es = new ExampleSplitter(folds, examples);

        double testErr = 0;
        double testMaj = 0;
        int i;
        for (i = 0; es.hasNext(); es.next()) {
            double thresh = train(batch, network, es.getTrain(), steps, epochs, restartCount, learnRate);
            testErr += test(network, thresh, es.getTest());
            testMaj += testM(es.getTest(), es.getTrain());
            Invalidator.invalidate(network);
            i++;
        }

        testErr /= i;
        testMaj /= i;
        System.out.println("Final error: " + testErr);
        System.out.println("Final majority error: " + testMaj);
    }

    private double testM(List<Example> test, List<Example> train) {
        int pos = 0;
        for (Example e: train)
            if (e.getExpectedValue() == 1)
                pos++;

        double err = 0;
        if (pos >= (double) train.size()/2) {
            for (Example e: test)
                if (e.getExpectedValue() == 0)
                    err++;
        } else {
            for (Example e: test)
                if (e.getExpectedValue() == 1)
                    err++;
        }

        err /= test.size();
        System.out.println("Majority for this fold -> " + err);
        return err;
    }

    public List<Example> createExamples(String[] ex) {
        ExampleFactory eFactory = new ExampleFactory();
        List<Example> examples = new ArrayList<Example>();
        for (int i = 0; i < ex.length; i++) {
            Example e = eFactory.construct(ex[i]);
            examples.add(e);
        }

        Collections.shuffle(examples);
        return examples;
    }

    public double train(Batch batch, KL network, List<Example> examples, int learningStepCount, int learningEpochs, int restartCount, double learnRate) {
        double thresh;
        if (batch == Batch.NO) {
            Learner s = new Learner();
            thresh = s.solve(network, examples, learningStepCount, learningEpochs, restartCount, learnRate);
        } else {
            BatchLearner bs = new BatchLearner();
            thresh = bs.solve(network, examples, learningStepCount, learningEpochs, restartCount, learnRate);
        }

        return thresh;
    }

    public double test(KL network, double thresh, List<Example> examples) {
        double error = 0.0;
        for (Example example: examples) {
            Ball b = Solvator.solve(network, example);
            double clas = b.val > thresh ? 1.0 : 0.0;
            System.out.println("Classified -> " + clas + " Expected -> " + example.getExpectedValue() + " Out -> " + b.val + " Thresh -> " + thresh);
            if (clas != example.getExpectedValue())
                error += 1.0;
        }

        double err = error / examples.size();
        System.out.println("Error for this fold -> " + err);
        return err;
    }
}
