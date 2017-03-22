/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metacentrum;

import lrnn.global.Glogger;

import java.io.*;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Gusta
 */
public class ScriptGenerator {

    //login to fronted

    private static final String metaDir = "../metacentrum";

    static Writer script;
    static Writer qsub;

    //-­W cgroup=true
    private static String serverPath = "cd /storage/plzen1/home/souregus/neuro_builds/";

    private static final String hours = "168";


    private static final String procesors = "1";
    private static final String memory = "16";
    private static final String javaPars = " -XX:+UseSerialGC -XX:-BackgroundCompilation -XX:NewSize=2000m -Xms4096m -Xmx" + memory + "g -Djava.util.concurrent.ForkJoinPool.common.parallelism=1 -Daffinity.reserved=1 ";

    private static String jarName = "StructureLearning.jar";

    public static String directName;

    static String head;
    private static boolean noOutput = false;

    private static String queue = "";
    private static String qsubStart = "";

    public static void main(String[] args) {
        //qsubStart = "module add torque-client";   //change to old (no-pro) planner (=arien or wagap)
        //queue = "-q default@wagap.cerit-sc.cz";  //switch from arien to wagap

        //queue = "-q uv@wagap-pro.cerit-sc.cz";  //switch from default arien-pro to wagap-pro

        try {
            //String common = "-gr avg -ac sig_sig -f 5 -ls 3000 -drawing 0 -alldiff 0 -debug 0 -bug 1 -out ../results/kernel ";
            String common = "-sbs 20 -sms 5 -aes 0 -dataset ";
            LinkedList<String[]> scripts = new LinkedList<>();
            scripts.add(Configurations.datasets);
            //scripts.add(Configurations.templates);
            //scripts.add(Configurations.seeds);
            //scripts.add(Configurations.sgd);
            //scripts.add(Configurations.learnRates);
            //scripts.add(Configurations.bpSteps);
            //scripts.add(Configurations.learnDecay);
            //scripts.add(Configurations.activations);
            //scripts.add(Configurations.initials);
            //scripts.add(Configurations.groundings);
            //scripts.add(Configurations.cumSteps);
            //scripts.add(Configurations.dropouts);
            generate("mlj2", "mlj2", scripts, common);
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
        String path = serverPath + neuroDir;
        head = path + "/dist/ \nmodule add jdk-8 \nexport OMP_NUM_THREADS=" + procesors + " \nsleep 60\njava " + javaPars + " -jar " + jarName + " ";
        qsub = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(metaDir + "/" + scriptDir + "/qsub.sh"), "utf-8"));
        qsub.write(qsubStart + "\n");
        LinkedList<String> configurations = Configurations.getConfigurations(scripts);
        for (String configuration : configurations) {
            String name = "script_" + (common + configuration).replaceAll(" ", "_").replaceAll("-", "").replaceAll("/", "_").replaceAll("\\.", "");
            script = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(metaDir + "/" + scriptDir + "/" + name + ".sh"), "utf-8"));
            script.write(head);
            script.write(common);
            script.write(configuration);
            if (noOutput) script.write("> /dev/null");
            script.flush();
            qsub.write("qsub " + queue + " -l select=1:ncpus="+procesors+":mem="+memory+"gb" + " -l walltime="+hours+":00:00 " + name + ".sh\n"
            //qsub.write("qsub " + queue + " -l walltime=" + hours + "h -l mem=" + memory + "g -l scratch=50mb -l nodes=1:ppn=" + procesors + "  " + name + ".sh\n"
            );
        }
        qsub.flush();
    }
}