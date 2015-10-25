/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer.learning;

/**
 *
 * @author Gusta
 */
public class LearningStep {

    private Double thresh;
    private Double error;
    private Double majorityErr;
    private Double dispersion;

    double sum100 = 0;
    int count100 = 0;

    void clear() {
        setThresh(null);
        setError(null);
        setMajorityErr(null);
        setDispersion(null);
    }

    /**
     * @return the thresh
     */
    public Double getThresh() {
        return thresh;
    }

    /**
     * @param thresh the thresh to set
     */
    public void setThresh(Double thresh) {
        this.thresh = thresh;
    }

    /**
     * @return the error
     */
    public Double getError() {
        return error;
    }

    /**
     * @param error the error to set
     */
    public void setError(Double error) {
        this.error = error;
    }

    /**
     * @return the majorityErr
     */
    public Double getMajorityErr() {
        return majorityErr;
    }

    /**
     * @param majorityErr the majorityErr to set
     */
    public void setMajorityErr(Double majorityErr) {
        this.majorityErr = majorityErr;
    }

    /**
     * @return the dispersion
     */
    public Double getDispersion() {
        return dispersion;
    }

    /**
     * @param dispersion the dispersion to set
     */
    public void setDispersion(Double dispersion) {
        this.dispersion = dispersion;
    }

    @Override
    public String toString() {
        return "sum: " + sum100 + ", count: " + count100;
    }

    public boolean isBetterThen(LearningStep bestResult) {
        if (error == null) {
            return true;
        }
        if (error > bestResult.error) {
            return true;
        }
        return false;
    }

}
