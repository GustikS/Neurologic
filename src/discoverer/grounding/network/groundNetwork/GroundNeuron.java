/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer.grounding.network.groundNetwork;

import discoverer.construction.network.KL;
import discoverer.global.Global;

/**
 *
 * @author Gusta
 */
public class GroundNeuron {

    public boolean dropMe = false;
    public double outputValue;
    public double sumedInputs;

    public int groundParentsCount;
    public int groundParentsChecked;
    public double groundParentDerivativeAccumulated;

    void invalidateValue() {
        outputValue = 0.00000000;
        groundParentsChecked = 0;
        groundParentDerivativeAccumulated = 0;
    }
}
