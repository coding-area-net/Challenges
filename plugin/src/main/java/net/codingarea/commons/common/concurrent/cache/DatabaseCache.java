package net.codingarea.commons.common.concurrent.cache;

import net.codingarea.commons.common.annotations.ReplaceWith;

import javax.annotation.Nonnull;

/**
 * @deprecated Use {@link com.google.common.cache.LoadingCache} instead
 *
 * @see com.google.common.cache.LoadingCache
 */
@Deprecated
@ReplaceWith("com.google.common.cache.LoadingCache")
public interface DatabaseCache<K, V> extends ICache<K, V> {

	@Nonnull
	V getData(@Nonnull K key);

}
