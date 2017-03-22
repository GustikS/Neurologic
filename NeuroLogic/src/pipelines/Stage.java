package pipelines;

import java.util.List;

/**
 * Created by gusta on 17.3.17.
 */
public interface Stage<T> {
    List<T> process(List<T> input);
}
