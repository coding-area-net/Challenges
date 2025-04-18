package net.codingarea.challenges.plugin.challenges.custom.settings.action.impl;

import net.codingarea.challenges.plugin.challenges.custom.settings.action.PlayerTargetAction;
import net.codingarea.challenges.plugin.challenges.type.helper.SubSettingsHelper;
import net.codingarea.challenges.plugin.utils.item.DefaultItem;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Map;

public class HungerPlayerAction extends PlayerTargetAction {

  public HungerPlayerAction(String name) {
    super(name, SubSettingsHelper.createEntityTargetSettingsBuilder(false, true)
      .createChooseItemChild("amount").fill(builder -> {
        String prefix = DefaultItem.getItemPrefix();

        for (int i = 1; i < 21; i++) {
          builder.addSetting(
            String.valueOf(i), new ItemBuilder(Material.ROTTEN_FLESH, prefix + "§7" + i).setAmount(i).build());
        }

      }));
  }

  @Override
  public Material getMaterial() {
    return Material.ROTTEN_FLESH;
  }

  @Override
  public void executeForPlayer(Player player, Map<String, String[]> subActions) {
    int newFoodLevel = player.getFoodLevel() - Integer.parseInt(subActions.get("amount")[0]);
    player.setFoodLevel(Math.max(newFoodLevel, 0));
  }

}
