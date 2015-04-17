package discoverer;

/**
 * Abstract class for all elements in the syntax parser
 */
public abstract class Element {
    public final String name;

    @Override
    public String toString() { return name; }
    public Element(String n) { name = n; }
    public String getName() { return name; }
}
