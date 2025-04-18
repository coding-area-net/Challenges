package net.codingarea.challenges.plugin.management.menu;

import net.codingarea.challenges.plugin.content.Message;

import javax.annotation.Nonnull;

public final class InventoryTitleManager {

	private InventoryTitleManager() {
	}

	@Nonnull
	public static String getTitle(@Nonnull String name) {
		return "§8» " + Message.forName("inventory-color").asString() + name;
	}

	@Nonnull
	public static String getMainMenuTitle() {
		return getTitle(Message.forName("menu-title").asString());
	}

	@Nonnull
	public static String getTitle(@Nonnull MenuType menu, int page) {
		return getTitle(menu.getName(), String.valueOf(page + 1));
	}

	@Nonnull
	public static String getTitle(@Nonnull MenuType menu, String... sub) {
		return getTitle(menu.getName(), sub);
	}

	@Nonnull
	public static String getTitle(@Nonnull String menu, String... sub) {
		String name = menu;
		for (String s : sub) {
			name += getTitleSplitter() + s;
		}
		return getTitle(name);
	}

	@Nonnull
	public static String getTitleSplitter() {
		return " §8┃ " + Message.forName("inventory-color").asString();
	}

	@Nonnull
	public static String getMenuSettingTitle(@Nonnull MenuType menu, @Nonnull String name, int page, boolean showPages) {
		return getTitle(menu.getName() + getTitleSplitter() + name + (showPages && false ? " §8• " + Message.forName("inventory-color") + (page + 1) : ""));
	}

	@Nonnull
	public static String getStatsTitle(@Nonnull String playerName) {
		return getTitle("§2Stats §8┃ §2" + playerName);
	}

	@Nonnull
	public static String getLeaderboardTitle() {
		return getTitle("§2Leaderboard");
	}

	@Nonnull
	public static String getLeaderboardTitle(@Nonnull String name, int page) {
		return getTitle("§2" + name + " §8┃ §2" + page);
	}

}
