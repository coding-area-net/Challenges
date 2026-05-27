package net.codingarea.commons.bukkit.utils.menu.positions;

import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.commons.bukkit.utils.menu.MenuClickInfo;
import net.codingarea.commons.bukkit.utils.menu.MenuPosition;
import org.jetbrains.annotations.NotNull;

public class EmptyMenuPosition implements MenuPosition {

  @Override
  public void handleClick(@NotNull MenuClickInfo info) {
    SoundSample.CLICK.play(info.getPlayer());
  }

}
