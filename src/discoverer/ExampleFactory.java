package discoverer;

import java.util.*;

/**
 * Factory for examples
 */
public class ExampleFactory {
    private static Map<String, Integer> constMap;
    private static Map<String, Integer> elMap;
    private static Map<Integer, List<Integer>> idMap;
    private static int constId;
    private static int elId;
    private static int possId;

    public int getElId() { return elId; }
    public int getPossId() { return possId; }

    public ExampleFactory() {
        constMap = new HashMap<String, Integer>();
        elMap = new HashMap<String, Integer>();
        idMap= new HashMap<Integer, List<Integer>>();
        constId = ConstantFactory.getConstCount();
        elId = ElementMapper.getElCount();
        possId = 0;
    }

    public Example construct(String ex) {
        String[][] tokens = Parser.parseExample(ex);
        double w = Double.parseDouble(tokens[0][0]);
        Example e = new Example(w, idMap);

        for (int i = 1; i < tokens.length; i++) {
            int[] raw = encode(tokens[i]);
            e.addChunk(raw);
        }

        e.setConstCount(constId);
        clear();
        return e;
    }

    private void clear() {
        constMap.clear();
        elMap.clear();
        idMap = new HashMap<Integer, List<Integer>>(); // idMap is in example, if clear() than it's deleted from example too
        constId = ConstantFactory.getConstCount();
        elId = ElementMapper.getElCount();
        possId = 0;
    }

    private int[] encode(String[] tokens) {
        int[] raw = new int[tokens.length];

        raw[0] = mapEl(tokens[0]);
        for (int i = 1; i < raw.length; i++)
            raw[i] = mapConst(tokens[i]);

        return raw;
    }

    private int mapConst(String name) {
        if (ConstantFactory.contains(name))
            return ConstantFactory.getMap(name);

        if (constMap.containsKey(name))
            return constMap.get(name);

        constMap.put(name, constId);
        constId++;
        return constId-1;
    }

    private int mapEl(String name) {
        if (ElementMapper.contains(name)) {
            int elId = ElementMapper.getMap(name);
            if (idMap.containsKey(elId)) {
                idMap.get(elId).add(possId);
            } else {
                ArrayList<Integer> ali = new ArrayList<Integer>();
                ali.add(possId);
                idMap.put(elId, ali);
            }
        } else if (elMap.containsKey(name)) {
            int elId = elMap.get(name);
            idMap.get(elId).add(possId);
        } else {
            elMap.put(name, elId++);
            ArrayList<Integer> ali = new ArrayList<Integer>();
            ali.add(possId);
            idMap.put(elId-1, ali);
        }
        return possId++;
    }
}
