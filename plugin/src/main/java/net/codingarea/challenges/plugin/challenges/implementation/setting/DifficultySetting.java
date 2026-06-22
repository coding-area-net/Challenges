package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Modifier;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.i18n.Prefix;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.bukkit.command.SenderCommand;
import net.codingarea.challenges.plugin.utils.bukkit.misc.BukkitStringUtils;
import net.codingarea.challenges.plugin.utils.item.DefaultItem;
import net.codingarea.challenges.plugin.utils.item.LegacyItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.MinecraftNameWrapper;
import net.codingarea.commons.common.config.Document;
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
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DifficultySetting extends Modifier implements SenderCommand, TabCompleter {

  public DifficultySetting() {
    super(MenuType.SETTINGS, null, 0, 3, 2, new ItemStack(Material.GLISTERING_MELON_SLICE), "difficulty");
  }

  @NotNull
  @Override
  public ItemStack getSettingsItemPreset() {
    switch (getValue()) {
      case 0:
        return DefaultItem.create(Material.LIME_DYE, getDifficultyName()).build();
      case 1:
        return DefaultItem.create(MinecraftNameWrapper.GREEN_DYE, getDifficultyName()).build();
      case 2:
        return DefaultItem.create(Material.ORANGE_DYE, getDifficultyName()).build();
      default:
        return DefaultItem.create(MinecraftNameWrapper.RED_DYE, getDifficultyName()).build();
    }
  }

  @Override
  protected void onValueChange() {
    setDifficulty(getDifficultyByValue(getValue()));
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

  @NotNull
  private Difficulty getCurrentDifficulty() {
    return Bukkit.getWorlds().isEmpty() ? Difficulty.NORMAL : ChallengeAPI.getGameWorld(Environment.NORMAL)
      .getDifficulty();
  }

  @NotNull
  private Difficulty getDifficultyByValue(int value) {
    Difficulty difficulty = Difficulty.values()[value];
    return difficulty == null ? Difficulty.NORMAL : difficulty;
  }

  @Override
  public void loadSettings(@NotNull Document document) {
    if (!document.contains("value"))
      setValue(getCurrentDifficulty().ordinal());

    super.loadSettings(document);
  }

  @Override
  public void onCommand(@NotNull CommandSender sender, @NotNull String[] args) throws Exception {

    if (args.length == 0) {
      MessageKey.of("command-difficulty-current").send(sender, Prefix.CHALLENGES, getDifficultyComponent());
      return;
    }

    int difficulty = getDifficultyValue(args[0]);
    if (difficulty == -1) {
      MessageKey.of("syntax").send(sender, Prefix.CHALLENGES, "difficulty <difficulty>");
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
  public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
    if (args.length > 1) return new ArrayList<>();
    return Arrays.asList("peaceful", "easy", "normal", "hard");
  }

  private int getDifficultyValue(@NotNull String input) {

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
