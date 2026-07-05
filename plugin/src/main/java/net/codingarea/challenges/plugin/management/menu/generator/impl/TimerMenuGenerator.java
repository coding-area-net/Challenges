package net.codingarea.challenges.plugin.management.menu.generator.impl;

import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.content.loader.LanguageLoader;
import net.codingarea.challenges.plugin.management.menu.generator.MultiPageMenuGenerator;
import net.codingarea.challenges.plugin.management.scheduler.policy.TimerPolicy;
import net.codingarea.challenges.plugin.management.scheduler.task.ScheduledTask;
import net.codingarea.challenges.plugin.management.scheduler.task.TimerTask;
import net.codingarea.challenges.plugin.management.scheduler.timer.TimerFormat;
import net.codingarea.challenges.plugin.management.scheduler.timer.TimerStatus;
import net.codingarea.challenges.plugin.utils.item.DefaultItem;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.MinecraftNameWrapper;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.commons.bukkit.utils.menu.MenuClickInfo;
import net.codingarea.commons.common.collection.pair.Tuple;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public class TimerMenuGenerator extends MultiPageMenuGenerator {

  public static final int SIZE = 5 * 9;
  public static final int START_SLOT = 20;
  public static final int SHOW_SLOT = 22;
  public static final int MODE_SLOT = 24;
  public static final int[] DAYS_SLOTS = {11, 20, 29};
  public static final int[] HOUR_SLOTS = {12, 21, 30};
  public static final int[] MINUTE_SLOTS = {14, 23, 32};
  public static final int[] SECOND_SLOTS = {15, 24, 33};
  public static final int PAGE_STATE = 0, PAGE_TIME = 1;
  public static final int SHIFT_CHANGE_TIME_AMOUNT = 10;

  private static final List<Tuple<int[], Integer>> SLOTS_AMOUNT_MAPPING = List.of(
    Tuple.of(DAYS_SLOTS, 24 * 60 * 60),
    Tuple.of(HOUR_SLOTS, 60 * 60),
    Tuple.of(MINUTE_SLOTS, 60),
    Tuple.of(SECOND_SLOTS, 1)
  );

  public TimerMenuGenerator() {
    ChallengeAPI.registerScheduler(this);
    Challenges.getInstance().getLoaderRegistry().subscribe(LanguageLoader.class, this::updatePages);
  }

  @TimerTask(status = {TimerStatus.PAUSED, TimerStatus.RUNNING})
  public void updateTimerStateInventory() {
    updatePage(PAGE_STATE);
  }

  @ScheduledTask(ticks = 20, timerPolicy = TimerPolicy.STARTED)
  public void updateTimeInventory() {
    updatePage(PAGE_TIME);
  }

  @NotNull
  @Override
  public GeneratorMenuPosition createMenuPosition(int page, @NotNull Player player) {
    return new TimerMenuPosition(page);
  }

  @NotNull
  @Override
  protected Object getMenuTitlePageArg(int page) {
    return switch (page) {
      case PAGE_STATE -> MessageKey.of("menu.timer.name-state");
      case PAGE_TIME -> MessageKey.of("menu.timer.name-time");
      default -> super.getMenuTitlePageArg(page);
    };
  }

  @Override
  public void setInventoryDecoration(@NotNull Inventory inventory, int page, @NotNull Locale locale) {
    super.setInventoryDecoration(inventory, page, locale); // background fill items
    for (int i : new int[]{1, 2, 6, 7, 9, 10, 16, 17, 27, 28, 34, 35, 37, 38, 39, 41, 42, 43}) {
      inventory.setItem(i, ItemBuilder.FILL_ITEM_CONTRAST);
    }
  }

  @Override
  public void updateInventoryContent(@NotNull Inventory inventory, int page, @NotNull Locale locale) {
    switch (page) {
      case PAGE_STATE -> updateTimerStatePage(inventory, locale);
      case PAGE_TIME -> updateTimePage(inventory, locale);
    }
  }

  @Override
  public int getPageCount() {
    return 2;
  }

  public void updateTimerStatePage(@NotNull Inventory inventory, @NotNull Locale locale) {
    inventory.setItem(START_SLOT, Challenges.getInstance().getChallengeTimer().isStarted() ?
      new ItemBuilder(locale, Material.LIME_DYE, MessageKey.of("menu.timer.item-started")).build() :
      new ItemBuilder(locale, MinecraftNameWrapper.RED_DYE, MessageKey.of("menu.timer.item-paused")).build());
    inventory.setItem(SHOW_SLOT, Challenges.getInstance().getChallengeTimer().isHidden() ?
      new ItemBuilder(locale, Material.BARRIER, MessageKey.of("menu.timer.item-hidden")).build() :
      new ItemBuilder(locale, Material.ENDER_EYE, MessageKey.of("menu.timer.item-shown")).build());
    inventory.setItem(MODE_SLOT, Challenges.getInstance().getChallengeTimer().isCountingUp() ?
      new ItemBuilder.SkullBuilder(locale, MessageKey.of("menu.timer.item-counting-up")).setBase64Texture(DefaultItem.SkullTextures.GREEN_ARROW_UP).build() :
      new ItemBuilder.SkullBuilder(locale, MessageKey.of("menu.timer.item-counting-down")).setBase64Texture(DefaultItem.SkullTextures.RED_ARROW_DOWN).build());
  }

  public void updateTimePage(@NotNull Inventory inventory, @NotNull Locale locale) {
    long time = Challenges.getInstance().getChallengeTimer().getTime();
    long seconds = time;
    long minutes = seconds / 60;
    long hours = minutes / 60;
    long days = hours / 24;
    seconds %= 60;
    minutes %= 60;
    hours %= 24;

    String format = TimerFormat.SIMPLE_FORMAT.format(time);

    setTimeItems(inventory, locale, DAYS_SLOTS, days, format, Material.GOLD_BLOCK,
      MessageKey.of("menu.timer.item-days"),
      MessageKey.of("menu.timer.item-days-add"),
      MessageKey.of("menu.timer.item-days-subtract"));
    setTimeItems(inventory, locale, HOUR_SLOTS, hours, format, Material.RAW_GOLD,
      MessageKey.of("menu.timer.item-hours"),
      MessageKey.of("menu.timer.item-hours-add"),
      MessageKey.of("menu.timer.item-hours-subtract"));
    setTimeItems(inventory, locale, MINUTE_SLOTS, minutes, format, Material.GOLD_INGOT,
      MessageKey.of("menu.timer.item-minutes"),
      MessageKey.of("menu.timer.item-minutes-add"),
      MessageKey.of("menu.timer.item-minutes-subtract"));
    setTimeItems(inventory, locale, SECOND_SLOTS, seconds, format, Material.GOLD_NUGGET,
      MessageKey.of("menu.timer.item-seconds"),
      MessageKey.of("menu.timer.item-seconds-add"),
      MessageKey.of("menu.timer.item-seconds-subtract"));
  }

  private void setTimeItems(@NotNull Inventory inventory, @NotNull Locale locale, int[] slots,
                            long amount, @NotNull String timerFormat, @NotNull Material displayItemMaterial, @NotNull MessageKey stateDescription,
                            @NotNull MessageKey addDescription, @NotNull MessageKey subtractDescription) {
    inventory.setItem(slots[1], createTimeStateItem(amount, timerFormat, displayItemMaterial, stateDescription, locale));
    inventory.setItem(slots[0], createTimeControlItem(true, amount, timerFormat, addDescription, locale));
    inventory.setItem(slots[2], createTimeControlItem(false, amount, timerFormat, subtractDescription, locale));
  }

  private ItemStack createTimeControlItem(boolean add, long amount, @NotNull String timerFormat, @NotNull MessageKey description, @NotNull Locale locale) {
    return new ItemBuilder(locale, add ? Material.DARK_OAK_BUTTON : Material.STONE_BUTTON, description, amount, timerFormat, SHIFT_CHANGE_TIME_AMOUNT).build();
  }

  private ItemStack createTimeStateItem(long amount, @NotNull String timerFormat, @NotNull Material displayItemMaterial, @NotNull MessageKey description, @NotNull Locale locale) {
    return new ItemBuilder(locale, displayItemMaterial, description, amount, timerFormat).setAmount((int) Math.max(amount, 1)).build();
  }

  @Override
  public int getInventorySize() {
    return SIZE;
  }

  public class TimerMenuPosition extends GeneratorMenuPosition {

    public TimerMenuPosition(int page) {
      super(page);
    }

    @Override
    public boolean handleMenuClick(@NotNull MenuClickInfo info) {
      return switch (page) {
        case PAGE_STATE -> handleTimerStateMenuClick(info);
        case PAGE_TIME -> handleTimeMenuClick(info);
        default -> false;
      };
    }

    private boolean handleTimerStateMenuClick(@NotNull MenuClickInfo info) {
      switch (info.getSlot()) {
        case START_SLOT -> {
          if (playNoPermissionsEffect(info.getPlayer())) return true;
          Challenges.getInstance().getChallengeTimer().toggle();
          return true;
        }
        case MODE_SLOT -> {
          if (playNoPermissionsEffect(info.getPlayer())) return true;
          Challenges.getInstance().getChallengeTimer().setCountingUp(!Challenges.getInstance().getChallengeTimer().isCountingUp());
          return true;
        }
        case SHOW_SLOT -> {
          if (playNoPermissionsEffect(info.getPlayer())) return true;
          Challenges.getInstance().getChallengeTimer().setHidden(!Challenges.getInstance().getChallengeTimer().isHidden());
          return true;
        }
      }
      return false;
    }

    private boolean handleTimeMenuClick(@NotNull MenuClickInfo info) {
      for (Tuple<int[], Integer> mapping : SLOTS_AMOUNT_MAPPING) {
        int[] slots = mapping.getFirst();
        for (int i = 0; i < 3; i++) {
          if (info.getSlot() != slots[i]) continue;
          if (playNoPermissionsEffect(info.getPlayer())) return true;

          if (i == 1) {
            Challenges.getInstance().getChallengeTimer().reset();
            SoundSample.BASS_OFF.play(info.getPlayer());
            updateTimeInventory();
            Challenges.getInstance().getChallengeTimer().updateActionbar();
            return true;
          }

          int amount = mapping.getSecond();
          boolean plus = i == 0;
          if (!plus) amount = -amount;
          if (info.isShiftClick()) amount *= SHIFT_CHANGE_TIME_AMOUNT;

          Challenges.getInstance().getChallengeTimer().addSeconds(amount);
          updateTimeInventory();

          SoundSample.PLOP.play(info.getPlayer());
          return true;
        }
      }
      return false;
    }

    private boolean playNoPermissionsEffect(@NotNull Player player) {
      if (mayManageTimer(player)) return false;
      Challenges.getInstance().getMenuManager().playNoPermissionsEffect(player);
      return true;
    }

    private boolean mayManageTimer(@NotNull Player player) {
      return player.hasPermission("challenges.timer");
    }

  }

}
