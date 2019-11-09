/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ida.utils.treeliker.csp;

import ida.utils.Sugar;
import ida.utils.collections.MultiList;

import java.util.*;

/**
 *
 * @author Ondra
 */
public class CSPProblem<T> {
    
    private Map<T,CSPVariable<T>> variablesByNames = new HashMap<T,CSPVariable<T>>();
    
    private LinkedHashSet<CSPVariable<T>> variables;
    
    private LinkedHashSet<TabularUnaryConstraint> unaryConstraints;
    
    private LinkedHashSet<BinaryConstraint> binaryConstraints;
    
    private MultiList<CSPVariable,TabularUnaryConstraint> containedInUnaryConstraints;
    
    private MultiList<CSPVariable,BinaryConstraint> containedInBinaryConstraints;
    
    public CSPProblem(Collection<CSPVariable<T>> variables, Collection<TabularUnaryConstraint> unaryConstraints, Collection<BinaryConstraint> binaryConstraints){
        this.variables = Sugar.linkedHashSetFromCollections(variables);
        this.variablesByNames = new HashMap<T,CSPVariable<T>>();
        for (CSPVariable<T> variable : this.variables){
            this.variablesByNames.put(variable.name(), variable);
        }
        this.unaryConstraints = Sugar.linkedHashSetFromCollections(unaryConstraints);
        this.binaryConstraints = Sugar.linkedHashSetFromCollections(binaryConstraints);
        this.containedInUnaryConstraints = new MultiList<CSPVariable,TabularUnaryConstraint>();
        for (TabularUnaryConstraint constraint : unaryConstraints){
            this.containedInUnaryConstraints.put(constraint.variableA(), constraint);
        }
        this.containedInBinaryConstraints = new MultiList<CSPVariable,BinaryConstraint>();
        for (BinaryConstraint constraint : binaryConstraints){
            containedInBinaryConstraints.put(constraint.variableA(), constraint);
            containedInBinaryConstraints.put(constraint.variableB(), constraint);
        }
    }
    
    public void addConstraint(TabularUnaryConstraint c){
        this.unaryConstraints.add(c);
        this.containedInUnaryConstraints.put(c.variableA(), c);
    }
    
    public void addConstraint(BinaryConstraint c){
        this.binaryConstraints.add(c);
        this.containedInBinaryConstraints.put(c.variableA(), c);
        this.containedInBinaryConstraints.put(c.variableB(), c);
    }
    
    public void removeConstraint(TabularUnaryConstraint c){
        this.unaryConstraints.remove(c);
        this.containedInBinaryConstraints.remove(c.variableA(), c);
    }
    
    public void removeConstraint(BinaryConstraint c){
        this.binaryConstraints.remove(c);
        this.containedInBinaryConstraints.remove(c.variableA(), c);
        this.containedInBinaryConstraints.remove(c.variableB(), c);
    }
    
    public LinkedHashSet<CSPVariable<T>> variables(){
        return this.variables;
    }
    
    public LinkedHashSet<TabularUnaryConstraint> unaryConstraints(){
        return this.unaryConstraints;
    }
    
    public LinkedHashSet<BinaryConstraint> binaryConstraints(){
        return this.binaryConstraints;
    }
    
    public CSPVariable<T> getCSPVariable(T name){
        return this.variablesByNames.get(name);
    }
    
    public void removeCSPVariable(T name){
        CSPVariable var = this.variablesByNames.remove(name);
        if (var != null){
            this.variables.remove(var);
            List<TabularUnaryConstraint> ciuc = this.containedInUnaryConstraints.get(var);
            this.containedInUnaryConstraints.remove(var);
            this.unaryConstraints.removeAll(ciuc);
            List<BinaryConstraint> cibc = this.containedInBinaryConstraints.get(var);
            this.containedInBinaryConstraints.remove(var);
            for (BinaryConstraint bc : cibc){
                if (bc.variableA() != var){
                    containedInBinaryConstraints.remove(bc.variableA(), bc);
                } else if (bc.variableB() != var){
                    containedInBinaryConstraints.remove(bc.variableB(), bc);
                }
            }
            this.binaryConstraints.removeAll(cibc);
        }
    }
    
    public List<BinaryConstraint> getBinaryConstraints(CSPVariable variable){
        return this.containedInBinaryConstraints.get(variable);
    }
    
    public Set<BinaryConstraint> getBinaryConstraints(Collection<CSPVariable> variables){
        LinkedHashSet<BinaryConstraint> set = new LinkedHashSet<BinaryConstraint>();
        for (CSPVariable var : variables){
            set.addAll(getBinaryConstraints(var));
        }
        return set;
    }
    
    /*public CSPProblem<T> createSubproblem(CSPVariable<T> center, int radius){
        Set<CSPVariable<T>> selected = new HashSet<CSPVariable<T>>();
        int counter = radius;
        Set<CSPVariable<T>> newLevel = new HashSet<CSPVariable<T>>();
        Set<CSPVariable<T>> oldLevel = new HashSet<CSPVariable<T>>();
        selected.add(center);
        oldLevel.add(center);
        while (counter-- > 0) {
            for (CSPVariable<T> var : oldLevel){
                for (BinaryConstraint bc : containedInBinaryConstraints.get(var)){
                    newLevel.add(bc.variableA());
                    newLevel.add(bc.variableB());
                }
            }
            oldLevel = Sugar.setDifference(newLevel, selected);
            selected.addAll(newLevel);
            newLevel = new HashSet<CSPVariable<T>>();
        }
        Set<UnaryConstraint> newUnaryConstraints = new HashSet<UnaryConstraint>();
        Set<BinaryConstraint> newBinaryConstraints = new HashSet<BinaryConstraint>();
        for (CSPVariable<T> var : selected){
            newUnaryConstraints.addAll(this.containedInUnaryConstraints.get(var));
            for (BinaryConstraint bc : this.containedInBinaryConstraints.get(var)){
                if (selected.contains(bc.variableA()) && selected.contains(bc.variableB())){
                    newBinaryConstraints.add(bc);
                }
            }
        }
        return new CSPProblem<T>(selected, newUnaryConstraints, newBinaryConstraints);
    }*/
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Variables: ").append(variables).append("\n");
        sb.append("UnaryConstraints: ").append(unaryConstraints).append("\n");
        sb.append("BinaryConstraints: ").append(binaryConstraints).append("\n");
        return sb.toString();
    }
    
    public static void clearCache(){
        EqualityConstraint.clearCache();
        TabularBinaryConstraint.clearCache();
        TabularUnaryConstraint.clearCache();
    }
}
