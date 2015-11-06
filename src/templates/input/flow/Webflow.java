/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package templates.input.flow;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import templates.input.StringParser;

/**
 *
 * @author Gusta
 */
public class Webflow extends Flow {

    long timestamp;

    public String hash;
    
    int sc_bytes;
    int cs_bytes;

    URL cs_url;
    URL cs_referer;
    String cs_username;
    int elapsedTime;
    String cs_userAgent;
    String cs_mimeType;
    String cs_label;
    int sc_httpStatus;
    String sc_resultCode;

    /**
     *
     * @param features
     * @param ida
     */
    public Webflow(String[] features, String ida) {
        if (features.length == 16) {
            hash = ida;
            
            try {
                timestamp = Long.parseLong(features[0]);
            } catch (NumberFormatException numberFormatException) {
            }
            srcIP = new IP(features[1]);
            try {
                srcPort = Integer.parseInt(features[2]);
            } catch (NumberFormatException numberFormatException) {
            }
            destIP = new IP(features[3]);
            try {
                destPort = Integer.parseInt(features[4]);
            } catch (NumberFormatException numberFormatException) {
            }
            try {
                sc_bytes = Integer.parseInt(features[5]);
                cs_bytes = Integer.parseInt(features[6]);
            } catch (NumberFormatException numberFormatException) {
            }
            try {
                cs_url = new URL(features[7]);
            } catch (MalformedURLException ex) {
                Logger.getLogger(Webflow.class.getName()).log(Level.SEVERE, null, ex);
            }
            cs_username = features[8];
            try {
                duration = Integer.parseInt(features[9]);
            } catch (NumberFormatException numberFormatException) {
            }
            cs_userAgent = features[10];
            cs_mimeType = features[11];
            cs_label = features[12];
            try {
                cs_referer = new URL(features[13]);
            } catch (MalformedURLException ex) {
                Logger.getLogger(Webflow.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                sc_httpStatus = Integer.parseInt(features[14]);
            } catch (NumberFormatException numberFormatException) {
            }
            sc_resultCode = features[15];
        }
    }

    public String getRelationalRepresentaiton() {
        StringParser sp = new StringParser();
        String urlStruct = sp.string2Structure("urlNext", cs_url.toString(), hash);
        String refererStruct = sp.string2Structure("refNext", cs_referer.toString(), hash);

        return "\n " + urlStruct + "\n" + refererStruct;
    }
}
