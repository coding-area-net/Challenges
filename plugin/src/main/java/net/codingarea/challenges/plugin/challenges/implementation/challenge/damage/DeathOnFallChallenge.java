package net.codingarea.challenges.plugin.challenges.implementation.challenge.damage;

import net.anweisen.utilities.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import javax.annotation.Nonnull;

@Since("2.0")
public class DeathOnFallChallenge extends Setting {

  public DeathOnFallChallenge() {
    super(MenuType.CHALLENGES);
    setCategory(SettingCategory.DAMAGE);
  }

  @Nonnull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.FEATHER, Message.forName("item-death-on-fall-challenge"));
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onEntityDamage(@Nonnull EntityDamageEvent event) {
    if (!(event.getEntity() instanceof Player)) return;
    if (!shouldExecuteEffect()) return;
    Player player = (Player) event.getEntity();
    if (ignorePlayer(player)) return;
    if (event.getCause() != DamageCause.FALL) return;
    event.setDamage(player.getHealth());
    event.setCancelled(false);
  }

}
