package discoverer;

import java.util.*;

/**
 * Factory for constatnts
 */
public class ConstantFactory {
    private static Map<String, Terminal> constMap = new HashMap<String, Terminal>();
    private static int nextConst = 0;

    public static Terminal construct(String name) {
        if (constMap.containsKey(name))
            return constMap.get(name);

        Terminal t = new Terminal(name, nextConst++);
        constMap.put(name, t);
        return t;
    }

    public static boolean contains(String name) { return constMap.containsKey(name); }
    public static int getMap(String name) { return constMap.get(name).getBind(); }
    public static int getConstCount() { return nextConst; }
    public static Map<String, Terminal> getConstMap() { return constMap; }
}
