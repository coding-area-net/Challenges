package net.codingarea.commons.common.annotations;

import javax.annotation.Nonnull;
import java.lang.annotation.*;

/**
 * Used to declare alternate names which are used in used or similar libraries.
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
public @interface AlsoKnownAs {

	@Nonnull
	String[] value();

}
