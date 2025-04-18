package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.anweisen.utilities.bukkit.utils.animation.SoundSample;
import net.codingarea.challenges.plugin.challenges.type.abstraction.CollectionGoal;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.item.ItemUtils;
import net.codingarea.challenges.plugin.utils.misc.ExperimentalUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class CollectMostItemsGoal extends CollectionGoal {

  public CollectMostItemsGoal() {
    super(ExperimentalUtils.getMaterials());
    setCategory(SettingCategory.SCORE_POINTS);
  }

  @Nonnull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.STICK, Message.forName("item-most-items-goal"));
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPickUp(@Nonnull EntityPickupItemEvent event) {
    if (!isEnabled()) return;
    if (!(event.getEntity() instanceof Player)) return;

    Player player = (Player) event.getEntity();
    ItemStack item = event.getItem().getItemStack();
    handleNewItem(item.getType(), player);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onClick(@Nonnull InventoryClickEvent event) {
    if (!shouldExecuteEffect()) return;
    if (!(event.getWhoClicked() instanceof Player)) return;

    Player player = (Player) event.getWhoClicked();
    ItemStack item = event.getCurrentItem();
    if (item == null) return;
    if (!ItemUtils.isObtainableInSurvival(item.getType())) return;

    handleNewItem(item.getType(), player);
  }

  protected void handleNewItem(@Nonnull Material material, @Nonnull Player player) {
    collect(player, material, () -> {
      Message.forName("item-collected").send(player, Prefix.CHALLENGES, material);
      SoundSample.PLING.play(player);
    });
  }

}
