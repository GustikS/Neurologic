/*
 * Copyright (c) 2015 Ondrej Kuzelka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package lrnn.learning;

import lrnn.construction.template.KL;
import lrnn.construction.template.Kappa;
import lrnn.construction.template.rules.KappaRule;
import lrnn.construction.template.Lambda;
import lrnn.construction.template.MolecularTemplate;
import lrnn.construction.template.rules.SubK;
import lrnn.construction.template.WeightInitializator;

/**
 * Invalidator for edge weights
 */
public class Invalidator {
    public static void invalidate(MolecularTemplate net) {
        KL kl = net.last;   //now after introducing MolecularTemplate-class this recursion could be shrotcuted by going throug arraylist of rules only
        
        if (kl instanceof Kappa)
            invalidate((Kappa) kl);
        else
            invalidate((Lambda) kl);
    }

    private static void invalidate(Kappa k) {
        if (k.isElement())
            return;

        //k.setWeight(WeightInitializator.init());
        k.initOffset();
        for (KappaRule kr: k.getRules())
            invalidate(kr);
    }

    private static void invalidate(KappaRule kr) {
        kr.setWeight(WeightInitializator.getWeight());
        invalidate(kr.getBody().getParent());
    }

    private static void invalidate(Lambda l) {
        for (SubK sk: l.getRule().getBody())
            invalidate(sk.getParent());
    }
}
