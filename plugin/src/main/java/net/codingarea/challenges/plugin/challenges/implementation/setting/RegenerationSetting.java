package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.codingarea.challenges.plugin.challenges.type.abstraction.Modifier;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.content.legacy.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.item.DefaultItems;
import net.codingarea.commons.bukkit.utils.item.StandardItemBuilder;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RegenerationSetting extends Modifier {

  public static final int DISABLED = 1, ENABLED = 2, NOT_NATURAL = 3;

  public RegenerationSetting() {
    super(MenuType.SETTINGS, null, 1, 3, ENABLED,
      new StandardItemBuilder.PotionBuilder(Material.POTION).setColor(Color.RED).build(), "regeneration");
  }

  @NotNull
  @Override
  public ItemStack getSettingsItemPreset() {
    if (getValue() == ENABLED) {
      return DefaultItems.createEnabledPreset();
    }
    return new ItemStack(Material.ORANGE_DYE);
  }

  @NotNull
  @Override
  public LocalizableMessage getSettingsName() {
    if (getValue() == NOT_NATURAL) {
      return MessageKey.of("item-regeneration-setting-not_natural");
    }
    return MessageKey.of("enabled");
  }

  @Override
  public void playValueChangeTitle() {
    if (getValue() == 1) {
      ChallengeHelper.playChallengeToggleTitle(this, false);
      return;
    }
    ChallengeHelper.playChallengeValueTitle(this, getValue() == 2 ? Message.forName("enabled") : Message.forName("item-regeneration-setting-not_natural"));
  }

  @Override
  public boolean isEnabled() {
    return getValue() != DISABLED;
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onEntityRegainHealth(EntityRegainHealthEvent event) {
    if (!(event.getEntity() instanceof Player)) return;

    if (getValue() == DISABLED) {
      event.setAmount(0);
      event.setCancelled(true);
      return;
    }

    if (getValue() == NOT_NATURAL) {
      if (event.getRegainReason() == RegainReason.SATIATED || event.getRegainReason() == RegainReason.REGEN) {
        event.setAmount(0);
        event.setCancelled(true);
      }
    }

  }

}
