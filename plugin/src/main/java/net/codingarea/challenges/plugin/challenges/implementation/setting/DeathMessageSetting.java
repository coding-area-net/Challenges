package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.codingarea.challenges.plugin.challenges.type.abstraction.Modifier;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.i18n.ArgumentFormat;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.content.legacy.Message;
import net.codingarea.challenges.plugin.content.i18n.Prefix;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.item.DefaultItems;
import net.codingarea.challenges.plugin.utils.misc.MinecraftNameWrapper;
import net.kyori.adventure.text.Component;
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
        ChallengeHelper.playChallengeToggleTitle(this, true);
        return;
      case VANILLA:
        ChallengeHelper.playChallengeValueTitle(this, Message.forName("item-death-message-setting-vanilla"));
        return;
      default:
        ChallengeHelper.playChallengeToggleTitle(this, false);
    }
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onDeath(@NotNull PlayerDeathEvent event) {
    if (hide) {
      event.deathMessage(null);
      return;
    }

    Player player = event.getEntity();
    switch (getValue()) {
      case ENABLED -> {
        EntityDamageEvent cause = player.getLastDamageCause();
        if (cause != null && cause.getCause() != DamageCause.CUSTOM) {
          MessageKey.of("death-message-cause").broadcast(Prefix.CHALLENGES, player, ArgumentFormat.DAMAGE_CAUSE.apply(cause));
        } else {
          MessageKey.of("death-message").broadcast(Prefix.CHALLENGES, player);
        }
      }
      case VANILLA -> {
        Component original = event.deathMessage();
        if (original != null) {
          for (Player target : Bukkit.getOnlinePlayers()) {
            target.sendMessage(Component.text().append(Prefix.CHALLENGES.getKey().asComponent(player)).append(original));
          }
        }
      }
    }

    event.deathMessage(null);
  }

  public void setHideMessagesTemporarily(boolean hide) {
    this.hide = hide;
  }

}
