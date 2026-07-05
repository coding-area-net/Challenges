package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.codingarea.challenges.plugin.challenges.type.abstraction.KillMobsGoal;
import net.codingarea.challenges.plugin.challenges.type.annotation.RequireVersion;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.content.legacy.Message;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.commons.bukkit.utils.misc.MinecraftVersion;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Since("2.2.0")
@RequireVersion(MinecraftVersion.V1_19)
public class KillAllBossesNewGoal extends KillMobsGoal {

  public KillAllBossesNewGoal() {
    super(SettingCategory.KILL_ENTITY, Arrays.asList(EntityType.ENDER_DRAGON, EntityType.WITHER, EntityType.ELDER_GUARDIAN, EntityType.WARDEN),
      new ItemStack(Material.NETHERITE_SWORD), "goal-all-bosses-new");
  }

  @Override
  public Message getBossbarMessage() {
    return Message.forName("bossbar-kill-all-bosses");
  }

}
