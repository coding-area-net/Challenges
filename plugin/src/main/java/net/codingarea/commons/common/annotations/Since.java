package net.codingarea.commons.common.annotations;

import javax.annotation.Nonnull;
import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.PACKAGE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Since {

	@Nonnull
	String value();

}
