package lrnn.construction;

import java.io.Serializable;

/**
 * Very general class Class for terminals - variables with binding<p>
 * Terminals are unique objects for every variable in the rules, unless with the
 * same name in the same rule
 */
public class Variable extends Element implements Serializable {

    private int bind = -1;
    private boolean dummy;
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Variable other = (Variable) obj;
        if (!this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    public Variable(String n) {
        super(n);
        dummy = false;
    }

    public Variable(String n, int constant) {
        this(n);
        bind = constant;
    }

    public void setDummy() {
        dummy = true;
    }

    public boolean isDummy() {
        return dummy;
    }

    public boolean isBind() {
        return bind != -1;
    }

    public int getBind() {
        return bind;
    }

    public String getName() {
        return name;
    }

    public void setBind(int i) {
        bind = i;
    }

    public void unBind() {
        bind = -1;
    }
}
