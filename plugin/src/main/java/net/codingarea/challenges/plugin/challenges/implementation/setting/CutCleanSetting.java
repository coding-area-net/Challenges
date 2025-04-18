package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.anweisen.utilities.bukkit.utils.animation.SoundSample;
import net.anweisen.utilities.common.annotations.Since;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.abstraction.MenuSetting;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.blocks.BlockDropManager.DropPriority;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.item.DefaultItem;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.item.ItemUtils;
import net.codingarea.challenges.plugin.utils.misc.BlockUtils;
import net.codingarea.challenges.plugin.utils.misc.InventoryUtils;
import net.codingarea.challenges.plugin.utils.misc.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

@Since("2.0")
public class CutCleanSetting extends MenuSetting {

  public CutCleanSetting() {
    super(MenuType.SETTINGS, Message.forName("menu-cut-clean-setting-settings"));
    registerSetting("iron->iron_ingot",
      new ConvertDropSubSetting(() -> new ItemBuilder(Material.IRON_INGOT, Message.forName("item-cut-clean-iron-setting")), true,
        Material.IRON_INGOT, "IRON_ORE", "DEEPSLATE_IRON_ORE"));
    registerSetting("gold->gold_ingot",
      new ConvertDropSubSetting(() -> new ItemBuilder(Material.GOLD_INGOT, Message.forName("item-cut-clean-gold-setting")), true,
        Material.GOLD_INGOT, "GOLD_ORE", "DEEPSLATE_GOLD_ORE"));
    registerSetting("coal->torch",
      new ConvertDropSubSetting(() -> new ItemBuilder(Material.COAL, Message.forName("item-cut-clean-coal-setting")), false,
        Material.TORCH, "COAL_ORE", "DEEPSLATE_COAL_ORE"));
    registerSetting("gravel->flint",
      new ConvertDropSubSetting(() -> new ItemBuilder(Material.FLINT, Message.forName("item-cut-clean-flint-setting")), false,
        Material.FLINT, "GRAVEL"));
    registerSetting("ore->veins",
      new BreakOreVeinsSubSetting(() -> new ItemBuilder(Material.GOLDEN_PICKAXE, Message.forName("item-cut-clean-vein-setting")), 1, 10));
    registerSetting("items->inventory",
      new DirectIntoInventorySubSetting(() -> new ItemBuilder(Material.CHEST, Message.forName("item-cut-clean-inventory-setting"))));
    registerSetting("row->cooked",
      new CookFoodSubSetting(() -> new ItemBuilder(Material.COOKED_BEEF, Message.forName("item-cut-clean-food-setting")), true));
  }

  @Nonnull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.IRON_AXE, Message.forName("item-cut-clean-setting"));
  }

  protected boolean directIntoInventory() {
    return getSetting("items->inventory").getAsBoolean();
  }

  private class DirectIntoInventorySubSetting extends BooleanSubSetting {

    public DirectIntoInventorySubSetting(@Nonnull Supplier<ItemBuilder> item) {
      super(item);
    }

    @Nonnull
    @Override
    public BooleanSubSetting setEnabled(boolean enabled) {
      return super.setEnabled(enabled);
    }

  }

  private class ConvertDropSubSetting extends BooleanSubSetting {

    protected final Material[] from;
    protected final Material to;

    public ConvertDropSubSetting(@Nonnull Supplier<ItemBuilder> item, boolean enabledByDefault,
                                 @Nonnull Material to, @Nonnull String... from) {
      super(item, enabledByDefault);

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
    public void onBlockBreak(@Nonnull BlockBreakEvent event) {
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

    public BreakOreVeinsSubSetting(@Nonnull Supplier<ItemBuilder> item, int min, int max) {
      super(item, min, max);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(@Nonnull BlockBreakEvent event) {
      if (!shouldExecuteEffect()) return;
      ItemStack itemInMainHand = event.getPlayer().getInventory().getItemInMainHand();
      if (!canBeBroken(event.getBlock(), itemInMainHand)) return;
      if (!event.getBlock().getType().name().contains("ORE")) return;

      breakBlockVein(event.getPlayer(), event.getBlock(), itemInMainHand);
    }

    private void breakBlockVein(@Nonnull Player player, @Nonnull Block block, @Nonnull ItemStack tool) {
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

    private boolean canBeBroken(@Nonnull Block block, @Nonnull ItemStack tool) {
      return !block.getDrops(tool).isEmpty();
    }

    @Nonnull
    @Override
    public ItemBuilder getSettingsItem() {
      return getAsBoolean() ? DefaultItem.value(getValue(), "§7Max Vein Size: §e") : DefaultItem.disabled();
    }

  }

  private class CookFoodSubSetting extends BooleanSubSetting {

    public CookFoodSubSetting(@Nonnull Supplier<ItemBuilder> item, boolean enabledByDefault) {
      super(item, enabledByDefault);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityKill(@Nonnull EntityDeathEvent event) {
      if (!isEnabled()) return;
      event.getDrops().replaceAll(item -> new ItemBuilder(ItemUtils.convertFoodToCookedFood(item.getType())).amount(item.getAmount()).build());

      Player killer = event.getEntity().getKiller();
      if (killer != null && directIntoInventory()) {
        event.getDrops().forEach(itemStack -> InventoryUtils.dropOrGiveItem(killer.getInventory(), killer.getLocation(), itemStack));
        event.getDrops().clear();
      }

    }

  }

}
