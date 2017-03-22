package networks.structure;

import java.util.Set;

/**
 * Created by gusta on 8.3.17.
 */
public class NeuralNetwork {
    String id;
    Set<Neuron> neurons;

    boolean isRecursive(){
        return false;
    }
}