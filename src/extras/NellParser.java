/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package extras;

import discoverer.global.Tuple;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

/**
 *
 * @author Gusta
 */
public class NellParser {

    static HashMap<String, Integer> conceptCounts = new HashMap<>(3000000);
    static HashMap<String, HashSet<String[]>> conceptRelations = new HashMap<>(3000000);
    static HashSet<String> blacklist = new HashSet<String>();

    static int relationsLimit = 10;
    
    public static void main(String[] args) {
        blacklist.add("concept:haswikipediaurl");
        blacklist.add("generalizations");
        loadFacts("C:\\Users\\Gusta\\Downloads\\NELL.08m.995.esv.csv\\NELL.08m.995.esv.csv");
        System.out.println("--------------------------------------------");
        HashSet<String> seedWords = new HashSet<>();
        seedWords.add("concept:mammal:monkeys");
        seedWords.add("concept:mammal:monkey");
        ArrayList<String[]> rulesFromSeedWord = rulesFromSeedWord(seedWords, 1000);
        System.out.println("--------------------------------------------");
        int a = 0;
        for (String[] strings : rulesFromSeedWord) {
            System.out.print(a++ + " ");
            for (String string : strings) {
                System.out.print(string + "\t");
            }
            System.out.println("");
        }
    }

    public static ArrayList<String[]> loadFacts(String p) {
        ArrayList<String[]> facts = new ArrayList<>();
        Path path = FileSystems.getDefault().getPath(p);
        int i = 0;
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line = null;
            Integer count;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                if (i++ % 100000 == 0) {
                    System.out.println(i + " --> " + line);
                }
                String[] split = line.split("\t");
                String[] fact = new String[4];
                fact[0] = split[0];
                fact[1] = split[1];
                fact[2] = split[2];
                fact[3] = split[4];
                //fact[4] = split[8];
                //fact[5] = split[9];

                for (int j = 0; j <= 2; j++) {
                    if ((count = conceptCounts.get(fact[j])) == null) {
                        conceptCounts.put(fact[j], 1);
                        HashSet<String[]> setik = new HashSet<>();
                        setik.add(fact);
                        conceptRelations.put(fact[j], setik);
                    } else {
                        conceptCounts.put(fact[j], count + 1);
                        HashSet<String[]> setik = conceptRelations.get(fact[j]);
                        setik.add(fact);
                        conceptRelations.put(fact[j], setik);
                    }
                }
                //facts.add(fact);
            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
        return facts;
    }

    public static ArrayList<String[]> rulesFromSeedWord(HashSet<String> seedWords, int limit) {
        ArrayList<String[]> outRules = new ArrayList<>();
        PriorityQueue<Tuple> priorityQueue = new PriorityQueue<>(10, new ConnectionDensityComparator());
        HashSet<String> priorityHash = new HashSet<>();
        for (String concept : conceptCounts.keySet()) {
            if (seedWords.contains(concept)) {
                priorityQueue.add(new Tuple(concept, 1));
                priorityHash.add(concept);
                System.out.println(concept);
            }
        }
        int a = 0;
        while (!priorityQueue.isEmpty() && outRules.size() < limit) {
            Tuple start = priorityQueue.remove();
            System.out.println(a++ + " --> " + start.x);
            HashSet<String[]> relations = conceptRelations.get(start.x);
            int rels = 0;
            for (String[] relation : relations) {
                if (rels++ > relationsLimit) {
                    continue;
                }
                outRules.add(relation);
                for (int i = 0; i < 3; i = i + 2) {
                    if (priorityHash.contains(relation[i]) || blacklist.contains(relation[i])) {
                        continue;
                    }
                    int score = getScore(relation[i], priorityHash);
                    System.out.println(relation[i] + " ----> " + score);
                    priorityQueue.add(new Tuple(relation[i], -1 * score));
                    priorityHash.add(relation[i]);
                }
            }
        }
        return outRules;
    }

    private static int getScore(String concept, HashSet<String> priorityHash) {
        int score = 0;
        HashSet<String[]> relations = conceptRelations.get(concept);
        for (String[] relation : relations) {
            for (String element : relation) {
                if (priorityHash.contains(element)) {
                    score++;
                }
            }
        }
        return score;
    }

    public static class ConnectionDensityComparator implements Comparator<Tuple> {

        @Override
        public int compare(Tuple a, Tuple b) {
            return Integer.compare((Integer) a.y, (Integer) b.y);
        }
    }
}
