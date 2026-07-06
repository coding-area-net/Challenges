package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.content.i18n.Prefix;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.bukkit.command.PlayerCommand;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Since("2.0")
public class EnderChestCommandSetting extends Setting implements PlayerCommand {

  public EnderChestCommandSetting() {
    super(MenuType.SETTINGS, null, new ItemStack(Material.ENDER_CHEST), "enderchest-command");
  }

  @Override
  public void onCommand(@NotNull Player player, @NotNull String[] args) throws Exception {
    if (!isEnabled() || ChallengeAPI.isWorldInUse()) {
      MessageKey.of("feature-disabled").send(player, Prefix.CHALLENGES);
      return;
    }

    player.openInventory(player.getEnderChest());
    MessageKey.of("command-enderchest-open").send(player, Prefix.CHALLENGES);
  }

}
