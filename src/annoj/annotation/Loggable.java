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
 * 
 * @author tajti
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Loggable {

}
