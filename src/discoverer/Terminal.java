package discoverer;

/**
 * Class for terminals - variables with binding<p>
 * Terminals are unique objects for every variable in the rules, unless with the same name in the same rule
 */
public class Terminal extends Element {
    private Integer bind;
    private boolean dummy;

    public Terminal(String n) {
        super(n);
        dummy = false;
    }

    public Terminal(String n, Integer constant) {
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
        return bind != null;
    }
    public Integer getBind() {
        return bind;
    }
    public String getName() {
        return name;
    }

    public void setBind(Integer i) {
        bind = i;
    }

    public void unBind() {
        bind = null;
    }
}
