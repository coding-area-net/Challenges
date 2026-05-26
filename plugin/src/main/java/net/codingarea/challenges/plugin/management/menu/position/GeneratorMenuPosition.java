package net.codingarea.challenges.plugin.management.menu.position;

import lombok.Getter;
import net.codingarea.challenges.plugin.management.menu.generator.MenuGenerator;
import net.codingarea.commons.bukkit.utils.menu.MenuPosition;

@Getter
public abstract class GeneratorMenuPosition implements MenuPosition {

  protected final MenuGenerator generator;
  protected final int page;

  public GeneratorMenuPosition(MenuGenerator generator, int page) {
    this.generator = generator;
    this.page = page;
  }

}
