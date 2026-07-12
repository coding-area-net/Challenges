package net.codingarea.challenges.plugin.challenges.custom.settings.action.impl;

import net.codingarea.challenges.plugin.challenges.custom.settings.action.EntityTargetAction;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SelectableKey;
import net.codingarea.challenges.plugin.challenges.type.helper.SubSettingsHelper;
import net.codingarea.challenges.plugin.content.i18n.ArgumentFormat;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class DamageEntityAction extends EntityTargetAction {

  public DamageEntityAction(String name) {
    super(name, SubSettingsHelper.createEntityTargetSettingsBuilder(true).createChooseItemChild("amount").fill(builder -> {
      for (int i = 1; i < 21; i++) {
        builder.addSetting(SelectableKey.of(String.valueOf(i), new ItemStack(Material.FERMENTED_SPIDER_EYE, i), ArgumentFormat.HEARTS.apply(i)));
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

  @NotNull
  @Override
  public Material getMaterial() {
    return Material.FERMENTED_SPIDER_EYE;
  }

}
