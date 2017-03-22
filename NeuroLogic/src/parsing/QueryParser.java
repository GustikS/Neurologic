package parsing;

import learning.Query;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * Created by gusta on 14.3.17.
 */
public interface QueryParser {

    boolean isValid(String input);

    public abstract List<Query> parseQueries(Reader reader) throws IOException;
}
