package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.codingarea.challenges.plugin.challenges.type.abstraction.KillEntityGoal;
import net.codingarea.challenges.plugin.challenges.type.annotation.RequireVersion;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.commons.bukkit.utils.misc.MinecraftVersion;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Since("2.2.0")
@RequireVersion(MinecraftVersion.V1_19)
public class KillWardenGoal extends KillEntityGoal {

  public KillWardenGoal() {
    super(SettingCategory.KILL_ENTITY, EntityType.WARDEN, new ItemStack(Material.ECHO_SHARD), "goal-warden");
  }

  @NotNull
  @Override
  public SoundSample getStartSound() {
    return new SoundSample().addSound(Sound.ENTITY_WARDEN_EMERGE, 0.2f);
  }

}
