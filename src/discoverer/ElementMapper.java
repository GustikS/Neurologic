package discoverer;

import java.util.*;

/**
 * Mapping for all elements(literals in rules and examples) String -> Integer
 */
public class ElementMapper {
    private static Map<String, Integer> elMap = new HashMap<String, Integer>();
    private static int elId = 0;

    /**
     * assigns this String a unique incrementing Integer<p>
     * as stored in elMap here
     * @param s
     * @return 
     */
    public static int map(String s) {
        if (elMap.containsKey(s))
            return elMap.get(s);

        elMap.put(s, elId);
        return elId++;
    }

    public static int getElCount() { return elId; }
    public static boolean contains(String name) { return elMap.containsKey(name); }
    public static int getMap(String name) { return elMap.get(name); }
    public static Map<String, Integer> getElMap() { return elMap; }
}
