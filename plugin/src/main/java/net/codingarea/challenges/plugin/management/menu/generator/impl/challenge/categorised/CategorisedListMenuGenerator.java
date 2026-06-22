package net.codingarea.challenges.plugin.management.menu.generator.impl.challenge.categorised;

import lombok.Getter;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.management.menu.generator.impl.challenge.ChallengeListMenuGenerator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CategorisedListMenuGenerator extends ChallengeListMenuGenerator {

  @Getter
  protected final SettingCategory category;

  public CategorisedListMenuGenerator(@NotNull MenuType menuType, @NotNull SettingCategory category) {
    this.menuType = menuType;
    this.category = category;
  }

  @NotNull
  @Override
  public LocalizableMessage getMenuName() {
    return createSubMenuTitle(super.getMenuName(), category.getMenuName());
  }

  @Override
  protected void handleNavigateOutOfMenu(@NotNull Player player) {
    Challenges.getInstance().getMenuManager().openMenu(player, menuType, 0);
  }
}
