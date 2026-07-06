package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.codingarea.challenges.plugin.challenges.type.abstraction.KillEntityGoal;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.utils.misc.MinecraftNameWrapper;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Since("2.0")
public class KillSnowGolemGoal extends KillEntityGoal {

  public KillSnowGolemGoal() {
    super(SettingCategory.KILL_ENTITY, MinecraftNameWrapper.SNOW_GOLEM, new ItemStack(Material.SNOWBALL), "snow-golem");
    this.killerNeeded = true;
  }

  @NotNull
  @Override
  public SoundSample getStartSound() {
    return new SoundSample().addSound(Sound.ENTITY_SNOW_GOLEM_DEATH, 1);
  }

}
