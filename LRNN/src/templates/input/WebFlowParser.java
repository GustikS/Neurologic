/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package templates.input;

import lrnn.global.Glogger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import templates.input.flow.Domain;
import templates.input.flow.Bag;
import templates.input.flow.Webflow;

/**
 *
 * @author Gusta
 */
public class WebFlowParser {

    static String inPath = "C:\\Users\\IBM_ADMIN\\Google Drive\\Github\\Neurologic\\in\\flows\\relational\\exs\\";

    static String outFile = "C:\\Users\\IBM_ADMIN\\Google Drive\\Github\\Neurologic\\in\\flows\\relational\\examples.txt";

    static String attributeSeparator = " (?=([^\"]*\"[^\"]*\")*[^\"]*$)";

    public static String mark = "--";
    
    static int maxflows = 10;
    static int maxbags = 5;

    static LinkedHashMap<String, String> labels = new LinkedHashMap<>();
    static LinkedHashMap<String, List<String>> domains = new LinkedHashMap<>(); //domain to users-bags
    static LinkedHashMap<String, List<Webflow>> bags = new LinkedHashMap<>();  //domain-user bag to flows

    static LinkedHashMap<String, String> relationalDataset = new LinkedHashMap<>();
    static BufferedWriter fw;

    public static void main(String[] args) throws IOException {
        File dir = new File(inPath);
        fw = new BufferedWriter(new FileWriter(outFile));
        //create domain-bag-flow objects
        parseDirs(dir);

        //create relational representation of domain
        createRelations();

        /*
        for (Map.Entry<String, String> labs : labels.entrySet()) {
            fw.write(relationalDataset.get(labs.getKey()) + "\n");
            System.out.println("\n" + relationalDataset.get(labs.getKey()));
        }
        */
        fw.flush();

    }

    static void createRelations() throws IOException {
        for (Map.Entry<String, List<String>> me : domains.entrySet()) {
            StringBuilder relSample = new StringBuilder();
            relSample.append(labels.get(me.getKey())).append(" ");
            Domain dom = new Domain(me.getKey());
            relSample.append(dom.getRelationalRepresentation()).append("\n");

            //and of corresponding bags(users)
            for (String bag : me.getValue()) {
                List<Webflow> flows = bags.get(bag);
                if (flows == null) {
                    System.out.println("");
                }
                Bag user = new Bag(bag, flows);
                relSample.append("hasBag(").append(dom.id).append(",").append(user.id).append("), ");
                relSample.append(user.getRelationalRepresentation()).append("\n");

                //and flows
                for (Webflow wf : flows) {
                    relSample.append("hasFlow(").append(user.id).append(",").append(wf.hash).append("), ");
                    relSample.append(wf.getRelationalRepresentaiton()).append("\n");
                }
            }
            //System.out.println(relSample.substring(relSample.length()-3, relSample.length()));
            relSample.replace(relSample.length() - 3, relSample.length(), ".\n");
            //System.out.println(relSample.substring(relSample.length()-3, relSample.length()));
            //relationalDataset.put(dom.url, relSample.toString());
            fw.write(relSample.toString() + "\n");
            System.out.println("\n" + relSample.toString());
        }
    }

    static void parseDirs(File dir) {
        int bagcount = 0;
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (File f : files) {
                if (f.isDirectory()) {
                    parseDirs(f);
                } else {
                    if (bagcount++ > maxbags){
                        break;
                    }
                    System.out.println(f + "\n");
                    if (domains.get(f.getParentFile().getName()) != null) {
                        domains.get(f.getParentFile().getName()).add(f.getParentFile().getName() + mark + f.getName());
                    } else {
                        List<String> ls = new ArrayList<>();
                        ls.add(f.getParentFile().getName() + mark + f.getName());
                        domains.put(f.getParentFile().getName(), ls);
                    }
                    bags.put(f.getParentFile().getName() + mark + f.getName(), flowsFromFile(f));
                    if (f.getParentFile().getParentFile().getName().contains("positive")) {
                        labels.put(f.getParentFile().getName(), "1.0");
                    } else {
                        labels.put(f.getParentFile().getName(), "0.0");
                    }
                }
            }
        }
    }

    private static List<Webflow> flowsFromFile(File f) {
        List<Webflow> flows = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;

            boolean positiveFormat = false;
            int linenum = 0;
            while ((line = br.readLine()) != null && linenum++ < maxflows) {
                System.out.println(line);
                String[] split = line.split(attributeSeparator);
                if (split.length == 16) {
                    flows.add(new Webflow(split, positiveFormat));
                } else if (split[13].contains("label")) {
                    positiveFormat = true;
                }
            }
            System.out.println("");
        } catch (Exception e) {
            Glogger.err("unable to open file" + e);
        }
        return flows;
    }
}
