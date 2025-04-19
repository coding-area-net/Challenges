package net.codingarea.challenges.plugin.challenges.implementation.challenge.entities;

import net.codingarea.commons.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.custom.settings.action.impl.RandomMobAction;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Objects;

@Since("2.1.3")
public class BlockMobsChallenge extends Setting {

  public BlockMobsChallenge() {
    super(MenuType.CHALLENGES);
    setCategory(SettingCategory.ENTITIES);
  }

  @NotNull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.BRICKS, Message.forName("item-block-mob-challenge"));
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onBlockBreak(BlockBreakEvent event) {
    if (!shouldExecuteEffect()) return;
    if (ignorePlayer(event.getPlayer())) return;
    EntityType type = globalRandom.choose(RandomMobAction.getLivingMobs());
    LivingEntity entity = (LivingEntity) event.getBlock().getWorld()
      .spawnEntity(event.getBlock().getLocation().add(0.5, 0, 0.5), type);

    Collection<ItemStack> drops = event.getBlock().getDrops(Objects.requireNonNull(event.getPlayer().getEquipment()).getItemInMainHand());

    int i = 0;
    // Put every stack in a different slot to make sure everything drops properly
    for (ItemStack drop : drops) {

      EquipmentSlot slot = EquipmentSlot.HEAD;
      EntityEquipment equipment = entity.getEquipment();

      switch (i) {
        case 0:
          assert equipment != null;
          equipment.setHelmetDropChance(1);
          break;
        case 1:
          slot = EquipmentSlot.CHEST;
          equipment.setChestplateDropChance(1);
          break;
        case 2:
          slot = EquipmentSlot.LEGS;
          equipment.setLeggingsDropChance(1);
          break;
        case 3:
          slot = EquipmentSlot.FEET;
          equipment.setBootsDropChance(1);
          break;
        case 4:
          slot = EquipmentSlot.HAND;
          equipment.setItemInMainHandDropChance(1);
          break;
        case 5:
          slot = EquipmentSlot.OFF_HAND;
          equipment.setItemInOffHandDropChance(1);
          break;
      }
      equipment.setItem(slot, drop);
      i++;
    }
    event.setDropItems(false);
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onEntityExplosion(EntityExplodeEvent event) {
    if (!shouldExecuteEffect()) return;
    for (Block block : event.blockList()) {
      block.setType(Material.AIR);
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onBlockExplosion(BlockExplodeEvent event) {
    if (!shouldExecuteEffect()) return;
    for (Block block : event.blockList()) {
      block.setType(Material.AIR);
    }
  }


}
