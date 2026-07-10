package net.codingarea.challenges.plugin.challenges.implementation.material;

import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.spigot.events.PlayerInventoryClickEvent;
import net.codingarea.challenges.plugin.spigot.events.PlayerPickupItemEvent;
import net.codingarea.challenges.plugin.utils.misc.InventoryUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class BlockMaterialSetting extends Setting {

  private final String name;
  private final List<Material> materials;

  public BlockMaterialSetting(@NotNull String name, @NotNull ItemStack displayItemPreset, @NotNull Material... materials) {
    super(MenuType.ITEMS, null, true, displayItemPreset, "material-rule." + name);
    this.name = name;
    this.materials = Arrays.asList(materials);
  }

  @NotNull
  @Override
  public LocalizableMessage getChallengeDescription() {
    return super.getChallengeDescription().withArgs(LocalizableMessage.joinList(3, materials));
  }

  private boolean isMaterialAllowed(Material material) {
    return !materials.contains(material);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
    if (isEnabled()) return;
    if (ChallengeAPI.isWorldInUse()) return;
    if (ignorePlayer(event.getPlayer())) return;
    if (isMaterialAllowed(event.getMaterial())) {
      if (event.getClickedBlock() == null) return;
      if (isMaterialAllowed(event.getClickedBlock().getType())) return;
      event.setCancelled(true);
      return;
    }
    event.setCancelled(true);

    dropMaterial(event.getPlayer().getLocation(), event.getPlayer().getInventory());
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerInventoryClick(@NotNull PlayerInventoryClickEvent event) {
    if (isEnabled()) return;
    if (ignorePlayer(event.getPlayer())) return;
    if (ChallengeAPI.isWorldInUse()) return;
    if (event.getCurrentItem() == null) return;
    if (event.getClickedInventory() == null) return;
    if (event.getClickedInventory().getHolder() != event.getPlayer()) return;
    if (isMaterialAllowed(event.getCurrentItem().getType())) return;
    event.setCancelled(true);

    dropMaterial(event.getPlayer().getLocation(), event.getClickedInventory());
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPlayerPickupItem(@NotNull PlayerPickupItemEvent event) {
    if (isEnabled()) return;
    if (ignorePlayer(event.getPlayer())) return;
    if (ChallengeAPI.isWorldInUse()) return;
    if (isMaterialAllowed(event.getItem().getItemStack().getType())) return;
    event.setCancelled(true);
  }

  public void dropMaterial(@NotNull Location location, @NotNull Inventory inventory) {
    for (int slot = 0; slot < inventory.getSize(); slot++) {
      ItemStack item = inventory.getItem(slot);
      if (item == null) continue;
      if (isMaterialAllowed(item.getType())) continue;
      InventoryUtils.dropItemByPlayer(location, item);
      inventory.setItem(slot, null);
    }
  }

  @NotNull
  @Override
  public String getUniqueName() {
    return "blockmaterial" + name;
  }

}
