package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.anweisen.utilities.common.config.Document;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Modifier;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.bukkit.command.SenderCommand;
import net.codingarea.challenges.plugin.utils.bukkit.misc.BukkitStringUtils;
import net.codingarea.challenges.plugin.utils.item.DefaultItem;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.MinecraftNameWrapper;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DifficultySetting extends Modifier implements SenderCommand, TabCompleter {

	public DifficultySetting() {
		super(MenuType.SETTINGS, 0, 3, 2);
	}

	@Override
	protected void onValueChange() {
		setDifficulty(getDifficultyByValue(getValue()));
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.GLISTERING_MELON_SLICE, Message.forName("item-difficulty-setting"));
	}

	@Nonnull
	@Override
	public ItemBuilder createSettingsItem() {
		switch (getValue()) {
			case 0:
				return DefaultItem.create(Material.LIME_DYE, getDifficultyName());
			case 1:
				return DefaultItem.create(MinecraftNameWrapper.GREEN_DYE, getDifficultyName());
			case 2:
				return DefaultItem.create(Material.ORANGE_DYE, getDifficultyName());
			default:
				return DefaultItem.create(MinecraftNameWrapper.RED_DYE, getDifficultyName());
		}
	}

	private String getDifficultyName() {
		return getDifficultyComponent().toLegacyText();
	}

	@Override
	public void playValueChangeTitle() {
		ChallengeHelper.playChangeChallengeValueTitle(this, getDifficultyName());
	}

	private void setDifficulty(Difficulty difficulty) {
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "minecraft:difficulty " + difficulty.name().toLowerCase());
		for (World world : Bukkit.getWorlds()) {
			world.setDifficulty(difficulty);
		}
	}

	@Nonnull
	private Difficulty getCurrentDifficulty() {
		return Bukkit.getWorlds().isEmpty() ? Difficulty.NORMAL : ChallengeAPI.getGameWorld(Environment.NORMAL)
				.getDifficulty();
	}

	@Nonnull
	private Difficulty getDifficultyByValue(int value) {
		Difficulty difficulty = Difficulty.values()[value];
		return difficulty == null ? Difficulty.NORMAL : difficulty;
	}

	@Override
	public void loadSettings(@Nonnull Document document) {
		if (!document.contains("value"))
			setValue(getCurrentDifficulty().ordinal());

		super.loadSettings(document);
	}

	@Override
	public void onCommand(@Nonnull CommandSender sender, @Nonnull String[] args) throws Exception {

		if (args.length == 0) {
			Message.forName("command-difficulty-current").send(sender, Prefix.CHALLENGES, getDifficultyComponent());
			return;
		}

		int difficulty = getDifficultyValue(args[0]);
		if (difficulty == -1) {
			Message.forName("syntax").send(sender, Prefix.CHALLENGES, "difficulty <difficulty>");
			return;
		}

		setValue(difficulty);
		Message.forName("command-difficulty-change").broadcast(Prefix.CHALLENGES, getDifficultyComponent());

	}

	private BaseComponent getDifficultyComponent() {
		TranslatableComponent name = BukkitStringUtils.getDifficultyName(getDifficultyByValue(getValue()));
		switch (getValue()) {
			case 0:
				name.setColor(ChatColor.GREEN);
				break;
			case 1:
				name.setColor(ChatColor.DARK_GREEN);
				break;
			case 2:
				name.setColor(ChatColor.GOLD);
				break;
			default:
				name.setColor(ChatColor.RED);
				break;
		}
		return name;
	}

	@Nullable
	@Override
	public List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String alias, @Nonnull String[] args) {
		if (args.length > 1) return new ArrayList<>();
		return Arrays.asList("peaceful", "easy", "normal", "hard");
	}

	private int getDifficultyValue(@Nonnull String input) {

		switch (input.toLowerCase()) {
			case "peaceful":
				return 0;
			case "easy":
				return 1;
			case "normal":
				return 2;
			case "hard":
				return 3;
		}

		try {
			int value = Integer.parseInt(input);
			if (value < 0 || value > 3) return -1;
			return value;
		} catch (Exception ex) {
			return -1;
		}

	}

}
