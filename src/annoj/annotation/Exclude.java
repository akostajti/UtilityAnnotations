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
 * Place before a field if you want to leave out that field from the string
 * returned by <code>toString</code>.
 *
 * @author tajti
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Exclude {

}
