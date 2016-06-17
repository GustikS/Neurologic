package discoverer.construction;

import static discoverer.construction.template.LightTemplate.weightFolder;
import discoverer.global.Global;
import discoverer.global.TextFileReader;
import discoverer.grounding.network.GroundKL;
import discoverer.learning.Saver;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Factory for constants
 */
public class ConstantFactory {

    private static Map<String, Variable> constMap = new HashMap<String, Variable>();    //this is static, needs to be cleaned between runs!!
    private static Map<String, double[]> embeddings;
    private static int nextConst = 0;

    public static void loadEmbeddings(String destination) {
        setEmbeddings(TextFileReader.loadEmbeddings(destination));
        for (String con : embeddings.keySet()) {
            construct(con);
        }
    }

    public static void clearConstantFactory() {
        if (Global.isDebugEnabled()) {
            System.out.println("before constructing, we need to do some extra cleaning here (in case this isn't a clean first run)!");
        }
        constMap.clear();
        nextConst = 0;
    }

    public static Variable construct(String name) {
        if (constMap.containsKey(name)) {
            return constMap.get(name);
        }

        Variable t = new Variable(name, nextConst++);
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

    public static Map<String, Variable> getConstMap() {
        return constMap;
    }

    /**
     * @return the embeddings
     */
    public static Map<String, double[]> getEmbeddings() {
        return embeddings;
    }

    /**
     * @param aEmbeddings the embeddings to set
     */
    public static void setEmbeddings(Map<String, double[]> aEmbeddings) {
        embeddings = aEmbeddings;
    }
    
    public static void exportEmbeddings(String destination){
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(weightFolder + destination + "_embeddings.csv"), "utf-8"));
            for (Map.Entry<String,double[]> ent : embeddings.entrySet()) {
                writer.write(ent.getKey() + ";");
                double[] values = ent.getValue();
                for (int i = 0; i < values.length; i++) {
                    writer.write(values[i] + ";");
                }
                writer.write("\n");
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Saver.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Saver.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(Saver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
