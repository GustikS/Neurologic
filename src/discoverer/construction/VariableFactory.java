package discoverer.construction;

import discoverer.construction.Variable;
import java.util.*;

/**
 * Factory for variables
 * - ensures same name variables will refer to the same variable instance with hashmap
 */
public class VariableFactory {
    private Map<String, Variable> varMap;

    /**
     * stores hashmap of terminals
     */
    public VariableFactory() {
        varMap = new HashMap<String, Variable>();
    }

    public Variable construct(String name) {
        Variable t = varMap.get(name);

        if (t == null) {
            t = new Variable(name);
            varMap.put(name, t);
            if (name.startsWith("_"))
                t.setDummy();
        }
        return t;
    }

    public void clear() {
        varMap.clear();
    }
}
