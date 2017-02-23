package extras;

import lrnn.construction.template.rules.KappaRule;
import lrnn.grounding.network.GroundLambda;
import lrnn.grounding.network.GroundKappa;

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
