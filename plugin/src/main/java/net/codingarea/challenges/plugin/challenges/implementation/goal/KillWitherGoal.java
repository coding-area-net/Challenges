package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.codingarea.challenges.plugin.challenges.type.abstraction.KillEntityGoal;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class KillWitherGoal extends KillEntityGoal {

  public KillWitherGoal() {
    super(SettingCategory.KILL_ENTITY, EntityType.WITHER, new ItemStack(Material.NETHER_STAR), "goal-wither");
  }

  @NotNull
  @Override
  public SoundSample getStartSound() {
    return new SoundSample().addSound(Sound.ENTITY_WITHER_SPAWN, 1);
  }

}
