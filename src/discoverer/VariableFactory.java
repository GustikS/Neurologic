package discoverer;

import java.util.*;

/**
 * Factory for variables
 */
public class VariableFactory {
    private Map<String, Terminal> varMap;

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
