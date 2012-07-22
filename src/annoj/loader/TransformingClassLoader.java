package annoj.loader;

import annoj.transformers.ClassTransformer;
import java.util.List;
import java.util.WeakHashMap;
import javassist.ClassPool;
import javassist.CtClass;
import org.apache.log4j.Logger;

/**
 * Custom classloader. It modifies bytecode in runtime then loads the modified class
 * definition. It's important that the classloader should only be used in a so called
 * bootstrap class which loads this classloader and then loads the classes with
 * it.<br/>
 * In the bootstrap class one can instantiate this class loader and pass a list
 * of <code>ClassTransformer</code> instances to it. When a class is loaded by the
 * loader the passed transformers will try to modify its bytecode before loading it.
 * 
 * @author Akos Tajti
 */
public class TransformingClassLoader extends ClassLoader {

    private WeakHashMap<String, Class> classes = new WeakHashMap<String, Class>();
    private ClassLoader parent;

    private List<ClassTransformer> transformers;
    
    /**
     * Sets the parent class loader and checks the permissions. If there's no 
     * permission to create a class loader then the constructo will fail.
     * 
     * @param transformers <code>ClassTransformer</code> instances to be used for
     * modifying loaded classes.
     */
    public TransformingClassLoader(List<ClassTransformer> transformers) {
        super(TransformingClassLoader.class.getClassLoader());
        SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkCreateClassLoader();
	}
        parent = TransformingClassLoader.class.getClassLoader();
        this.transformers = transformers;
    }

    @Override
    public Class loadClass(String className, boolean resolve) throws ClassNotFoundException {
        Class c = classes.get(className);
        if (!checkClassName(className)) {
            c = parent.loadClass(className);
        }

        if (c == null) {
            c = findClass(className);
            if (c == null) {
                c = super.loadClass(className, resolve);
                if (c == null) {
                    throw new ClassNotFoundException(className);
                }
            }

            if (resolve) {
                resolveClass(c);
            }
        }
 
        return c;
    }

    private boolean checkClassName(String className) {
        if (className.startsWith("java.") || className.startsWith("javax.")) {
            return false;
        }

        return true;
    }

    @Override
    public Class findClass(String className) throws ClassNotFoundException {

        try {
            ClassPool.doPruning = false;

            ClassTransformer.initClassTransformersForClass(transformers, className);

            CtClass ct = ClassPool.getDefault().get(className);
            byte[] code = ct.toBytecode();

            Class cl = defineClass(className, code, 0, code.length);

            if (cl == null) {
                throw new ClassNotFoundException();
            }

            classes.put(className, cl);

            ClassPool.doPruning = true;
            return cl;
        } catch (Exception ex) {
            Logger.getLogger(TransformingClassLoader.class.getName()).error(ex);
            throw new ClassNotFoundException(className);
        }

    }
}
