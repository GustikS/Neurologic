/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package templates.input;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.*;
import templates.Templator;

/**
 *
 * @author Gusta
 */
public class JsonFlowParser extends Templator {

    static HashMap<String, JSONObject> incidents = new HashMap<>();
    static HashMap<String, HashSet<URL>> urls = new HashMap<>();
    static HashSet<Character> alphabet = new HashSet<>();
    static int exampleLimit = 100000;

    public static void main(String[] args) {
        bondTypes = false;
        bondClusters = 1;
        randomFeatures = false;
        kappaPrefix = "letter";

        ArrayList<JSONObject> flows = getFlows("C:\\incidents20150701-anonymized\\");

        flows2Csv(flows, "in/strings/urls.csv");

        HashMap<String, HashSet> incident = getincidents(flows);

        ArrayList<String> lines = new ArrayList<>();
        for (String url : urls.keySet()) {
            //System.out.println("------------ " + url);
            for (URL uri : urls.get(url)) {
                //System.out.println(uri.toString());
                String line;
                String host = uri.getHost();
                String path = uri.getFile();
                String query = uri.getQuery();
                if (url.startsWith("malware|dga")) {
                    line = "1.0 " + makeRelational(host);
                } else {
                    line = "0.0 " + makeRelational(host);
                }
                System.out.println(line);
                lines.add(line);
            }
        }
        writeSimple(lines, "in\\flows\\examples");
        System.out.println("----------------");
        HashSet<String> lits = new HashSet<>();
        for (char c : alphabet) {
            System.out.println(c);
            lits.add(c + "/1");
        }
        ArrayList<String> lambda = createLambdaBindings(lits, kappaPrefix);
        ArrayList<String> ff = makeFullFeatures(2, 4, lambda, null, 3, 0);
        lambda.addAll(ff);
        for (String row : lambda) {
            System.out.println(row);
        }
        writeSimple(lambda, "in\\flows\\rules");
    }

    public static ArrayList<JSONObject> getFlows(String path) {
        ArrayList<JSONObject> flows = new ArrayList<>();
        File[] files = new File(path).listFiles();
        int length = files.length;
        length = 10;
        for (int i = 0; i < length; i++) {
            BufferedReader br = null;
            try {
                System.out.println(i);
                br = new BufferedReader(new FileReader(files[i]));
                String line;
                int a = 0;
                while ((line = br.readLine()) != null) {
                    JSONObject jo = new JSONObject(line);
                    flows.add(jo);
                    if (a++ > exampleLimit) {
                        break;
                    }
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(JsonFlowParser.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(JsonFlowParser.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JSONException ex) {
                Logger.getLogger(JsonFlowParser.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    br.close();
                } catch (IOException ex) {
                    Logger.getLogger(JsonFlowParser.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return flows;
    }

    private static HashMap<String, HashSet> getincidents(ArrayList<JSONObject> flows) {
        HashMap<String, HashSet> inci = new HashMap<>();
        for (JSONObject flow : flows) {
            try {
                JSONObject cta = (JSONObject) flow.get("cta");
                String id = (String) cta.get("incidentId");
                String name = (String) cta.get("incidentName");
                if (inci.get(id) == null) {
                    inci.put(id, new HashSet());
                }
                inci.get(id).add(flow);
                if (incidents.get(id) == null) {
                    incidents.put(id, cta);
                }
                if (urls.get(name) == null) {
                    urls.put(name, new HashSet<URL>());
                }
                try {
                    urls.get(name).add(new URL(flow.getJSONObject("http").getString("url")));
                } catch (MalformedURLException ex) {
                    Logger.getLogger(JsonFlowParser.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (JSONException ex) {
                Logger.getLogger(JsonFlowParser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return inci;
    }

    public static String makeRelational(String str) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length() - 1; i++) {
            alphabet.add(str.charAt(i));
            sb.append("bond(").append(str.charAt(i)).append("").append(i).append(",").append(str.charAt(i + 1)).append("").append(i + 1).append("), ");
            sb.append(str.charAt(i)).append("(").append(str.charAt(i)).append(i).append("), ");
        }
        sb.append(str.charAt(str.length() - 1)).append("(").append(str.charAt(str.length() - 1)).append(str.length() - 1).append(").");
        alphabet.add(str.charAt(str.length() - 1));

        return sb.toString();
    }

    private static void flows2Csv(ArrayList<JSONObject> flows, String path) {
        Writer wr;
        try {
            wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "utf-8"));

            for (JSONObject flow : flows) {
                JSONObject cta = (JSONObject) flow.get("cta");
                String id = (String) cta.get("incidentId");
                String name = (String) cta.get("incidentName");
                String event = (String) cta.get("eventClassification");
                String confidence = (String) cta.get("incidentConfidence");
                String url = flow.getJSONObject("http").getString("url");
                        
                wr.write(url + ", " + name + ", " + event + ", " + confidence + ", " + id + "\n");
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(JsonFlowParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(JsonFlowParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(JsonFlowParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(JsonFlowParser.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
