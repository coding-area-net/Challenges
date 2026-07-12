package net.codingarea.challenges.plugin.challenges.custom.settings.action.impl;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.custom.settings.action.PlayerTargetAction;
import net.codingarea.challenges.plugin.challenges.type.helper.SubSettingsHelper;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.content.i18n.Prefix;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class ExecuteCommandAction extends PlayerTargetAction {

  // Static because cannot be accessed before super has been called
  public static int maxCommandLength = Challenges.getInstance().getConfigDocument().getInt("custom-challenge-settings.max-command-length");
  private static List<String> commandsThatCanBeExecuted = Challenges.getInstance().getConfigDocument().getStringList("custom-challenge-settings.allowed-commands-to-execute");

  public ExecuteCommandAction(String name) {
    super(name, SubSettingsHelper.createEntityTargetSettingsBuilder(true, true, true).createTextInputChild("command", player -> {
      MessageKey.of("custom-command-info").send(player, Prefix.CUSTOM, "/" + String.join(" /", commandsThatCanBeExecuted));
    }, (player, input) -> {
      String cmd = input.split(" ")[0].toLowerCase();

      if (!commandsThatCanBeExecuted.contains(cmd)) {
        MessageKey.of("custom-command-not-allowed").send(player, Prefix.CUSTOM, cmd);
        return false;
      }

      if (input.length() > maxCommandLength) {
        MessageKey.of("custom-chars-max_length").send(player, Prefix.CUSTOM, maxCommandLength);
        return false;
      }
      return true;
    }));
    // Reload from config on reload
    maxCommandLength = Challenges.getInstance().getConfigDocument().getInt("custom-challenge-settings.max-command-length");
    commandsThatCanBeExecuted = Challenges.getInstance().getConfigDocument().getStringList("custom-challenge-settings.allowed-commands-to-execute");
  }

  @Override
  public void executeForPlayer(Player player, Map<String, String[]> subActions) {
    String fullCommand = subActions.get("command")[0];

    CommandSender sender = Bukkit.getConsoleSender();
    if (player != null) {
      fullCommand = "execute as " + player.getName() + " at " + player.getName() + " run " + fullCommand;
    }

    Bukkit.getServer().dispatchCommand(sender, fullCommand);
  }

  @Override
  public Material getMaterial() {
    return Material.COMMAND_BLOCK;
  }
}
