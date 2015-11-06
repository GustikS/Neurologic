/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package templates.input.flow;

import java.net.URL;

/**
 *
 * @author Gusta
 */
public class Flow {

    public int ID;
    public String eventID;
    //
    public Start start;
    public int duration;
    public Protocol protocol;
    public IP srcIP;
    public int srcPort;
    public IP destIP;
    public int destPort;
    public Flags flags;
    //private AbstractAttribute TOS;
    public int packets;
    public int bytes;
    //private AbstractAttribute flows;

    private String label;
    //
    private double[] numFeatures;
    private String[] strFeatures;

    public Flow() {
    }

}
