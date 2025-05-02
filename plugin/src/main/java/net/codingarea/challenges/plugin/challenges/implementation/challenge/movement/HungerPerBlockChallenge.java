package net.codingarea.challenges.plugin.challenges.implementation.challenge.movement;

import net.codingarea.commons.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifier;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.BlockUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

import javax.annotation.Nonnull;

@Since("2.0")
public class HungerPerBlockChallenge extends SettingModifier {

  public HungerPerBlockChallenge() {
    super(MenuType.CHALLENGES, 1, 20, 2);
    setCategory(SettingCategory.MOVEMENT);
  }

  @Nonnull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.ROTTEN_FLESH, Message.forName("item-hunger-block-challenge"));
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPlayerMove(@Nonnull PlayerMoveEvent event) {
    if (!shouldExecuteEffect()) return;
    if (ignorePlayer(event.getPlayer())) return;
    if (BlockUtils.isSameBlockLocationIgnoreHeight(event.getFrom(), event.getTo())) return;
    int newFoodLevel = event.getPlayer().getFoodLevel() - getValue();
    event.getPlayer().setFoodLevel(Math.max(newFoodLevel, 0));
  }

}
