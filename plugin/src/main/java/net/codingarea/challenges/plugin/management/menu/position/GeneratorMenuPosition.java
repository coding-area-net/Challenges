package net.codingarea.challenges.plugin.management.menu.position;

import lombok.Getter;
import net.codingarea.commons.bukkit.utils.menu.MenuPosition;
import net.codingarea.challenges.plugin.management.menu.generator.MenuGenerator;

@Getter
public abstract class GeneratorMenuPosition implements MenuPosition {

  protected final MenuGenerator generator;
  protected final int page;

  public GeneratorMenuPosition(MenuGenerator generator, int page) {
    this.generator = generator;
    this.page = page;
  }

}
