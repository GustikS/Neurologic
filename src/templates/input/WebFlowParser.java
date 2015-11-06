/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package templates.input;

import discoverer.global.Glogger;
import templates.input.flow.Flow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import templates.input.flow.Webflow;

/**
 *
 * @author Gusta
 */
public class WebFlowParser {

    static String path = "C:\\cisco-samples\\negatives-flows-dynamic-train\\negative-flows-all\\69.31.117.207";
    static String attributeSeparator = " (?=([^\"]*\"[^\"]*\")*[^\"]*$)";

    static HashMap<String, List<Webflow>> dataset = new HashMap<>();

    static HashMap<String, String> relationalDataset = new HashMap<>();

    public static void main(String[] args) {
        File dir = new File(path);
        parseDir(dir);
        for (Map.Entry<String, List<Webflow>> me : dataset.entrySet()) {
            String sample = createRelationalSample("class", me.getValue());
            relationalDataset.put(me.getKey(), sample);
        }

        for (Map.Entry<String, String> rels : relationalDataset.entrySet()) {
            System.out.println("\n" + rels.getKey() + "\n" + rels.getValue());
        }
    }

    static String createRelationalSample(String label, List<Webflow> flows) {
        StringBuilder sb = new StringBuilder();
        sb.append(label).append(" :- ");
        for (Webflow f : flows) {
            sb.append("\n");
            sb.append("flow(").append(f.hash).append("), ");
            sb.append(f.getRelationalRepresentaiton());
        }
        sb.append(".");
        return sb.toString();
    }

    static void parseDir(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (File f : files) {
                if (f.isDirectory()) {
                    parseDir(f);
                } else {
                    System.out.println(f + "\n");
                    dataset.put(f.getPath(), flowsFromFile(f));
                }
            }
        }
    }

    private static List<Webflow> flowsFromFile(File f) {
        List<Webflow> flows = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;
            int id = 0;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                String[] split = line.split(attributeSeparator);
                if (split.length == 16) {
                    flows.add(new Webflow(split, "flow" + id++));
                }
            }
            System.out.println("");
        } catch (Exception e) {
            Glogger.err("unable to open file" + e);
        }
        return flows;
    }
}
