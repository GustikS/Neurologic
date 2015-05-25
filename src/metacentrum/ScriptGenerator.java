/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metacentrum;

import discoverer.global.Glogger;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gusta
 */
public class ScriptGenerator {

    static Writer script;
    static Writer qsub;

    private static final String walltime = "2w";
    private static final String queue = "-q q_" + walltime + "@wagap.cerit-sc.cz";
    private static final String javaPars = " -XX:+UseSerialGC -XX:NewSize=2000m -Xms4096m -Xmx14g";
    private static final String memory = "16gb";

    private static final String procesors = "1";

    public static String directName;

    static String head;
    private static final String metaDir = "../metacentrum";

    public static void main(String[] args) {
        try {
            String common = "-f 5 ";
            LinkedList<String[]> scripts = new LinkedList<>();
            scripts.add(Configurations.datasets);
            //scripts.add(Configurations.templates);
            //scripts.add(Configurations.seeds);
            //scripts.add(Configurations.sgd);
            scripts.add(Configurations.learnRates);
            //scripts.add(Configurations.learnDecay);
            scripts.add(Configurations.activations);
            //scripts.add(Configurations.initials);
            scripts.add(Configurations.groundings);
            //scripts.add(Configurations.cumSteps);
            //scripts.add(Configurations.dropouts);
            generate("allDatasets", "allDatasetsNCIGI", scripts, common);
        } catch (IOException ex) {
            Logger.getLogger(ScriptGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static void generate(String projectDir, String scriptDir, LinkedList<String[]> scripts, String common) throws IOException {
        directName = scriptDir;
        File theDir = new File(metaDir + "/" + scriptDir);
        if (!theDir.exists()) {
            System.out.println("creating directory " + metaDir + "/" + scriptDir);
            boolean result = false;
            try {
                theDir.mkdir();
                result = true;
            } catch (SecurityException se) {
                Glogger.err(se.getMessage());
            }
            if (result) {
                System.out.println("DIR created");
            }
        }
        createScripts(projectDir, scriptDir, scripts, common);
    }

    private static void createScripts(String neuroDir, String scriptDir, LinkedList<String[]> scripts, String common) throws IOException {
        String path = "cd /storage/brno2/home/souregus/neuro_builds/" + neuroDir;
        head = path + "/dist/ \nmodule add jdk-8 \njava " + javaPars + " -jar neurologic.jar ";
        qsub = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(metaDir + "/" + scriptDir + "/qsub.sh"), "utf-8"));

        LinkedList<String> configurations = Configurations.getConfigurations(scripts);
        for (String configuration : configurations) {
            String name = "script_" + (common + configuration).replaceAll(" ", "_").replaceAll("-", "").replaceAll("/", "_").replaceAll("\\.", "");
            script = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(metaDir + "/" + scriptDir + "/" + name + ".sh"), "utf-8"));
            script.write(head);
            script.write(common);
            script.write(configuration);
            script.flush();
            qsub.write("qsub " + queue + " -l walltime=" + walltime + " -l mem=" + memory + " -l scratch=50mb -l nodes=1:ppn=" + procesors + "  " + name + ".sh\n"
            );
        }
        qsub.flush();
    }
}
