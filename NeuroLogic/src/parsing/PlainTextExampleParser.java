package parsing;

import constructs.example.GroundExample;

import java.io.Reader;
import java.util.List;

/**
 * Created by gusta on 14.3.17.
 */
public class PlainTextExampleParser implements ExampleParser {

    @Override
    public boolean isValid(String input) {
        return false;
    }

    @Override
    public List<GroundExample> parseExamples(Reader reader) {
        return null;
    }
}