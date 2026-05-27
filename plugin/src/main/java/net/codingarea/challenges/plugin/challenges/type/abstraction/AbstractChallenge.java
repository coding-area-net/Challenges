package net.codingarea.challenges.plugin.challenges.type.abstraction;

import lombok.Getter;
import lombok.Setter;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.IChallenge;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.management.server.scoreboard.ChallengeBossBar;
import net.codingarea.challenges.plugin.management.server.scoreboard.ChallengeScoreboard;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.commons.common.annotations.DeprecatedSince;
import net.codingarea.commons.common.collection.IRandom;
import net.codingarea.commons.common.config.Document;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class AbstractChallenge implements IChallenge, Listener {

  protected static final Challenges plugin = Challenges.getInstance();
  protected static final IRandom globalRandom = IRandom.create();

  private static final Map<Class<? extends AbstractChallenge>, AbstractChallenge> firstInstanceByClass = new HashMap<>();
  @Getter
  private static final boolean ignoreCreativePlayers, ignoreSpectatorPlayers;

  static {
    Document ignoreDocument = Challenges.getInstance().getConfigDocument().getDocument("ignore-players");
    ignoreCreativePlayers = ignoreDocument.getBoolean("creative");
    ignoreSpectatorPlayers = ignoreDocument.getBoolean("spectator");
  }

  protected final MenuType menu;
  @Getter
  protected final ChallengeBossBar bossbar = new ChallengeBossBar();
  @Getter
  protected final ChallengeScoreboard scoreboard = new ChallengeScoreboard();
  @Setter
  protected SettingCategory category;
  private String name;
  private ItemStack cachedDisplayItem;

  public AbstractChallenge(@NotNull MenuType menu) {
    this.menu = menu;
    firstInstanceByClass.put(this.getClass(), this);
  }

  @NotNull
  public static <C extends AbstractChallenge> C getFirstInstance(@NotNull Class<C> classOfChallenge) {
    return classOfChallenge.cast(firstInstanceByClass.get(classOfChallenge));
  }

  public static void broadcast(@NotNull Consumer<? super Player> action) {
    Bukkit.getOnlinePlayers().forEach(action);
  }

  public static void broadcastFiltered(@NotNull Consumer<? super Player> action) {
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (ignorePlayer(player)) continue;
      action.accept(player);
    }
  }

  public static void broadcastIgnored(@NotNull Consumer<? super Player> action) {
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (!ignorePlayer(player)) continue;
      action.accept(player);
    }
  }

  public static boolean ignorePlayer(@NotNull Player player) {
    return ignoreGameMode(player.getGameMode());
  }

  public static boolean ignoreGameMode(@NotNull GameMode gameMode) {
    return (isIgnoreSpectatorPlayers() && gameMode == GameMode.SPECTATOR) || (isIgnoreCreativePlayers() && gameMode == GameMode.CREATIVE);
  }

  @NotNull
  @Override
  public final MenuType getType() {
    return menu;
  }

  @Override
  public SettingCategory getCategory() {
    return category;
  }

  protected final void updateItems() {
    ChallengeHelper.updateItems(this);
  }

  @NotNull
  @Override
  public ItemStack getDisplayItem() {
    if (cachedDisplayItem != null) return cachedDisplayItem.clone();
    cachedDisplayItem = createDisplayItem().build();
    return cachedDisplayItem.clone();
  }

  @NotNull
  @Override
  public ItemStack getSettingsItem() {
    ItemBuilder item = createSettingsItem();
    String[] description = getSettingsDescription();
    if (description != null && isEnabled()) {
      item.appendLore(" ");
      item.appendLore(description);
    }

    return item.build();
  }

  @Nullable
  protected String[] getSettingsDescription() {
    return null;
  }

  @NotNull
  public abstract ItemBuilder createDisplayItem();

  @NotNull
  public abstract ItemBuilder createSettingsItem();

  @NotNull
  @Override
  public String getUniqueGamestateName() {
    return getUniqueName();
  }

  @NotNull
  @Override
  public String getUniqueName() {
    return name != null ? name : (name = getClass().getSimpleName().toLowerCase()
      .replace("setting", "")
      .replace("challenge", "")
      .replace("modifier", "")
      .replace("goal", "")
    );
  }

  @Override
  public void handleShutdown() {
  }

  @Override
  public void writeGameState(@NotNull Document document) {
  }

  @Override
  public void loadGameState(@NotNull Document document) {
  }

  @Override
  public void writeSettings(@NotNull Document document) {
  }

  @Override
  public void loadSettings(@NotNull Document document) {
  }

  protected boolean shouldExecuteEffect() {
    return isEnabled() && ChallengeAPI.isStarted() && !ChallengeAPI.isWorldInUse();
  }

  /**
   * @deprecated Use {@link ChallengeHelper#kill(Player)}
   */
  @Deprecated
  @DeprecatedSince("2.1.0")
  public void kill(@NotNull Player player) {
    ChallengeHelper.kill(player);
  }

  /**
   * @deprecated Use {@link ChallengeHelper#kill(Player, int)}
   */
  @Deprecated
  @DeprecatedSince("2.1.0")
  public void kill(@NotNull Player player, int delay) {
    ChallengeHelper.kill(player, delay);

  }

  @NotNull
  protected final Document getGameStateData() {
    return plugin.getConfigManager().getGamestateConfig().getDocument(this.getUniqueGamestateName());
  }

  @NotNull
  protected final Document getPlayerData(@NotNull UUID player) {
    return getGameStateData().getDocument("player").getDocument(player.toString());
  }

  @NotNull
  protected final Document getPlayerData(@NotNull Player player) {
    return getPlayerData(player.getUniqueId());
  }

}
