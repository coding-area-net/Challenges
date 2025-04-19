package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.commons.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.type.abstraction.KillEntityGoal;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;

import javax.annotation.Nonnull;

@Since("2.0")
public class KillElderGuardianGoal extends KillEntityGoal {

  public KillElderGuardianGoal() {
    super(EntityType.ELDER_GUARDIAN);
    setCategory(SettingCategory.KILL_ENTITY);
  }

  @Nonnull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.PRISMARINE_SHARD, Message.forName("item-elder-guardian-goal"));
  }

  @Nonnull
  @Override
  public SoundSample getStartSound() {
    return new SoundSample().addSound(Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1);
  }

}
