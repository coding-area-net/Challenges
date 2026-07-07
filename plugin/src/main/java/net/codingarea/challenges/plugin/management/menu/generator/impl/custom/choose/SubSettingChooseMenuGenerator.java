package net.codingarea.challenges.plugin.management.menu.generator.impl.custom.choose;

import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.management.menu.generator.impl.custom.IParentCustomGenerator;
import net.codingarea.challenges.plugin.utils.misc.MapUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;

public class SubSettingChooseMenuGenerator extends CustomChooseItemMenuGenerator {

  private final IParentCustomGenerator parent;
  private final String key;

  public SubSettingChooseMenuGenerator(@NotNull String key, @NotNull IParentCustomGenerator parent,
                                       @NotNull LinkedHashMap<String, ItemStack> items, @NotNull LocalizableMessage title) {
    super(title, items);
    this.key = key;
    this.parent = parent;
  }

  @Override
  public void onItemClick(@NotNull Player player, @NotNull String itemKey) {
    parent.accept(player, null, MapUtils.createStringArrayMap(key, itemKey));
  }

  @Override
  protected void handleNavigateOutOfMenu(@NotNull Player player) {
    parent.decline(player);
  }

}
