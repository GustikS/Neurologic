/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer.grounding.network;

import discoverer.construction.Terminal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Gusta
 */
public abstract class GroundKL {

    private static int counter = 0;

    public boolean dropMe = false;

    private int groundParents;
    private int groundParentsChecked;
    private double groundParentDerivative;

    private Double value;
    private Double valueAvg;

    private int id;
    private List<Integer> termList;

    /**
     * this is a truly ground K/L
     *
     * @param terms
     */
    public GroundKL(List<Terminal> terms) {
        groundParents = 0;
        groundParentsChecked = 0;
        groundParentDerivative = 0;

        id = counter++;
        termList = new ArrayList<Integer>();

        if (terms != null) {
            for (Terminal t : terms) {
                termList.add(t.getBind());
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
    public List<Integer> getTermList() {
        return termList;
    }

    /**
     * @param termList the termList to set
     */
    public void setTermList(List<Integer> termList) {
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
}
