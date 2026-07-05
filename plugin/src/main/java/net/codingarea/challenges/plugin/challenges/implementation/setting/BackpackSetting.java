package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifier;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeConfigHelper;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.legacy.Message;
import net.codingarea.challenges.plugin.content.i18n.Prefix;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.management.menu.InventoryTitleManager;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.bukkit.command.PlayerCommand;
import net.codingarea.challenges.plugin.utils.bukkit.container.BukkitSerialization;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.commons.common.config.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BackpackSetting extends SettingModifier implements PlayerCommand {

  public static final int SHARED = 1, PLAYER = 2;

  private final int size;
  private final Map<UUID, Inventory> backpacks = new HashMap<>();
  private final Inventory sharedBackpack;

  public BackpackSetting() {
    super(MenuType.SETTINGS, null, 1, 2, SHARED, new ItemStack(Material.CHEST), "backpack-setting");
    size = Math.clamp(ChallengeConfigHelper.getSettingsDocument().getInt("backpack-size") * 9L, 9, 6 * 9);
    sharedBackpack = createInventory("§5Team Backpack");
  }

  @NotNull
  @Override
  public ItemStack getSettingsItemPreset() {
    return super.getSettingsItemPreset();
  }

//  @NotNull
//  @Override
//  public LegacyItemBuilder createSettingsItem() {
//    if (getValue() == SHARED)
//      return DefaultItem.create(Material.ENDER_CHEST, Message.forName("item-backpack-setting-team"));
//    return DefaultItem.create(Material.PLAYER_HEAD, Message.forName("item-backpack-setting-player"));
//  }


  @Override
  public void playValueChangeTitle() {
    switch (getValue()) {
      case SHARED:
        ChallengeHelper.playChallengeValueTitle(this, Message.forName("item-backpack-setting-team"));
        break;
      case PLAYER:
        ChallengeHelper.playChallengeValueTitle(this, Message.forName("item-backpack-setting-player"));
        break;
      default:
        ChallengeHelper.playChallengeToggleTitle(this, false);
    }
  }

  @Override
  public void onCommand(@NotNull Player player, @NotNull String[] args) {
    if (ChallengeAPI.isPaused()) {
      MessageKey.of("timer-not-started").send(player, Prefix.BACKPACK);
      SoundSample.BASS_OFF.play(player);
      return;
    }

    if (!isEnabled()) {
      MessageKey.of("backpacks-disabled").send(player, Prefix.BACKPACK);
      SoundSample.BASS_OFF.play(player);
      return;
    }

    if (getValue() == SHARED || getValue() == PLAYER) {
      MessageKey.of("backpack-opened").send(player, Prefix.BACKPACK, getValue() == SHARED ? "§5Team Backpack" : "§6Player Backpack");
      player.openInventory(getCurrentBackpack(player));
      SoundSample.OPEN.play(player);
    } else {
      MessageKey.of("backpacks-disabled").send(player, Prefix.BACKPACK);
      SoundSample.BASS_OFF.play(player);
    }
  }

  @Override
  public void loadGameState(@NotNull Document document) {
    super.loadGameState(document);

    loadChecked(document, "shared", sharedBackpack);

    Document players = document.getDocument("players");
    for (String key : players.keys()) {
      loadChecked(players, key, backpacks.computeIfAbsent(UUID.fromString(key), k -> createInventory("§6Backpack")));
    }

  }

  protected void loadChecked(@NotNull Document document, @NotNull String key, @NotNull Inventory inventory) {

    if (document.isDocument(key)) {
      loadLegacy(document.getDocument(key), inventory);
    } else {
      try {
        String value = document.getString(key);
        if (value == null) return;
        BukkitSerialization.fromBase64(inventory, value);
      } catch (IOException exception) {
        Challenges.getInstance().getILogger().error("", exception);
      }
    }

  }

  protected void loadLegacy(@NotNull Document document, @NotNull Inventory inventory) {

    for (String key : document.keys()) {
      try {
        int index = Integer.parseInt(key);
        ItemStack item = document.getSerializable(key, ItemStack.class);
        inventory.setItem(index, item);
      } catch (Exception ignored) {
      }
    }
  }

  @Override
  public void writeGameState(@NotNull Document document) {
    super.writeGameState(document);

    write(document, "shared", sharedBackpack);

    Document players = document.getDocument("players");
    backpacks.forEach((uuid, inventory) -> {
      write(players, uuid.toString(), inventory);
    });
  }

  protected void write(@NotNull Document document, @NotNull String key, @NotNull Inventory inventory) {
    document.set(key, BukkitSerialization.toBase64(inventory));
  }

  @NotNull
  protected Inventory createInventory(@NotNull String title) {
    return Bukkit.createInventory(null, size, InventoryTitleManager.getTitle(title));
  }

  @NotNull
  protected Inventory getCurrentBackpack(@NotNull Player player) {
    return (getValue() == SHARED) ? sharedBackpack : backpacks.computeIfAbsent(player.getUniqueId(), key -> createInventory("§6Backpack"));
  }

}
