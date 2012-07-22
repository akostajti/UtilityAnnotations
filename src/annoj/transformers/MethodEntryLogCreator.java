package annoj.transformers;

import annoj.annotation.Loggable;
import java.util.Arrays;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.apache.log4j.Logger;

/**
 * Class transformer to create logs on every method invocation. For logging it uses
 * <code>java.util.logging.Logger</code>.
 * <br/>
 * Method invocations are logged on debug level.
 * 
 * @author Akos Tajti
 */
public class MethodEntryLogCreator extends ClassTransformer {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    //TODO: implement pattern handling in logging parameters
    @Override
    protected void doModification(String className) throws Exception {
        logger.debug(className);
        ClassPool pool = ClassPool.getDefault();
        CtClass ct = pool.get(className);
        CtMethod[] methods = ct.getDeclaredMethods();


        Object o = getAnnotation(ct.getAnnotations(), Loggable.class);
        String[] ms = null;

        if (o != null) {
            Loggable l = (Loggable) o;
            ms = l.methods();
        }

        if (ms != null) {
            logger.debug(Arrays.asList(ms));
            logger.debug("empty= " + ms.length);
        }
        logger.debug(containsAnnotation(ct.getAnnotations(), Loggable.class));
        boolean logAll = containsAnnotation(ct.getAnnotations(), Loggable.class) && ms != null && ms.length == 1 && ms[0].equals("");

        logger.debug("logall= " + logAll);

        for (CtMethod method : methods) {
            logger.debug(method.getName());
            logger.debug("contains= " + containsAnnotation(method.getAnnotations(), Loggable.class));
            if (logAll || containsAnnotation(method.getAnnotations(), Loggable.class) || (ms != null && Arrays.asList(ms).contains(method.getName()))) {
                String sd = "{java.util.logging.Logger.getLogger(\"" + ct.getName() + "\").info(\"invoked: " + ct.getName() + "." + method.getName() + "(\" + java.util.Arrays.asList($args) +\")\");}";
                method.insertBefore(sd);
            }
        }

        commitClassChanges(ct);
    }
}
