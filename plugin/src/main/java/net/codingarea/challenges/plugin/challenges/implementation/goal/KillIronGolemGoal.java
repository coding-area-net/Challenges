package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.codingarea.challenges.plugin.challenges.type.abstraction.KillEntityGoal;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Since("2.0")
public class KillIronGolemGoal extends KillEntityGoal {

  public KillIronGolemGoal() {
    super(SettingCategory.KILL_ENTITY, EntityType.IRON_GOLEM, new ItemStack(Material.IRON_INGOT), "iron-golem");
    this.killerNeeded = true;
  }

  @NotNull
  @Override
  public SoundSample getStartSound() {
    return new SoundSample().addSound(Sound.ENTITY_IRON_GOLEM_HURT, 1);
  }

}
