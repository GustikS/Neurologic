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
import java.util.ArrayList;
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

    private static String memory = "20gb";
    private static String walltime = "2d";
    private static String procesors = "4";

    public static String directName;

    static String head;

    public static void main(String[] args) {
        try {
            LinkedList<String[]> scripts = new LinkedList<>();
            scripts.add(Configurations.groundings);
            scripts.add(Configurations.initials);
            scripts.add(Configurations.seeds);
            generate("seeds", "ground_inits", scripts);
        } catch (IOException ex) {
            Logger.getLogger(ScriptGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static void generate(String projectDir, String scriptDir, LinkedList<String[]> scripts) throws IOException {
        directName = scriptDir;
        File theDir = new File("metacentrum/" + scriptDir);
        if (!theDir.exists()) {
            System.out.println("creating directory metacentrum/" + scriptDir);
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
        createScripts(projectDir, scriptDir, scripts);
    }

    private static void createScripts(String neuroDir, String scriptDir, LinkedList<String[]> scripts) throws IOException {
        String path = "cd /storage/brno2/home/souregus/neuro_builds/" + neuroDir;
        head = path + "/dist/ \nmodule add jdk-8 \njava -jar neurologic.jar -e ../in/muta/examples -r ../in/muta/rules ";
        qsub = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("metacentrum/" + scriptDir + "/qsub.sh"), "utf-8"));

        LinkedList<String> configurations = Configurations.getConfigurations(scripts);
        for (String configuration : configurations) {
            String name = "script_" + configuration.replaceAll(" ", "_").replaceAll("-", "_");
            script = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("metacentrum/" + scriptDir + "/" + name + ".sh"), "utf-8"));
            script.write(head);
            script.write(configuration);
            script.flush();
            qsub.write("qsub -l walltime=" + walltime + " -l mem=" + memory + " -l scratch=50mb -l nodes=1:ppn=" + procesors + "  " + name + ".sh\n"
            );
        }
        qsub.flush();
    }

}
