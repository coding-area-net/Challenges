package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifierCollectionGoal;
import net.codingarea.challenges.plugin.content.i18n.Prefix;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.management.menu.info.ChallengeMenuClickInfo;
import net.codingarea.challenges.plugin.spigot.events.PlayerInventoryClickEvent;
import net.codingarea.challenges.plugin.spigot.events.PlayerPickupItemEvent;
import net.codingarea.challenges.plugin.utils.misc.ExperimentalUtils;
import net.codingarea.challenges.plugin.utils.misc.ListBuilder;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.commons.bukkit.utils.misc.MinecraftVersion;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CollectWoodGoal extends SettingModifierCollectionGoal {

  private static final boolean newNether = MinecraftVersion.current().isNewerOrEqualThan(MinecraftVersion.V1_16);
  private static final int
    OVERWORLD = 1,
    NETHER = 2,
    BOTH = 3;

  public CollectWoodGoal() {
    super(SettingCategory.FASTEST_TIME, 1, newNether ? 3 : 1, new ItemStack(Material.GOLDEN_AXE), "collect-wood-goal");
  }

//  @NotNull
//  @Override
//  public LegacyItemBuilder createSettingsItem() {
//    if (!newNether) return DefaultItem.enabled();
//    if (getValue() == OVERWORLD)
//      return DefaultItem.create(Material.OAK_LOG, Message.forName("item-collect-wood-goal-overworld"));
//    if (getValue() == NETHER)
//      return DefaultItem.create(Material.WARPED_STEM, Message.forName("item-collect-wood-goal-nether"));
//    return DefaultItem.create(Material.CRYING_OBSIDIAN, Message.forName("item-collect-wood-goal-both"));
//  }

  @Override
  public void handleClick(@NotNull ChallengeMenuClickInfo info) {
    if (!newNether && info.isLowerItemClick() && enabled) {
      setEnabled(false);
      SoundSample.playStatusSound(info.getPlayer(), enabled);
      playStatusUpdateTitle();
    } else {
      super.handleClick(info);
    }
  }

  @Override
  protected void onEnable() {
    super.onEnable();
    setTarget(getWoodMaterials());
    checkCollects();
  }

  @Override
  protected void onValueChange() {
    setTarget(getWoodMaterials());
    checkCollects();
  }

  @NotNull
  private Object[] getWoodMaterials() {
    return new ListBuilder<Material>().fill(builder -> {
      for (Material material : ExperimentalUtils.getMaterials()) {
        if (isSearched(material))
          builder.add(material);
      }
    }).build().toArray();
  }

  private boolean isLog(@NotNull Material material) {
    return material.name().contains("LOG") && !material.name().contains("STRIPPED");
  }

  private boolean isNetherLog(@NotNull Material material) {
    return material == Material.WARPED_STEM || material == Material.CRIMSON_STEM;
  }

  private boolean isSearched(@NotNull Material material) {
    return getValue() == OVERWORLD && isLog(material) ||
      getValue() == NETHER && isNetherLog(material) ||
      getValue() == BOTH && (isLog(material) || isNetherLog(material));
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

  private void handleCollect(@NotNull Player player, @NotNull Material material) {
    collect(player, material, () -> {
      MessageKey.of("item-collected").send(player, Prefix.CHALLENGES, material);
      SoundSample.PLING.play(player);
    });
  }

}
