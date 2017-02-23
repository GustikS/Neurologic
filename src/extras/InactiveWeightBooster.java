package extras;
//
//import lrnn.construction.network.rules.KappaRule;
//import lrnn.construction.NetworkFactory;
//import lrnn.grounding.evaluation.Ball;
//
///**
// * Experimetal!
// * Boost inactive weights in lk-network
// */
//public class InactiveWeightBooster {
//    public static void boost(Ball b) {
//        for (KappaRule kr: NetworkFactory.getKappaRules())
//            if (!b.getActiveRules().contains(kr))
//                kr.increaseWeight(0.5);
//    }
//}
