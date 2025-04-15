package net.codingarea.challenges.plugin.utils.bukkit.jumpgeneration;

import net.anweisen.utilities.common.collection.IRandom;
import org.bukkit.block.Block;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

/**
 * @author anweisen | <a href="https://github.com/anweisen">...</a>
 * @since 2.0
 */
@FunctionalInterface
public interface IJumpGenerator {

	@Nonnull
	@CheckReturnValue
	Block next(@Nonnull IRandom random, @Nonnull Block startingPoint, boolean includeFourBlockJumps, boolean includeUpGoing);

}
