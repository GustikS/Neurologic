package discoverer.construction;

import java.io.Serializable;

/**
 * Very general class Class for terminals - variables with binding<p>
 * Terminals are unique objects for every variable in the rules, unless with the
 * same name in the same rule
 */
public class Variable extends Element implements Serializable {

    private int bind = -1;
    private boolean dummy;

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
