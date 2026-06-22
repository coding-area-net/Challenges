package net.codingarea.challenges.plugin.management.menu.generator.impl.challenge;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.IChallenge;
import net.codingarea.challenges.plugin.challenges.type.annotation.ChallengeAnnotations;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.management.files.ConfigManager;
import net.codingarea.challenges.plugin.management.menu.info.ChallengeMenuClickInfo;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.commons.bukkit.utils.item.StandardItemBuilder;
import net.codingarea.commons.bukkit.utils.menu.MenuClickInfo;
import net.codingarea.commons.common.config.Document;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.*;

public class ChallengeListMenuGenerator extends ChallengesMenuGenerator {

  public static final int SIZE = 4 * 9;
  public static final int[] DISPLAY_SLOTS = {10, 11, 12, 13, 14, 15, 16}; // settings at +9 each by default
  public static final int SLOT_OFFSET_UPPER = -9;
  public static final int SLOT_OFFSET_DISPLAY = 0;
  public static final int SLOT_OFFSET_SETTINGS = 9;
  public static final int SLOT_OFFSET_LOWER = 2 * 9;
  private static final int[] SLOT_OFFSETS = {SLOT_OFFSET_UPPER, SLOT_OFFSET_DISPLAY, SLOT_OFFSET_SETTINGS, SLOT_OFFSET_LOWER};

  protected final List<IChallenge> assignedChallengesCache = new LinkedList<>();

  protected final boolean newSuffix;
  protected final boolean updatedSuffix;
  protected final boolean displayNewInFront;

  public ChallengeListMenuGenerator() {
    Document config = Challenges.getInstance().getConfigDocument();
    newSuffix = config.getBoolean(ConfigManager.Keys.NEW_SUFFIX);
    updatedSuffix = config.getBoolean(ConfigManager.Keys.UPDATED_SUFFIX);
    displayNewInFront = config.getBoolean(ConfigManager.Keys.NEW_IN_FRONT);
  }

  @Override
  public void addToCache(@NotNull IChallenge challenge) {
    if (displayNewInFront) {
      assignedChallengesCache.add(countNewChallenges(), challenge);
    } else {
      assignedChallengesCache.add(challenge);
    }
  }

  @Override
  public boolean isCached(@NonNull IChallenge challenge) {
    return assignedChallengesCache.contains(challenge);
  }

  @Override
  public int getCachedCount() {
    return assignedChallengesCache.size();
  }

  @Override
  public int getEnabledCount() {
    return (int) assignedChallengesCache.stream()
      .filter(IChallenge::isEnabled)
      .count();
  }

  @NotNull
  @Override
  public Optional<IChallenge> getFirstEnabledChallenge() {
    return assignedChallengesCache.stream()
      .filter(IChallenge::isEnabled)
      .findFirst();
  }

  @Override
  public void removeFromCache(@NotNull IChallenge challenge) {
    assignedChallengesCache.remove(challenge);
  }

  @Override
  public void resetCache() {
    assignedChallengesCache.clear();
  }

  @NotNull
  @Override
  public GeneratorMenuPosition createMenuPosition(int page, @NotNull Player player) {
    return new ChallengeListMenuPosition(page);
  }

  /**
   * @param slotOffset See {@link #SLOT_OFFSETS}
   * @return Whether an action can be executed at this offset (otherwise fallback behavior/sound will be played)
   */
  public boolean executeChallengeAction(@NotNull IChallenge challenge, int slotOffset, @NotNull MenuClickInfo info) {
    if (slotOffset == SLOT_OFFSET_DISPLAY || slotOffset == SLOT_OFFSET_SETTINGS) {
      challenge.handleClick(new ChallengeMenuClickInfo(info, slotOffset == SLOT_OFFSET_DISPLAY));
      return true;
    }
    return false;
  }

  @Override
  public void updateInventoryContent(@NotNull Inventory inventory, int page, @NotNull Locale locale) {
    List<IChallenge> challengesForPage = getChallengesForPage(page);
    Iterator<IChallenge> iterator = challengesForPage.iterator(); // LinkedList: poor performance for random access
    for (int i = 0; iterator.hasNext(); i++) {
      IChallenge challenge = iterator.next();
      updateChallengeDisplayAt(challenge, inventory, i, locale);
    }
  }

  @Override
  public void updateElementDisplay(@NotNull IChallenge challenge) {
    int challengeIndex = assignedChallengesCache.indexOf(challenge);
    if (challengeIndex == -1) return; // not registered here?

    int page = getPageOfChallenge(challengeIndex);
    int slotIndex = challengeIndex % DISPLAY_SLOTS.length;

    updatePage(page, (inventory, locale) -> setChallengeItemsAt(challenge, inventory, slotIndex, locale));
  }

  protected void updateChallengeDisplayAt(@NotNull IChallenge challenge, @NotNull Inventory inventory, int slotsIndex, @NotNull Locale locale) {
    setChallengeItemsAt(challenge, inventory, slotsIndex, locale);
    setChallengeUpdateItemsAt(challenge, inventory, slotsIndex, locale);
  }

  protected void setChallengeUpdateItemsAt(@NotNull IChallenge challenge, @NotNull Inventory inventory, int slotIndex, @NotNull Locale locale) {
    if (newSuffix && ChallengeAnnotations.isNew(challenge)) {
      inventory.setItem(DISPLAY_SLOTS[slotIndex] + SLOT_OFFSET_UPPER, new StandardItemBuilder(Material.LIME_STAINED_GLASS_PANE, "§0").build());
      inventory.setItem(DISPLAY_SLOTS[slotIndex] + SLOT_OFFSET_LOWER, new StandardItemBuilder(Material.LIME_STAINED_GLASS_PANE, "§0").build());
    } else if (updatedSuffix && ChallengeAnnotations.isUpdated(challenge)) {
      inventory.setItem(DISPLAY_SLOTS[slotIndex] + SLOT_OFFSET_UPPER, new StandardItemBuilder(Material.YELLOW_STAINED_GLASS_PANE, "§0").build());
      inventory.setItem(DISPLAY_SLOTS[slotIndex] + SLOT_OFFSET_LOWER, new StandardItemBuilder(Material.YELLOW_STAINED_GLASS_PANE, "§0").build());
    }
  }

  protected void setChallengeItemsAt(@NotNull IChallenge challenge, @NotNull Inventory inventory, int slotsIndex, @NotNull Locale locale) {
    inventory.setItem(DISPLAY_SLOTS[slotsIndex] + SLOT_OFFSET_DISPLAY, createChallengeDisplayItem(challenge, locale));
    inventory.setItem(DISPLAY_SLOTS[slotsIndex] + SLOT_OFFSET_SETTINGS, createChallengeSettingsItem(challenge, locale));
    // TODO might need overrideable getter for settings slot
  }

  @NotNull
  protected ItemStack createChallengeDisplayItem(@NotNull IChallenge challenge, @NotNull Locale locale) {
    ItemBuilder item = challenge.getDisplayItem(locale);

    if (newSuffix && ChallengeAnnotations.isNew(challenge)) {
      item.appendName(MessageKey.of("new-challenge-suffix"));
    } else if (updatedSuffix && ChallengeAnnotations.isUpdated(challenge)) {
      item.appendName(MessageKey.of("updated-challenge-suffix"));
    }

    return item.build();
  }

  @NotNull
  protected ItemStack createChallengeSettingsItem(@NotNull IChallenge challenge, @NotNull Locale locale) {
    return challenge.getSettingsItem(locale).build();
  }

  @NotNull
  protected List<IChallenge> getChallengesForPage(int page) {
    int startIndex = page * DISPLAY_SLOTS.length;
    int endIndex = Math.min(startIndex + DISPLAY_SLOTS.length, assignedChallengesCache.size());
    return assignedChallengesCache.subList(startIndex, endIndex);
  }

  public int getPageOfChallenge(IChallenge challenge) {
    int index = assignedChallengesCache.indexOf(challenge);
    return getPageOfChallenge(index);
  }

  protected int getPageOfChallenge(int indexOfChallenge) {
    if (indexOfChallenge == -1) return -1; // challenge not registered here?
    return (indexOfChallenge / DISPLAY_SLOTS.length);
  }

  @Override
  public int getPageCount() {
    // must have at least one page!
    return Math.max(1, Math.ceilDiv(assignedChallengesCache.size(), DISPLAY_SLOTS.length));
  }

  protected int countNewChallenges() {
    int count = 0;
    for (IChallenge challenge : assignedChallengesCache) {
      if (ChallengeAnnotations.isNew(challenge)) {
        count++;
      } else if (displayNewInFront) {
        break;
      }
    }
    return count;
  }

  @Override
  public boolean hasAnyNewChallenges() {
    if (assignedChallengesCache.isEmpty()) return false;
    if (displayNewInFront) {
      IChallenge first = assignedChallengesCache.getFirst();
      return ChallengeAnnotations.isNew(first);
    }
    for (IChallenge challenge : assignedChallengesCache) {
      if (ChallengeAnnotations.isNew(challenge)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean hasAnyUpdatedChallenges() {
    for (IChallenge challenge : assignedChallengesCache) {
      if (ChallengeAnnotations.isUpdated(challenge)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public int getInventorySize() {
    return SIZE;
  }

  protected int findSlotOffset(int clickedSlot, int checkForSlot) {
    for (int offset : SLOT_OFFSETS) {
      if (clickedSlot == checkForSlot + offset) {
        return offset;
      }
    }
    return -1;
  }

  public class ChallengeListMenuPosition extends GeneratorMenuPosition {

    public ChallengeListMenuPosition(int page) {
      super(page);
    }

    @Override
    public boolean handleMenuClick(@NotNull MenuClickInfo info) {
      for (int i = 0; i < DISPLAY_SLOTS.length; i++) {
        int slot = DISPLAY_SLOTS[i];
        int offset = findSlotOffset(info.getSlot(), slot);
        if (offset == -1) continue;

        int challengeIndex = page * DISPLAY_SLOTS.length + i;
        if (challengeIndex >= assignedChallengesCache.size()) return false; // page not full

        IChallenge challenge = assignedChallengesCache.get(challengeIndex); // TODO slow LinkedList random access
        return executeChallengeAction(challenge, offset, info);
      }

      return false;
    }
  }

}
