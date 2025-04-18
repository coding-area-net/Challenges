package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.anweisen.utilities.bukkit.utils.animation.SoundSample;
import net.anweisen.utilities.common.misc.StringUtils;
import net.codingarea.challenges.plugin.challenges.type.abstraction.CollectionGoal;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;

import javax.annotation.Nonnull;

public class CollectMostDeathsGoal extends CollectionGoal {

  public CollectMostDeathsGoal() {
    super(DamageCause.values());
    setCategory(SettingCategory.SCORE_POINTS);
  }

  @Nonnull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.LAVA_BUCKET, Message.forName("item-most-deaths-goal"));
  }

  @EventHandler
  public void onDeath(@Nonnull PlayerDeathEvent event) {
    if (!shouldExecuteEffect()) return;

    EntityDamageEvent lastCause = event.getEntity().getLastDamageCause();
    if (lastCause == null) return;

    DamageCause cause = lastCause.getCause();
    if (cause == DamageCause.CUSTOM) return;

    collect(event.getEntity(), cause, () -> {
      Message.forName("death-collected").send(event.getEntity(), Prefix.CHALLENGES, StringUtils.getEnumName(cause));
      SoundSample.PLING.play(event.getEntity());
    });
  }

}
