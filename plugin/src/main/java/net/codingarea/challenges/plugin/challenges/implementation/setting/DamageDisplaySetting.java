package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.challenges.type.annotation.Updated;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.i18n.ArgumentFormat;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.content.i18n.Prefix;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Updated("2.4")
public class DamageDisplaySetting extends Setting {

  public DamageDisplaySetting() {
    super(MenuType.SETTINGS, null, true, new ItemStack(Material.COMMAND_BLOCK), "damage-display");
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onDamage(@NotNull EntityDamageEvent event) {
    if (ChallengeAPI.isPaused() || event.getCause() == DamageCause.CUSTOM || !isEnabled())
      return;
    if (!(event.getEntity() instanceof Player)) return;
    if (ChallengeHelper.finalDamageIsNull(event)) return;

    LocalizableMessage damageDisplay = ArgumentFormat.HEARTS_LIMITED.apply(event.getFinalDamage());
    LocalizableMessage causeDisplay = ArgumentFormat.DAMAGE_CAUSE.apply(event);
    getChallengeMessageKey("message").broadcast(Prefix.DAMAGE, event.getEntity(), damageDisplay, causeDisplay);
  }
}
