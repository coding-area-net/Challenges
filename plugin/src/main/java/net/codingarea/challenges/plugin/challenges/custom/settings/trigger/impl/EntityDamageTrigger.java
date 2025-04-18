package net.codingarea.challenges.plugin.challenges.custom.settings.trigger.impl;

import net.anweisen.utilities.bukkit.utils.item.ItemBuilder.PotionBuilder;
import net.anweisen.utilities.common.misc.StringUtils;
import net.codingarea.challenges.plugin.challenges.custom.settings.trigger.ChallengeTrigger;
import net.codingarea.challenges.plugin.challenges.type.helper.SubSettingsHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.utils.item.DefaultItem;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.util.*;

public class EntityDamageTrigger extends ChallengeTrigger {

  public EntityDamageTrigger(String name) {
    super(name, SubSettingsHelper.createEntityTypeSettingsBuilder(true, true).createChooseMultipleChild("damage_cause").fill(builder -> {

      List<PotionEffectType> types = new ArrayList<>(
        Arrays.asList(PotionEffectType.values()));
      Collections.shuffle(types, new Random(1));

      builder.addSetting(SubSettingsHelper.ANY, new ItemBuilder(Material.NETHER_STAR, Message.forName("item-custom-trigger-damage-any")));

      DamageCause[] values = DamageCause.values();
      for (int i = 0; i < values.length; i++) {
        DamageCause cause = values[i];
        PotionEffectType effectType = types.get(i);

        builder.addSetting(cause.name(),
          new PotionBuilder(Material.TIPPED_ARROW,
            DefaultItem.getItemPrefix() + StringUtils.getEnumName(cause))
            .color(effectType.getColor())
            .build());

      }

    }));
  }

  @Override
  public Material getMaterial() {
    return Material.FLINT_AND_STEEL;
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onDeath(@Nonnull EntityDamageEvent event) {
    createData()
      .entity(event.getEntity())
      .event(event)
      .entityType(event.getEntityType())
      .data("damage_cause", SubSettingsHelper.ANY, event.getCause().name())
      .execute();
  }

}
