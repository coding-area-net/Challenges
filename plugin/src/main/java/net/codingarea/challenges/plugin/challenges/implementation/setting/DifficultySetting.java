package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Modifier;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.content.i18n.Prefix;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.bukkit.command.SenderCommand;
import net.codingarea.challenges.plugin.utils.misc.MinecraftNameWrapper;
import net.codingarea.commons.common.config.Document;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
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
    return switch (getDifficultyByValue(getValue())) {
      case PEACEFUL -> new ItemStack(Material.LIME_DYE);
      case EASY -> new ItemStack(MinecraftNameWrapper.GREEN_DYE);
      case NORMAL -> new ItemStack(Material.ORANGE_DYE);
      case HARD -> new ItemStack(MinecraftNameWrapper.RED_DYE);
    };
  }

  @NotNull
  @Override
  public Component getSettingsName() {
    return getDifficultyComponent();
  }

  @Override
  protected void onValueChange() {
    setDifficulty(getDifficultyByValue(getValue()));
  }

  @Override
  public void playValueChangeTitle() {
    ChallengeHelper.playChallengeValueTitle(this, getDifficultyComponent());
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
      getChallengeMessageKey("command-current").send(sender, Prefix.CHALLENGES, getDifficultyComponent());
      return;
    }

    int difficulty = getDifficultyValue(args[0]);
    if (difficulty == -1) {
      MessageKey.of("syntax").send(sender, Prefix.CHALLENGES, "difficulty <difficulty>");
      return;
    }

    setValue(difficulty);
    getChallengeMessageKey("command-set").broadcast(Prefix.CHALLENGES, getDifficultyComponent());
    playValueChangeTitle();
  }

  @NotNull
  private Component getDifficultyComponent() {
    Difficulty difficulty = getDifficultyByValue(getValue());
    return Component.translatable(difficulty).color(getDifficultyColor(difficulty));
  }

  @NotNull
  private TextColor getDifficultyColor(@NotNull Difficulty difficulty) {
    return switch (difficulty) {
      case PEACEFUL -> NamedTextColor.DARK_GREEN;
      case EASY -> NamedTextColor.GREEN;
      case NORMAL -> NamedTextColor.GOLD;
      case HARD -> NamedTextColor.RED;
    };
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
