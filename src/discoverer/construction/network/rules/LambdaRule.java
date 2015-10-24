package discoverer.construction.network.rules;

import discoverer.construction.Terminal;
import discoverer.construction.network.rules.SubL;
import discoverer.construction.network.rules.SubK;
import discoverer.global.Global;
import java.io.Serializable;
import java.util.*;

/**
 * Lambda clause - a real conjunctive rule
 */
public class LambdaRule extends Rule implements Serializable {

    private SubL head;
    private List<SubK> body;
    private Double lowerBound;

    public LambdaRule() {
        body = new ArrayList<SubK>();
        drawn = false;
    }

    public int getBodyLen() {
        return body.size();
    }

    public List<SubK> getBody() {
        return body;
    }

    public void addHead(SubL h) {
        head = h;
    }
    public boolean drawn;

    public boolean isDrawn() {
        return drawn;
    }

    public void setDrawn(boolean b) {
        drawn = b;
    }

    public void setLowerBound(Double d) {
        lowerBound = d;
    }

    public Double getLowerBound() {
        return lowerBound;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(head.toString());
        sb.append(" :- ");
        if (body.size() > 0) {
            for (SubK sk : body) {
                sb.append(sk.toString());
                sb.append(",");
            }
            sb.replace(sb.length() - 1, sb.length(), ".");
        }
        return sb.toString();
    }

    /**
     * adds grounded Kappa literal to body adds every not Binded variable to
     * unbound Terminal List
     *
     * @param e
     */
    public void addBodyEl(SubK e) {
        body.add(e);
        for (Terminal t : e.getTerms()) {
            if (!t.isBind()) {
                unbound.add(t);
            }
        }
    }

    public SubL getHead() {
        return head;
    }

    @Override
    public Terminal getNextUnbound() {
        for (Terminal var : unbound) {
            if (var.isDummy()) {
                return var;
            }
        }

        ArrayList<Terminal> winners = new ArrayList<Terminal>();
        int highestScore = 0;

        for (Terminal var : unbound) {
            int newScore = countScoreFor(var);

            if (newScore == highestScore) {
                winners.add(var);
            } else if (newScore > highestScore) {
                highestScore = newScore;
                winners.clear();
                winners.add(var);
            }
        }

        int randomIndex = Global.getRg().nextInt(winners.size());
        return winners.get(randomIndex);
    }

    public int countScoreFor(Terminal var) {
        int score = 0;
        for (SubK sk : body) {
            score += sk.countScoreFor(var);
        }

        return score;
    }
}
