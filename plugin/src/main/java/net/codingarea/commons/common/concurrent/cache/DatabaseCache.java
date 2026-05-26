package net.codingarea.commons.common.concurrent.cache;

import net.codingarea.commons.common.annotations.ReplaceWith;

import javax.annotation.Nonnull;

/**
 * @see com.google.common.cache.LoadingCache
 * @deprecated Use {@link com.google.common.cache.LoadingCache} instead
 */
@Deprecated
@ReplaceWith("com.google.common.cache.LoadingCache")
public interface DatabaseCache<K, V> extends ICache<K, V> {

  @Nonnull
  V getData(@Nonnull K key);

}
