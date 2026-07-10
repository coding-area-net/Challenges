package net.codingarea.challenges.plugin.challenges.implementation.challenge.randomizer;

import net.codingarea.challenges.plugin.challenges.type.abstraction.TimedChallenge;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.utils.misc.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@Since("2.0")
public class RandomItemRemovingChallenge extends TimedChallenge {

  public RandomItemRemovingChallenge() {
    super(MenuType.CHALLENGES, SettingCategory.RANDOMIZER, 1, 30, 30, new ItemStack(Material.DROPPER), "random-item-removing");
  }

  @Nullable
  @Override
  public LocalizableMessage getSettingsDescription() {
    return ChallengeHelper.getSettingsDescriptionIntervalSeconds(getValue());
  }

  @Override
  protected int getSecondsUntilNextActivation() {
    return getValue();
  }

  @Override
  protected void onTimeActivation() {
    restartTimer();

    for (Player player : Bukkit.getOnlinePlayers()) {
      if (ignorePlayer(player)) continue;
      if (player.getInventory().getContents().length == 0) continue;

      Bukkit.getScheduler().runTask(plugin, () -> {
        InventoryUtils.removeRandomItem(player.getInventory());
      });
    }
  }

}
