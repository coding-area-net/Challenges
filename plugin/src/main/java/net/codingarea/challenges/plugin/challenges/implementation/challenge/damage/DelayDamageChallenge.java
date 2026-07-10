package net.codingarea.challenges.plugin.challenges.implementation.challenge.damage;

import net.codingarea.challenges.plugin.challenges.type.abstraction.TimedChallenge;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.content.i18n.Prefix;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Since("2.3.2")
public class DelayDamageChallenge extends TimedChallenge {

  private boolean canGetDamage = false;
  private final Map<Player, Double> damageMap = new HashMap<>();

  public DelayDamageChallenge() {
    super(MenuType.CHALLENGES, SettingCategory.DAMAGE, 1, 64, 4, false, new ItemStack(Material.REDSTONE), "delay-damage");
  }

  @Override
  public LocalizableMessage getSettingsDescription() {
    return ChallengeHelper.getSettingsDescriptionTimeSeconds(getValue() * 30);
  }

  @Override
  protected int getSecondsUntilNextActivation() {
    return getValue() * 30;
  }

  @Override
  protected void onTimeActivation() {

    double totalDamage = damageMap.values().stream()
      .mapToDouble(Double::doubleValue)
      .sum();

    int playerCount = damageMap.size();

    if (playerCount > 0) {
      canGetDamage = true;
      for (Player player : Bukkit.getOnlinePlayers()) {
        player.damage(totalDamage);
        MessageKey.of(("extreme-force-battle-took-damage")).send(player, Prefix.DAMAGE, totalDamage / 2);
      }
      canGetDamage = false;
    }

    damageMap.clear();
    restartTimer();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerDamage(@NotNull EntityDamageEvent event) {
    if (!shouldExecuteEffect()) return;
    if (!(event.getEntity() instanceof Player player)) return;
    if (ignorePlayer(player)) return;
    if (canGetDamage) return;

    double damage = event.getFinalDamage();

    damageMap.put(player, damageMap.getOrDefault(player, 0.0) + damage);
    event.setCancelled(true);
  }
}
