package net.codingarea.challenges.plugin.management.menu.generator.categorised;

import net.anweisen.utilities.bukkit.utils.animation.SoundSample;
import net.anweisen.utilities.bukkit.utils.menu.MenuPosition;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.IChallenge;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.InventoryTitleManager;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.implementation.SettingsMenuGenerator;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.InventoryUtils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CategorisedMenuGenerator extends SettingsMenuGenerator {

  private final Map<SettingCategory, CategorisedSettingsMenuGenerator> categories = new LinkedHashMap<>();

  @Override
  public void addChallengeToCache(@NotNull IChallenge challenge) {
    SettingCategory category = challenge.getCategory() != null ? challenge.getCategory() : getMiscCategory();
    if (!categories.containsKey(challenge.getCategory())) {
      categories.computeIfAbsent(category, challengeCategory -> {
        CategorisedSettingsMenuGenerator generator = new CategorisedSettingsMenuGenerator(this, category);
        generator.setMenuType(getMenuType());
        return generator;
      });
    }
    categories.get(category).addChallengeToCache(challenge);
  }

  @Override
  public void resetChallengeCache() {
    categories.clear();
  }

  private SettingCategory getMiscCategory() {
    if (getMenuType() == MenuType.GOAL) {
      return SettingCategory.MISC_GOAL;
    }
    return SettingCategory.MISC_CHALLENGE;
  }

  @Override
  public void generatePage(@NotNull Inventory inventory, int page) {

    int i = 0;
    for (Map.Entry<SettingCategory, CategorisedSettingsMenuGenerator> entry : getCategoriesForPage(page)) {
      SettingCategory category = entry.getKey();
      CategorisedSettingsMenuGenerator generator = entry.getValue();

      ItemBuilder builder = category.getDisplayItem();
      String attachment = getLoreAttachment(generator);
      if (!attachment.isEmpty()) {
        builder.appendLore("", attachment);
      }

      int slot = i + 10;
      if (i >= 7) slot += 2;

      for (IChallenge challenge : generator.getChallenges()) {
        if (newSuffix && isNew(challenge)) {
          builder.appendName(" " + Message.forName("new-challenge"));
          break;
        }
      }

      inventory.setItem(slot, builder.build());
      i++;
    }

  }

  private String getLoreAttachment(CategorisedSettingsMenuGenerator generator) {

    if (getMenuType() == MenuType.GOAL) {
      for (IChallenge challenge : generator.getChallenges()) {
        if (challenge.isEnabled()) {
          return Message.forName("lore-category-activated").asString();
        }
      }
      return Message.forName("lore-category-deactivated").asString();
    }
    long activatedCount = generator.getChallenges().stream().filter(IChallenge::isEnabled).count();
    return Message.forName("lore-category-activated-count").asString(activatedCount, generator.getChallenges().size());
  }

  @Override
  public void updateItem(IChallenge challenge) {
    generateInventories();

    for (Map.Entry<SettingCategory, CategorisedSettingsMenuGenerator> entry : categories.entrySet()) {
      if (entry.getValue().getChallenges().contains(challenge)) {
        entry.getValue().updateGeneratorItem(challenge);
      }
    }

  }

  @Override
  public int getPagesCount() {
    return (int) (Math.ceil((double) categories.size() / getEntriesPerPage()));
  }

  public int getEntriesPerPage() {
    return 14;
  }

  @Override
  public MenuPosition getMenuPosition(int page) {
    return info -> {
      if (InventoryUtils.handleNavigationClicking(this,
        getNavigationSlots(page),
        page,
        info,
        () -> Challenges.getInstance().getMenuManager().openGUIInstantly(info.getPlayer()))) {
        return;
      }

      int i = 0;
      for (Map.Entry<SettingCategory, CategorisedSettingsMenuGenerator> entry : getCategoriesForPage(page)) {
        int slot = i + 10;
        if (i >= 7) slot += 2;
        if (slot == info.getSlot()) {
          SettingsMenuGenerator generator = entry.getValue();
          SoundSample.CLICK.play(info.getPlayer());
          generator.open(info.getPlayer(), 0);
          return;
        }
        i++;
      }

      SoundSample.CLICK.play(info.getPlayer());
    };
  }

  private List<Map.Entry<SettingCategory, CategorisedSettingsMenuGenerator>> getCategoriesForPage(int page) {

    List<Map.Entry<SettingCategory, CategorisedSettingsMenuGenerator>> list = categories.entrySet()
      .stream()
      // Priority might be removed in a future version
      .sorted(Comparator.comparingInt(value -> value.getKey().getPriority()))
      .collect(Collectors.toList());

    int entriesPerPage = getEntriesPerPage();
    int startIndex = page * entriesPerPage;
    int endIndex = Math.min(page * entriesPerPage + entriesPerPage, categories.size());

    return list.subList(startIndex, endIndex);
  }

  public static class CategorisedSettingsMenuGenerator extends SettingsMenuGenerator {

    private final CategorisedMenuGenerator generator;
    private final SettingCategory category;

    public CategorisedSettingsMenuGenerator(CategorisedMenuGenerator generator, SettingCategory category) {
      this.generator = generator;
      this.category = category;
      this.onLeaveClick = player -> {
        SoundSample.CLICK.play(player);
        generator.open(player, 0);
      };
    }

    @Override
    protected String getTitle(int page) {
      String[] strings = category.getMessageSupplier().get().asArray();
      String display = strings.length == 0 ? "" : ChatColor.stripColor(strings[0]);
      return InventoryTitleManager.getTitle(getMenuType(), display, String.valueOf(page + 1));
    }

    @Override
    public void updateItem(IChallenge challenge) {
      generator.updateItem(challenge);
      super.updateItem(challenge);
    }

    public void updateGeneratorItem(IChallenge challenge) {
      super.updateItem(challenge);
    }

  }

}
