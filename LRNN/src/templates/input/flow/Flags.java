/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package templates.input.flow;

import java.util.HashMap;

/**
 *
 * @author Gustiik
 */
public class Flags extends AbstractAttribute {

    public static final char[] alphabet = new char[]{'.', 'U', 'A', 'P', 'R', 'S', 'F'};
    private char[] flChar = new char[6];
    private String flString;
    private int flInt;

    public Flags() {
    }

    public Flags(char[] flChar) {
        this.flChar = flChar;
        String tmp = "";
        for (int i = 0; i < flChar.length; i++) {
            tmp += flChar[i];
        }
        this.flString = tmp;
        setFlInt();
    }

    public Flags(String flS) {
        this.flString = flS.trim();
        for (int i = 0; i < flString.length(); i++) {
            this.flChar[i] = flString.charAt(i);
        }
        setFlInt();
    }

    public boolean equals(Flags fl) {
        if (this.getFlString().equals(fl.getFlString())) {
            return true;
        }
        return false;
    }

    /**
     * @return the flChar
     */
    public char[] getFlChar() {
        return flChar;
    }

    /**
     * @param flChar the flChar to set
     */
    public void setFlChar(char[] flChar) {
        this.flChar = flChar;
    }

    /**
     * @return the flString
     */
    public String getFlString() {
        return flString;
    }

    /**
     * @param flString the flString to set
     */
    public void setFlString(String flString) {
        this.flString = flString.trim();
    }

    /**
     * @return the flInt
     */
    public int getFlInt() {
        return flInt;
    }

    /**
     * @param flInt the flInt to set
     */
    public void setFlInt(int flInt) {
        this.flInt = flInt;
    }

    public void setFlInt() {
        String tmp = "";
        for (int i = 0; i < flString.length(); i++) {
            if (flString.charAt(i) == '.') {
                tmp += 1;
            } else {
                tmp += i + 2;
            }
        }
        flInt = Integer.parseInt(tmp);
    }

    @Override
    public String toStringValue() {
        return flString;
    }

    @Override
    public int compareTo(AbstractAttribute att) {
        if (att.getClass() != getClass()) {
            return Integer.MIN_VALUE;
        } else {
            Flags fl = (Flags) att;
            if (fl.flInt == this.flInt) {
                return 0;
            } else if (fl.flInt < this.flInt) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}
