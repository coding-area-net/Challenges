package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.challenges.plugin.challenges.type.abstraction.KillEntityGoal;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;

import javax.annotation.Nonnull;

public class KillWitherGoal extends KillEntityGoal {

  public KillWitherGoal() {
    super(EntityType.WITHER);
    setCategory(SettingCategory.KILL_ENTITY);
  }

  @Nonnull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.NETHER_STAR, Message.forName("item-wither-goal"));
  }

  @Nonnull
  @Override
  public SoundSample getStartSound() {
    return new SoundSample().addSound(Sound.ENTITY_WITHER_SPAWN, 1);
  }

}
