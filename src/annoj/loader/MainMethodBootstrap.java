package annoj.loader;

import annoj.transformers.ClassTransformer;
import annoj.transformers.MethodEntryLogCreator;
import annoj.transformers.ToStringCreator;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Bootstrap class for loading classes with main method. Its argument is an array
 * in which the 0th element is the binary name of the class in which the main method to call is
 * declared. The other elements are the arguments to that main method.
 * <br/>
 * This class loads a class by the <code>TransformingClassLoader</code> so all nodes
 * of the loaded class hierarchy will be loaded by this loader.<br/>
 * A typical usage:<br/>
 * <pre>
 *      String[] a = new String[args.length + 1];
        a[0] = "classloadertest.Main";
        System.arraycopy(args, 0, a, 1, args.length);
        MainMethodBootstrap.main(a);
 * </pre>
 * In the example, instead of calling main directly in class <code>Main</code> we
 * call it using <code>MainMethodBootstrap</code> that gives us the opportunity to load
 * all classes by <code>TransformingClassLoader</code>. The class loader then processes
 * the annotations in every classes it loads.
 * @author Tajti Ákos
 */
public class MainMethodBootstrap {
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        List<ClassTransformer> transformers = new ArrayList<ClassTransformer>(2);
        transformers.add(new ToStringCreator());
        transformers.add(new MethodEntryLogCreator());
        TransformingClassLoader loader = new TransformingClassLoader(transformers);
        
        Class cl = loader.loadClass(args[0]);
        Method m = cl.getMethod("main", String[].class);
        String[] toPass = new String[args.length -1];
        System.arraycopy(args, 1, toPass, 0, args.length-1);
        m.invoke(null, new Object[]{toPass});
    }
}
