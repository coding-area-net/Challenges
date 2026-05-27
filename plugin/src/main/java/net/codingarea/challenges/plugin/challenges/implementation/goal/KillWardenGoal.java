package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.codingarea.challenges.plugin.challenges.type.abstraction.KillEntityGoal;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.challenges.annotations.RequireVersion;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.commons.bukkit.utils.misc.MinecraftVersion;
import net.codingarea.commons.common.annotations.Since;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

@Since("2.2.0")
@RequireVersion(MinecraftVersion.V1_19)
public class KillWardenGoal extends KillEntityGoal {

  public KillWardenGoal() {
    super(EntityType.WARDEN);
    setCategory(SettingCategory.KILL_ENTITY);
  }

  @NotNull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.ECHO_SHARD, Message.forName("item-warden-goal"));
  }

  @NotNull
  @Override
  public SoundSample getStartSound() {
    return new SoundSample().addSound(Sound.ENTITY_WARDEN_EMERGE, 0.2f);
  }

}
