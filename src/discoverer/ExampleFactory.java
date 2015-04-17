package discoverer;

import java.util.*;

/**
 * Factory for examples
 */
public class ExampleFactory {
    private static Map<String, Integer> constMap;
    //map of all example elements -> ID, unique only within the scope of an example!
    private static Map<String, Integer> elMap;
    //map of all example literal name elementMapper ID -> index of occurence, literals exclusively in the example are unique only within the scope of the example!
    private static Map<Integer, List<Integer>> idMap;
    private static int constId;
    //local example's unique literal ID
    private static int elId;
    //position of actual(increasing) literal in example
    private static int possId;

    public int getElId() { return elId; }
    public int getPossId() { return possId; }

    /**
     * stores map of constants, elements(literals) and their IDs
     */
    public ExampleFactory() {
        constMap = new HashMap<String, Integer>();
        elMap = new HashMap<String, Integer>();
        idMap= new HashMap<Integer, List<Integer>>();
        constId = ConstantFactory.getConstCount();
        elId = ElementMapper.getElCount();
        possId = 0;
    }

    /**
     * constructs example from literal conjunction<p>
     * adds chunk from numeric representation of every ground literal(variables)
     * @param ex
     * @return 
     */
    public Example construct(String ex) {
        String[][] tokens = Parser.parseExample(ex);
        double w = Double.parseDouble(tokens[0][0]);
        //new example, contains idMap of literal IDs -> occurences
        Example e = new Example(w, idMap);

        for (int i = 1; i < tokens.length; i++) {
            //encode this literal(variables) into numeric IDs properly(hashmaps)
            int[] raw = encode(tokens[i]);
            //add this literal(variables) IDs as chunk to chunk-store
            e.addChunk(raw);
        }

        e.setConstCount(constId);
        //resets all example-related hashmaps for IDs
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

    /**
     * takes literal(variables) in String form and encodes it into int[]<p>
     * through the use of Element and Constant mappers<p>
     * the IDs are unique only within an example (reset after each example)
     * @param tokens
     * @return 
     */
    private int[] encode(String[] tokens) {
        int[] raw = new int[tokens.length];

        //literal name to ID
        raw[0] = mapEl(tokens[0]);
        for (int i = 1; i < raw.length; i++)
            //every constant name to ID
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

    /**
     * maps literal name onto a unique ID <p>
     * builds idMap representation (literal ID -> occurrence indices) in the process
     * @param name
     * @return 
     */
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