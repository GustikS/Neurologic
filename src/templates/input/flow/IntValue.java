/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package templates.input.flow;

import java.text.ParseException;

/**
 *
 * @author Gustiik
 */
public class IntValue extends AbstractAttribute {

    private int value;

    public IntValue(String val) {
        try {
            this.value = Integer.parseInt(val);
        } catch (NumberFormatException e) {
            if (val.contains("entropy")) {
                this.value = Integer.parseInt(val.substring(val.indexOf("<") + 1, val.indexOf("<") + 2));
            } else {
                String[] split = val.split("E");
                this.value = (int) (Integer.parseInt(split[0]) * Math.pow(10, Integer.parseInt(split[1])));
            }
        }
    }

    public IntValue(int val) {
        this.value = val;
    }

    /**
     * @return the value
     */
    public int getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toStringValue() {
        return String.valueOf(value);
    }

    @Override
    public int compareTo(AbstractAttribute att) {
        double value;
        
        if (att.getClass() == IntValue.class) {
            value = ((IntValue) att).getValue();
        } else if (att.getClass() == RealValue.class) {
            value = ((RealValue) att).getValue();
        } else {
            return Integer.MIN_VALUE;
        }
        if (Math.abs(value - this.getValue()) < 0.01) {
            return 0;
        } else if (value < this.getValue()) {
            return 1;
        } else {
            return -1;

        }
    }
}
