package discoverer.construction.network;

import discoverer.construction.Element;
import discoverer.global.Global;
import java.io.Serializable;

/**
 * Kappa or Lambda node
 */
public abstract class KL extends Element implements Serializable {

    public double dropout;

    public KL() {
    }

    public KL(String name) {
        super(name);
        if (name.startsWith("final")) {
            dropout = -1;
        } else {
            dropout = Global.getDropout();
        }
    }
}
