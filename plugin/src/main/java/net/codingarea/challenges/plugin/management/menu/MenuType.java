package net.codingarea.challenges.plugin.management.menu;

import lombok.Getter;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.management.menu.generator.AbstractMenuGenerator;
import net.codingarea.challenges.plugin.management.menu.generator.IMenuGenerator;
import net.codingarea.challenges.plugin.management.menu.generator.impl.TimerMenuGenerator;
import net.codingarea.challenges.plugin.management.menu.generator.impl.challenge.ChallengeListMenuGenerator;
import net.codingarea.challenges.plugin.management.menu.generator.impl.challenge.categorised.CategorisedMenuGenerator;
import net.codingarea.challenges.plugin.management.menu.generator.impl.custom.CustomHomeMenuGenerator;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;


public enum MenuType {

  TIMER("timer", Material.CLOCK, new TimerMenuGenerator(), false),
  GOAL("goal", Material.COMPASS, new CategorisedMenuGenerator()),
  DAMAGE("damage", Material.IRON_SWORD, new ChallengeListMenuGenerator()),
  ITEMS("items-blocks", Material.STICK, new ChallengeListMenuGenerator()),
  CHALLENGES("challenges", Material.BOOK, new CategorisedMenuGenerator()),
  SETTINGS("settings", Material.COMPARATOR, new ChallengeListMenuGenerator()),
  CUSTOM("custom", Material.WRITABLE_BOOK, new CustomHomeMenuGenerator());

  private final String key;
  @Getter
  private final Material displayItemMaterial;
  @Getter
  private final IMenuGenerator menuGenerator;
  @Getter
  private final boolean usable;

  MenuType(@NotNull String key, @NotNull Material displayItemMaterial, AbstractMenuGenerator menuGenerator, boolean usable) {
    this.key = key;
    this.displayItemMaterial = displayItemMaterial;
    this.menuGenerator = menuGenerator;
    this.usable = usable;

    menuGenerator.setMenuType(this);
  }

  MenuType(@NotNull String key, @NotNull Material displayItemMaterial, AbstractMenuGenerator menuGenerator) {
    this(key, displayItemMaterial, menuGenerator, true);
  }

  /**
   * Colored, translation key used in item names.
   */
  @NotNull
  public MessageKey getDisplayName() {
    return MessageKey.of("menu." + key + ".display"); // TODO =name
  }

  /**
   * Uncolored, translation key used in inventory titles.
   */
  @NotNull
  public MessageKey getMenuName() {
    return MessageKey.of("menu." + key + ".name"); // TODO =menu
  }

  @SuppressWarnings("unchecked")
  public <T extends IMenuGenerator> void executeWithGenerator(@NotNull Class<T> clazz, @NotNull Consumer<T> action) {
    if (clazz.isAssignableFrom(menuGenerator.getClass())) {
      action.accept((T) menuGenerator);
    }
  }

}
