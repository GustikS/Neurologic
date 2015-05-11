package discoverer.construction.network;

import discoverer.construction.Element;
import discoverer.global.Global;

/**
 * Kappa or Lambda node
 */
public abstract class KL extends Element {

    public double dropout;

    public KL(String name) {
        super(name);
        if (name.startsWith("final")) {
            dropout = -1;
        } else {
            dropout = Global.dropout;
        }
    }
}
