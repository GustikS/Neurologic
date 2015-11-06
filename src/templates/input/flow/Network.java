/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package templates.input.flow;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gustiik
 */
public class Network {

    IP subnetIp;
    int subnetInt;
    int mask;

    public Network(String subnet, int mask) {
        this.mask = -1 << (32 - mask);
        this.subnetInt = ipString2Int(subnet);
        this.subnetIp = new IP(subnet);
    }

    public static void main(String[] args) {
        Network s = new Network("147.32.80.0",21);
        System.out.println(s.subnetInt + "  " + s.mask + "  "  + s.ipString2Int("147.32.80.1") + "  " + s.isWithin("147.32.80.1"));
        System.out.println(s.subnetInt + "  " + s.mask + "  "  + s.ipString2Int("147.32.87.1") + "  " + s.isWithin("147.32.84.25"));
    }
    
    public int ipString2Int(String s) {
        Inet4Address a;
        int i = 0;
        try {
            a = (Inet4Address) InetAddress.getByName(s);
            byte[] b = a.getAddress();
            i = ((b[0] & 0xFF) << 24) | ((b[1] & 0xFF) << 16) | ((b[2] & 0xFF) << 8) | ((b[3] & 0xFF) << 0);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Network.class.getName()).log(Level.SEVERE, null, ex);
        }
        return i;
    }

    public boolean isWithin(String ip) {
        int adr = ipString2Int(ip);

        if ((subnetInt & mask) == (adr & mask)) {
            return true;
        }
        return false;
    }
}
