/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lrnn.construction.template.specialPredicates;

import lrnn.construction.ConstantFactory;

/**
 *
 * @author Gusta
 */
public class SimilarityPredicate extends SpecialPredicate {
    
    public SimilarityPredicate(String iname){
        name= iname;
    }

    @Override
    public double evaluate(String arg) {
        String[] args = arg.split(",");
        if (args.length == 2) {
            return getSimilarity(args[0], args[1]);
        } else {
            return 0;
        }
    }

    public static double getSimilarity(String a, String b) {
        if (a.equals(b)) {
            return 1.0;
        }
        if (a.equals("VAR") || b.equals("VAR")){
            return 1.0;
        }
        double[] vectorA = ConstantFactory.getEmbeddings().get(a);
        double[] vectorB = ConstantFactory.getEmbeddings().get(b);
        if (vectorA != null && vectorB != null) {
            double similarity = 0;
            double asum = 0;
            double bsum = 0;
            for (int i = 0; i < vectorB.length; i++) {
                similarity += vectorA[i] * vectorB[i];
                asum += vectorA[i] * vectorA[i];
                bsum += vectorB[i] * vectorB[i];
            }
            similarity = similarity / (Math.sqrt(asum) * Math.sqrt(bsum));
            return similarity;
        } else {
            return 0;   //I dont know embeddings for some of these
        }
    }

    @Override
    public void update(String arg, double gradient) {
        String[] args = arg.split(",");
        if (args.length == 2) {
            updateEmbeddings(args[0], args[1], gradient);
        } else {
            return;
        }
    }

    private void updateEmbeddings(String a, String b, double gradient) {
        double[] vectorA = ConstantFactory.getEmbeddings().get(a);
        double[] vectorB = ConstantFactory.getEmbeddings().get(b);
        
        if (vectorA == null || vectorB == null){
            return;
        }
        
        double diff = 0;
        for (int i = 0; i < vectorA.length; i++) {
            diff = vectorB[i] - vectorA[i];
            vectorA[i] += diff * gradient;
            vectorB[i] -= diff * gradient;
        }
        ConstantFactory.getEmbeddings().put(a, vectorA);
        ConstantFactory.getEmbeddings().put(b, vectorB);
    }
}
