package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.codingarea.challenges.plugin.challenges.type.abstraction.KillEntityGoal;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.commons.common.annotations.Since;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

@Since("2.0")
public class KillIronGolemGoal extends KillEntityGoal {

  public KillIronGolemGoal() {
    super(EntityType.IRON_GOLEM);
    setCategory(SettingCategory.KILL_ENTITY);
    this.killerNeeded = true;
  }

  @NotNull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.IRON_INGOT, Message.forName("item-iron-golem-goal"));
  }

  @NotNull
  @Override
  public SoundSample getStartSound() {
    return new SoundSample().addSound(Sound.ENTITY_IRON_GOLEM_HURT, 1);
  }

}
