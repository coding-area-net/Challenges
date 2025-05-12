package net.codingarea.commons.bukkit.utils.misc;

import com.google.common.base.Preconditions;
import net.codingarea.commons.common.logging.ILogger;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * This class gives access to
 * - api functions which are not directly implemented in some versions of bukkit or spigot
 * - some basic nms functionality.
 */
public final class BukkitReflectionUtils {

	private static final ILogger logger = ILogger.forThisClass();

	private BukkitReflectionUtils() {
	}

	public static double getAbsorptionAmount(@Nonnull Player player) {
		Class<?> classOfPlayer = player.getClass();

		try {
			return player.getAbsorptionAmount();
		} catch (Throwable ignored) {
		}

		try {
			Method getHandleMethod = classOfPlayer.getMethod("getHandle");
			getHandleMethod.setAccessible(true);

			Object handle = getHandleMethod.invoke(player);
			Class<?> classOfHandle = handle.getClass();

			Method getAbsorptionMethod = classOfHandle.getMethod("getAbsorptionHearts");
			getAbsorptionMethod.setAccessible(true);
			return (double) (float) getAbsorptionMethod.invoke(handle);
		} catch (Throwable ignored) {
		}

		logger.warn("Could not get absorption amount for player of class {}", classOfPlayer.getName());
		return 0;
	}

	public static boolean isAir(@Nonnull Material material) {
		try {
			return material.isAir();
		} catch (Throwable ignored) {
		}

		switch (material.name()) {
			case "AIR":
			case "VOID_AIR":
			case "CAVE_AIR":
			case "LEGACY_AIR":
				return true;
			default:
				return false;
		}
	}

	public static int getMinHeight(@Nonnull World world) {
		try {
			return world.getMinHeight();
		} catch (Throwable ignored) {
		}

		return 0;
	}

	/**
	 * @return if the entity is in water, {@code false} otherwise or if not implemented
	 * @deprecated not implemented in all forks of bukkit
	 */
	@Deprecated
	public static boolean isInWater(@Nonnull Entity entity) {
		try {
			return entity.isInWater();
		} catch (Throwable ignored) {
		}

		return false;
	}

	private static final Pattern VALID_KEY = Pattern.compile("[a-z0-9/._-]+");

	/**
	 * Get a NamespacedKey from the supplied string.
	 *
	 * The default namespace will be Minecraft's (i.e.
	 * {@link NamespacedKey#minecraft(String)}).
	 *
	 * @param key the key to convert to a NamespacedKey
	 * @return the created NamespacedKey. null if invalid
	 * @see #fromString(String, Plugin)
	 */
	@Nullable
	public static NamespacedKey fromString(@Nonnull String key) {
		return fromString(key, null);
	}

	/**
	 * Does not exists in versions prior to 1.14
	 *
	 * Get a NamespacedKey from the supplied string with a default namespace if
	 * a namespace is not defined. This is a utility method meant to fetch a
	 * NamespacedKey from user input. Please note that casing does matter and
	 * any instance of uppercase characters will be considered invalid. The
	 * input contract is as follows:
	 * <pre>
	 * fromString("foo", plugin) -{@literal >} "plugin:foo"
	 * fromString("foo:bar", plugin) -{@literal >} "foo:bar"
	 * fromString(":foo", null) -{@literal >} "minecraft:foo"
	 * fromString("foo", null) -{@literal >} "minecraft:foo"
	 * fromString("Foo", plugin) -{@literal >} null
	 * fromString(":Foo", plugin) -{@literal >} null
	 * fromString("foo:bar:bazz", plugin) -{@literal >} null
	 * fromString("", plugin) -{@literal >} null
	 * </pre>
	 *
	 * @param string the string to convert to a NamespacedKey
	 * @param defaultNamespace the default namespace to use if none was
	 * supplied. If null, the {@code minecraft} namespace
	 * ({@link NamespacedKey#minecraft(String)}) will be used
	 * @return the created NamespacedKey. null if invalid key
	 * @see #fromString(String)
	 */
	@Nullable
	public static NamespacedKey fromString(@Nonnull String string, @Nullable Plugin defaultNamespace) {
		Preconditions.checkArgument(!string.isEmpty(), "Input string must not be empty or null");

		String[] components = string.split(":", 3);
		if (components.length > 2) {
			return null;
		}

		String key = (components.length == 2) ? components[1] : "";
		if (components.length == 1) {
			String value = components[0];
			if (value.isEmpty() || !VALID_KEY.matcher(value).matches()) {
				return null;
			}

			return (defaultNamespace != null) ? new NamespacedKey(defaultNamespace, value) : NamespacedKey.minecraft(value);
		} else if (components.length == 2 && !VALID_KEY.matcher(key).matches()) {
			return null;
		}

		String namespace = components[0];
		if (namespace.isEmpty()) {
			return (defaultNamespace != null) ? new NamespacedKey(defaultNamespace, key) : NamespacedKey.minecraft(key);
		}

		if (!VALID_KEY.matcher(namespace).matches()) {
			return null;
		}

		return new NamespacedKey(namespace, key);
	}

}
