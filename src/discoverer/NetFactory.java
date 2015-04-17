package discoverer;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory for whole network
 */
public class NetFactory {
    private KappaFactory kFactory = new KappaFactory();
    private LambdaFactory lFactory = new LambdaFactory();
    private VariableFactory vFactory = new VariableFactory();

    private static List<KappaRule> kappaRules = new ArrayList<KappaRule>();

    public static List<KappaRule> getKappaRules() {
        return kappaRules;
    }

    public KL construct(String[] rules) {
        KL kl = null;
        for (int x = 0; x < rules.length; x++) {
            String[][] tokens = Parser.parseRule(rules[x]);

            boolean isLambdaLine = tokens[0][0].isEmpty();

            kl = isLambdaLine ? handleLambdaLine(tokens) : handleKappaLine(tokens);

            vFactory.clear();
        }

        return kl;
    }

    private Terminal constructTerm(String s) {
        boolean isVariable = s.matches("^[A-Z].*");

        return isVariable ? vFactory.construct(s) : ConstantFactory.construct(s);
    }

    private Lambda handleLambdaLine(String[][] tokens) {
        Lambda l = lFactory.construct(tokens[1][0]);
        SubL sl = new SubL(l);
        for (int i = 1; i < tokens[1].length; i++) {
            Terminal v = vFactory.construct(tokens[1][i]);
            sl.addVariable(v);
        }
        LambdaRule lr = new LambdaRule();
        lr.addHead(sl);

        for (int i = 2; i < tokens.length; i++) {
            Kappa k = kFactory.construct(tokens[i][0]);
            SubK sk = new SubK(k, false);
            for (int j = 1; j < tokens[i].length; j++) {
                Terminal t = constructTerm(tokens[i][j]);
                sk.addVariable(t);
            }
            lr.addBodyEl(sk);
        }

        l.setRule(lr);
        return l;
    }

    private Kappa handleKappaLine(String[][] tokens) {
        Double w = Double.parseDouble(tokens[0][0]);
        Kappa k = kFactory.construct(tokens[1][0]);
        SubK sk = new SubK(k, true);
        for (int i = 1; i < tokens[1].length; i++) {
            Terminal v = vFactory.construct(tokens[1][i]);
            sk.addVariable(v);
        }
        KappaRule kr = new KappaRule(w);
        kr.addHead(sk);
        kappaRules.add(kr);

        for (int i = 2; i < tokens.length; i++) {
            Lambda l =  lFactory.construct(tokens[i][0]);
            SubL sl = new SubL(l);
            for (int j = 1; j < tokens[i].length; j++) {
                Terminal t = constructTerm(tokens[i][j]);
                sl.addVariable(t);
            }
            kr.setBody(sl);
        }

        k.addRule(kr);
        return k;
    }
}
