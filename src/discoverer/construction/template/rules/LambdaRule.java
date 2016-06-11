package discoverer.construction.template.rules;

import discoverer.construction.Variable;
import discoverer.construction.template.rules.SubL;
import discoverer.construction.template.rules.SubK;
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
        body = new ArrayList<>();
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
        for (Variable t : h.getTermsList()) {
            if (!t.isBind()) {
                unbound.add(t);
                allVars.add(t);
            }
        }
    }

    public void addBodyConjunct(SubK e) {
        body.add(e);
        for (Variable t : e.getTerms()) {
            if (!t.isBind()) {
                unbound.add(t);
                allVars.add(t);
            }
        }
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

    public SubL getHead() {
        return head;
    }

    @Override
    /**
     * choose next variable from the yet unbound variables of this rule, choose
     * the one with a highest score/priority = the one that appears in body
     * literals with the most of other variables within then already
     * grounded(bind), choose randomly at draws
     */
    public Variable getNextUnbound() {
        for (Variable var : unbound) {
            if (var.isDummy()) {
                return var;
            }
        }

        ArrayList<Variable> winners = new ArrayList<>(unbound.size());
        int highestScore = Integer.MIN_VALUE;   //choose the variable with the least number of remaining problems (highest negative value)
        for (Variable var : unbound) {
            int newScore = countScoreFor(var);

            if (newScore == highestScore) {
                winners.add(var);
            } else if (newScore > highestScore) {
                highestScore = newScore;
                winners.clear();
                winners.add(var);
            }
        }
        //if a draw, take a randomized choice of variable
        int randomIndex = Global.getRandomInt(winners.size());
        return winners.get(randomIndex);
    }

    /**
     * score = how many grounded(bind) terms do the body literals contain taht
     * have this variable present within them?
     *
     * @param var
     * @return
     */
    public int countScoreFor(Variable var) {
        int score = 0;
        for (SubK sk : body) {
            //get number of remaining free vars other than var for each SubK
            if (Global.relativeVariableSelection) {
                score += sk.countScoreFor2(var);
            }
        }

        return score;  //return number of (remaining) problems for this var as a negative value!
    }

    @Override
    public Rule getUnbindClone() {
        LambdaRule clone = new LambdaRule();

        //clone and unbind the head
        SubL sl = new SubL(this.head.getParent(), true);
        for (Variable t : this.head.getTerms()) {
            Variable tt = null;
            if (!clone.unbound.contains(t)) {
                tt = new Variable(t.name);
                clone.unbound.add(tt);
            } else {
                for (Variable var : clone.unbound) {
                    if (t.equals(var)) {
                        tt = var;
                        break;
                    }
                }
            }
            sl.addVariable(tt);
        }
        clone.addHead(sl);

        //clone and unbind the body
        for (SubK sk : this.body) {
            SubK sk2 = new SubK(sk.getParent(), true);
            for (Variable t : sk.getTerms()) {
                Variable tt = null;
                if (!clone.unbound.contains(t)) {
                    tt = new Variable(t.name);
                    clone.unbound.add(tt);
                } else {
                    for (Variable var : clone.unbound) {
                        if (t.equals(var)) {
                            tt = var;
                            break;
                        }
                    }
                }
                sk2.addVariable(tt);
            }
            clone.body.add(sk2);
        }

        clone.originalName = this.originalName;
        return clone;
    }
}
