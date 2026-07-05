package net.codingarea.challenges.plugin.challenges.type.abstraction;

import lombok.Getter;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.IChallenge;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.management.server.scoreboard.ChallengeActionBar;
import net.codingarea.challenges.plugin.management.server.scoreboard.ChallengeBossBar;
import net.codingarea.challenges.plugin.management.server.scoreboard.ChallengeScoreboard;
import net.codingarea.challenges.plugin.utils.item.DefaultItems;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.commons.common.collection.IRandom;
import net.codingarea.commons.common.config.Document;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
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
  protected final SettingCategory category;
  @Getter
  protected final ChallengeBossBar bossbar = new ChallengeBossBar();
  @Getter
  protected final ChallengeScoreboard scoreboard = new ChallengeScoreboard();
  @Getter
  protected final ChallengeActionBar actionbar = new ChallengeActionBar();
  @Getter
  protected ItemStack displayItemPreset;

  @Getter
  private final String nameMessageKey;
  private String uniqueName;

  public AbstractChallenge(@NotNull MenuType menu, @Nullable SettingCategory category,
                           @NotNull ItemStack displayItemPreset, @NotNull String nameMessageKey) {
    this.menu = menu;
    this.category = category;
    this.displayItemPreset = displayItemPreset;
    this.nameMessageKey = nameMessageKey;
    firstInstanceByClass.put(this.getClass(), this);
  }

  @NotNull
  public static <C extends AbstractChallenge> C getFirstInstance(@NotNull Class<C> classOfChallenge) {
    return classOfChallenge.cast(firstInstanceByClass.get(classOfChallenge));
  }

  // TODO extract
  public static void broadcast(@NotNull Consumer<? super Player> action) {
    Bukkit.getOnlinePlayers().forEach(action);
  }

  // TODO extract
  public static void broadcastFiltered(@NotNull Consumer<? super Player> action) {
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (ignorePlayer(player)) continue;
      action.accept(player);
    }
  }

  // TODO extract
  public static void broadcastIgnored(@NotNull Consumer<? super Player> action) {
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (!ignorePlayer(player)) continue;
      action.accept(player);
    }
  }

  // TODO extract
  @CheckReturnValue
  public static boolean ignorePlayer(@NotNull Player player) {
    return ignoreGameMode(player.getGameMode());
  }

  // TODO extract
  @CheckReturnValue
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
  public ItemBuilder getDisplayItem(@NotNull Locale locale) {
    return new ItemBuilder(locale, displayItemPreset, MessageKey.of("challenge.display-format"),
      getChallengeName(), getChallengeDescription());
  }

  /**
   * @implSpec If overridden, use {@link MessageKey#withArgs(Object...)} for placeholders
   */
  @NotNull
  @Override
  public LocalizableMessage getChallengeName() {
    return getChallengeMessageKey("name");
  }

  /**
   * @implSpec If overridden, use {@link MessageKey#withArgs(Object...)} for placeholders
   */
  @NotNull
  @Override
  public LocalizableMessage getChallengeDescription() {
    return getChallengeMessageKey("desc");
  }

  @NotNull
  protected MessageKey getChallengeMessageKey(@NotNull String keySuffix) {
    return MessageKey.of("challenge." + nameMessageKey + "." + keySuffix);
  }

  @NotNull
  @Override
  public ItemBuilder getSettingsItem(@NotNull Locale locale) {
    ItemStack preset = isEnabled() ? getSettingsItemPreset() : getDisabledSettingsItemPreset();
    // apply formatting dynamically, to prevent duplicate format references
    ItemBuilder item = new ItemBuilder(locale, preset, MessageKey.of("challenge.settings-format"),
      isEnabled() ? getSettingsName() : MessageKey.of("disabled")); // no need to override disabled name/item

    LocalizableMessage description = getSettingsDescription();
    if (description != null && isEnabled()) {
      item.appendLore(MessageKey.of("challenge.settings-format-lore"), description);
    }

    return item;
  }

  @NotNull
  protected ItemStack getDisabledSettingsItemPreset() {
    return DefaultItems.createDisabledPreset();
  }

  /**
   * @implNote Only used if {@link #isEnabled()}.
   *           Name/Lore will be overwritten by formatting.
   *           Set name in {@link #getSettingsName()} and lore in {@link #getSettingsDescription()}
   */
  @NotNull
  public ItemStack getSettingsItemPreset() {
    return DefaultItems.createEnabledPreset();
  }

  /**
   * @implNote Only used if {@link #isEnabled()}, format will be applied dynamically
   * @implSpec Should ideally either return a {@link LocalizableMessage}, {@link MessageKey}
   *           or {@link net.kyori.adventure.text.Component} for dynamic (e.g. translatable) styling
   */
  @NotNull
  public Object getSettingsName() {
    return MessageKey.of("generic.enabled");
  }

  /**
   * @implNote Only used if {@link #isEnabled()}, format will be applied dynamically
   */
  @Nullable
  public LocalizableMessage getSettingsDescription() {
    return null;
  }

  /**
   * @implSpec override {@link #getUniqueName()} instead
   */
  @NotNull
  @Override
  public final String getUniqueGamestateName() {
    return getUniqueName();
  }

  @NotNull
  @Override
  public String getUniqueName() {
    return uniqueName != null ? uniqueName : (uniqueName = getClass().getSimpleName().toLowerCase()
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
