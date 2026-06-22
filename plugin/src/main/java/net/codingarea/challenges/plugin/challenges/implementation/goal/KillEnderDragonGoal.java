package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.codingarea.challenges.plugin.challenges.type.abstraction.KillEntityGoal;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class KillEnderDragonGoal extends KillEntityGoal {

  public KillEnderDragonGoal() {
    super(SettingCategory.KILL_ENTITY, EntityType.ENDER_DRAGON, Environment.THE_END, true, new ItemStack(Material.DRAGON_EGG), "goal-ender-dragon");
    setOneWinner(false);
  }

  @Nullable
  @Override
  public SoundSample getWinSound() {
    return null;
  }

}
