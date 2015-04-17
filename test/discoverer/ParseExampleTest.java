package discoverer;

import static org.junit.Assert.*;
import org.junit.*;

public class ParseExampleTest {

    @Test
    public void parseExample1() {
        String[][] results = {
            {"1.0"},
            {"bond","tr000_4","tr000_2","0"},
            {"cl","tr000_4"},
            {"c","tr000_2"},
            {"1","0"},
            {"bond","tr000_2","tr000_4","0"},
            {"bond","tr000_5","tr000_2","1"},
            {"h","tr000_5"},
            {"1","1"},
            {"bond","tr000_2","tr000_5","1"},
            {"bond","tr000_3","tr000_2","2"},
            {"cl","tr000_3"},
            {"1","2"},
            {"bond","tr000_2","tr000_3","2"},
            {"bond","tr000_2","tr000_1","3"},
            {"cl","tr000_1"},
            {"1","3"},
            {"bond","tr000_1","tr000_2","3"},
        };
        String example = "1.0 bond(tr000_4, tr000_2, 0), cl(tr000_4), c(tr000_2), 1(0), bond(tr000_2, tr000_4, 0), bond(tr000_5, tr000_2, 1), h(tr000_5), 1(1), bond(tr000_2, tr000_5, 1), bond(tr000_3, tr000_2, 2), cl(tr000_3), 1(2), bond(tr000_2, tr000_3, 2), bond(tr000_2, tr000_1, 3), cl(tr000_1), 1(3), bond(tr000_1, tr000_2, 3).";
        String[][] tokens = Parser.parseExample(example);
        assertEquals("Length of literals", results.length, tokens.length);
        for (int i = 0; i < tokens.length; i++)
            for (int j = 0; j < tokens[i].length; j++)
                assertTrue("Comparison of tokens", tokens[i][j].equals(results[i][j]));
    }

    @Test
    public void parseExample2() {
        String[][] results = {
            {"1"},
            {"bond","tr000_4","tr000_2","0"},
        };
        String example = "1 bond(tr000_4, tr000_2, 0).";
        String[][] tokens = Parser.parseExample(example);
        assertEquals("Length of literals", results.length, tokens.length);
        for (int i = 0; i < tokens.length; i++)
            for (int j = 0; j < tokens[i].length; j++)
                assertTrue("Comparison of tokens", tokens[i][j].equals(results[i][j]));
    }

    @Ignore
    public void parseExample3() {
        String[][] results = {
            {"1"},
            {"bond","tr000_4","tr000_2","0"},
            {"atom"},
            {"camelCase"},
            {"color", "brown", "a"},
            {"noConst"},
        };
        String example = "1 bond(tr000_4, tr000_2, 0), atom, camelCase, color(brown, a), noConst.";
        String[][] tokens = Parser.parseExample(example);
        assertEquals("Length of literals", results.length, tokens.length);
        for (int i = 0; i < tokens.length; i++)
            for (int j = 0; j < tokens[i].length; j++)
                assertTrue("Comparison of tokens", tokens[i][j].equals(results[i][j]));
    }
}
