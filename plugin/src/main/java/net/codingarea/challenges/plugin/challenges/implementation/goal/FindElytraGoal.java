package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.anweisen.utilities.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.type.abstraction.FindItemGoal;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

@Since("2.1.1")
public class FindElytraGoal extends FindItemGoal {

  public FindElytraGoal() {
    super(Material.ELYTRA);
    setCategory(SettingCategory.FASTEST_TIME);
  }

  @NotNull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.ELYTRA, Message.forName("item-find-elytra-goal"));
  }

}
