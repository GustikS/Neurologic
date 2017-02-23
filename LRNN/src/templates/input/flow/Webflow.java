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

    
    boolean noURL = true;
    /**
     *
     * @param features
     * @param ida
     */
    public Webflow(String[] features, boolean positiveFormat) {
        if (features.length == 16) {
            hash = "fl" + id++;

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
                System.out.println("malformed URL: " + features[7]);
            }
            cs_username = features[8];
            try {
                duration = Integer.parseInt(features[9]);
            } catch (NumberFormatException numberFormatException) {
            }
            cs_userAgent = features[10];
            cs_mimeType = features[11];

            int i = 12;
            if (positiveFormat) {
                cs_label = features[i++];
            }
            try {
                cs_referer = new URL(features[i++]);
            } catch (MalformedURLException ex) {
                System.out.println("malformed referer's URL: " + features[i - 1]);
            }
            try {
                sc_httpStatus = Integer.parseInt(features[i++]);
            } catch (NumberFormatException numberFormatException) {
            }
            sc_resultCode = features[i++];

            if (!positiveFormat) {
                cs_label = features[i];
            }
        }
    }

    public String getRelationalRepresentaiton() {
        StringParser sp = new StringParser();
        StringBuilder sb = new StringBuilder();

        sb.append("flow(").append(hash).append("), ");

        sb.append("destPort(").append(hash).append(",").append("p" + destPort).append("), ");
        sb.append("port" + destPort).append("(").append("p" + destPort).append("), ");
        sb.append("srcPort(").append(hash).append(",").append("p" + srcPort).append("), ");
        sb.append("port" + srcPort).append("(").append("p" + srcPort).append("), ");
        sb.append("httpStatus(").append(hash).append(",").append("h" + sc_httpStatus).append("), ");
        sb.append("http" + sc_httpStatus).append("(").append("h" + sc_httpStatus).append("), ");

        sb.append("duration(").append(hash).append(",").append("d" + duration).append("), ");
        sb.append(NumericValuePredicate.getRelational("time", "d" + duration)).append(", ");
        sb.append("csBytes(").append(hash).append(",").append("b" + cs_bytes).append("), ");
        sb.append(NumericValuePredicate.getRelational("bytes", "b" + cs_bytes)).append(", ");
        sb.append("scBytes(").append(hash).append(",").append("b" + sc_bytes).append("), ");
        sb.append(NumericValuePredicate.getRelational("bytes", "b" + sc_bytes)).append(", ");

        sb.append("hasURL(").append(hash).append(",").append(hash).append("-Url), ");
        sb.append("hasRef(").append(hash).append(",").append(hash).append("-Ref), ");
        
        if (!noURL) {
            sb.append("\n");
            sb.append(sp.string2Structure("next", cs_url.toString(), hash + "-Url"));
            if (cs_referer != null) {
                sb.append("\n");
                sb.append(sp.string2Structure("next", cs_referer.toString(), hash + "-Ref"));
            }
        }

        return sb.toString();
    }
}
