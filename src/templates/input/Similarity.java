/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package templates.input;

import discoverer.construction.ConstantFactory;
import discoverer.construction.template.specialPredicates.SimilarityPredicate;
import discoverer.global.TextFileReader;
import java.util.Map;

/**
 *
 * @author Gusta
 */
public class Similarity {

    public static void main(String[] args) {
        Similarity sim = new Similarity();
        ConstantFactory.loadEmbeddings("C:\\Users\\Gusta\\googledrive\\Github\\LRNNoldVersion\\electrons\\embeds.csv");
        System.out.println(sim.createSimilarity3Elements(ConstantFactory.getEmbeddings()));
        System.out.println(sim.createSimilarityKernels(3, TextFileReader.readFile("C:\\Users\\Gusta\\googledrive\\Github\\LRNNoldVersion\\electrons\\atoms.txt", 100000)));
    }

    public String createSimilarityKernels(int count, String[] atoms) {
        StringBuilder sb = new StringBuilder();
        int portion = 1;
        int a = 0;
        for (int i = 1; i <= count; i++) {
            for (int j = 1; j < 10; j = j + portion) {
                sb.append(j / 10.0 + " similarK" + i + "(A,B) :- similarKer" + i + "_" + a + "(A,B).\n");
                sb.append("similarKer" + i + "_").append(a).append("(A,B) :- getType(B,T), similarEl(A,T,D), @geq(D,").append(Double.toString(j / 10.0)).append("), @leq(D,").append(Double.toString(1)).append(").\n");
                a++;
            }
            a = 0;
            sb.append("\n");
        }
        sb.append("\n\n");
        for (String atom : atoms) {
            sb.append("1.0 getType(A," + atom.toLowerCase() + ") :- " + atom.toLowerCase() + "(A).\n");
        }
        return sb.toString();
    }

    public String createSimilarityChargeKernels(int clusters, String[] atoms) {
        StringBuilder sb = new StringBuilder();
        int portion = 2;
        int a = 0;
        for (int i = 1; i <= clusters; i++) {
            for (int j = -6; j < 10; j = j + portion) {
                sb.append(j / 10.0 + " atomKappa_A" + i + "(A) :- chargeKer" + i + "_" + a + "(A).\n");
                sb.append("chargeKer" + i + "_").append(a).append("(A) :- charge(A,D), @geq(D,").append(Double.toString(j / 10.0)).append("), @leq(D,").append(Double.toString(1)).append(").\n");
                a++;
            }
            a = 0;
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
            if (!ent1.getKey().startsWith("prototype")) {
                continue;
            }
            for (Map.Entry<String, double[]> ent2 : embeddings.entrySet()) {
                double value = SimilarityPredicate.getSimilarity(ent1.getKey(), ent2.getKey());

                sb.append("similarEl(").append(ent1.getKey().toLowerCase()).append(",").append(ent2.getKey().toLowerCase()).append(",").append(value).append(")\n");

                values[i++] = value;
            }
            sb.append("\n");
        }
        sb.append("\n\n");
        return sb.toString();
    }
}
