package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.codingarea.challenges.plugin.challenges.type.abstraction.KillMobsGoal;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Since("2.0")
public class KillAllBossesGoal extends KillMobsGoal {

  public KillAllBossesGoal() {
    super(SettingCategory.KILL_ENTITY, Arrays.asList(EntityType.ENDER_DRAGON, EntityType.WITHER, EntityType.ELDER_GUARDIAN),
      new ItemStack(Material.DIAMOND_SWORD), "goal-all-bosses");
  }

  @Override
  public Message getBossbarMessage() {
    return Message.forName("bossbar-kill-all-bosses");
  }

}
