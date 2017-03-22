package constructs.example;

import constructs.template.Atom;
import learning.Query;
import networks.evaluation.values.Value;

/**
 * Created by Gusta on 04.10.2016.
 */
public class AtomQuery extends Query {
    GroundExample example;

    Atom atom;

    @Override
    public Value evaluate() {

        //turn this query into a neural network - expensive!!
        return null;
    }
}