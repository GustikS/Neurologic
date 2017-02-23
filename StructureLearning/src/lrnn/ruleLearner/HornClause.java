/*
 * Copyright (c) 2015 Ondrej Kuzelka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package lrnn.ruleLearner;

import ida.ilp.logic.*;
import ida.utils.Sugar;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by kuzelkao_cardiff on 20/01/17.
 */
public class HornClause {

    private Clause body;

    private Literal head;

    public HornClause(Literal head, Clause body){
        this.head = head;
        this.body = body;
    }

    public HornClause(Clause c){
        for (Literal l : c.literals()){
            if (!l.isNegated()){
                this.head = l;
                break;
            }
        }
        if (this.head == null){
            this.body = LogicUtils.flipSigns(c);
        } else {
            this.body = LogicUtils.flipSigns(new Clause(Sugar.setDifference(c.literals(), this.head)));
        }
    }


    public Clause unify(Literal query){
        if (query.predicate() != this.head.predicate() || query.arity() != this.head.arity()){
            return null;
        }
        Map<Term,Term> subs = new HashMap<Term, Term>();
        for (int i = 0; i < query.arity(); i++){
            Term ht = this.head.get(i);
            Term qt = query.get(i);
            if (ht instanceof Constant && !ht.equals(qt)){
                return null;
            } else if (ht instanceof Variable){
                if (subs.containsKey(ht)){
                    Term substituted = subs.get(ht);
                    if (!substituted.equals(qt)){
                        return null;
                    }
                } else {
                    subs.put(ht, qt);
                }
            }
        }
        return LogicUtils.substitute(this.body, subs);
    }

    public Set<Variable> variables(){
        return Sugar.union(this.body.variables(), variables(this.head));
    }

    private Set<Variable> variables(Literal l){
        Set<Variable> retVal = new HashSet<Variable>();
        if (l != null) {
            for (int i = 0; i < l.arity(); i++) {
                if (l.get(i) instanceof Variable) {
                    retVal.add((Variable) l.get(i));
                }
            }
        }
        return retVal;
    }

    public Literal head(){
        return this.head;
    }

    public Clause body(){
        return this.body;
    }

    public Clause toClause(){
        return new Clause(Sugar.union(LogicUtils.flipSigns(this.body().literals()), this.head() == null ? Sugar.<Literal>list() : Sugar.<Literal>list(this.head)));
    }

    public String toString(){
        if (this.head == null){
            return " <- "+body.toString();
        }
        return head.toString()+" <- "+body.toString();
    }

    public boolean equals(Object o){
        if (o instanceof HornClause){
            HornClause hc = (HornClause)o;
            return hc.body.equals(this.body) && (hc.head == this.head || (hc.head != null && this.head != null && hc.head.equals(this.head)));
        }
        return false;
    }

    public int hashCode(){
        return this.body.hashCode() + (this.head == null ? 0 : this.head.hashCode());
    }

    public static void main(String[] args){
        HornClause c = new HornClause(Clause.parse("a(X,Y),!b(X),!b(X,Y),!c(X,Y,Z)"));
        Literal q = Literal.parseLiteral("a(1,A)");
        System.out.println(c.unify(q));
    }
}
