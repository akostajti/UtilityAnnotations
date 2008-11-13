/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package annoj.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for marking a mehod or a class loggable. If a method or a class has the
 * annotation then <code>MethodEntryLogCreator</code> will make modifications on it.
 * <br/>
 * Details: <code>MethodEntryLogCreator</code> inserts a loging method call in the
 * first line of every method to log.
 * <br/>
 * The annotation has one parameter: <code>methods</code>. If the paramter is present, it
 * contains the method names that must be logged. If not present, then all methods will be logged.
 * <br/>
 * It <code>@Loggable</code> is used before a method name, then the method will be logged.
 * Example:<br/>
 * <pre>
 *      @Loggable(methods = {"getSomething"})
 *      public class C{
 *          ...
 *      }
 * </pre>
 * In this example the invocation of method <code>getSomething</code> in class <code>C</code>
 * will be logged.<br/>
 * <pre>
*      public class C{
 *          @Loggable
 *          public void getSomething(){
 *          }
 *      }
 * </pre>
 * <br/>
 * In the above case only <code>getSomething</code> will be logged.
 * <br/>
 * Not only public methods can be made loggable - unless the security manager is
 * configured otherwise.
 * @author tajti
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Loggable {
    String[] methods() default "";
}
