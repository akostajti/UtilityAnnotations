package annoj.transformers;

import annoj.annotation.Exclude;
import annoj.annotation.Include;
import annoj.annotation.ToString;
import java.util.ArrayList;
import java.util.List;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import org.apache.log4j.Logger;

/**
 * Subclass of <code>ClassTransformer</code> for creating <code>toString</code>
 * method in classes that don't implement it. The method is created at bytecode level,
 * before class loading. <br/>
 * Method <code>initBeforeApplicationStart</code> modifies the classes and attempts
 * to load them. If the classes had already been loaded, then <code>CannotCompileException</code>
 * is thrown. So the method should be invoked in the first line of the application and shouldnt
 * be invoked in a class annotated with any of the annotations isn package <code>annoj.annotations</code>.
 * <br />
 * A typical use of the class should look like this:
 * <pre>
 *      ToStringCreator c = new ToStringCreator();
 *      c.initBeforeApplicationStart("/home/tajti/packages");
 * </pre>
 * 
 * <br />
 * After this invocation the classes in the packages listed in file /home/tajti/packages 
 * are modified and loaded. Any instantiation of these classes sees the modified definition so
 * the <code>toString</code> method can be invoked without problem on them.
 * @author Akos Tajti
 */
public class ToStringCreator extends ClassTransformer {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Adds method <code>toString</code> to class named <code>className</code>. The 
     * properties of the added method are controled by the annotations in the target class.
     * 
     * @param className Name of the class to modify.
     * @throws java.lang.Exception
     */
    public void doModification(String className) throws Exception {
        logger.debug(className);
        ClassPool pool = ClassPool.getDefault();
        CtClass ct = pool.get(className);

        try {
            ct.getDeclaredMethod("toString", new CtClass[]{});
        } catch (NotFoundException nfex) {
            logger.debug("toStringNotFound");
//            if (createMethod(ct)) {
//                logger.debug("create method true");
//
//            }
            createMethod(ct);
            commitClassChanges(ct);
        }
    }

    private boolean createMethod(CtClass ct) throws ClassNotFoundException, CannotCompileException {
        List<String> fieldsToInclude = new ArrayList<String>();
        boolean classIsAnnotated = containsAnnotation(ct.getAnnotations(), ToString.class);
        boolean packageIsAnnotated = isPackageAnnotatedWith(ct.getPackageName(), ToString.class);
        CtField[] fields = ct.getDeclaredFields();
        Object[] classAnnotations = ct.getAnnotations();

        for (CtField field : fields) {
            Object[] anns = field.getAnnotations();
            if ((packageIsAnnotated && !containsAnnotation(classAnnotations, Exclude.class)) || (classIsAnnotated && !containsAnnotation(anns, Exclude.class)) || containsAnnotation(anns, Include.class)) {
                fieldsToInclude.add(field.getName());
            }
        }

        if (fieldsToInclude.size() == 0) {
            return false;        //TODO nullenőrzés
        }
        String methodBody = createMethodBody(fieldsToInclude);
        if (methodBody != null) {
            createMethod(ct, "public String toString()", methodBody);
        }

        return true;
    }

    /**
     * Creates the body of the method given the list of the fields to use. 
     * 
     * @param fieldsToInclude
     * @return
     */
    protected String createMethodBody(List<String> fieldsToInclude) {
        if (fieldsToInclude != null && fieldsToInclude.size() == 0) {
            return null;
        }
        StringBuilder builder = new StringBuilder("{return \"[");
        boolean isFirst = true;

        for (String fieldName : fieldsToInclude) {
            if (!isFirst) {
                builder.append(",");
            } else {
                isFirst = false;
            }
            builder.append(fieldName).append("= \"+ ").append(fieldName).append("+ \"");
        }

        builder.deleteCharAt(builder.length() - 1);
        builder.append("\"]\";}");

        return builder.toString();
    }
}
