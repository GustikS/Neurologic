package discoverer;

import java.util.*;
//settings
public class Global {
    public static Random rg = new Random();
    public static boolean cacheEnabled = true;
    public static boolean forwardCheckEnabled = true;
    public static final boolean debugEnabled = false;
    public static boolean pruning = true;
    public static double falseAtomValue = -1;   //non-entailed example output
    
    public static String grounding = Main.defaultGrounding;
    
    public static void setAvg(){
        grounding = "avg";
        pruning = false;    //important!
        forwardCheckEnabled = true;
    }

    public static void setMax() {
        grounding = "max";
        pruning = true;
        forwardCheckEnabled = true;
    }
}
