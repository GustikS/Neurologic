/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ida.ilp.treeLiker.impl;

import ida.ilp.treeLiker.Block;
import ida.ilp.treeLiker.Consts;
import ida.ilp.treeLiker.Domain;
import ida.utils.collections.MultiMap;
import ida.utils.tuples.Pair;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An implementation class for pre-computing MultiMaps containing
 * pairs: [EXAMPLE_ID, TERM] as keys and sets of Blocks which have TERM
 * iterable their term-domain computed w.r.t. the example with id EXAMPLE_ID.
 * 
 * @author Ondra
 */
public class TermsInDomainsOfBuilder implements Runnable {

    private final MultiMap<Pair<Integer,Integer>,Block> termsInDomainsOf;

    private Map<Integer,Domain[]> computedDomains;

    private ConcurrentHashMap<Integer,Block> posFeaturesLookup;

    private int[] mask;

    /**
     * Creates new instance of class TermsInDomainsOfBuilder.
     * @param termsInDomainsOf MultiMap to which the results should bge stored
     * @param computedDomains pre-computed term-domains of the blocks
     * @param blocksLookup Map which maps ids to Blocks
     * @param mask
     */
    public TermsInDomainsOfBuilder(MultiMap<Pair<Integer,Integer>,Block> termsInDomainsOf, Map<Integer,Domain[]> computedDomains,
            ConcurrentHashMap<Integer,Block> blocksLookup, int[] mask){
        this.termsInDomainsOf = termsInDomainsOf;
        this.computedDomains = computedDomains;
        this.posFeaturesLookup = blocksLookup;
        this.mask = mask;
    }

    @Override
    public void run(){
        for (Map.Entry<Integer,Domain[]> entry : computedDomains.entrySet()){
            int exampleIndex = 0;
            for (Domain domain : entry.getValue()){
                if (mask[exampleIndex] == Consts.POSITIVE){
                    synchronized (this.termsInDomainsOf){
                        for (int term : domain.integerSet().values()){
                            termsInDomainsOf.put(new Pair<Integer,Integer>(exampleIndex,term), posFeaturesLookup.get(entry.getKey()));
                        }
                    }
                }
                exampleIndex++;
            }
        }
    }
}