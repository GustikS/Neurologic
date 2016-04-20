/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package extras;

import discoverer.grounding.network.GroundKappa;
import discoverer.grounding.network.GroundLambda;
import java.util.List;
/*
/**
 * this is cut out from crossvalidation for testing of rings that output max values for LRNN paper
 * @author Gusta

public class RingsToxic {
    
            GroundKappa toxic;
            if (b.getLast() instanceof GroundLambda) {
                GroundLambda last = (GroundLambda) b.getLast();
                List<GroundKappa> conjuncts = last.getConjuncts();
                toxic = conjuncts.get(0);
            } else {
                GroundKappa last = (GroundKappa) b.getLast();
                toxic = last;
            }

            GroundLambda ring5 = null;
            GroundLambda ring4 = null;
            GroundLambda ring3 = null;
            for (int i = 0; i < toxic.getDisjuncts().size(); i++) {
                if (toxic.getDisjuncts().get(i).x.getGeneral().getName().contains("ring5")) {
                    ring5 = toxic.getDisjuncts().get(i).x;
                }
                if (toxic.getDisjuncts().get(i).x.getGeneral().getName().contains("ring4")) {
                    ring4 = toxic.getDisjuncts().get(i).x;
                }
                if (toxic.getDisjuncts().get(i).x.getGeneral().getName().contains("ring3")) {
                    ring3 = toxic.getDisjuncts().get(i).x;
                }
            }
            if (ring5 != null) {
                String r5 = "";
                for (GroundKappa gk : ring5.getConjuncts()) {
                    //r5 += example.constantNames.get(term) + "-";
                    r5 += gk.getDisjuncts().get(0).x.getConjuncts().get(0).getGeneral().getName() + "-";
                }
                r5 += ring5.getConjuncts().get(ring5.getConjuncts().size() - 1).getDisjuncts().get(0).x.getConjuncts().get(1).getGeneral().getName();
                atoms.put(r5, ring5.getValueAvg());

                r5 = "";
                for (Integer term : ring5.getTermList()) {
                    r5 += example.getExample().constantNames.get(term) + "-";
                }
                ballvalues.put(r5, ring5.getValueAvg());
            }

            if (ring4 != null) {
                String r4 = "";
                for (GroundKappa gk : ring4.getConjuncts()) {
                    //r5 += example.constantNames.get(term) + "-";
                    r4 += gk.getDisjuncts().get(0).x.getConjuncts().get(0).getGeneral().getName() + "-";
                }
                r4 += ring4.getConjuncts().get(ring4.getConjuncts().size() - 1).getDisjuncts().get(0).x.getConjuncts().get(1).getGeneral().getName();
                atoms.put(r4, ring4.getValueAvg());

                r4 = "";
                for (Integer term : ring4.getTermList()) {
                    r4 += example.getExample().constantNames.get(term) + "-";
                }
                ballvalues.put(r4, ring4.getValueAvg());
            }

            if (ring3 != null) {
                String r3 = "";
                for (GroundKappa gk : ring3.getConjuncts()) {
                    //r5 += example.constantNames.get(term) + "-";
                    r3 += gk.getDisjuncts().get(0).x.getConjuncts().get(0).getGeneral().getName() + "-";
                }
                r3 += ring3.getConjuncts().get(ring3.getConjuncts().size() - 1).getDisjuncts().get(0).x.getConjuncts().get(1).getGeneral().getName();
                atoms.put(r3, ring3.getValueAvg());

                r3 = "";
                for (Integer term : ring3.getTermList()) {
                    r3 += example.getExample().constantNames.get(term) + "-";
                }
                ballvalues.put(r3, ring3.getValueAvg());
            } else {
                //ballvalues.add(-1.0);
            }
    
}
*/