package discoverer.construction;

import discoverer.global.Global;
import java.util.*;

/**
 * Factory for constants
 */
public class ConstantFactory {

    private static Map<String, Terminal> constMap = new HashMap<String, Terminal>();    //this is static, needs to be cleaned between runs!!
    private static int nextConst = 0;

    public static void clearConstantFactory() {
        if (Global.debugEnabled) {
            System.out.println("before constructing, we need to do some extra cleaning here (in case this isn't a clean first run)!");
        }
        constMap.clear();
        nextConst = 0;
    }

    public static Terminal construct(String name) {
        if (constMap.containsKey(name)) {
            return constMap.get(name);
        }

        Terminal t = new Terminal(name, nextConst++);
        constMap.put(name, t);
        return t;
    }

    public static boolean contains(String name) {
        return constMap.containsKey(name);
    }

    public static int getMap(String name) {
        return constMap.get(name).getBind();
    }

    public static int getConstCount() {
        return nextConst;
    }

    public static Map<String, Terminal> getConstMap() {
        return constMap;
    }
}
