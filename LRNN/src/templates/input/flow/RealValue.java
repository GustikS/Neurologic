/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package templates.input.flow;

/**
 *
 * @author Gustiik
 */
public class RealValue extends AbstractAttribute {

    private double value;

    public RealValue(String val) {
        try {
            this.value = Double.parseDouble(val);
        } catch (Exception e) {
            this.value = Double.MAX_VALUE;
        }
    }

    public RealValue(Double val) {
        this.value = val;
    }

    /**
     * @return the value
     */
    public double getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(double value) {
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
        
        if (Math.abs(value - this.value) < 0.01) {
            return 0;
        } else if (value < this.value) {
            return 1;
        } else {
            return -1;
        }
    }
}

