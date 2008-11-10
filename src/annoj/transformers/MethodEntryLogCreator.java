/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package annoj.transformers;

import annoj.annotation.Loggable;
import java.util.List;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

/**
 * Class transformer to create logs on every method invocation.
 *
 * @author Tajti Ákos
 */
public class MethodEntryLogCreator extends ClassTransformer{

    @Override
    protected void doModification(String className) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass ct = pool.get(className);
        CtMethod[] methods = ct.getDeclaredMethods();

        boolean logAll = containsAnnotation(ct.getAnnotations(), Loggable.class);
        
        for (CtMethod method : methods) {
            if (logAll || containsAnnotation(method.getAnnotations(), Loggable.class)) {
                method.insertBefore("{System.out.println(\"invoked: " + ct.getName() + "." + method.getName() + "(\" + java.util.Arrays.asList($args) +\")\");}");
            }
        }
        
        commitClassChanges(ct);
    }

}
