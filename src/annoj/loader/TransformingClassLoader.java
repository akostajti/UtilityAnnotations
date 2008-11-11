/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package annoj.loader;

import annoj.transformers.ClassTransformer;
import java.util.List;
import java.util.WeakHashMap;
import javassist.ClassPool;
import javassist.CtClass;

/**
 * Custom classloader. It modifies bítecode in runtime then loads the modified class
 * definition. It's important that the classloader should only be used in a so called
 * bootstrap class which loads this classloader and then loads the classes with
 * it.
 *
 * @author Tajti Ákos
 */
public class TransformingClassLoader extends ClassLoader {

    private WeakHashMap<String, Class> classes = new WeakHashMap<String, Class>();
    private ClassLoader parent;

    private List<ClassTransformer> transformers;
    
    /**
     * Sets parent classloader ans checks permissions.
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

//            ToStringCreator creator = new ToStringCreator();
//            creator.doModification(className);
            
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
            //Logger.getLogger(TransformingClassLoader.class.getName()).log(Level.SEVERE, null, ex);
            throw new ClassNotFoundException(className);
        }

    }
}
