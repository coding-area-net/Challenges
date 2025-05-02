package net.codingarea.challenges.plugin.challenges.custom.settings.action.impl;

import net.codingarea.challenges.plugin.challenges.custom.settings.action.EntityTargetAction;
import net.codingarea.challenges.plugin.challenges.type.helper.SubSettingsHelper;
import net.codingarea.challenges.plugin.utils.item.DefaultItem;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.Map;

public class DamageEntityAction extends EntityTargetAction {

  public DamageEntityAction(String name) {
    super(name, SubSettingsHelper.createEntityTargetSettingsBuilder(true).createChooseItemChild("amount").fill(builder -> {
      String prefix = DefaultItem.getItemPrefix();
      for (int i = 1; i < 21; i++) {
        builder.addSetting(
          String.valueOf(i), new ItemBuilder(Material.FERMENTED_SPIDER_EYE, prefix + "§7" + (i / 2f) + " §c❤").setAmount(i).build());
      }
    }));
  }

  @Override
  public void executeFor(Entity entity, Map<String, String[]> subActions) {
    int amount = Integer.parseInt(subActions.get("amount")[0]);
    if (entity instanceof LivingEntity) {
      ((LivingEntity) entity).setNoDamageTicks(0);
      ((LivingEntity) entity).damage(amount);
      ((LivingEntity) entity).setNoDamageTicks(0);
    }
  }

  @Override
  public Material getMaterial() {
    return Material.FERMENTED_SPIDER_EYE;
  }

}
