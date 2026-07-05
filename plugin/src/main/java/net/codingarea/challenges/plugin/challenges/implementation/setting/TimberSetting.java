package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.codingarea.challenges.plugin.challenges.type.abstraction.AbstractChallenge;
import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifier;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.legacy.Message;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.misc.BlockUtils;
import net.codingarea.commons.bukkit.utils.item.ItemUtils;
import net.codingarea.commons.bukkit.utils.misc.BukkitReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TimberSetting extends SettingModifier {

  public static final int LOGS_LEAVES = 2;

  public TimberSetting() {
    super(MenuType.SETTINGS, null, 2, new ItemStack(Material.DIAMOND_AXE), "item-timber-setting");
  }

  @NotNull
  @Override
  public ItemStack getSettingsItemPreset() {
    if (getValue() == LOGS_LEAVES)
      return new ItemStack(Material.OAK_LEAVES);
    return new ItemStack(Material.OAK_LOG);
  }

  @NotNull
  @Override
  public LocalizableMessage getSettingsName() {
    return getValue() == LOGS_LEAVES ? MessageKey.of("item-timber-setting-logs-and-leaves") : MessageKey.of("item-timber-setting-logs");
  }

  @Override
  public void playValueChangeTitle() {
    ChallengeHelper.playChallengeValueTitle(this, getValue() == LOGS_LEAVES ? Message.forName("item-timber-setting-logs-and-leaves") : Message.forName("item-timber-setting-logs"));
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onBreak(@NotNull BlockBreakEvent event) {
    if (!shouldExecuteEffect()) return;
    if (!isLog(event.getBlock().getType())) return;

    List<Block> treeBlocks = getAllTreeBlocks(event.getBlock(), getValue() == LOGS_LEAVES);
    ItemStack item = event.getPlayer().getInventory().getItemInMainHand();

    final int[] index = {0};

    boolean damageItem = !Objects.requireNonNull(item.getItemMeta()).isUnbreakable() && !AbstractChallenge.getFirstInstance(NoItemDamageSetting.class).isEnabled();

    Bukkit.getScheduler().runTaskTimer(plugin, timer -> {
      for (int i = 0; i < 2 && !treeBlocks.isEmpty(); i++) {
        Block block = treeBlocks.get(index[0]);
        breakBlock(block, item, damageItem);

        index[0]++;
        if (index[0] >= treeBlocks.size()) {
          timer.cancel();
          return;
        }
      }
    }, 0, 1);

  }

  private void breakBlock(@NotNull Block block, @NotNull ItemStack item, boolean damageItem) {
    if (isLog(block.getType())) {
      ChallengeHelper.breakBlock(block, item);
      if (damageItem) {
        ItemUtils.damageItem(item);
      }
    } else if (isLeaves(block.getType())) {
      ChallengeHelper.breakBlock(block, item);
    }
  }

  private List<Block> getAllTreeBlocks(@NotNull Block block, boolean leaves) {
    List<Block> allBlocks = new ArrayList<>();

    List<Block> currentBlocks = new ArrayList<>();
    currentBlocks.add(block);

    for (int i = 0; i < 8; i++) {

      ArrayList<Block> lastBlocks = new ArrayList<>(currentBlocks);
      currentBlocks.clear();

      for (Block currentBlock : lastBlocks) {
        for (Block blockAround : BlockUtils.getBlocksAroundBlock(currentBlock)) {
          if (BukkitReflectionUtils.isAir(blockAround.getType())) continue;
          if (allBlocks.contains(blockAround)) continue;

          if (currentBlock.getType() == blockAround.getType() || (leaves && isLeafMaterial(currentBlock.getType(), blockAround.getType()))) {
            allBlocks.add(blockAround);
            currentBlocks.add(blockAround);
          }
        }
      }

      if (currentBlocks.isEmpty())
        break;

    }

    return allBlocks;
  }

  private boolean isLog(Material material) {
    String name = material.name();
    return name.contains("LOG") || name.contains("STEM");
  }

  private boolean isLeaves(Material material) {
    return material.name().endsWith("LEAVES") || material.name().endsWith("WART_BLOCK");
  }

  public boolean isLeafMaterial(@NotNull Material logMaterial, @NotNull Material leafMaterial) {
    // Exceptions like nether wood
    if (logMaterial.name().equals("CRIMSON_STEM"))
      return leafMaterial.name().equals("NETHER_WART_BLOCK") || leafMaterial.name().equals("SHROOMLIGHT");
    if (logMaterial.name().equals("WARPED_STEM"))
      return leafMaterial.name().equals("WARPED_WART_BLOCK") || leafMaterial.name().equals("SHROOMLIGHT");

    int firstUnderscore = logMaterial.name().indexOf("_");
    if (firstUnderscore == -1) return false;
    String logPrefix = logMaterial.name().substring(0, firstUnderscore);
    return leafMaterial.name().startsWith(logPrefix) && leafMaterial.name().endsWith("LEAVES");
  }

}
