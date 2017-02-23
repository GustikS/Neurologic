/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package templates.input.flow;

/**
 *
 * @author Gustiik
 */
public class Protocol extends AbstractAttribute {

    private String strProtocol;
    private int intProtocol;

    public Protocol() {
    }

    public Protocol(String prot) {
        this.strProtocol = prot.trim();
        setIntProtocol();
    }

    public Protocol(int prot) {
        this.intProtocol = prot;
        setStrProtocol();
    }

    /**
     * @return the intProtocol
     */
    public int getIntProtocol() {
        return intProtocol;
    }

    /**
     * @param intProtocol the intProtocol to set
     */
    public void setIntProtocol(int intProtocol) {
        this.intProtocol = intProtocol;
    }

    public void setIntProtocol() {
        String prot = strProtocol;
        if (prot.equalsIgnoreCase("TCP")) {
            intProtocol = 0;
        }
        if (prot.equalsIgnoreCase("UDP")) {
            intProtocol = 1;
        }
        if (prot.equalsIgnoreCase("ICMP")) {
            intProtocol = 2;
        }
        if (prot.equalsIgnoreCase("PIM")) {
            intProtocol = 3;
        }
    }

    /**
     * @return the strProtocol
     */
    public String getStrProtocol() {
        return strProtocol;
    }

    /**
     * @param strProtocol the strProtocol to set
     */
    public void setStrProtocol(String strProtocol) {
        this.strProtocol = strProtocol.trim();
    }

    public void setStrProtocol() {
        int prot = intProtocol;
        if (prot == 0) {
            strProtocol = "TCP";
        }
        if (prot == 1) {
            strProtocol = "UDP";
        }
        if (prot == 2) {
            strProtocol = "ICMP";
        }
        if (prot == 3) {
            strProtocol = "PIM";
        }
    }

    @Override
    public String toStringValue() {
        return strProtocol;
    }

    @Override
    public int compareTo(AbstractAttribute att) {
        if (att.getClass() != getClass()) {
            return Integer.MIN_VALUE;
        } else {
            Protocol pro = (Protocol) att;
            if (pro.intProtocol == this.intProtocol) {
                return 0;
            } else if (pro.intProtocol < this.intProtocol) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}
