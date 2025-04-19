package net.codingarea.commons.bukkit.utils.wrapper;

import net.codingarea.commons.common.misc.ReflectionUtils;
import org.bukkit.Material;

import javax.annotation.Nonnull;

/**
 * This class allows you to use materials, whose names are changed at some point, in most versions.
 * For example the red dye was first named {@code ROSE_RED} but then renamed to {@code RED_DYE}.
 * To prevent unwanted {@link NoSuchFieldError NoSuchFieldErrors}, you should use this wrapper instead of a direct call to the material enum {@link Material}.
 */
public final class MaterialWrapper {

	private MaterialWrapper() {}

	public static final Material GREEN_DYE  = getMaterialByNames("CACTUS_GREEN", "GREEN_DYE");
	public static final Material RED_DYE    = getMaterialByNames("ROSE_RED", "RED_DYE");
	public static final Material YELLOW_DYE = getMaterialByNames("DANDELION_YELLOW", "YELLOW_DYE");
	public static final Material SIGN       = getMaterialByNames("SIGN", "OAK_SIGN");

	@Nonnull
	private static Material getMaterialByNames(@Nonnull String... names) {
		return ReflectionUtils.getFirstEnumByNames(Material.class, names);
	}

}
