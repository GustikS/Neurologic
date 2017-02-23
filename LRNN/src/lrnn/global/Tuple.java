package lrnn.global;

import java.io.Serializable;

public class Tuple<X, Y> implements Serializable{
    public final X x;
    public final Y y;

    public Tuple(X xx, Y yy) {
        x = xx;
        y = yy;
    }
}
