package constructs.example;

import constructs.template.Atom;
import ida.utils.tuples.Pair;
import learning.Query;
import networks.evaluation.functions.Activation;
import networks.evaluation.values.Value;

import java.util.List;

/**
 * Created by gusta on 13.3.17.
 */
public class CompositeQuery extends Query{
    GroundExample example;

    /**
     * list of query atoms with possible negations
     */
    List<Pair<Atom,Activation>> queryAtoms;

    Activation aggregationFcn;
    Activation activationFcn;

    @Override
    public Value evaluate() {
        //turn this query into a neural network - expensive!!
        return null;
    }
}