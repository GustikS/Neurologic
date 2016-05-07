package extras;

import discoverer.construction.template.rules.KappaRule;
import discoverer.grounding.network.GroundLambda;
import discoverer.grounding.network.GroundKappa;

public class Triple {
    public final GroundKappa gk;
    public final GroundLambda gl;
    public final KappaRule kr;

    public Triple(GroundKappa k, GroundLambda l, KappaRule r) {
        gk = k;
        gl = l;
        kr = r;
    }
}
