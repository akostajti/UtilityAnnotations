package annoj.loader;

import annoj.transformers.ClassTransformer;
import annoj.transformers.MethodEntryLogCreator;
import annoj.transformers.ToStringCreator;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Bootstrap class for loading classes with callMainMethod method. Its argument is an array
 * in which the 0th element is the binary name of the class in which the callMainMethod method to call is
 * declared. The other elements are the arguments to that callMainMethod method.
 * <br/>
 * This class loads a class by the <code>TransformingClassLoader</code> so all nodes
 * of the loaded class hierarchy will be loaded by this loader.<br/>
 * A typical usage:<br/>
 * <pre>
        MainMethodBootstrap.callMainMethod("classloadertest.Main", args);
 * </pre>
 * In the example, instead of calling callMainMethod directly in class <code>Main</code> we
 * call it using <code>MainMethodBootstrap</code> that gives us the opportunity to load
 * all classes by <code>TransformingClassLoader</code>. The class loader then processes
 * the annotations in every classes it loads.
 * @author Akos Tajti
 */
public class MainMethodBootstrap {

    /**
     * Call main method in class <code>className</code> with arguments given in
     * <code>args</code>. The class is loaded by <code>TransformingClassLoader</code>.
     * 
     * @param className
     * @param args
     * @throws java.lang.ClassNotFoundException
     * @throws java.lang.NoSuchMethodException
     * @throws java.lang.IllegalAccessException
     * @throws java.lang.IllegalArgumentException
     * @throws java.lang.reflect.InvocationTargetException
     */
    public static void callMainMethod(String className, String[] args) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        List<ClassTransformer> transformers = new ArrayList<ClassTransformer>(2) {
            {
                add(new ToStringCreator());
                add(new MethodEntryLogCreator());
            }
        };
        
        TransformingClassLoader loader = new TransformingClassLoader(transformers);

        Class<?> cl = loader.loadClass(className);
        Method m = cl.getMethod("main", String[].class);
        m.invoke(null, new Object[]{args});
    }
}
