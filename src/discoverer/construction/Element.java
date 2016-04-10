package discoverer.construction;

import java.io.Serializable;

/**
 * Abstract class for all elements in the syntax parser
 */
public abstract class Element implements Serializable {

    public final String name;

    public Element() {
        name = null;
    }

    @Override
    public String toString() {
        return name;
    }

    public Element(String n) {
        name = n;
    }

    public String getName() {
        return name;
    }
}
