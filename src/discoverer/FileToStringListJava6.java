package discoverer;

import java.util.*;
import java.io.*;

/**
 * Ugly java6 filetostring
 */
public class FileToStringListJava6 {
    public static String[] convert(String p, int maxLineLength) {
        List<String> lines = new ArrayList<String>();
        BufferedReader buffReader = null;
        try {
            buffReader = new BufferedReader(new FileReader(p));
            String line = null;
            while ((line = buffReader.readLine()) != null)
                if (line.length() != 0 && line.length() < maxLineLength)
                    lines.add(line);

        } catch(IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                buffReader.close();
            } catch(IOException ioe1) {
                //Leave It
            }
        }

        return ListToArray(lines);
    }

    private static String[] ListToArray(List<String> list) {
        String[] ret = new String[list.size()];
        for (int i = 0; i < ret.length; i++)
            ret[i] = list.get(i);

        return ret;
    }
}
