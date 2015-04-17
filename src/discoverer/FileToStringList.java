package discoverer;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

/**
 * Nice java7 filetostring
 */
public class FileToStringList {
    public static String[] convert(String p) {
        List<String> lines = new ArrayList<String>();
        Path path = FileSystems.getDefault().getPath(p);

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line = null;
            while ((line = reader.readLine()) != null)
                if (line.length() != 0 && line.length() < 1000)
                    lines.add(line);

        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
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
