/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package discoverer.grounding.evaluation.struct;

import discoverer.construction.network.rules.KappaRule;
import discoverer.global.Global;
import discoverer.global.Tuple;
import discoverer.grounding.evaluation.Ball;
import discoverer.grounding.evaluation.Evaluator;
import discoverer.grounding.network.GroundKL;
import discoverer.grounding.network.GroundKappa;
import discoverer.grounding.network.GroundLambda;
import java.util.HashSet;
import java.util.Map;

/**
 *
 * @author Gusta
 */
public class Dropout {

    public static void dropoutMax(Ball b) {
        GroundKL kl = b.getLast();
        if (kl instanceof GroundKappa) {
            drop((GroundKappa) kl);
        } else {
            drop((GroundLambda) kl);
        }
    }

    private static void drop(GroundKappa last) {
        last.dropMe = Global.rg.nextDouble() < last.getGeneral().dropout;
        for (Tuple<GroundLambda, KappaRule> disj : last.getDisjuncts()) {
            drop(disj.x);
        }
    }

    private static void drop(GroundLambda last) {
        last.dropMe = Global.rg.nextDouble() < last.getGeneral().dropout;
        for (GroundKappa conj : last.getConjuncts()) {
            drop(conj);
        }
    }

    public static void dropoutAvg(Ball b) {
        GroundKL kl = b.getLast();
        if (kl instanceof GroundKappa) {
            dropAvg((GroundKappa) kl);
        } else {
            dropAvg((GroundLambda) kl);
        }
    }

    private static void dropAvg(GroundKappa last) {
        last.dropMe = Global.rg.nextDouble() < last.getGeneral().dropout;
        for (Tuple<HashSet<GroundLambda>, KappaRule> disj : last.getDisjunctsAvg()) {
            for (GroundLambda gl : disj.x) {
                dropAvg(gl);
            }
        }
    }

    private static void dropAvg(GroundLambda last) {
        last.dropMe = Global.rg.nextDouble() < last.getGeneral().dropout;
        for (Map.Entry<GroundKappa, Integer> conj : last.getConjunctsAvg().entrySet()) {
            dropAvg(conj.getKey());
        }
    }

}
