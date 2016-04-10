/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer.grounding.network;

import discoverer.construction.Variable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Gusta
 */
public abstract class GroundKL implements Serializable {

    private static int counter = 0;

    public boolean dropMe = false;

    private int groundParents;
    private int groundParentsChecked;
    private double groundParentDerivative;

    private Double value;
    private Double valueAvg;

    private int id;
    private int[] termList;
    private String[] termNames;

    /**
     * this is a truly ground K/L
     *
     * @param terms
     */
    public GroundKL(List<Variable> terms) {
        groundParents = 0;
        groundParentsChecked = 0;
        groundParentDerivative = 0;

        id = counter++;
        termList = new int[terms.size()];
        termNames = new String[terms.size()];

        if (terms != null) {
            for (int i = 0; i < terms.size(); i++) {
                termList[i] = terms.get(i).getBind();
                termNames[i] = terms.get(i).getName();
            }
        }
    }

    public GroundKL() {
        groundParents = 0;
        groundParentsChecked = 0;
        groundParentDerivative = 0;

        id = counter++;
    }

    public abstract GroundKL cloneMe();

    //public abstract void transform2Arrays();
    /**
     * delete values and parent counters for backpropagation calculation
     */
    public void invalidate() {
        groundParentsChecked = 0;
        groundParentDerivative = 0;
        value = null;
        valueAvg = null;
    }

    /**
     * @return the groundParents
     */
    public int getGroundParents() {
        return groundParents;
    }

    /**
     * @param groundParents the groundParents to set
     */
    public void setGroundParents(int groundParents) {
        this.groundParents = groundParents;
    }

    public void incrGroundParents() {
        this.groundParents++;
    }

    /**
     * @return the groundParentsChecked
     */
    public int getGroundParentsChecked() {
        return groundParentsChecked;
    }

    /**
     * @param groundParentsChecked the groundParentsChecked to set
     */
    public void setGroundParentsChecked(int groundParentsChecked) {
        this.groundParentsChecked = groundParentsChecked;
    }

    public void incrGroundParentsChecked() {
        this.groundParentsChecked++;
    }

    /**
     * @return the value
     */
    public Double getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(Double value) {
        this.value = value;
    }

    /**
     * @return the valueAvg
     */
    public Double getValueAvg() {
        return valueAvg;
    }

    /**
     * @param valueAvg the valueAvg to set
     */
    public void setValueAvg(Double valueAvg) {
        this.valueAvg = valueAvg;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the termList
     */
    public int[] getTermList() {
        return termList;
    }

    /**
     * @param termList the termList to set
     */
    public void setTermList(int[] termList) {
        this.termList = termList;
    }

    /**
     * @return the groundParentDerivative
     */
    public double getGroundParentDerivative() {
        return groundParentDerivative;
    }

    /**
     * @param groundParentDerivative the groundParentDerivative to set
     */
    public void setGroundParentDerivative(double groundParentDerivative) {
        this.groundParentDerivative = groundParentDerivative;
    }

    public void addGroundParentDerivative(double gParentDerivative) {
        this.groundParentDerivative += gParentDerivative;
    }

    /**
     * @return the termNames
     */
    public String[] getTermNames() {
        return termNames;
    }

    /**
     * @param termNames the termNames to set
     */
    public void setTermNames(String[] termNames) {
        this.termNames = termNames;
    }
}
