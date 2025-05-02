package net.codingarea.commons.bukkit.utils.menu.positions;

import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.commons.bukkit.utils.menu.MenuClickInfo;
import net.codingarea.commons.bukkit.utils.menu.MenuPosition;

import javax.annotation.Nonnull;

public class EmptyMenuPosition implements MenuPosition {

	@Override
	public void handleClick(@Nonnull MenuClickInfo info) {
		SoundSample.CLICK.play(info.getPlayer());
	}

}
