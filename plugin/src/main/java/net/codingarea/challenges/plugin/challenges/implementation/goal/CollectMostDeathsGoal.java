package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.codingarea.challenges.plugin.challenges.type.abstraction.CollectionGoal;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.content.i18n.Prefix;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.commons.common.misc.StringUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CollectMostDeathsGoal extends CollectionGoal {

  public CollectMostDeathsGoal() {
    super(SettingCategory.SCORE_POINTS, new ItemStack(Material.LAVA_BUCKET), "most-deaths-goal", DamageCause.values());
  }

  @EventHandler
  public void onDeath(@NotNull PlayerDeathEvent event) {
    if (!shouldExecuteEffect()) return;

    EntityDamageEvent lastCause = event.getEntity().getLastDamageCause();
    if (lastCause == null) return;

    DamageCause cause = lastCause.getCause();
    if (cause == DamageCause.CUSTOM) return;

    collect(event.getEntity(), cause, () -> {
      MessageKey.of("death-collected").send(event.getEntity(), Prefix.CHALLENGES, StringUtils.getEnumName(cause));
      SoundSample.PLING.play(event.getEntity());
    });
  }

}
