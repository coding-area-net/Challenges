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
public class KillElderGuardianGoal extends KillEntityGoal {

  public KillElderGuardianGoal() {
    super(SettingCategory.KILL_ENTITY, EntityType.ELDER_GUARDIAN, new ItemStack(Material.PRISMARINE_SHARD), "goal-elder-guardian");
  }

  @NotNull
  @Override
  public SoundSample getStartSound() {
    return new SoundSample().addSound(Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1);
  }

}
