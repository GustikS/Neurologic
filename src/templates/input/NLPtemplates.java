/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package templates.input;

import discoverer.construction.ConstantFactory;
import discoverer.construction.TemplateFactory;
import discoverer.construction.Variable;
import discoverer.construction.template.KL;
import discoverer.construction.template.Kappa;
import discoverer.construction.template.Lambda;
import discoverer.construction.template.rules.SubKL;
import discoverer.construction.template.specialPredicates.SimilarityPredicate;
import discoverer.global.TextFileReader;
import discoverer.grounding.network.GroundKL;
import discoverer.grounding.network.GroundKappa;
import discoverer.grounding.network.GroundLambda;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Gusta
 */
public class NLPtemplates extends NellParser {

    public static void main(String[] args) {
        //zoo();
        createKernelTemplate();
    }

    public static void zoo() {
        NLPtemplates nlp = new NLPtemplates();
        String[] prototypes = new String[]{"mammal", "bird", "reptile", "fish", "amphibian", "insect", "crustacean"};
        String[] prototypes2 = new String[]{"waterAnimal", "landAnimal", "airAnimal"};

        String[] features = new String[]{"hair", "feathers", "eggs", "milk", "airborne", "aquatic", "predator", "toothed", "backbone", "breathes", "venomous", "fins"};
        String path = "C:\\Users\\Gusta\\googledrive\\Github\\LRNN\\in\\NLP\\animals\\zoo\\zoo.csv";
        String[] readFile = TextFileReader.readFile(path, 10000000);
        LinkedHashMap<String, List<Integer>> vectors = loadAnimalVectors(readFile, features.length);
        String template = nlp.createPrototypeTemplate(prototypes, features, vectors.keySet().toArray(new String[vectors.size()]));

        template += "\n\n" + nlp.createPrototypeTemplate(prototypes2, features, prototypes);

        String[] allPrototypes = Arrays.copyOf(prototypes, prototypes.length + prototypes2.length);
        System.arraycopy(prototypes2, 0, allPrototypes, prototypes.length, prototypes2.length);

        template = template + "\n\n" + nlp.createClusterRules(allPrototypes);

        String queries = nlp.createQueries(vectors, features);
        System.out.println(template);
        System.out.println(queries);
        
        String[] groupA = new String[]{"stingray","dogfish"};
        String[] groupB = new String[]{"catfish"};
        
        System.out.println(nlp.createMustLinks(groupA, groupB));
    }

    public static void smallAnimalWorld() {
        NLPtemplates nlp = new NLPtemplates();
        String[] prototypes = new String[]{"carnivore", "herbivore", "omnivore"};
        String[] features = new String[]{"eatsMeat", "eatsHerbs", "eatsEverything", "sharpTeeth", "flatTeeth", "bothTeeth"};
        String[] animals = new String[]{"zebra", "lion", "cheetah", "antilope", "tiger", "hippo", "dog"};
        String createPrototypeTemplate = nlp.createPrototypeTemplate(prototypes, features, animals);
        System.out.println(createPrototypeTemplate);
    }

    private static LinkedHashMap<String, List<Integer>> loadAnimalVectors(String[] vectors, int numFeats) {
        LinkedHashMap<String, List<Integer>> result = new LinkedHashMap<>();
        for (String vector : vectors) {
            String[] split = vector.split(";");
            List<Integer> feats = new ArrayList<>();
            result.put(split[0], feats);
            for (int i = 1; i <= numFeats; i++) {
                feats.add(Integer.parseInt(split[i]));
            }
        }
        return result;
    }

    public String createPrototypeTemplate(String[] prototypes, String[] features, String[] animals) {
        LinkedHashSet<String[]> triples = new LinkedHashSet<>();
        for (String animal : animals) {
            for (String prototype : prototypes) {
                String[] triple = new String[]{animal, "generalizations", prototype, "0.5"};
                triples.add(triple);
            }
        }
        for (String feature : features) {
            for (String prototype : prototypes) {
                String[] triple = new String[]{prototype, "hasFeature", feature, "0.5"};
                triples.add(triple);
            }
        }
        return generateTemplateFromTriples(triples, false);
    }

    private String createQueries(LinkedHashMap<String, List<Integer>> vectors, String[] features) {
        StringBuilder sb = new StringBuilder();
        for (String animal : vectors.keySet()) {
            List<Integer> vector = vectors.get(animal);
            for (int i = 0; i < vector.size(); i++) {
                if (vector.get(i) > 0) {
                    sb.append("1.0 ");
                } else {
                    sb.append("0.0 ");
                }
                sb.append("holdsKrek(").append(animal).append(",hasFeature,").append(features[i]).append(")\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private String createClusterRules(String[] prototypes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < prototypes.length; i++) {
            sb.append("1.0 holdsK(A,B,C) :- holdsLrek" + i + "(A,B,C).\n");
            sb.append("holdsLrek").append(i).append("(A,B,C) :- holdsK(A,generalizations,").append(prototypes[i]).append("), holdsK(").append(prototypes[i]).append(",B,C).\n\n");
        }
        return sb.toString();
    }

    public static void createKernelTemplate() {
        String[] features = new String[]{"hair", "feathers", "eggs", "milk", "airborne", "aquatic", "predator", "toothed", "backbone", "breathes", "venomous", "fins"};
        String path = "C:\\Users\\Gusta\\googledrive\\Github\\LRNN\\in\\NLP\\animals\\zoo\\zoo.csv";
        //String[] features = new String[]{"tail", "domestic", "catsize"};
        //String path = "C:\\Users\\Gusta\\googledrive\\Github\\LRNN\\in\\NLP\\animals\\zoo\\zoo2.csv";

        String[] readFile = TextFileReader.readFile(path, 10000000);
        LinkedHashMap<String, List<Integer>> vectors = loadAnimalVectors(readFile, features.length);

        String[] prototypes = new String[]{"deer", "sparrow", "honeybee", "cheetah", "octopus", "worm", "herring", "termite"};

        NLPtemplates nlp = new NLPtemplates();
        ConstantFactory.loadEmbeddings("C:\\Users\\Gusta\\googledrive\\Github\\LRNN\\in\\NLP\\animals\\zoo\\embeddings.csv");
        System.out.println(nlp.createSimilarity3Elements(ConstantFactory.getEmbeddings()));
        System.out.println(nlp.createSimilarityKernels());
        System.out.println(nlp.createProtypeTemplate3(prototypes, vectors, features));
        System.out.println(nlp.createQueries(vectors, features));
    }

    public String createSimilarity2Elements(Map<String, double[]> embeddings) {
        double[] values = new double[embeddings.size() * embeddings.size()];
        int i = 0;
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, double[]> ent1 : embeddings.entrySet()) {
            for (Map.Entry<String, double[]> ent2 : embeddings.entrySet()) {
                double value = SimilarityPredicate.getSimilarity(ent1.getKey(), ent2.getKey());

                sb.append(value).append(" similarEl(").append(ent1.getKey()).append(",").append(ent2.getKey()).append(")\n");

                values[i++] = value;
            }
            sb.append("\n");
        }
        sb.append("\n\n");
        return sb.toString();
    }

    public String createSimilarity3Elements(Map<String, double[]> embeddings) {
        double[] values = new double[embeddings.size() * embeddings.size()];
        int i = 0;
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, double[]> ent1 : embeddings.entrySet()) {
            for (Map.Entry<String, double[]> ent2 : embeddings.entrySet()) {
                double value = SimilarityPredicate.getSimilarity(ent1.getKey(), ent2.getKey());

                sb.append("1.0 similarEl(").append(ent1.getKey()).append(",").append(ent2.getKey()).append(",").append(value).append(")\n");

                values[i++] = value;
            }
            sb.append("\n");
        }
        sb.append("\n\n");
        return sb.toString();
    }

    public String createSimilarityKernels() {
        StringBuilder sb = new StringBuilder();
        int num = 10;
        int portion = 2;
        int a = 0;
        for (int j = -10; j < 10; j = j + portion) {
            sb.append("0.5 similarK(A,B) :- similarKer" + a + "(A,B).\n");
            sb.append("similarKer").append(a++).append("(A,B) :- similarEl(A,B,D), @geq(D,").append(Double.toString(j / 10.0)).append("), @leq(D,").append(Double.toString(1)).append(").\n");
        }
        return sb.toString();
    }

    private String createProtypeTemplate(String[] prototypes, LinkedHashMap<String, List<Integer>> prototypeFeatures, String[] features) {
        StringBuilder sb = new StringBuilder();

        for (String prototype : prototypes) {
            int j = 0;
            for (Integer feature : prototypeFeatures.get(prototype)) {
                if (feature == 1) {
                    sb.append("1.0 holdsK(S,P,O) :- holdsL").append(i).append("(S,P,O).\n");
                    sb.append("holdsL").append(i).append("(S,P,O) :- similar(S,").append(prototype).append("),@eq(P,hasFeature),@eq(O,").append(features[j]).append(").\n");
                    i++;
                }
                j++;
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private String createProtypeTemplate2(String[] prototypes, LinkedHashMap<String, List<Integer>> prototypeFeatures, String[] features) {
        StringBuilder sb = new StringBuilder();

        for (String prototype : prototypes) {
            int j = 0;
            for (Integer feature : prototypeFeatures.get(prototype)) {
                if (feature == 1) {
                    sb.append("1.0 holdsK(S,P,O) :- holdsL").append(i).append("(S,P,O).\n");
                    sb.append("holdsL").append(i).append("(S,P,O) :- @eq(S,").append(prototype).append("),@eq(P,hasFeature),@eq(O,").append(features[j]).append(").\n");
                }
                i++;
                j++;
            }
            sb.append("\n");
        }
        i = 0;
        for (String prototype : prototypes) {
            sb.append("1.0 holdsKrek(S,P,O) :- holdsLrek").append(i).append("(S,P,O).\n");
            sb.append("holdsLrek").append(i++).append("(S,hasFeature,O) :- similarK(S,").append(prototype).append("), holdsK(").append(prototype).append(",hasFeature,O).\n");
        }
        return sb.toString();
    }

    public String createProtypeTemplate3(String[] prototypes, LinkedHashMap<String, List<Integer>> prototypeFeatures, String[] features) {
        StringBuilder sb = new StringBuilder();

        for (String prototype : prototypes) {
            int j = 0;
            for (Integer feature : prototypeFeatures.get(prototype)) {
                if (feature == 1) {
                    sb.append("1.0 holdsK(").append(prototype).append(",hasFeature,").append(features[j]).append(")\n");
                }
                j++;
            }
            sb.append("\n");
        }
        i = 0;
        for (String prototype : prototypes) {
            sb.append("1.0 holdsKrek(S,P,O) :- holdsLrek").append(i).append("(S,P,O).\n");
            sb.append("holdsLrek").append(i++).append("(S,hasFeature,O) :- similarK(S,").append(prototype).append("), holdsK(").append(prototype).append(",hasFeature,O).\n");
        }
        return sb.toString();
    }

    public String createMustLinks(String[] groupA, String[] groupB) {
        StringBuilder sb = new StringBuilder();
        for (String a : groupA) {
            for (String b : groupB) {
                sb.append("1.0 mustLink(").append(a).append(",").append(b).append(")\n");
            }
        }
        sb.append("\n\n");
        sb.append("1.0 holdsK(A,generalizations,C) :- holdsL(A,generalizations,C).\n");
        sb.append("holdsL(A,generalizations,B) :- mustLink(A,X), holdsK(X,generalizations,B).\n");
        return sb.toString();
    }
}
