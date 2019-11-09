/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ida.utils.treeliker.csp;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 *
 * @author Ondra
 */
public class Propagation<T> {
    
    private CSPProblem<T> problem;
    
    public Propagation(CSPProblem<T> problem){
        this.problem = problem;
    }
    
    public boolean nodeConsistencyPropagation(){
        for (TabularUnaryConstraint constraint : problem.unaryConstraints()){
            constraint.filterDomainOfVariableA();
            if (constraint.variableA().domain().isEmpty()){
                return false;
            }
        }
        for (BinaryConstraint constraint : problem.binaryConstraints()){
            
        }
        return true;
    }
    
    public boolean arcConsistencyPropagation(){
        if (problem == null){
            return false;
        }
        return arcConsistencyPropagation(null);
    }
    
    public boolean arcConsistencyPropagation(Collection<CSPVariable> first){
        if (!nodeConsistencyPropagation()){
            return false;
        }
        SetStack workList = new SetStack();
        SetStack initialWorkList = new SetStack();
        if (first == null){
            workList.pushAll(problem.binaryConstraints());
        } else {
            initialWorkList.pushAll(problem.getBinaryConstraints(first));
        }
        while (!initialWorkList.isEmpty() || !workList.isEmpty()){
            BinaryConstraint constraint = null;
            if (initialWorkList.isEmpty()){
                constraint = workList.pop();
            } else {
                constraint = initialWorkList.pop();
            }
            if (constraint.filterDomainOfVariableA()){
                if (constraint.variableA().domain().isEmpty()){
                    return false;
                } else {
                    for (BinaryConstraint bc : problem.getBinaryConstraints(constraint.variableA())){
                        if (bc.variableA() != constraint.variableB() || bc.variableB() != constraint.variableB()){
                            if (initialWorkList.isEmpty() || !initialWorkList.contains(bc)){
                                workList.push(bc);
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
    
    private static class SetStack {
        
        private Set<BinaryConstraint> set = new HashSet<BinaryConstraint>();
        
        private Stack<BinaryConstraint> orderedSet = new Stack<BinaryConstraint>();
        
        public SetStack(){
            
        }
        
        public void pushAll(Collection<BinaryConstraint> coll){
            for (BinaryConstraint t : coll){
                this.push(t);
            }
        }
        
        public void push(BinaryConstraint t){
            if (!set.contains(t)){
                orderedSet.push(t);
                //orderedSet.add(t.variableA().domain().size(), t);
                set.add(t);
            }
        }
        
        public BinaryConstraint pop(){
            BinaryConstraint t = orderedSet.pop();
            set.remove(t);
            return t;
        }
        
        public boolean isEmpty(){
            return set.isEmpty();
        }
        
        public boolean contains(BinaryConstraint t){
            return set.contains(t);
        }
    }
    
//    private static class SetStack<T> {
//        
//        private long index;
//        
//        private Map<T,Long> map = new HashMap<T,Long>();
//        
//        private TreeMap<Long,T> treeMap = new TreeMap<Long,T>();
//        
//        public SetStack(){
//            
//        }
//        
//        public void pushAll(Collection<T> coll){
//            for (T t : coll){
//                this.push(t);
//            }
//        }
//        
//        public void push(T t){
//            Long key = null;
//            if ((key = map.get(t)) != null){
//                treeMap.remove(key);
//            }
//            treeMap.put(index, t);
//            map.put(t, index);
//            index++;
//        }
//        
//        public T pop(){
//            T t = treeMap.remove(treeMap.lastKey());
//            map.remove(t);
//            return t;
//        }
//        
//        public boolean isEmpty(){
//            return treeMap.isEmpty();
//        }
//    }
}
