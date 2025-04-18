package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.anweisen.utilities.bukkit.utils.animation.SoundSample;
import net.anweisen.utilities.bukkit.utils.misc.MinecraftVersion;
import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifierCollectionGoal;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.management.menu.info.ChallengeMenuClickInfo;
import net.codingarea.challenges.plugin.spigot.events.PlayerInventoryClickEvent;
import net.codingarea.challenges.plugin.spigot.events.PlayerPickupItemEvent;
import net.codingarea.challenges.plugin.utils.item.DefaultItem;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.ExperimentalUtils;
import net.codingarea.challenges.plugin.utils.misc.ListBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import javax.annotation.Nonnull;

public class CollectWoodGoal extends SettingModifierCollectionGoal {

  private static final boolean newNether = MinecraftVersion.current().isNewerOrEqualThan(MinecraftVersion.V1_16);
  private static final int
    OVERWORLD = 1,
    NETHER = 2,
    BOTH = 3;

  public CollectWoodGoal() {
    super(1, newNether ? 3 : 1);
    setCategory(SettingCategory.FASTEST_TIME);
  }

  @Nonnull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.GOLDEN_AXE, Message.forName("item-collect-wood-goal"));
  }

  @Nonnull
  @Override
  public ItemBuilder createSettingsItem() {
    if (!newNether) return DefaultItem.enabled();
    if (getValue() == OVERWORLD)
      return DefaultItem.create(Material.OAK_LOG, Message.forName("item-collect-wood-goal-overworld"));
    if (getValue() == NETHER)
      return DefaultItem.create(Material.WARPED_STEM, Message.forName("item-collect-wood-goal-nether"));
    return DefaultItem.create(Material.CRYING_OBSIDIAN, Message.forName("item-collect-wood-goal-both"));
  }

  @Override
  public void handleClick(@Nonnull ChallengeMenuClickInfo info) {
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

  @Nonnull
  private Object[] getWoodMaterials() {
    return new ListBuilder<Material>().fill(builder -> {
      for (Material material : ExperimentalUtils.getMaterials()) {
        if (isSearched(material))
          builder.add(material);
      }
    }).build().toArray();
  }

  private boolean isLog(@Nonnull Material material) {
    return material.name().contains("LOG") && !material.name().contains("STRIPPED");
  }

  private boolean isNetherLog(@Nonnull Material material) {
    return material == Material.WARPED_STEM || material == Material.CRIMSON_STEM;
  }

  private boolean isSearched(@Nonnull Material material) {
    return getValue() == OVERWORLD && isLog(material) ||
      getValue() == NETHER && isNetherLog(material) ||
      getValue() == BOTH && (isLog(material) || isNetherLog(material));
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPickupItem(@Nonnull PlayerPickupItemEvent event) {
    if (!shouldExecuteEffect()) return;
    Material material = event.getItem().getItemStack().getType();
    Player player = event.getPlayer();
    handleCollect(player, material);
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPlayerInventoryClick(@Nonnull PlayerInventoryClickEvent event) {
    if (!shouldExecuteEffect()) return;
    if (event.isCancelled()) return;
    if (event.getClickedInventory() == null) return;
    if (event.getClickedInventory().getHolder() != event.getPlayer()) return;
    if (event.getCurrentItem() == null) return;
    Player player = event.getPlayer();
    Material material = event.getCurrentItem().getType();
    handleCollect(player, material);
  }

  private void handleCollect(@Nonnull Player player, @Nonnull Material material) {
    collect(player, material, () -> {
      Message.forName("item-collected").send(player, Prefix.CHALLENGES, material);
      SoundSample.PLING.play(player);
    });
  }

}
