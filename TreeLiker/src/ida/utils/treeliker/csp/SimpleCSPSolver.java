/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ida.utils.treeliker.csp;

import ida.utils.Sugar;
import ida.utils.collections.IntegerSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ondra
 */
public class SimpleCSPSolver {
    
    private CSPProblem<?> problem;
    
    private Propagation propagation;
    
    private Map<CSPVariable, Integer> currentSolution;
    
    private List<Map<CSPVariable, Integer>> solutions;
    
    public SimpleCSPSolver(CSPProblem problem){
        this.problem = problem;
        this.propagation = new Propagation(problem);
    }
    
    public boolean solve(){
        return this.solve(1);
    }
    
    public boolean solve(int maxSolutions){
        if (!this.propagation.arcConsistencyPropagation()){
            return false;
        } else {
            this.solutions = new ArrayList<Map<CSPVariable,Integer>>();
            this.currentSolution = new HashMap<CSPVariable,Integer>();
            backtracking(maxSolutions);
            return this.solutions.size() > 0;
        }
    }
    
    public List<Map<CSPVariable, Integer>> getSolutions(){
        return this.solutions;
    }
    
    private boolean backtracking(int maxSolutions){
        HashMap<CSPVariable,IntegerSet> domains = new HashMap<CSPVariable,IntegerSet>();
        CSPVariable selectedVar = null;
        for (CSPVariable var : this.problem.variables()){
            if (!currentSolution.containsKey(var)){
                if (selectedVar == null || selectedVar.domain().size() > var.domain().size()){
                    selectedVar = var;
                }
            }
        }
        if (selectedVar == null){
            this.solutions.add(currentSolution);
            this.currentSolution = Sugar.mapFromMaps(this.currentSolution);
            if (solutions.size() < maxSolutions){
                return false;
            } else {
                return true;
            }
        }
        for (Integer val : selectedVar.domain().values()){
            currentSolution.put(selectedVar, val);
            storeDomains(problem, domains);
            selectedVar.setDomain(IntegerSet.createIntegerSet(val));
            if (propagation.arcConsistencyPropagation(Sugar.list(selectedVar))){
                if (backtracking(maxSolutions)){
                    return true;
                }
            }
            refreshDomains(problem, domains);
        }
        currentSolution.remove(selectedVar);
        return false;
    }
    
    private static void refreshDomains(CSPProblem<?> problem, Map<CSPVariable,IntegerSet> domains){
        for (CSPVariable variable : problem.variables()){
            variable.setDomain(domains.get(variable));
        }
    }
    
    private static void storeDomains(CSPProblem<?> problem, Map<CSPVariable,IntegerSet> domains){
        for (CSPVariable variable : problem.variables()){
            domains.put(variable, variable.domain());
        }
    }
}
