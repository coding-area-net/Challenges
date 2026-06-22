package net.codingarea.challenges.plugin.management.menu.generator;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.i18n.LanguageProvider;
import net.codingarea.commons.bukkit.utils.animation.AnimatedInventory;
import net.codingarea.commons.bukkit.utils.menu.MenuPosition;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class SingleAnimatedMenuGenerator implements IMenuGenerator {

  private final Map<Locale, AnimatedInventory> inventoryCache = new HashMap<>();

  @NotNull
  public abstract MenuPosition createMenuPosition();

  // TODO impl dynamic formatting as in Multi-/SinglePageMenuGenerator
  @NotNull
  public abstract AnimatedInventory createAnimatedInventory(@NotNull Locale locale);

  @NotNull
  public AnimatedInventory getOrCreateInventory(@NotNull Locale locale) {
    return inventoryCache.computeIfAbsent(locale, this::createAnimatedInventory);
  }

  @Override
  public void openMenu(@NotNull Player player, int page) {
    Locale locale = findLanguageProvider().getPlayerLanguage(player);
    AnimatedInventory inventory = getOrCreateInventory(locale);
    MenuPosition.set(player, createMenuPosition());
    inventory.open(player);
  }

  public void openMenuInstantly(@NotNull Player player, boolean playSound) {
    Locale locale = findLanguageProvider().getPlayerLanguage(player);
    AnimatedInventory inventory = getOrCreateInventory(locale);
    MenuPosition.set(player, createMenuPosition());
    inventory.openNotAnimated(player, playSound);
  }

  @Override
  public void updateOrGeneratePages(@NotNull Locale locale) {
    inventoryCache.put(locale, createAnimatedInventory(locale));
  }

  @Override
  public void updatePages() {
    // updating every frame of the animation is tedious, just recreate it
    List<Locale> locales = new ArrayList<>(inventoryCache.keySet());
    inventoryCache.clear();
    locales.forEach(this::getOrCreateInventory); // stores generated inventory
  }

  @Override
  public void updatePage(int page) {
    updatePages(); // only one page
  }

  @Override
  public final int getPageCount() {
    return 1;
  }

  @NotNull
  protected LanguageProvider findLanguageProvider() {
    return Challenges.getInstance().getTranslationManager().getLanguageProvider();
  }
}
