package discoverer;

import static org.junit.Assert.*;
import org.junit.*;

public class ParseRuleTest {
    @Test
    public void parseRule1() {
        String[][] results = {
            {""},
            {"lambda","QQQ"},
            {"atomKappa_1","X"},
            {"bond","X","Y","B1"},
            {"bondKappa_2","B1"},
            {"atomKappa_1","Y"},
            {"bond","Y","Z","B2"},
            {"bondKappa_1","B2"},
            {"atomKappa_1","Y"},
        };
        String rule = "lambda(QQQ) :- atomKappa_1(X), bond(X,Y,B1), bondKappa_2(B1), atomKappa_1(Y), bond(Y,Z,B2), bondKappa_1(B2), atomKappa_1(Y).";

        String[][] tokens = Parser.parseRule(rule);
        assertEquals("Length of literals", results.length, tokens.length);
        for (int i = 0; i < tokens.length; i++)
            for (int j = 0; j < tokens[i].length; j++)
                assertTrue("Comparison of tokens", tokens[i][j].equals(results[i][j]));
    }

    @Test
    public void parseRule2() {
        String[][] results = {
            {"0.0"},
            {"atomKappa_1","X"},
            {"atomLambda_14","X"},
        };
        String rule = "0.0 atomKappa_1(X) :- atomLambda_14(X).";

        String[][] tokens = Parser.parseRule(rule);
        assertEquals("Length of literals", results.length, tokens.length);
        for (int i = 0; i < tokens.length; i++)
            for (int j = 0; j < tokens[i].length; j++)
                assertTrue("Comparison of tokens", tokens[i][j].equals(results[i][j]));

    }

    @Ignore
    public void parseRule3() {
        String[][] results = {
            {"0.0"},
            {"atomKappa_1"},
            {"atomLambda_14"},
        };
        String rule = "0.0 atomKappa_1 :- atomLambda_14.";

        String[][] tokens = Parser.parseRule(rule);
        assertEquals("Length of literals", results.length, tokens.length);
        for (int i = 0; i < tokens.length; i++)
            for (int j = 0; j < tokens[i].length; j++)
                assertTrue("Comparison of tokens", tokens[i][j].equals(results[i][j]));

    }
}

        /*
         *for (int i = 0; i < tokens.length; i++) {
         *    System.out.print("{");
         *    for (int j = 0; j < tokens[i].length; j++) {
         *        if (j != 0)
         *            System.out.print(",");
         *        System.out.print("\"" + tokens[i][j] + "\"");
         *    }
         *    System.out.print("},");
         *    System.out.println();
         *}
         */
