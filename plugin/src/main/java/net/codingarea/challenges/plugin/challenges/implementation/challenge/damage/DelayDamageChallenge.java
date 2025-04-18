package net.codingarea.challenges.plugin.challenges.implementation.challenge.damage;

import net.anweisen.utilities.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.type.abstraction.TimedChallenge;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@Since("2.3.2")
public class DelayDamageChallenge extends TimedChallenge {

  private boolean canGetDamage = false;
  private final Map<Player, Double> damageMap = new HashMap<>();

  public DelayDamageChallenge() {
    super(MenuType.CHALLENGES, 1, 64, 4, false);
    setCategory(SettingCategory.DAMAGE);
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
        Message.forName(("extreme-force-battle-took-damage")).send(player, Prefix.DAMAGE, totalDamage / 2);
      }
      canGetDamage = false;
    }

    damageMap.clear();
    restartTimer();
  }

  @Override
  public @NotNull ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.REDSTONE, Message.forName("item-delay-damage-description"));
  }

  @Nullable
  @Override
  protected String[] getSettingsDescription() {
    return Message.forName("item-time-seconds-description").asArray(getValue() * 30);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerDamage(@Nonnull EntityDamageEvent event) {
    if (!shouldExecuteEffect()) return;
    if (!(event.getEntity() instanceof Player)) return;
    if (ignorePlayer((Player) event.getEntity())) return;
    if (canGetDamage) return;

    double damage = event.getFinalDamage();
    Player player = (Player) event.getEntity();

    damageMap.put(player, damageMap.getOrDefault(player, 0.0) + damage);
    event.setCancelled(true);
  }
}
