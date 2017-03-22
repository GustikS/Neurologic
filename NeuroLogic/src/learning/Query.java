package learning;

import networks.evaluation.values.Value;

/**
 * Created by Gusta on 04.10.2016.
 */
public abstract class Query {
    double importance;
    public abstract Value evaluate();
}