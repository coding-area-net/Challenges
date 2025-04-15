package net.codingarea.challenges.plugin.challenges.custom.settings;

import javax.annotation.Nonnull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author sehrschlechtYT | <a href="https://github.com/sehrschlechtYT">...</a>
 * @since 2.2.3
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FallbackNames {

    @Nonnull
    String[] value();

}
