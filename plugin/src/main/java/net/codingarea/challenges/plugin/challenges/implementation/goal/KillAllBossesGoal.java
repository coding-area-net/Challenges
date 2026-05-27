package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.codingarea.challenges.plugin.challenges.type.abstraction.KillMobsGoal;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.commons.common.annotations.Since;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Since("2.0")
public class KillAllBossesGoal extends KillMobsGoal {

  public KillAllBossesGoal() {
    super(Arrays.asList(EntityType.ENDER_DRAGON, EntityType.WITHER, EntityType.ELDER_GUARDIAN));
    setCategory(SettingCategory.KILL_ENTITY);
  }

  @Override
  public Message getBossbarMessage() {
    return Message.forName("bossbar-kill-all-bosses");
  }

  @NotNull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.DIAMOND_SWORD, Message.forName("item-all-bosses-goal"));
  }

}
