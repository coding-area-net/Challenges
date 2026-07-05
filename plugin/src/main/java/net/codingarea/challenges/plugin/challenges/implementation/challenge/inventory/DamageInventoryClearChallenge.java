package net.codingarea.challenges.plugin.challenges.implementation.challenge.inventory;

import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifier;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.legacy.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class DamageInventoryClearChallenge extends SettingModifier {

  public DamageInventoryClearChallenge() {
    super(MenuType.CHALLENGES, SettingCategory.INVENTORY, 1, 2, new ItemStack(Material.CHEST), "damage-inv-clear");
  }

  @EventHandler
  public void onDamage(@NotNull EntityDamageEvent event) {
    if (!(event.getEntity() instanceof Player)) return;
    if (!shouldExecuteEffect()) return;
    if (ChallengeHelper.finalDamageIsNull(event)) return;

    if (getValue() == 1) {
      Bukkit.getOnlinePlayers().forEach(player -> player.getInventory().clear());
    } else {
      Player player = (Player) event.getEntity();
      player.getInventory().clear();
    }
  }

//  @NotNull
//  @Override
//  public LegacyItemBuilder createSettingsItem() {
//    if (getValue() == 1) {
//      return DefaultItem.create(Material.ENDER_CHEST, Message.forName("everyone"));
//    } else {
//      return DefaultItem.create(Material.PLAYER_HEAD, Message.forName("player"));
//    }
//  }

  @Override
  public void playValueChangeTitle() {
    ChallengeHelper.playChallengeValueTitle(this, getValue() == 0 ? Message.forName("everyone").asString() : Message.forName("player").asString());
  }

}
