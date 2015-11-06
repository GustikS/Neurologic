/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package templates.input.flow;

/**
 *
 * @author Gustiik
 */
public abstract class AbstractAttribute implements Comparable<AbstractAttribute>{

    private String name;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public abstract int compareTo(AbstractAttribute att);
    
    public abstract String toStringValue();
    
}
