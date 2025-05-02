package net.codingarea.challenges.plugin.challenges.implementation.goal.forcebattle;

import net.codingarea.commons.common.annotations.Since;
import net.codingarea.commons.common.config.Document;
import net.codingarea.challenges.plugin.challenges.implementation.goal.forcebattle.targets.ItemTarget;
import net.codingarea.challenges.plugin.challenges.type.abstraction.ForceBattleDisplayGoal;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.spigot.events.PlayerInventoryClickEvent;
import net.codingarea.challenges.plugin.spigot.events.PlayerPickupItemEvent;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.InventoryUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

@Since("2.1.3")
public class ForceItemBattleGoal extends ForceBattleDisplayGoal<ItemTarget> {

  public ForceItemBattleGoal() {
    super(Message.forName("menu-force-item-battle-goal-settings"));

    registerSetting("give-item", new BooleanSubSetting(
      () -> new ItemBuilder(Material.CHEST, Message.forName("item-force-item-battle-goal-give-item")),
      false
    ));
  }

  @Override
  protected ItemTarget[] getTargetsPossibleToFind() {
    List<Material> materials = ItemTarget.getPossibleItems();
    return materials.stream().map(ItemTarget::new).toArray(ItemTarget[]::new);
  }

  @Override
  public ItemTarget getTargetFromDocument(Document document, String path) {
    return new ItemTarget(document.getEnum(path, Material.class));
  }

  @Override
  public List<ItemTarget> getListFromDocument(Document document, String path) {
    return document.getEnumList(path, Material.class).stream().map(ItemTarget::new).collect(Collectors.toList());
  }

  @Override
  public Message getLeaderboardTitleMessage() {
    return Message.forName("force-item-battle-leaderboard");
  }

  @NotNull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.ITEM_FRAME, Message.forName("item-force-item-battle-goal"));
  }

  @Override
  public void handleJokerUse(Player player) {
    if (giveItemOnSkip()) {
      InventoryUtils.dropOrGiveItem(player.getInventory(), player.getLocation(), currentTarget.get(player.getUniqueId()).getTarget());
    }
    super.handleJokerUse(player);
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onClick(PlayerInventoryClickEvent event) {
    if (!shouldExecuteEffect()) return;
    if (ignorePlayer(event.getPlayer())) return;
    if (event.getClickedInventory() == null) return;
    if (event.getCurrentItem() == null) return;
    Material material = currentTarget.get(event.getPlayer().getUniqueId()).getTarget();
    if (material == null) return;
    if (material == event.getCurrentItem().getType()) {
      handleTargetFound(event.getPlayer());
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPickup(PlayerPickupItemEvent event) {
    if (!shouldExecuteEffect()) return;
    if (ignorePlayer(event.getPlayer())) return;
    Material material = currentTarget.get(event.getPlayer().getUniqueId()).getTarget();
    if (material == null) return;
    if (material == event.getItem().getItemStack().getType()) {
      handleTargetFound(event.getPlayer());
    }
  }

  private boolean giveItemOnSkip() {
    return getSetting("give-item").getAsBoolean();
  }

}
