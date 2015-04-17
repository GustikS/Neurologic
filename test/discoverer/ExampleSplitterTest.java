package discoverer;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

import org.junit.Test;

public class ExampleSplitterTest {
    @Test
    public void test() {
        String[] ex = FileToStringListJava6.convert("/home/asch/study/dp/data/1/examples.txt", 700);

        ExampleFactory eFactory = new ExampleFactory();
        List<Example> examples = new ArrayList<Example>();
        for (int i = 0; i < ex.length; i++) {
            Example e = eFactory.construct(ex[i]);
            examples.add(e);
        }

        ExampleSplitter es = new ExampleSplitter(10, examples);
        List<Example> a = es.getTest();
        List<Example> b = es.getTrain();
        System.out.println(examples.size());
        System.out.println(a.size());
        System.out.println(a.get(1));
        System.out.println(b.size());
        System.out.println(b.get(5));
        
    }
}
