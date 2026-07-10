package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.codingarea.challenges.plugin.challenges.type.abstraction.Modifier;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class DamageMultiplierModifier extends Modifier {

  public DamageMultiplierModifier() {
    super(MenuType.SETTINGS, null, 10, new ItemStack(Material.STONE_SWORD), "damage-multiplier");
  }

  @NotNull
  @Override
  public LocalizableMessage getSettingsName() {
    return ChallengeHelper.getSettingsDescriptionModifierMultiplier(getValue());
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onDamage(@NotNull EntityDamageEvent event) {
    if (!(event.getEntity() instanceof Player)) return;
    event.setDamage(event.getDamage() * getValue());
  }

  @Override
  public void playValueChangeTitle() {
    ChallengeHelper.playChallengeValueTitle(this, getValue() + "x");
  }

}
