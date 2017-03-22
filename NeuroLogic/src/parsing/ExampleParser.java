package parsing;

import constructs.example.GroundExample;

import java.io.Reader;
import java.util.List;

/**
 * Created by gusta on 14.3.17.
 */
public interface ExampleParser {

    boolean isValid(String input);

    List<GroundExample> parseExamples(Reader reader);
}