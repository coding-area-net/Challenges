package net.codingarea.challenges.plugin.challenges.implementation.challenge.damage;

import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.spigot.events.PlayerInventoryClickEvent;
import net.codingarea.challenges.plugin.spigot.events.PlayerPickupItemEvent;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryAction;

import javax.annotation.Nonnull;

public class DamagePerItemChallenge extends Setting {

  public DamagePerItemChallenge() {
    super(MenuType.CHALLENGES);
    setCategory(SettingCategory.DAMAGE);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPickup(@Nonnull PlayerPickupItemEvent event) {
    if (!shouldExecuteEffect()) return;
    if (ignorePlayer(event.getPlayer())) return;
    applyDamage(event.getPlayer(), event.getItem().getItemStack().getAmount());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onClick(@Nonnull PlayerInventoryClickEvent event) {
    if (event.isCancelled()) return; // ignoreCancelled not working on own event
    if (!shouldExecuteEffect()) return;
    if (ignorePlayer(event.getPlayer())) return;
    if (event.getAction() == InventoryAction.NOTHING) return;
    if (event.getCurrentItem() == null) return;
    applyDamage(event.getPlayer(), event.getCurrentItem().getAmount());
  }

  private void applyDamage(@Nonnull Player player, int amount) {
    player.setNoDamageTicks(0);
    player.damage(amount);
  }

  @Nonnull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.SHEARS, Message.forName("item-damage-item-challenge"));
  }

}
