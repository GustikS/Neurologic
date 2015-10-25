package examples;

import discoverer.*;
import discoverer.construction.NetworkFactory;
import discoverer.construction.network.KL;
import discoverer.construction.ExampleFactory;
import discoverer.construction.example.Example;
import discoverer.construction.network.LiftedNetwork;
import discoverer.drawing.Dotter;
import discoverer.drawing.GroundDotter;
import discoverer.global.Global;
import discoverer.grounding.evaluation.GroundedTemplate;
import discoverer.grounding.Grounder;
import java.util.*;
import static org.junit.Assert.*;
import org.junit.*;

public class People {

    @Test
    public void blueTest() {
        Global.setLambdaActivation(Global.activationSet.sig);
        Global.setKappaActivation(Global.activationSet.sig);
        Global.setWeightInit(Global.weightInitSet.handmade);
        Global.setRg(new Random(1));
        
        String[] rules = {
            "mother(C,M):-parent(C,M),female(M).",
            "father(C,F):-parent(C,F),male(F)."
        };
        
        String example = "1.0 male(bob),female(alice),parent(bob,alice),parent(eve,alice).";
        
        NetworkFactory nf = new NetworkFactory();
        LiftedNetwork last = nf.construct(rules);

        ExampleFactory ef = new ExampleFactory();
        Example e = ef.construct(example);

        GroundedTemplate b = Grounder.solve(last.last, e);

        Dotter.draw(last.last, "people");
        GroundDotter.draw(b, "peopleGround");
        GroundDotter.drawAVG(b, "peopleGroundAvg");
    }

}
