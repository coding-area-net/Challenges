package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.abstraction.menu.MenuSetting;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.management.blocks.BlockDropManager.DropPriority;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.misc.BlockUtils;
import net.codingarea.challenges.plugin.utils.misc.InventoryUtils;
import net.codingarea.challenges.plugin.utils.misc.Utils;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.commons.bukkit.utils.item.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Since("2.0")
public class CutCleanSetting extends MenuSetting {

  public CutCleanSetting() {
    super(MenuType.SETTINGS, null, new ItemStack(Material.IRON_AXE), "cut-clean");
    registerSetting("iron->iron_ingot", new ConvertDropSubSetting(
      new ItemStack(Material.IRON_INGOT), getChallengeMessageKey("sub.iron"),
      true, Material.IRON_INGOT, "IRON_ORE", "DEEPSLATE_IRON_ORE"
    ));
    registerSetting("gold->gold_ingot", new ConvertDropSubSetting(
      new ItemStack(Material.GOLD_INGOT), getChallengeMessageKey("sub.gold"),
      true, Material.GOLD_INGOT, "GOLD_ORE", "DEEPSLATE_GOLD_ORE"
    ));
    registerSetting("coal->torch", new ConvertDropSubSetting(
      new ItemStack(Material.COAL), getChallengeMessageKey("sub.coal"),
      false, Material.TORCH, "COAL_ORE", "DEEPSLATE_COAL_ORE"
    ));
    registerSetting("gravel->flint", new ConvertDropSubSetting(
      new ItemStack(Material.FLINT), getChallengeMessageKey("sub.flint"),
      false, Material.FLINT, "GRAVEL"
    ));
    registerSetting("ore->veins", new BreakOreVeinsSubSetting(
      new ItemStack(Material.GOLDEN_PICKAXE), getChallengeMessageKey("sub.vein"),
      1, 10, 3
    ));
    registerSetting("items->inventory", new DirectIntoInventorySubSetting(
      new ItemStack(Material.CHEST), getChallengeMessageKey("sub.inventory"),
      false
    ));
    registerSetting("row->cooked", new CookFoodSubSetting(
      new ItemStack(Material.COOKED_BEEF), getChallengeMessageKey("sub.food"),
      true
    ));
  }

  protected boolean directIntoInventory() {
    return getSetting("items->inventory").getAsBoolean();
  }

  private class DirectIntoInventorySubSetting extends BooleanSubSetting {

    public DirectIntoInventorySubSetting(@NotNull ItemStack displayItemPreset, @NotNull MessageKey nameMessageKeySpace, boolean enabledByDefault) {
      super(displayItemPreset, nameMessageKeySpace, enabledByDefault);
    }

  }

  private class ConvertDropSubSetting extends BooleanSubSetting {

    protected final Material[] from;
    protected final Material to;

    public ConvertDropSubSetting(@NotNull ItemStack displayItemPreset, @NotNull MessageKey nameMessageKeySpace,
                                 boolean enabledByDefault, @NotNull Material to, @NotNull String... from) {
      super(displayItemPreset, nameMessageKeySpace, enabledByDefault);

      List<Material> materials = new ArrayList<>();
      for (String s : from) {
        Material material = Utils.getMaterial(s);
        if (material != null) {
          materials.add(material);
        }
      }

      this.from = materials.toArray(new Material[0]);
      this.to = to;
    }

    @Override
    public void onEnable() {
      if (from == null) return;
      for (Material material : from) {
        Challenges.getInstance().getBlockDropManager().setCustomDrops(material, to, DropPriority.CUT_CLEAN);
      }
    }

    @Override
    public void onDisable() {
      if (from == null) return;
      for (Material material : from) {
        Challenges.getInstance().getBlockDropManager().resetCustomDrop(material, DropPriority.CUT_CLEAN);
      }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(@NotNull BlockBreakEvent event) {
      if (!shouldExecuteEffect()) return;
      if (!directIntoInventory()) return;
      if (!event.isDropItems()) return;
      Material type = event.getBlock().getType();

      for (Material material : from) {
        if (type != material) continue;
        List<Material> customDrops = Challenges.getInstance().getBlockDropManager().getCustomDrops(event.getBlock().getType());
        if (customDrops.isEmpty()) return;
        event.setDropItems(false);
        customDrops.forEach(drop -> InventoryUtils.dropOrGiveItem(event.getPlayer().getInventory(), event.getBlock().getLocation(), drop));
      }

    }

  }

  private class BreakOreVeinsSubSetting extends NumberAndBooleanSubSetting {

    public BreakOreVeinsSubSetting(@NotNull ItemStack displayItemPreset, @NotNull MessageKey nameMessageKeySpace,
                                   int min, int max, int defaultValue) {
      super(displayItemPreset, nameMessageKeySpace, min, max, defaultValue);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(@NotNull BlockBreakEvent event) {
      if (!shouldExecuteEffect()) return;
      ItemStack itemInMainHand = event.getPlayer().getInventory().getItemInMainHand();
      if (!canBeBroken(event.getBlock(), itemInMainHand)) return;
      if (!event.getBlock().getType().name().contains("ORE")) return;

      breakBlockVein(event.getPlayer(), event.getBlock(), itemInMainHand);
    }

    private void breakBlockVein(@NotNull Player player, @NotNull Block block, @NotNull ItemStack tool) {
      Material material = block.getType();

      List<Block> allBlocks = new ArrayList<>();
      List<Block> nextBlocks = new ArrayList<>();
      nextBlocks.add(block);

      for (int i = 0; i < getValue(); i++) {

        if (nextBlocks.isEmpty()) break;

        List<Block> lastBlocks = new ArrayList<>(nextBlocks);
        nextBlocks.clear();

        for (Block currentMiddleBlock : lastBlocks) {
          for (Block sideBlock : BlockUtils.getBlocksAroundBlock(currentMiddleBlock)) {
            if (sideBlock.getType() == material && sideBlock != block && !allBlocks.contains(sideBlock) && allBlocks.size() < getValue()) {
              allBlocks.add(sideBlock);
              nextBlocks.add(sideBlock);
            }
          }
        }
      }

      AtomicInteger index = new AtomicInteger();
      Bukkit.getScheduler().runTaskTimer(plugin, timer -> {

        SoundSample.LOW_PLOP.play(player);
        for (int i = 0; i < 2; i++) {

          if (index.get() >= allBlocks.size()) {
            timer.cancel();
            return;
          }

          Block currentBlock = allBlocks.get(index.get());
          if (currentBlock.getType() == material) {
            ChallengeHelper.breakBlock(currentBlock, tool, player.getInventory());
          }

          index.getAndIncrement();
        }
      }, 0, 2);

    }

    private boolean canBeBroken(@NotNull Block block, @NotNull ItemStack tool) {
      return !block.getDrops(tool).isEmpty();
    }

    // TODO settings name
//    @NotNull
//    @Override
//    public LegacyItemBuilder getSettingsItem() {
//      return getAsBoolean() ? DefaultItem.value(getValue(), "§7Max Vein Size: §e") : DefaultItem.disabled();
//    }

  }

  private class CookFoodSubSetting extends BooleanSubSetting {

    public CookFoodSubSetting(@NotNull ItemStack displayItemPreset, @NotNull MessageKey nameMessageKeySpace, boolean enabledByDefault) {
      super(displayItemPreset, nameMessageKeySpace, enabledByDefault);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityKill(@NotNull EntityDeathEvent event) {
      if (!isEnabled()) return;
      event.getDrops().replaceAll(item -> new ItemStack(ItemUtils.convertFoodToCookedFood(item.getType()), item.getAmount()));

      Player killer = event.getEntity().getKiller();
      if (killer != null && directIntoInventory()) {
        event.getDrops().forEach(itemStack -> InventoryUtils.dropOrGiveItem(killer.getInventory(), killer.getLocation(), itemStack));
        event.getDrops().clear();
      }

    }

  }

}
