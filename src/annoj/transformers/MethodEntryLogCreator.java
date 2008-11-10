/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package annoj.transformers;

import annoj.annotation.Loggable;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

/**
 * Class transformer to create logs on every method invocation. For logging it uses
 * log4j so it can be configured via log4j.properties. IMPORTANT: log4j-*.jar and log4j.properties
 * have to be on the classpath.
 * <br/>
 * Every method is logged on info level.
 * 
 * @author Tajti Ákos
 */
public class MethodEntryLogCreator extends ClassTransformer {

    @Override
    protected void doModification(String className) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass ct = pool.get(className);
        CtMethod[] methods = ct.getDeclaredMethods();

        boolean logAll = containsAnnotation(ct.getAnnotations(), Loggable.class);

        for (CtMethod method : methods) {
            if (logAll || containsAnnotation(method.getAnnotations(), Loggable.class)) {
                String sd = "{org.apache.log4j.Logger.getLogger(\"" + ct.getName() + "\").info(\"invoked: " + ct.getName() + "." + method.getName() + "(\" + java.util.Arrays.asList($args) +\")\");}";
                method.insertBefore(sd);
            }
        }

        commitClassChanges(ct);
    }
}
