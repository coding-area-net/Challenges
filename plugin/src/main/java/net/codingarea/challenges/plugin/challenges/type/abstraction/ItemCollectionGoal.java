package net.codingarea.challenges.plugin.challenges.type.abstraction;

import net.codingarea.challenges.plugin.challenges.type.helper.GoalHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.i18n.Prefix;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.spigot.events.PlayerInventoryClickEvent;
import net.codingarea.challenges.plugin.spigot.events.PlayerPickupItemEvent;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ItemCollectionGoal extends CollectionGoal {

  public ItemCollectionGoal(@Nullable SettingCategory category, @NotNull ItemStack displayItemPreset,
                            @NotNull String nameMessageKey, @NotNull Material... target) {
    super(category,  displayItemPreset, nameMessageKey, target);
  }

  public ItemCollectionGoal(@Nullable SettingCategory category, boolean enabledByDefault, @NotNull ItemStack displayItemPreset,
                            @NotNull String nameMessageKey, @NotNull Material... target) {
    super(category, enabledByDefault, displayItemPreset, nameMessageKey, target);
  }

  protected void handleCollect(@NotNull Player player, @NotNull Material material) {
    collect(player, material, () -> {
      MessageKey.of("item-collected").send(player, Prefix.CHALLENGES, material);
      SoundSample.PLING.play(player);
    });
  }

  @Override
  protected void onEnable() {
    scoreboard.setContent(GoalHelper.createScoreboard(() -> getPoints(new AtomicInteger(), true),
      player -> Collections.singletonList(Message.forName("items-to-collect").asString(target.length))));
    scoreboard.show();
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPickupItem(@NotNull PlayerPickupItemEvent event) {
    if (!shouldExecuteEffect()) return;
    Material material = event.getItem().getItemStack().getType();
    Player player = event.getPlayer();
    handleCollect(player, material);
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPlayerInventoryClick(@NotNull PlayerInventoryClickEvent event) {
    if (!shouldExecuteEffect()) return;
    if (event.isCancelled()) return;
    if (event.getClickedInventory() == null) return;
    if (event.getClickedInventory().getHolder() != event.getPlayer()) return;
    if (event.getCurrentItem() == null) return;
    Player player = event.getPlayer();
    Material material = event.getCurrentItem().getType();
    handleCollect(player, material);
  }

}
