/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package annoj.transformers;

import annoj.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

/**
 * This class is used for modifiyng class bytecode. It can add methods to classes.
 * Which methods are added is determined by annotations. The <code>initBeforeApplicationStart</code>
 * method is used to instrument the bytecode of the classes and loading them. The modified classes
 * are loaded by the context classloader so the method may not work on application servers
 * and in enviroments with many classloaders.
 * <br/>
 * The classes and packages to modify are listed in a config file. The file name is given to 
 * <code>initBeforeApplicationStart</code> as a parameter.
 * <br/>
 * If the config file contains the name of a package, then the <code>ClassTransformer</code>
 * instance is able to modify all classes in the package.
 * 
 * @author Tajti Ákos
 */
public abstract class ClassTransformer {

    protected ClassTransformer nextClassTransformer;
    protected ClassTransformer previousClassTransformer;

    protected void createMethod(CtClass ct, String methodNameNadSignature, String methodBody) throws CannotCompileException {
        CtMethod newMethod = CtNewMethod.make("public String toString()" + methodBody, ct);
        ct.addMethod(newMethod);
    }

    /**
     * Creates a method is class called <code>className</code>. 
     */
    protected abstract void doModification(String className) throws Exception;

    /**
     * Checks if an array of objects contains an instance of <code>annotationClass</code>ú
     * and returns <code>true</code> if yes.
     * 
     * @param anns
     * @param annotationClass
     * @return
     */
    protected boolean containsAnnotation(Object[] anns, Class annotationClass) {
        for (Object o : anns) {
            if (annotationClass.isInstance(o)) {
                return true;
            }
        }

        return false;
    }

    /**
     * The method accepts a file name as a parameter. The file is a imple text file
     * in wich there are package and class names listed one per every line. the packages
     * and classes must be available for the context classloader.
     * <br/>
     * The method modifies the classes listed in the file and the classes in the packages
     * listed in the file if the are annotated with any of the annotations in package
     * <code>annoj.annotation</code>.
     * 
     * @param fileName
     * @throws java.io.FileNotFoundException
     * @throws java.lang.Exception
     */
    protected void initBeforeApplicationStart(String fileName) throws FileNotFoundException, Exception {
        if (fileName == null) {
            return;
        }
        List<String> classList = new ArrayList<String>();
        for (Scanner scanner = new Scanner(new File(fileName)); scanner.hasNextLine();) {
            String line = scanner.nextLine();
            List<String> cl = DirectoryTraverser.getClassNames(line);
            if (cl.size() != 0) {
                classList.addAll(cl);
            } else {
                classList.add(scanner.nextLine());
            }
        }

        doModificationForClasses(classList);
    }

    /**
     * Commits class definition changes anfd loads the class. If the <code>nextClassTransformer</code>
     * is available, then it is called before commit. If the <code>previousClassTransformer</code>
     * is available, then class loading is left to it.
     * 
     * @param ct
     * @throws java.lang.Exception
     */
    protected void commitClassChanges(CtClass ct) throws Exception {
        if (nextClassTransformer != null) {
            nextClassTransformer.doModification(ct.getName());
        }

        //TODO: ezt a kommentet felülvizsgálni
        if (/*doModification(ct) && */previousClassTransformer == null) {
            ct.toClass();
        }
    }

    /**
     * Modifies a list of classes. The classnames are provided in fully qualified form.
     * 
     * @param classList
     * @throws java.lang.Exception
     */
    protected void doModificationForClasses(List<String> classList) throws Exception {
        if (classList != null) {
            for (String className : classList) {
                doModification(className);
            }
        }
    }

    /**
     * Sets the next <code>ClassTransformer</code> in the chain. This class transformer
     * will be invoked before commiting class changes.
     * 
     * @param ct
     */
    protected void setNextClassTransformer(ClassTransformer ct) {
        nextClassTransformer = ct;
    }

    /**
     * Sets the previous class transformer. If this field is not null then the changes
     * of the class will be commited in previous class loader.
     * 
     * @param ct
     */
    protected void setPreviousClassTransformer(ClassTransformer ct) {
        previousClassTransformer = ct;
    }

    /**
     * Gets a list of class transformers and let them modify the classes in order.
     * The classes and packages to modify are listed in the config file.<br/>
     * A general invocation of the method should look like this:<br/>
     * <pre>
     *      ClassTransformer toStringC = new ToStringCreator();
     *      ClassTransformer methodEntryC = MethodEntryLogCreator();
     *      List<ClassTransformer> list = new List<ClassTransformer>(2);
     *      ClassTransformer.initClassTransformers(list, configFileName);
     * </pre>
     * <br/>
     * In the above case <code>toStringC</code> does its modifications then lets
     * <code>methodEntryC</code> to do its own work. After <code>methodEntryC</code> had finished
     * <toStringC</code> invokes <code>commitClassChanges</code> to commit the modifications
     * of the class definition and to load the class.
     * 
     * @param trans
     * @param configFileName
     * @throws java.lang.Exception
     */
    public static void initClassTransformers(List<ClassTransformer> trans, String configFileName) throws Exception {
        if (trans != null && trans.size() >= 2) {
            for (int i = 0, size = trans.size(); i < size - 1; i++) {
                ClassTransformer t1 = trans.get(i);
                ClassTransformer t2 = trans.get(i + 1);
                t1.setNextClassTransformer(t2);
                t2.setPreviousClassTransformer(t1);
            }
        }

        trans.get(0).initBeforeApplicationStart(configFileName);
    }

    /**
     * Checks is a package is annotate with an annotation type of <code>annotationType</code>.
     * Packages can be annotated by annotating class <code>PackageInfo</code> in the package.
     * If this class doesnt exist or exists but isnt annotated the package is not annotated.
     * 
     * @param packageName
     * @param annotationType
     * @return
     */
    protected boolean isPackageAnnotatedWith(String packageName, Class annotationType) {
        try {
            Class cl = Class.forName(packageName + ".PackageInfo");
            if (cl.getAnnotation(annotationType) != null) {
                return true;
            }
        } catch (ClassNotFoundException clnex) {
            return false;
        }

        return false;
    }
}
