package discoverer;

public class Parser {
    /**
     * Parse example from string format into string tokens. It tries to trim
     * spaces but does NOT check for syntax errors.
     * p1(a,b),p2(c,d). => {{p1,a,b},{p2,c,d}}
     *
     * @param example String which represents example
     *
     * @return Tokens with separated strings
     */
    public static String[][] parseQuery(String example) {

        String[] literals = example.replaceAll(" ", "").split("\\)[,.]");
        String[][] tokens = new String[literals.length][];

        for (int i = 0; i < literals.length; i++)
            tokens[i] = parseLiteral(literals[i]);

        return tokens;
    }

    public static String[][] parseExample(String example) {
        int expLen = getWeightLen(example);
        String expected = example.substring(0,expLen).replaceAll(" ", "");

        String[] literals = example.substring(expLen).replaceAll("[ .]", "").split("\\)[,]");
        String[][] tokens = new String[literals.length+1][];

        tokens[0] = new String[1];
        tokens[0][0] = expected;

        for (int i = 0; i < literals.length; i++)
            tokens[i+1] = parseLiteral(literals[i]);

        return tokens;
    }

    /**
     * Parse rule from string format into string tokens. It tries to trim
     * spaces but does NOT check for syntax errors.
     * p(A,B) :- p1(a,b),p2(c,d). => {{p,A,B,},{p1,a,b},{p2,c,d}}
     *
     * @param rule String which represents rule
     *
     * @return Tokens with separated strings
     */
    public static String[][] parseRule(String rule) {
        int weightLen = getWeightLen(rule);
        String weight = rule.substring(0,weightLen).replaceAll(" ", "");

        String[] ruleSplit = rule.substring(weightLen).replaceAll(" ", "").split(":-");

        String[] parsedHead = parseLiteral(ruleSplit[0]);
        String[] bodyLiterals = ruleSplit[1].split("\\)[,.]");


        String[][] tokens = new String[bodyLiterals.length + 2][];

        tokens[0] = new String[1];
        tokens[0][0] = weight;
        tokens[1] = parsedHead;
        for (int i = 0; i < bodyLiterals.length; i++)
            tokens[i+2] = parseLiteral(bodyLiterals[i]);

        /*
         *if (tokens[0][0].isEmpty()) {
         *    String[][] tokens2 = new String[tokens.length-1][];
         *    System.arraycopy(tokens, 1, tokens2, 0, tokens.length-1);
         *    return tokens2;
         *}
         */

        return tokens;
    }

    /**
     * Parse literal from string format into string tokens. It uses '(' and ','
     * as a separators. It does NOT check for syntax errors or white spaces.
     *
     * @param literal String which represents literal
     *
     * @return Tokens with separated strings
     */
    private static String[] parseLiteral(String literal) {
        String[] tokens = literal.replaceAll("\\)", "").split("[\\(,]");

        return tokens;
    }

    /**
     * Helper function for determining the last index of weight value when parsing.
     *
     * @param line Line with given rule
     * @return index of last index
     */
    private static int getWeightLen(String line) {
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (!(c == '.' || c == ' ' || (c >= '0' && c <= '9')))
                return i;
        }

        return 0;
    }
}
