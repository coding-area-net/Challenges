package net.codingarea.challenges.plugin.spigot.command;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.i18n.Prefix;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.content.loader.LanguageLoader;
import net.codingarea.challenges.plugin.utils.bukkit.command.Completer;
import net.codingarea.challenges.plugin.utils.bukkit.command.SenderCommand;
import net.codingarea.challenges.plugin.utils.misc.Utils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LanguageCommand implements SenderCommand, Completer {

  @Override
  public void onCommand(@NotNull CommandSender sender, @NotNull String[] args) throws Exception {
    if (args.length < 1) {
      MessageKey.of("syntax").send(sender, Prefix.CHALLENGES, "setlang <language>");
      return;
    }
    switch (args[0].toLowerCase()) {
      case "german":
      case "deutsch":
      case "de":
        Challenges.getInstance().getLoaderRegistry().findLoaderByClassOrThrow(LanguageLoader.class).changeLanguage("de");
        break;
      case "english":
      case "englisch":
      case "en":
        Challenges.getInstance().getLoaderRegistry().findLoaderByClassOrThrow(LanguageLoader.class).changeLanguage("en");
        break;
      default:
        MessageKey.of("unsuported-language").send(sender, Prefix.CHALLENGES, args[0]);
    }
  }

  @Override
  public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
    return Utils.filterRecommendations(args[0], "german", "english");
  }

}
