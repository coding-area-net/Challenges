package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.commons.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.type.abstraction.KillEntityGoal;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.MinecraftNameWrapper;
import org.bukkit.Material;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

@Since("2.0")
public class KillSnowGolemGoal extends KillEntityGoal {

  public KillSnowGolemGoal() {
    super(MinecraftNameWrapper.SNOW_GOLEM);
    setCategory(SettingCategory.KILL_ENTITY);
    this.killerNeeded = true;
  }

  @Nonnull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.SNOWBALL, Message.forName("item-snow-golem-goal"));
  }

  @Nonnull
  @Override
  public SoundSample getStartSound() {
    return new SoundSample().addSound(Sound.ENTITY_SNOW_GOLEM_DEATH, 1);
  }

}
