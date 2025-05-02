package net.codingarea.challenges.plugin.spigot.listener;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.content.loader.LanguageLoader;
import net.codingarea.challenges.plugin.utils.misc.FontUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class HelpListener implements Listener {

  @EventHandler(priority = EventPriority.MONITOR)
  public void onCommand(@Nonnull PlayerCommandPreprocessEvent event) {

    String message = event.getMessage().toLowerCase();
    if (message.isEmpty()) return;

    String[] args = message.substring(1).trim().split(" ");
    String commandName = args[0];
    if (commandName.contains(":")) {
      commandName = commandName.substring(commandName.indexOf(':') + 1);
    }

    PluginCommand command = Challenges.getInstance().getCommand("help");
    if (command == null) return;
    List<String> names = new ArrayList<>(command.getAliases());
    names.add("help");

    if (!names.contains(commandName)) return;

    Player sender = event.getPlayer();

    sendMessage(sender, "§7This server is running §e§lChallenges §ev" + Challenges.getInstance().getVersion());
    sendMessage(sender, "");
    sendMessage(sender, "§7Made by §eCodingArea §8(§eanweisen & KxmischesDomi§8)");
    sendMessage(sender, "§7Visit the source at §egithub.com/anweisen/Challenges");
    sendMessage(sender, "§7Download at §espigotmc.org/resources/80548");
    sendMessage(sender, "§7For more join our discord §ediscord.gg/74Ay5zF");

  }

  public void sendMessage(CommandSender sender, String msg) {
    LanguageLoader languageLoader = Challenges.getInstance().getLoaderRegistry().getFirstLoaderByClass(LanguageLoader.class);
    if (languageLoader != null && languageLoader.isSmallCapsFont()) {
      msg = FontUtils.toSmallCaps(msg);
    }
    sender.sendMessage(Prefix.CHALLENGES + msg);

  }

}
