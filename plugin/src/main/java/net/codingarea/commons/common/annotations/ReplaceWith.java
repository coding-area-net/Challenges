package net.codingarea.commons.common.annotations;

import javax.annotation.Nonnull;
import java.lang.annotation.*;

/**
 * @see Deprecated
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.PACKAGE, ElementType.PARAMETER, ElementType.TYPE})
public @interface ReplaceWith {

	@Nonnull
	String value();

}
