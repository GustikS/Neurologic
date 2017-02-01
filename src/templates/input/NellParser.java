/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package templates.input;

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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/**
 *
 * @author Gusta
 */
public class NellParser {

    static HashMap<String, Integer> conceptCounts = new HashMap<>(3000000);
    static HashMap<String, LinkedList<String[]>> conceptRelations = new HashMap<>(3000000);
    static HashSet<String> blacklist = new HashSet<String>();

    static int relationsLimit = 10000000;

    public static void main(String[] args) {
        blacklist.add("concept:haswikipediaurl");
        //blacklist.add("generalizations");
        loadFacts("C:\\Users\\Gusta\\Downloads\\NELL.08m.995.esv.csv\\NELL.08m.995.esv.csv");
        System.out.println("--------------------------------------------");
        HashSet<String> seedWords = new HashSet<>();
        //seedWords.add("concept:actor");
        //seedWords.add("concept:celebrity");
        //seedWords.add("concept:male");
        //seedWords.add("generalizations");

        //LinkedHashSet<String[]> rulesFromSeedWord = rulesFromSeedWords(seedWords, 100);
        LinkedHashSet<String[]> rulesFromSeedWord = getActorsCluster("concept:actor");

        System.out.println("--------------------------------------------");
        /*
        int a = 0;
        for (String[] strings : rulesFromSeedWord) {
            System.out.print(a++ + " ");
            for (String string : strings) {
                System.out.print(string + "\t");
            }
            System.out.println("");
        }
        */
        System.out.println("-----------------");
        for (String[] strings : rulesFromSeedWord) {
            System.out.println("R(" + strings[0] + "," + strings[2] + ")");
        }
        //System.out.println(generateTemplateFromTriples(rulesFromSeedWord, true));
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

                if (!fact[0].startsWith("concept") || !fact[2].startsWith("concept")) {
                    continue;
                }

                //save all rules for:
                // 0 = subject  1 = predicate  2 = object
                for (int j : new int[]{0, 2}) {
                    if ((count = conceptCounts.get(fact[j])) == null) {
                        conceptCounts.put(fact[j], 1);
                        LinkedList<String[]> setik = new LinkedList<>();
                        setik.add(fact);
                        conceptRelations.put(fact[j], setik);
                    } else {
                        conceptCounts.put(fact[j], count + 1);
                        LinkedList<String[]> setik = conceptRelations.get(fact[j]);
                        if (fact[1].equals("generalizations")) {
                            setik.addFirst(fact);
                        } else {
                            setik.add(fact);
                        }
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

    public static LinkedHashSet<String[]> getActorsCluster(String seedword) {
        LinkedHashSet<String[]> outRules = new LinkedHashSet<>();
        LinkedList<String[]> triples = conceptRelations.get(seedword);
        for (String[] triple : triples) {
            if (triple[1].equals("generalizations") && triple[2].equals(seedword)) {
                outRules.addAll(getGeneralizations(triple[0], 0));
            }
        }
        return outRules;
    }

    public static LinkedHashSet<String[]> rulesFromSeedWords(HashSet<String> seedWords, int limit) {
        LinkedHashSet<String[]> outRules = new LinkedHashSet<>();
        PriorityQueue<Tuple> priorityQueue = new PriorityQueue<>(10, new ConnectionDensityComparator());
        HashSet<String> priorityHash = new HashSet<>();
        for (String concept : conceptCounts.keySet()) {
            if (seedWords.contains(concept)) {
                priorityQueue.add(new Tuple(concept, 1000.0));
                priorityHash.add(concept);
                System.out.println(concept);
            }
        }
        int a = 0;
        while (!priorityQueue.isEmpty() && outRules.size() < limit) {

            //get a top scoring concept
            Tuple start = priorityQueue.remove();
            System.out.println(a++ + " --> " + start.x);
            LinkedList<String[]> relations = conceptRelations.get(start.x);
            int rels = 0;

            //extend the top concept with all its generalizations
            relations.addAll(getGeneralizations((String) start.x, 10));

            //for all its relations
            for (String[] relation : relations) {
                //if the triplet doesnt connect at least two entities
                /*
                if (getAbsoluteScore(relation, priorityHash) <= 0) {
                    continue;
                }
                 */
                //add the relation to output
                outRules.add(relation);

                //for every relation from the top cocnept, add to priorityQueue:
                // 0 = subject  1 = predicate  2 = object
                for (int i : new int[]{0, 2}) {

                    if (priorityHash.contains(relation[i]) || blacklist.contains(relation[i])) {
                        continue;
                    }
                    double score = getScore(relation[i], priorityHash);

                    //System.out.println(relation[i] + " ----> " + score);
                    priorityQueue.add(new Tuple(relation[i], -1 * score));
                    priorityHash.add(relation[i]);
                }
            }
        }
        return outRules;
    }

    private static double getScore(String concept, HashSet<String> priorityHash) {
        double score = 0;
        LinkedList<String[]> relations = conceptRelations.get(concept);
        if (relations == null) {
            return 0;
        }
        for (String[] relation : relations) {
            for (int i = 0; i < 3; i = i + 1) {
                if (priorityHash.contains(relation[i])) {
                    score++;
                }
            }
        }
        score = score / relations.size() / 3.0;
        return score;
    }

    private static LinkedList<String[]> getGeneralizations(String concept, int level) {
        LinkedList<String[]> rules = new LinkedList<>();
        LinkedList<String> generals = new LinkedList<>();
        generals.add(concept);
        while (!generals.isEmpty()) {
            String top = generals.remove();
            LinkedList<String[]> relations = conceptRelations.get(top);
            if (relations == null) {
                continue;
            }
            int i = 0;
            while (i < relations.size() && relations.get(i)[1].equals("generalizations")) {
                if (relations.get(i)[0].equals(top)) {
                    rules.add(relations.get(i));
                    generals.add(relations.get(i)[2]);
                }
                i++;
            }
            if (level-- == 0) {
                break;
            }
        }
        return rules;
    }

    private static int getAbsoluteScore(String[] triplet, HashSet<String> priorityHash) {
        int score = 0;
        for (int i = 0; i < 3; i = i + 1) {
            if (priorityHash.contains(triplet[i])) {
                score++;
            }
        }
        return score;
    }

    private static String createTriple(String[] triple, int i, boolean withSimilarities) {
        double conf;
        try {
            conf = Double.parseDouble(triple[3]);
            if (triple[3].equals("NaN")) {
                conf = 0.1;
            }
        } catch (Exception e) {
            conf = 0.1;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(conf).append(" holdsK(S,P,O) :- holdsL").append(i).append("(S,P,O).\n");
        if (withSimilarities) {
            sb.append("1.0 similarK" + i + "s(A,B) :- similar(A,B).\n");
            sb.append("1.0 similarK" + i + "p(A,B) :- similar(A,B).\n");
            sb.append("1.0 similarK" + i + "o(A,B) :- similar(A,B).\n");

            sb.append("holdsL").append(i).append("(S,P,O) :- ");
            sb.append("similarK" + i + "s(S,").append(triple[0].replaceAll(":", "_")).append("),");
            sb.append("similarK" + i + "p(P,").append(triple[1].replaceAll(":", "_")).append("),");
            sb.append("similarK" + i + "o(O,").append(triple[2].replaceAll(":", "_")).append(").\n");
        } else {
            sb.append("holdsL").append(i).append("(S,P,O) :- ");
            sb.append("similar(S,").append(triple[0].replaceAll(":", "_")).append("),");
            sb.append("similar(P,").append(triple[1].replaceAll(":", "_")).append("),");
            sb.append("similar(O,").append(triple[2].replaceAll(":", "_")).append(").\n");
        }
        return sb.toString();
    }

    public static class ConnectionDensityComparator implements Comparator<Tuple> {

        @Override
        public int compare(Tuple a, Tuple b) {
            return Double.compare((Double) a.y, (Double) b.y);
        }
    }

    static int i = 0;

    static String generateTemplateFromTriples(LinkedHashSet<String[]> triples, boolean withSimilarities) {
        StringBuilder sb = new StringBuilder();

        for (String[] triple : triples) {
            sb.append(createTriple(triple, i, withSimilarities));
            i++;
        }
        return sb.toString();
    }

    public String expandFeaturesUP(List<String[]> triples) {
        String concept = triples.get(0)[0];
        LinkedList<String[]> generalizations = getGeneralizations(concept, 10);
        int i = 0;
        StringBuilder sb = new StringBuilder();
        for (String[] generalization : generalizations) {
            String general = generalization[2];
            for (String[] triple : triples) {
                sb.append(createTriple(new String[]{general, triple[1], triple[2]}, i++, true));
            }
        }
        return sb.toString();
    }
}
