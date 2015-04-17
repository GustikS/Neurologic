package discoverer;

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
