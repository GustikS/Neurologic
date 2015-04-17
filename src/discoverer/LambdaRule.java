package discoverer;

import java.util.*;

/**
 * Lambda clause
 */
public class LambdaRule extends Rule {
    private SubL head;
    private List<SubK> body;
    private Double lowerBound;

    public LambdaRule() {
        body = new ArrayList<SubK>();
        drawn = false;
    }
    public int getBodyLen() { return body.size(); }
    public List<SubK> getBody() { return body; }
    public void addHead(SubL h) { head = h; }
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
        String s = head.toString();
        s += ":-";
        for (SubK sk: body) {
            s += sk.toString();
            s += ",";
        }
        return s;
    }


    public void addBodyEl(SubK e) {
        body.add(e);
        for (Terminal t: e.getTerms())
            if (!t.isBind())
                unbound.add(t);
    }

    protected SubL getHead() { return head; }

    @Override
    public Terminal getNextUnbound() {
        for (Terminal var: unbound)
            if (var.isDummy())
                return var;

        ArrayList<Terminal> winners = new ArrayList<Terminal>();
        int highestScore = 0;

        for (Terminal var: unbound) {
            int newScore = countScoreFor(var);

            if (newScore == highestScore) {
                winners.add(var);
            } else if (newScore > highestScore) {
                highestScore = newScore;
                winners.clear();
                winners.add(var);
            }
        }

        int randomIndex = Global.rg.nextInt(winners.size());
        return winners.get(randomIndex);
    }

    public int countScoreFor(Terminal var) {
        int score = 0;
        for (SubK sk: body)
            score += sk.countScoreFor(var);

        return score;
    }
}
