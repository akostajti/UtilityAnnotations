package annoj.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Place before a class definition if you want a <code>toString</code> method be generated
 * for the class. The method is also generated if the <code>@ToString</code> annotation 
 * is not present but one of the fields are marked with annotation <code>@Include</code>.
 * <br/>
 * The annotation can be used for annotating packages. In this case you must create
 * a class named <code>PackageInfo</code> in the desired package and annotate that class.
 * 
 * @author Akos Tajti
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ToString {

}
