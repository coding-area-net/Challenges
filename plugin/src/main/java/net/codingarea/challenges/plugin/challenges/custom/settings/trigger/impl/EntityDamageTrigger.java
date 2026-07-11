package net.codingarea.challenges.plugin.challenges.custom.settings.trigger.impl;

import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SelectableKey;
import net.codingarea.challenges.plugin.challenges.custom.settings.trigger.ChallengeTrigger;
import net.codingarea.challenges.plugin.challenges.type.helper.SubSettingsHelper;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.commons.bukkit.utils.item.StandardItemBuilder;
import net.codingarea.commons.common.misc.StringUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class EntityDamageTrigger extends ChallengeTrigger {

  public EntityDamageTrigger(String name) {
    super(name, SubSettingsHelper.createEntityTypeSettingsBuilder(true, true).createChooseMultipleChild("damage_cause").fill(builder -> {

      List<PotionEffectType> types = new ArrayList<>(
        Arrays.asList(PotionEffectType.values()));
      Collections.shuffle(types, new Random(1));

      builder.addSetting(SelectableKey.ofName(SubSettingsHelper.ANY, Material.NETHER_STAR, "item-custom-trigger-damange-any"));

      DamageCause[] values = DamageCause.values();
      for (int i = 0; i < values.length; i++) {
        DamageCause cause = values[i];
        PotionEffectType effectType = types.get(i);

        ItemStack displayItemPreset = new StandardItemBuilder.PotionBuilder(Material.TIPPED_ARROW).color(effectType.getColor()).build();
        // TODO correct formatting
        builder.addSetting(SelectableKey.of(cause.name(), displayItemPreset, LocalizableMessage.wrap(StringUtils.getEnumName(cause))));
      }

    }));
  }

  @Override
  public Material getMaterial() {
    return Material.FLINT_AND_STEEL;
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onDeath(@NotNull EntityDamageEvent event) {
    createData()
      .entity(event.getEntity())
      .event(event)
      .entityType(event.getEntityType())
      .data("damage_cause", SubSettingsHelper.ANY, event.getCause().name())
      .execute();
  }

}
