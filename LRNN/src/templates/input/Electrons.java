/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package templates.input;

import lrnn.global.TextFileReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import templates.Convertor;

/**
 *
 * @author Gusta
 */
public class Electrons {

    static LinkedHashMap<String, String[]> electrons = new LinkedHashMap<>();

    public static void main(String[] args) {
        Map<String, Integer> dict = new HashMap<>();
        dict.put("s", 1);
        dict.put("p", 2);
        dict.put("d", 3);
        dict.put("f", 4);

        String[] readFile = TextFileReader.readFile("../electrons/electron_configs.csv", 10000);
        for (int i = 1; i < readFile.length; i++) {
            String[] split = readFile[i].split(";");
            String symbol = split[2];
            String[] orbitals = split[4].split(" ");

            String[] embedding = new String[5];
            for (int j = 0; j < embedding.length; j++) {
                embedding[j] = "0";
            }
            int maxLayer = 0;
            for (String orbit : orbitals) {
                if (orbit.startsWith("[")) {
                    continue;
                }
                maxLayer = Integer.parseInt(orbit.substring(0, 1)) > maxLayer ? Integer.parseInt(orbit.substring(0, 1)) : maxLayer;
            }
            embedding[0] = maxLayer + "";
            for (String orbit : orbitals) {
                if (orbit.startsWith("[")) {
                    continue;
                }
                //if (maxLayer == Integer.parseInt(orbit.substring(0, 1))) {
                    embedding[dict.get(orbit.substring(1, 2))] = orbit.substring(2, Math.max(orbit.indexOf(" "), orbit.length()));
                //}
            }
            electrons.put(symbol, embedding);
        }
        Writer test;
        try {
            test = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("../electrons/embeds2.csv"), "utf-8"));
            for (Map.Entry<String, String[]> emb : electrons.entrySet()) {
                test.write(emb.getKey() + ";");
                for (int i = 0; i < emb.getValue().length; i++) {
                    test.write(emb.getValue()[i] + ";");
                }
                test.write("\n");
            }
            test.close();
        } catch (UnsupportedEncodingException ex1) {
            Logger.getLogger(Convertor.class.getName()).log(Level.SEVERE, null, ex1);
        } catch (FileNotFoundException ex1) {
            Logger.getLogger(Convertor.class.getName()).log(Level.SEVERE, null, ex1);
        } catch (IOException ex1) {
            Logger.getLogger(Convertor.class.getName()).log(Level.SEVERE, null, ex1);
        }
    }
}
