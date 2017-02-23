/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package templates.input.flow;

/**
 *
 * @author Gustiik
 */
public class IP extends AbstractAttribute{

    private int[] ipInt = new int[6];
    private String ipString;
    private long ipLong;

    public IP() {
    }

    public IP(int[] ipInt) {
        this.ipInt = ipInt;
        String tmp = "";
        for (int i = 0; i < ipInt.length; i++) {
            tmp += ipInt[i];
        }
        this.ipString = tmp;
        setIpLong();
    }

    public IP(String ipString) {
        this.ipString = ipString.trim();
        String[] tmp = ipString.trim().split("\\.");
        for (int i = 0; i < tmp.length; i++) {
            this.ipInt[i] = Integer.parseInt(tmp[i]);
        }
        setIpLong();
    }

    public boolean equals(IP ip) {
        if (this.getIpString().equals(ip.getIpString())) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return this.ipString;
    }

    /**
     * @return the ipInt
     */
    public int[] getIpInt() {
        return ipInt;
    }

    /**
     * @param ipInt the ipInt to set
     */
    public void setIpInt(int[] ipInt) {
        this.ipInt = ipInt;
    }

    /**
     * @return the ipString
     */
    public String getIpString() {
        return ipString;
    }

    /**
     * @param ipString the ipString to set
     */
    public void setIpString(String ipString) {
        this.ipString = ipString.trim();
    }

    /**
     * @return the ipLong
     */
    public long getIpLong() {
        return ipLong;
    }

    /**
     * @param ipLong the ipLong to set
     */
    public void setIpLong(long ipLong) {
        this.ipLong = ipLong;
    }

    public void setIpLong() {
        String ipStr = ipString.replaceAll("\\.", "").trim();
        ipLong = Long.parseLong(ipStr);
    }
    
        @Override
    public String toStringValue(){
        return ipString;
    }
    
    @Override
    public int compareTo(AbstractAttribute att) {
        if (att.getClass() != getClass()){
            return Integer.MIN_VALUE;
        } else {
            IP ip = (IP) att;
            if (ip.ipLong == this.getIpLong()){
                return 0;
            } else if (ip.ipLong < this.getIpLong()){
                return 1;
            } else {
                return -1;
            }
        }
    }
}
