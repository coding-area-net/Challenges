package net.codingarea.challenges.plugin.management.menu.generator.impl.challenge.categorised;

import net.codingarea.challenges.plugin.challenges.type.IChallenge;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.management.menu.generator.impl.challenge.ChallengeListMenuGenerator;
import net.codingarea.challenges.plugin.management.menu.generator.impl.challenge.ChallengesMenuGenerator;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.commons.bukkit.utils.menu.MenuClickInfo;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CategorisedMenuGenerator extends ChallengesMenuGenerator {

  public static final int SIZE = 4 * 9;
  public static final int[] SLOTS = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25};
  public static final int MAX_ITEMS_PER_ROW = 7;

  protected final Map<SettingCategory, CategorisedListMenuGenerator> categoryGenerators = new HashMap<>();
  protected final List<SettingCategory> orderedCategories = new ArrayList<>();

  @Override
  public void addToCache(@NotNull IChallenge challenge) {
    SettingCategory category = getChallengeCategoryOrMics(challenge);
    CategorisedListMenuGenerator generator = categoryGenerators.computeIfAbsent(category, forCategory -> {
      orderedCategories.add(forCategory);
      orderedCategories.sort(Comparator.comparingInt(SettingCategory::getPriority));
      return new CategorisedListMenuGenerator(menuType, forCategory);
    });
    generator.addToCache(challenge);
  }

  @Override
  public void removeFromCache(@NotNull IChallenge challenge) {
    SettingCategory category = getChallengeCategoryOrMics(challenge);
    CategorisedListMenuGenerator generator = categoryGenerators.get(category);
    if (generator != null) {
      generator.removeFromCache(challenge);
    }
  }

  @Override
  public void resetCache() {
    for (CategorisedListMenuGenerator generator : categoryGenerators.values()) {
      generator.resetCache();
    }
  }

  @Override
  public int getCachedCount() {
    int count = 0;
    for (CategorisedListMenuGenerator generator : categoryGenerators.values()) {
      count += generator.getCachedCount();
    }
    return count;
  }

  @Override
  public int getEnabledCount() {
    int count = 0;
    for (CategorisedListMenuGenerator generator : categoryGenerators.values()) {
      count += generator.getEnabledCount();
    }
    return count;
  }

  @NotNull
  @Override
  public Optional<IChallenge> getFirstEnabledChallenge() {
    for (CategorisedListMenuGenerator generator : categoryGenerators.values()) {
      Optional<IChallenge> challenge = generator.getFirstEnabledChallenge();
      if (challenge.isPresent()) {
        return challenge;
      }
    }
    return Optional.empty();
  }

  @Override
  public boolean isCached(@NotNull IChallenge element) {
    SettingCategory category = getChallengeCategoryOrMics(element);
    CategorisedListMenuGenerator generator = categoryGenerators.get(category);
    return generator != null && generator.isCached(element);
  }

  @Override
  public void updateElementDisplay(@NotNull IChallenge challenge) {
    SettingCategory category = getChallengeCategoryOrMics(challenge);
    CategorisedListMenuGenerator generator = categoryGenerators.get(category);
    if (generator != null) {
      generator.updateElementDisplay(challenge);

      int index = orderedCategories.indexOf(category);
      int[] slots = getSlots();
      int page = index / slots.length;
      int slotIndex = index % slots.length;
      updatePage(page, (inventory, locale) -> setCategoryItemsAt(category, inventory, slots, slotIndex, locale));
    }
  }

  @Override
  public void updateInventoryContent(@NotNull Inventory inventory, int page, @NotNull Locale locale) {
    int[] slots = getSlots();
    List<SettingCategory> categories = getCategoriesForPage(page, slots);
    int len = categories.size();
    for (int i = 0; i < len; i++) {
      SettingCategory category = categories.get(i);
      setCategoryDisplayAt(category, inventory, slots, i, locale);
    }
  }

  private void setCategoryDisplayAt(@NotNull SettingCategory category, @NotNull Inventory inventory, int[] slots, int slotIndex, @NotNull Locale locale) {
    setCategoryItemsAt(category, inventory, slots, slotIndex, locale);
    setCategoryUpdateItemsAt(category, inventory, slots, slotIndex, locale);
  }

  private void setCategoryItemsAt(@NotNull SettingCategory category, @NotNull Inventory inventory, int[] slots, int slotIndex, @NotNull Locale locale) {
    inventory.setItem(slots[slotIndex], createCategoryDisplayItem(locale, category));
  }

  private void setCategoryUpdateItemsAt(@NotNull SettingCategory category, @NotNull Inventory inventory, int[] slots, int slotIndex, @NotNull Locale locale) {
    CategorisedListMenuGenerator generator = categoryGenerators.get(category);
    if (generator != null) {
      // This will break if there are more than 2 rows!
      int row = slotIndex / getMaxItemsPerRow();
      int offset = row == 0 ? -9 : +9;
      if (generator.isNewSuffix() && generator.hasAnyNewChallenges()) {
        inventory.setItem(slots[slotIndex] + offset, ChallengeListMenuGenerator.NEW_CHALLENGE_ITEM);
      } else if (generator.isUpdatedSuffix() && generator.hasAnyUpdatedChallenges()) {
        inventory.setItem(slots[slotIndex] + offset, ChallengeListMenuGenerator.UPDATED_CHALLENGE_ITEM);
      }
    }
  }

  @NotNull
  protected ItemStack createCategoryDisplayItem(@NotNull Locale locale, @NotNull SettingCategory category) {
    ItemBuilder item = new ItemBuilder(locale, category.getDisplayItemPreset(), MessageKey.of("challenge.display-format"),
      category.getDisplayName(), category.getDescription());

    CategorisedListMenuGenerator generator = categoryGenerators.get(category);
    if (generator != null) {
      if (generator.isNewSuffix() && generator.hasAnyNewChallenges()) {
        item.appendName(MessageKey.of("new-challenge-suffix"));
      } else if (generator.isUpdatedSuffix() && generator.hasAnyUpdatedChallenges()) {
        item.appendName(MessageKey.of("updated-challenge-suffix"));
      }

      item.appendLore(category.getDescriptionInfo(),
        generator.getEnabledCount(), generator.getCachedCount(),
        generator.getFirstEnabledChallenge().map(IChallenge::getChallengeName).orElse(MessageKey.of("generic.disabled")));
    }

    return item.build();
  }

  @NotNull
  protected List<SettingCategory> getCategoriesForPage(int page, @NotNull int[] slots) {
    int startIndex = page * slots.length;
    int endIndex = Math.min(startIndex + slots.length, orderedCategories.size());
    return orderedCategories.subList(startIndex, endIndex);
  }

  @NotNull
  protected SettingCategory getChallengeCategoryOrMics(@NotNull IChallenge challenge) {
    SettingCategory category = challenge.getCategory();
    if (category != null) return category;
    return getMiscCategory();
  }

  @NotNull
  protected SettingCategory getMiscCategory() {
    if (menuType == MenuType.GOAL) {
      return SettingCategory.MISC_GOAL;
    }
    return SettingCategory.MISC_CHALLENGE;
  }

  @NotNull
  @Override
  public GeneratorMenuPosition createMenuPosition(int page, @NotNull Player player) {
    return new CategorisedOverviewMenuPosition(page);
  }

  @Override
  public boolean hasAnyNewChallenges() {
    for (CategorisedListMenuGenerator menuGenerator : categoryGenerators.values()) {
      if (menuGenerator.hasAnyNewChallenges()) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean hasAnyUpdatedChallenges() {
    for (CategorisedListMenuGenerator menuGenerator : categoryGenerators.values()) {
      if (menuGenerator.hasAnyUpdatedChallenges()) {
        return true;
      }
    }
    return false;
  }

  @NotNull
  public int[] getSlots() {
    return SLOTS;
  }

  public int getMaxItemsPerRow() {
    return MAX_ITEMS_PER_ROW;
  }

  @Override
  public int getInventorySize() {
    return SIZE;
  }

  @Override
  public int getPageCount() {
    return Math.max(1, Math.ceilDiv(categoryGenerators.size(), getSlots().length));
  }

  public class CategorisedOverviewMenuPosition extends GeneratorMenuPosition {

    public CategorisedOverviewMenuPosition(int page) {
      super(page);
    }

    @Override
    public boolean handleMenuClick(@NotNull MenuClickInfo info) {
      int[] slots = getSlots();
      for (int i = 0; i < slots.length; i++) {
        if (info.getSlot() != slots[i]) continue;

        List<SettingCategory> categories = getCategoriesForPage(page, slots);
        if (i >= categories.size()) return false; // empty slot; page not full
        SettingCategory category = categories.get(i);
        CategorisedListMenuGenerator generator = categoryGenerators.get(category);
        if (generator != null) {
          generator.openMenu(info.getPlayer(), 0);
          SoundSample.CLICK.play(info.getPlayer());
        }
        return true;
      }

      return false;
    }
  }
}
