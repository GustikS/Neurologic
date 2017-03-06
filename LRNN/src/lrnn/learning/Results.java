package lrnn.learning;

import auc.AUCCalculator;
import lrnn.global.Global;
import lrnn.global.Glogger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * class for collection of results, providing accuracies, statistics etc.
 */
public class Results {

    public final List<Result> results;
    public LearningStep actualResult;
    public ArrayList<LearningStep> trainingHistory;
    int history = Global.getHistory();

    //--------
    public LearningStep training;
    public LearningStep testing;
    public LearningStep majority;

    public boolean convergence() {
        if (trainingHistory.size() > 0) {
            int last = trainingHistory.size() >= history ? history : trainingHistory.size();
            last = trainingHistory.size() - last;
            if ((trainingHistory.get(last).sum100 / trainingHistory.get(last).count100 - actualResult.sum100 / actualResult.count100) < Global.getConvergenceLimit()) {
                Glogger.info("converged: " + trainingHistory.get(last).sum100 / trainingHistory.get(last).count100 + " - " + actualResult.sum100 / actualResult.count100 + " < " + Global.getConvergenceLimit());
                if (trainingHistory.size() > history) {
                    return true;
                }
            }
            Glogger.info("not yet converged: " + trainingHistory.get(last).sum100 / trainingHistory.get(last).count100 + " - " + actualResult.sum100 / actualResult.count100 + " > " + Global.getConvergenceLimit());
        }
        return false;
    }

    public Results() {
        actualResult = new LearningStep();
        results = new ArrayList<Result>();
        trainingHistory = new ArrayList<>(Global.getCumMaxSteps());
    }

    public void clearResultList() {
        actualResult = new LearningStep();
        results.clear();
    }

    public void add(Result result) {
        results.add(result);
    }

    public List<Result> get() {
        return results;
    }

    public double getThreshold() {
        if (actualResult.getThresh() == null) {
            computeTrain();
        }
        return actualResult.getThresh();
    }

    public double getLearningError() {
        if (actualResult.getError() == null) {
            computeTrain();
        }
        return actualResult.getError();
    }

    public double getMajorityClass() {
        if (actualResult.getMajorityErr() == null) {
            computeTrain();
        }
        return actualResult.getMajorityErr();
    }

    public double getDispersion() {
        if (actualResult.getDispersion() == null) {
            computeTrain();
        }
        return actualResult.getDispersion();
    }

    /**
     * including computing (moving) threshold
     */
    public void computeTrain() {
        computeMajority();
        computeMSE();
        Collections.sort(results); //iterate in ascending order

        int bad = 0;
        for (Result current : results) {    //counting of negative exs.
            if (current.getExpected() == 0) {
                bad++;
            }
        }
        //---------
        double sumPos = 0, sumNeg = 0, bestErr = results.size();
        double negValues = 0, posValues = 0;
        int numPos = results.size() - bad;
        int numNeg = bad;
        int bestIndex = -1;
        int j = 0;
        while (true) {
            if (j >= results.size()) {
                break;
            }
            double err = (sumPos + numNeg - sumNeg) / (double) (numPos + numNeg);
            if (err < bestErr) {
                bestIndex = j;
                bestErr = err;
            }
            do {
                if (results.get(j).getExpected() == 1.0) {
                    sumPos++;
                    posValues += results.get(j).getActual();
                } else if (results.get(j).getExpected() == 0.0) {
                    sumNeg++;
                    negValues += results.get(j).getActual();
                } else {
                    throw new IllegalStateException();
                }
                j++;
            } while (j < results.size() - 1 && results.get(j).getActual() == results.get(j - 1).getActual());
        }
        actualResult.setError(bestErr);
        actualResult.setThresh(results.get(bestIndex).getActual());
        actualResult.setDispersion(Math.abs((negValues / numNeg) - (posValues / numPos)));
        /*//--------------

        int bestErr = results.size();
        int zeroes = bad;
        int ones = results.size() - bad;    //count of positive exs.
        double zeroSum = 0.0;
        double oneSum = 0.0;
        Result bestResult = null;
        Result nextResult = null;
        int i = 1;
        for (Result current : results) {    //searching for best threshold separation
            //deciding the threshold:
            if (bad < bestErr) {
                bestResult = current;
                bestErr = bad;
                if (i < results.size()) {
                    nextResult = results.get(i);
                } else {
                    nextResult = bestResult;
                }
            }

            if (current.getExpected() == 1) {   //count positive/negative
                oneSum += current.getActual();  //sum their values
                bad += 1;
            } else {
                zeroSum += current.getActual();
                bad -= 1;
            }

            i++;
        }


        actualResult.setDispersion(Math.abs((zeroSum / zeroes) - (oneSum / ones)));

        actualResult.setError((double) bestErr / results.size());    // misclassified/all

        if (bestResult != null) {
            double left = bestResult.getActual();
            double right = nextResult.getActual();
            //actualResult.setThresh((Double) left + (right - left) / 2);
            actualResult.setThresh(left);
        } else {
            actualResult.setThresh((Double) 0.5);
        }

*/
        //---------update running avg for past---------
        actualResult.sum100 = actualResult.getError();
        actualResult.count100 = 1;
        if (trainingHistory.size() > 0) {
            actualResult.count100 = trainingHistory.size() + 1;
            actualResult.sum100 += trainingHistory.get(trainingHistory.size() - 1).sum100;
        }
        if (trainingHistory.size() >= history) {
            actualResult.count100 = history;
            actualResult.sum100 -= trainingHistory.get(trainingHistory.size() - history).getError();
        }

        Glogger.info(actualResult.toString());
        //convergence();
        testcheck();
        trainingHistory.add(actualResult);
    }

    public boolean isBetterThen(Results res2) {
        if (actualResult == null) {
            return true;
        }
        if (actualResult.getError() > res2.actualResult.getError()) {
            return true;
        }
        if (actualResult.getError() == res2.actualResult.getError() && actualResult.getMse() > res2.actualResult.getMse()) {
            return true;
        }
        return false;
    }

    public void computeTest() {
        computeMajority();
        computeMSE();
        try {
            computeAUC();
        } catch (Exception ex) {
            Logger.getLogger(Results.class.getName()).log(Level.SEVERE, null, ex);
        }
        double error = 0;
        for (Result res : results) {
            double clas = res.getActual() >= training.getThresh() ? 1.0 : 0.0;
            Glogger.info("Example Classified -> " + clas + " Expected -> " + res.getExpected() + " Out -> " + res.getActual() + " Thresh -> " + training.getThresh());
            if (clas != res.getExpected()) {
                error += 1.0;
            }
        }
        double testError = error / results.size();

        //to also compute dispersion and optimal threshold but keep results ordering on test set
        List<Result> tmpResults = new ArrayList<>(results);
        computeTrain();
        results.clear();
        results.addAll(tmpResults);

        actualResult.setRecalculatedThrehError(actualResult.getError());
        actualResult.setError(testError);
    }

    void computeMSE() {
        double mse = 0;
        for (Result result : results) {
            mse += (result.getExpected() - result.getActual()) * (result.getExpected() - result.getActual());
        }
        mse /= results.size();
        actualResult.setMse(mse);
    }

    void computeMajority() {
        int pos = 0;
        for (Result result : results) {
            if (result.getExpected() == 1) {
                pos++;
            }
        }
        if (pos <= (double) results.size() / 2) {
            actualResult.setMajorityErr((Double) (double) pos / results.size());  //majorityErr = negative/all (there's majority of positive, or will be flipped)
        } else {
            actualResult.setMajorityErr((Double) (double) (results.size() - pos) / results.size());
        }
    }

    public void computeAUC() throws IOException {
        FileWriter fw = null;
        fw = new FileWriter(new File(Global.outputFolder + "auc.txt"));
        for (Result result : results) {
            fw.write(result.getActual() + " " + (int) result.getExpected() + "\n");
        }
        fw.close();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream orgStream = System.out;
        PrintStream myPrintStream = new PrintStream(baos);
        System.setOut(myPrintStream);
        try {
            AUCCalculator.main(new String[]{Global.outputFolder + "auc.txt", "list"});
        }catch (Exception e){
            Glogger.err("Problem with AUC calculation");
            System.setOut(orgStream);
            return;
        }
        String toFind = "Area Under the Curve for Precision - Recall is";
        String result = new String(baos.toByteArray(), StandardCharsets.UTF_8);
        String substring = result.substring(result.indexOf(toFind) + toFind.length(), result.indexOf("\n", result.indexOf(toFind) + toFind.length()));
        actualResult.setAUCpr(Double.parseDouble(substring));
        toFind = "Area Under the Curve for ROC is";
        result = new String(baos.toByteArray(), StandardCharsets.UTF_8);
        substring = result.substring(result.indexOf(toFind) + toFind.length(), result.indexOf("\n", result.indexOf(toFind) + toFind.length()));
        actualResult.setAUCroc(Double.parseDouble(substring));

        System.setOut(orgStream);
    }

    void testcheck() {
        double error = 0;
        for (Result res : results) {
            double clas = res.getActual() >= actualResult.getThresh() ? 1.0 : 0.0;
            //Glogger.info("Classified -> " + clas + " Expected -> " + example.getExpectedValue() + " Out -> " + ballValue + " Thresh -> " + res.getThreshold());
            if (clas != res.getExpected()) {
                error += 1.0;
            }
        }
        double err = error / results.size();
        Glogger.info("Fold Train error calculated (testCheck) : " + err);
    }
}
