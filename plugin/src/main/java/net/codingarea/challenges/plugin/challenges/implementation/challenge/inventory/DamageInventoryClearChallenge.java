package net.codingarea.challenges.plugin.challenges.implementation.challenge.inventory;

import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifier;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.DefaultItem;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import javax.annotation.Nonnull;

public class DamageInventoryClearChallenge extends SettingModifier {

  public DamageInventoryClearChallenge() {
    super(MenuType.CHALLENGES, 1, 2);
    setCategory(SettingCategory.INVENTORY);
  }

  @EventHandler
  public void onDamage(@Nonnull EntityDamageEvent event) {
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

  @Nonnull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.CHEST, Message.forName("item-damage-inv-clear-challenge"));
  }

  @Nonnull
  @Override
  public ItemBuilder createSettingsItem() {
    if (getValue() == 1) {
      return DefaultItem.create(Material.ENDER_CHEST, Message.forName("everyone"));
    } else {
      return DefaultItem.create(Material.PLAYER_HEAD, Message.forName("player"));
    }
  }

  @Override
  public void playValueChangeTitle() {
    ChallengeHelper.playChangeChallengeValueTitle(this, getValue() == 0 ? Message.forName("everyone").asString() : Message.forName("player").asString());
  }

}
