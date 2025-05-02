package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.challenges.plugin.challenges.type.abstraction.KillEntityGoal;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.EntityType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class KillEnderDragonGoal extends KillEntityGoal {

  public KillEnderDragonGoal() {
    super(EntityType.ENDER_DRAGON, Environment.THE_END, true);
    setCategory(SettingCategory.KILL_ENTITY);
    setOneWinner(false);
  }

  @Nullable
  @Override
  public SoundSample getWinSound() {
    return null;
  }

  @Nonnull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.DRAGON_EGG, Message.forName("item-dragon-goal"));
  }

}
