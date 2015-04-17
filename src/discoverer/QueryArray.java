package discoverer;

import java.util.ArrayList;
import java.util.List;

/**
 * Object for query
 */
public class QueryArray {
    private List<Integer> body;

    public QueryArray() {
        body = new ArrayList<Integer>();
    }

    public int size() {
        return body.size();
    }

    public int get(int index) {
        return body.get(index);
    }

    public void set(int index, int value) {
        body.set(index, value);
    }

    public void add(int value) {
        body.add(value);
    }

    public void addAll(List<Integer> li) {
        body.addAll(li);
    }

    @Override
    public int hashCode() {
        return body.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;

        if (o instanceof IntArray) {
            IntArray otherArray = (IntArray) o;

            for (int i = 0; i < body.size(); i++)
                if (body.get(i) != otherArray.get(i)) return false;

            return true;
        }

        if (o instanceof QueryArray) {
            QueryArray qa = (QueryArray) o;

            for (int i = 0; i < body.size(); i++)
                if (body.get(i) != qa.get(i)) return false;

            return true;
        }

        System.out.println("Unexpected type!");
        return false;
    }
}
