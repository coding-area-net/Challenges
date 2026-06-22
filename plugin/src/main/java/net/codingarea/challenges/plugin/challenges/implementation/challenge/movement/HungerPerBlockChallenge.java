package net.codingarea.challenges.plugin.challenges.implementation.challenge.movement;

import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifier;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.utils.misc.BlockUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Since("2.0")
public class HungerPerBlockChallenge extends SettingModifier {

  public HungerPerBlockChallenge() {
    super(MenuType.CHALLENGES, SettingCategory.MOVEMENT, 1, 20, 2, new ItemStack(Material.ROTTEN_FLESH), "hunger-block");
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPlayerMove(@NotNull PlayerMoveEvent event) {
    if (!shouldExecuteEffect()) return;
    if (ignorePlayer(event.getPlayer())) return;
    if (BlockUtils.isSameBlockLocationIgnoreHeight(event.getFrom(), event.getTo())) return;
    int newFoodLevel = event.getPlayer().getFoodLevel() - getValue();
    event.getPlayer().setFoodLevel(Math.max(newFoodLevel, 0));
  }

}
