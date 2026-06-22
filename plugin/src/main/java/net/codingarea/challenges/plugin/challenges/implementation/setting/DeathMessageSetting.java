package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.codingarea.challenges.plugin.challenges.type.abstraction.Modifier;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.i18n.Prefix;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.item.DefaultItem;
import net.codingarea.challenges.plugin.utils.item.DefaultItems;
import net.codingarea.challenges.plugin.utils.item.LegacyItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.MinecraftNameWrapper;
import net.codingarea.challenges.plugin.utils.misc.NameHelper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class DeathMessageSetting extends Modifier {

  public static final int
    ENABLED = 2,
    VANILLA = 3;

  private boolean hide;

  public DeathMessageSetting() {
    super(MenuType.SETTINGS, null, 1, 3, ENABLED, new ItemStack(Material.BOW), "death-message");
  }

  @NotNull
  @Override
  public ItemStack getSettingsItemPreset() {
    return switch (getValue()) {
      case VANILLA -> new ItemStack(MinecraftNameWrapper.SIGN);
      case ENABLED -> DefaultItems.createEnabledPreset();
      default -> DefaultItems.createDisabledPreset();
    };
  }

  @Override
  public void playValueChangeTitle() {
    switch (getValue()) {
      case ENABLED:
        ChallengeHelper.playToggleChallengeTitle(this, true);
        return;
      case VANILLA:
        ChallengeHelper.playChangeChallengeValueTitle(this, Message.forName("item-death-message-setting-vanilla"));
        return;
      default:
        ChallengeHelper.playToggleChallengeTitle(this, false);
    }
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onDeath(@NotNull PlayerDeathEvent event) {
    event.setDeathMessage(null);
    if (hide) return;

    String original = event.getDeathMessage();
    Player entity = event.getEntity();
    switch (getValue()) {
      case ENABLED:
        EntityDamageEvent cause = entity.getLastDamageCause();
        if (cause != null && cause.getCause() != DamageCause.CUSTOM) {
          Message.forName("death-message-cause").broadcast(Prefix.CHALLENGES, NameHelper.getName(entity), DamageDisplaySetting.getCause(cause));
        } else {
          Message.forName("death-message").broadcast(Prefix.CHALLENGES, NameHelper.getName(entity));
        }
        return;
      case VANILLA:
        if (original != null) {
          Bukkit.broadcastMessage(Prefix.CHALLENGES + "§7" + original);
        }
    }
  }

  public void setHideMessagesTemporarily(boolean hide) {
    this.hide = hide;
  }

}
