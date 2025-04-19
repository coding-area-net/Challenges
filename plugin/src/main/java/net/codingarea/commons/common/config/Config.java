package net.codingarea.commons.common.config;

import net.codingarea.commons.common.config.exceptions.ConfigReadOnlyException;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * @see Document
 */
public interface Config extends Propertyable {

	/**
	 * Sets the value at the given path.
	 *
	 * Setting a value to {@code null} has the same effect as {@link #remove(String) removing} it.
	 * {@code config.set(path, null)} is equivalent with {@code config.remove(path)}
	 *
	 * @param value The value to change to, {@code null} to remove
	 * @return {@code this} for chaining
	 *
	 * @throws ConfigReadOnlyException
	 *         If this is {@link #isReadonly() readonly}
	 */
	@Nonnull
	Config set(@Nonnull String path, @Nullable Object value);

	/**
	 * @throws ConfigReadOnlyException
	 *         If this is {@link #isReadonly() readonly}
	 */
	@Nonnull
	Config clear();

	/**
	 * Removing a value has the same effect as {@link #set(String, Object) setting} it to {@code null}
	 * {@code config.set(path, null)} is equivalent with {@code config.remove(path)}
	 *
	 * @return {@code this} for chaining
	 *
	 * @throws ConfigReadOnlyException
	 *         If this is {@link #isReadonly() readonly}
	 */
	@Nonnull
	Config remove(@Nonnull String path);

	boolean isReadonly();

	/**
	 * @return A new config which is readonly, or {@code this} if already {@link #isReadonly() readonly}
	 */
	@Nonnull
	@CheckReturnValue
	Config readonly();

	@Nonnull
	@Override
	default <O extends Propertyable> Config apply(@Nonnull Consumer<O> action) {
		return (Config) Propertyable.super.apply(action);
	}

	@Nonnull
	@Override
	default <O extends Propertyable> Config applyIf(boolean expression, @Nonnull Consumer<O> action) {
		return (Config) Propertyable.super.applyIf(expression, action);
	}

	@Nonnull
	default Config increment(@Nonnull String path, double amount) {
		return set(path, getDouble(path) + amount);
	}

	@Nonnull
	default Config decrement(@Nonnull String path, double amount) {
		return set(path, getDouble(path) - amount);
	}

	@Nonnull
	default Config multiply(@Nonnull String path, double factor) {
		return set(path, getDouble(path) * factor);
	}

	@Nonnull
	default Config divide(@Nonnull String path, double divisor) {
		return set(path, getDouble(path) / divisor);
	}

	@Nonnull
	default Config setIfAbsent(@Nonnull String path, @Nonnull Object defaultValue) {
		if (!contains(path))
			set(path, defaultValue);
		return this;
	}

}
