package parsing;

import learning.Query;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * Created by gusta on 14.3.17.
 */
public class PlainTextQueryParser implements QueryParser{
    @Override
    public boolean isValid(String input) {
        return false;
    }

    @Override
    public List<Query> parseQueries(Reader reader) throws IOException {
        return null;
    }
}