package net.codingarea.challenges.plugin.spigot.command;

import com.google.common.collect.Lists;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.commons.common.config.Document;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.utils.bukkit.command.PlayerCommand;
import net.codingarea.challenges.plugin.utils.misc.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseCommand implements PlayerCommand, TabCompleter {

  private final Map<String, DatabaseCommandExecutor> databaseExecutors;
  private final boolean savePlayerConfigs;
  private final boolean savePlayerChallenges;

  public DatabaseCommand() {
    savePlayerConfigs = Challenges.getInstance().getConfigDocument().getBoolean("save-player-configs");
    savePlayerChallenges = Challenges.getInstance().getConfigDocument().getBoolean("save-player-custom_challenges");

    databaseExecutors = new HashMap<>();
    databaseExecutors.put("settings", new DatabaseCommandExecutor() {
      @Override
      public void save(Player player) throws Exception {
        if (checkFeatureDisabled(savePlayerConfigs, player)) return;
        Challenges.getInstance().getChallengeManager().saveSettings(player);
        Message.forName("player-config-loaded").send(player, Prefix.CHALLENGES);
      }

      @Override
      public void load(Player player) throws Exception {
        if (checkFeatureDisabled(savePlayerConfigs, player)) return;
        Document config = Challenges.getInstance().getDatabaseManager().getDatabase()
          .query("challenges")
          .select("config")
          .where("uuid", player.getUniqueId())
          .execute().firstOrEmpty().getDocument("config");
        Challenges.getInstance().getChallengeManager().loadSettings(config);
        Message.forName("player-config-loaded").send(player, Prefix.CHALLENGES);
      }

      @Override
      public void reset(Player player) throws Exception {
        if (checkFeatureDisabled(savePlayerConfigs, player)) return;
        Challenges.getInstance().getDatabaseManager().getDatabase().update("challenges")
          .where("uuid", player.getUniqueId())
          .set("config", null)
          .execute();
        Message.forName("player-config-reset").send(player, Prefix.CHALLENGES);
      }
    });
    databaseExecutors.put("customs", new DatabaseCommandExecutor() {

      @Override
      public void save(Player player) throws Exception {
        if (checkFeatureDisabled(savePlayerChallenges, player)) return;
        Challenges.getInstance().getChallengeManager().saveCustomChallenges(player);
        Message.forName("player-custom_challenges-saved").send(player, Prefix.CHALLENGES);
      }

      @Override
      public void load(Player player) throws Exception {
        if (checkFeatureDisabled(savePlayerChallenges, player)) return;
        Document config = Challenges.getInstance().getDatabaseManager().getDatabase()
          .query("challenges")
          .select("custom_challenges")
          .where("uuid", player.getUniqueId())
          .execute().firstOrEmpty().getDocument("custom_challenges");
        Challenges.getInstance().getChallengeManager().loadCustomChallenges(config);
        Message.forName("player-custom_challenges-loaded").send(player, Prefix.CHALLENGES);
      }

      @Override
      public void reset(Player player) throws Exception {
        if (checkFeatureDisabled(savePlayerChallenges, player)) return;
        Challenges.getInstance().getDatabaseManager().getDatabase().update("challenges")
          .where("uuid", player.getUniqueId())
          .set("custom_challenges", null)
          .execute();
        Message.forName("player-custom_challenges-reset").send(player, Prefix.CHALLENGES);
      }
    });
  }

  @Override
  public void onCommand(@NotNull Player player, @NotNull String[] args) throws Exception {

    if (checkFeatureDisabled(Challenges.getInstance().getDatabaseManager().isConnected(), player)) {
      return;
    }

    if (args.length != 2 || !databaseExecutors.containsKey(args[1])) {
      Message.forName("syntax").send(player, Prefix.CHALLENGES, "database <save/load/reset> <settings/customs>");
      return;
    }

    String type = args[1].toLowerCase();
    DatabaseCommandExecutor executor = databaseExecutors.get(type);

    switch (args[0].toLowerCase()) {
      case "save":
        executor.save(player);
        break;
      case "load":
        executor.load(player);
        break;
      case "reset":
        executor.reset(player);
        break;
      default:
        Message.forName("syntax").send(player, Prefix.CHALLENGES, "database <save/load/reset> <settings/customs>");
    }

  }

  /**
   * Checks for disabled features and sends a proper messages to indicate that
   */
  private boolean checkFeatureDisabled(boolean enabled, @Nonnull Player player) {
    if (!enabled) {
      Message.forName("feature-disabled").send(player, Prefix.CHALLENGES);
      SoundSample.BASS_OFF.play(player);
      return true;
    }
    return false;
  }

  @Nullable
  @Override
  public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                    @NotNull String alias, @NotNull String[] args) {

    if (args.length <= 1) {
      return Utils.filterRecommendations(args[0], "save", "load", "reset");
    } else if (args.length == 2) {
      return Utils.filterRecommendations(args[1], databaseExecutors.keySet().toArray(new String[0]));
    }
    return Lists.newLinkedList();
  }


  private interface DatabaseCommandExecutor {
    void save(Player player) throws Exception;

    void load(Player player) throws Exception;

    void reset(Player player) throws Exception;
  }

}
