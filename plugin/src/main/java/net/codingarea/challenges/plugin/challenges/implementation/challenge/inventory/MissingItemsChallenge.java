package net.codingarea.challenges.plugin.challenges.implementation.challenge.inventory;

import net.anweisen.utilities.bukkit.utils.animation.SoundSample;
import net.anweisen.utilities.bukkit.utils.menu.MenuClickInfo;
import net.anweisen.utilities.bukkit.utils.menu.MenuPosition;
import net.anweisen.utilities.common.annotations.Since;
import net.anweisen.utilities.common.collection.pair.Tuple;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.abstraction.TimedChallenge;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.management.menu.InventoryTitleManager;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.utils.bukkit.command.PlayerCommand;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.item.ItemUtils;
import net.codingarea.challenges.plugin.utils.misc.ExperimentalUtils;
import net.codingarea.challenges.plugin.utils.misc.InventoryUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Since("2.0")
public class MissingItemsChallenge extends TimedChallenge implements PlayerCommand {

  private final Map<UUID, Tuple<Inventory, MenuPosition>> inventories = new HashMap<>();
  private List<Material> materials;

  public MissingItemsChallenge() {
    super(MenuType.CHALLENGES, 1, 10, 5, false);
    setCategory(SettingCategory.INVENTORY);
  }

  @Nullable
  @Override
  protected String[] getSettingsDescription() {
    return Message.forName("item-time-seconds-range-description").asArray(getValue() * 60 - 10, getValue() * 60 + 10);
  }

  @Override
  public void playValueChangeTitle() {
    ChallengeHelper.playChallengeSecondsRangeValueChangeTitle(this, getValue() * 60 - 10, getValue() * 60 + 10);
  }

  @Override
  protected int getSecondsUntilNextActivation() {
    return globalRandom.around(getValue() * 60, 10);
  }

  @Nonnull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.FISHING_ROD, Message.forName("item-missing-items-challenge"));
  }

  @Override
  protected void onEnable() {
    materials = Arrays.stream(ExperimentalUtils.getMaterials())
      .filter(Material::isItem)
      .filter(ItemUtils::isObtainableInSurvival)
      .collect(Collectors.toList());
  }

  @Override
  protected void onDisable() {
    materials = null;
  }

  @Override
  protected void onTimeActivation() {
    broadcastFiltered(this::startGuessingGame);

    if (inventories.isEmpty()) {
      restartTimer();
    }

  }

  private void startGuessingGame(@Nonnull Player player) {

    BukkitTask task = new BukkitRunnable() {

      int timeLeft = (int) (2.5 * 60);

      @Override
      public void run() {
        timeLeft--;

        if (!inventories.containsKey(player.getUniqueId())) {
          cancel();
          return;
        }

        if (timeLeft == 0) {
          cancel();
          Tuple<Inventory, MenuPosition> tuple = inventories.remove(player.getUniqueId());
          tuple.getSecond().handleClick(new MenuClickInfo(player, tuple.getFirst(), false, false, 1000));
        } else if (timeLeft <= 5) {
          new SoundSample().addSound(Sound.BLOCK_NOTE_BLOCK_BASS, 0.5F, (float) (timeLeft - 1) / 10 + 1).play(player);
        }

      }

    }.runTaskTimer(plugin, 20, 20);

    createMissingItemsInventory(player, unused -> {
      if (!anyRunningGame()) {
        inventories.clear();
        restartTimer();
      }
      task.cancel();
    });

  }

  private void createMissingItemsInventory(@Nonnull Player player, Consumer<Void> onFinish) {

    int targetSlot = InventoryUtils.getRandomFullSlot(player.getInventory());
    if (targetSlot == -1) return;

    SoundSample.PLOP.play(player);

    ItemStack targetItem = player.getInventory().getItem(targetSlot);
    if (targetItem == null) return;
    Tuple<Inventory, Integer> tuple = generateMissingItemsInventory(targetItem);

    Tuple<Inventory, MenuPosition> inventoryTuple = new Tuple<>(tuple.getFirst(), menuClickInfo -> {
      InventoryUtils.giveItem(player, targetItem);
      inventories.remove(player.getUniqueId());
      onFinish.accept(null);
      if (menuClickInfo.getSlot() == tuple.getSecond()) {
        SoundSample.LEVEL_UP.play(player);
        player.closeInventory();
      } else {
        player.closeInventory();
        kill(player);
      }
    });
    inventories.put(player.getUniqueId(), inventoryTuple);

    player.getInventory().setItem(targetSlot, null);

    sendInfoText(player);

  }

  private void sendInfoText(@Nonnull Player player) {
    String message = Message.forName("missing-items-inventory").asString("§7");
    String openMessage = Message.forName("missing-items-inventory-open").asString();

    TextComponent messageComponent = new TextComponent(Prefix.CHALLENGES + message + " ");

    TextComponent clickComponent = new TextComponent(openMessage);
    clickComponent.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/openmissingitems"));
    clickComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Collections.singletonList(new TextComponent("§2§l✔ §8┃ §7" + Message.forName("open").asString())).toArray(new BaseComponent[0])));

    messageComponent.addExtra(clickComponent);
    player.spigot().sendMessage(messageComponent);
  }

  private boolean openGameInventory(@Nonnull Player player) {
    Tuple<Inventory, MenuPosition> inventoryTuple = inventories.get(player.getUniqueId());
    if (inventoryTuple == null) return false;
    player.openInventory(inventoryTuple.getFirst());
    MenuPosition.set(player, inventoryTuple.getSecond());
    return true;
  }

  private Tuple<Inventory, Integer> generateMissingItemsInventory(@Nonnull ItemStack itemStack) {
    Inventory inventory = Bukkit.createInventory(MenuPosition.HOLDER, 6 * 9, InventoryTitleManager.getTitle(Message.forName("missing-items-inventory").asString(Message.forName("inventory-color").asString())));

    int targetSlot = globalRandom.nextInt(inventory.getSize());
    inventory.setItem(targetSlot, itemStack);

    for (int slot = 0; slot < inventory.getSize(); slot++) {
      if (slot == targetSlot) continue;
      try {
        inventory.setItem(slot, getRandomItem(itemStack));
      } catch (Exception exception) {
        inventory.setItem(slot, new ItemStack(Material.BARRIER));
        Challenges.getInstance().getLogger().error("", exception);
      }

    }

    return new Tuple<>(inventory, targetSlot);
  }

  private ItemStack getRandomItem(@Nonnull ItemStack blacklisted) {
    if (materials == null) onEnable();

    Material material = globalRandom.choose(materials);
    ItemStack itemStack = new ItemStack(material);

    if (itemStack.getItemMeta() instanceof Damageable && 1 < material.getMaxDurability()) {
      ((Damageable) itemStack.getItemMeta()).setDamage(globalRandom.range(1, material.getMaxDurability()));
    } else if (1 < itemStack.getMaxStackSize() && globalRandom.nextInt(100) <= 20) {
      itemStack.setAmount(globalRandom.range(1, itemStack.getMaxStackSize()));
    }

    if (itemStack.isSimilar(blacklisted) && itemStack.getAmount() == blacklisted.getAmount()) {
      return getRandomItem(blacklisted);
    }

    return itemStack;
  }

  @Override
  public void onCommand(@Nonnull Player player, @Nonnull String[] args) throws Exception {
    if (openGameInventory(player)) {
      SoundSample.OPEN.play(player);
    } else {
      SoundSample.BASS_OFF.play(player);
    }
  }

  private boolean anyRunningGame() {
    return inventories.keySet().stream().anyMatch(uuid -> Bukkit.getOfflinePlayer(uuid).isOnline());
  }

}
