package net.codingarea.challenges.plugin.management.menu;

import net.codingarea.challenges.plugin.content.legacy.Message;
import org.jetbrains.annotations.NotNull;

@Deprecated
public final class InventoryTitleManager {

  private InventoryTitleManager() {
  }

  @NotNull
  public static String getTitle(@NotNull String name) {
    return "§8» " + Message.forName("inventory-color").asString() + name;
  }

  @NotNull
  public static String getMainMenuTitle() {
    return getTitle(Message.forName("menu-title").asString());
  }

  @NotNull
  public static String getTitle(@NotNull MenuType menu, int page) {
    return "getTitle(...)";
  }

  @NotNull
  public static String getTitle(@NotNull MenuType menu, String... sub) {
    return "getTitle(...)";
  }

  @NotNull
  public static String getTitle(@NotNull String menu, String... sub) {
    StringBuilder name = new StringBuilder(menu);
    for (String s : sub) {
      name.append(getTitleSplitter()).append(s);
    }
    return getTitle(name.toString());
  }

  @NotNull
  public static String getTitleSplitter() {
    return " §8┃ " + Message.forName("inventory-color").asString();
  }

  @NotNull
  public static String getMenuSettingTitle(@NotNull MenuType menu, @NotNull String name, int page, boolean showPages) {
    return getTitle("getMenuSettingTitle(...)" + getTitleSplitter() + name + (showPages ? " §8• " + Message.forName("inventory-color") + (page + 1) : ""));
  }

  @NotNull
  public static String getStatsTitle(@NotNull String playerName) {
    return getTitle("§2Stats §8┃ §2" + playerName);
  }

  @NotNull
  public static String getLeaderboardTitle() {
    return getTitle("§2Leaderboard");
  }

  @NotNull
  public static String getLeaderboardTitle(@NotNull String name, int page) {
    return getTitle("§2" + name + " §8┃ §2" + page);
  }

}
