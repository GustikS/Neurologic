package discoverer;

import java.util.*;

/**
 * Factory for variables
 * - ensures same name variables will refer to the same variable instance with hashmap
 */
public class VariableFactory {
    private Map<String, Terminal> varMap;

    /**
     * stores hashmap of terminals
     */
    public VariableFactory() {
        varMap = new HashMap<String, Terminal>();
    }

    public Terminal construct(String name) {
        Terminal t = varMap.get(name);

        if (t == null) {
            t = new Terminal(name);
            varMap.put(name, t);
            if (name.contains("DMY"))
                t.setDummy();
        }
        return t;
    }

    public void clear() {
        varMap.clear();
    }
}
