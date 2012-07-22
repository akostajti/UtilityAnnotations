package annoj.sample;

import annoj.transformers.ClassTransformer;
import annoj.transformers.MethodEntryLogCreator;
import annoj.transformers.ToStringCreator;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Akos Tajti
 */
public class Sample {
    public static void main(String[] args) throws Exception {
        ToStringCreator creator = new ToStringCreator();
        MethodEntryLogCreator logCreator = new MethodEntryLogCreator();
        List<ClassTransformer> trans = new ArrayList<ClassTransformer>(2);
        trans.add(creator);
        trans.add(logCreator);
        
//        creator.doModification("annoj.test.TestClass");
        ClassTransformer.initClassTransformers(trans, "/home/tajti/assistclasses");
        //creator.initBeforeApplicationStart("/home/tajti/assistclasses");
        TestClass tc = new TestClass(10, "www", 1.1);
        System.out.println(tc);
        System.out.println(new TestClass2());
        tc.x("almafa");
    }
}
