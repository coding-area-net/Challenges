package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Modifier;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.loader.LanguageLoader;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.item.DefaultItem;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;

import javax.annotation.Nonnull;
import java.util.Objects;

public class LanguageSetting extends Modifier {

  public static final int ENGLISH = 1;
  public static final int GERMAN = 2;

  public static final String GERMAN_SKULL = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWU3ODk5YjQ4MDY4NTg2OTdlMjgzZjA4NGQ5MTczZmU0ODc4ODY0NTM3NzQ2MjZiMjRiZDhjZmVjYzc3YjNmIn19fQ",
    ENGLISH_SKULL = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODgzMWM3M2Y1NDY4ZTg4OGMzMDE5ZTI4NDdlNDQyZGZhYTg4ODk4ZDUwY2NmMDFmZDJmOTE0YWY1NDRkNTM2OCJ9fX0";

  public LanguageSetting() {
    super(MenuType.SETTINGS, 1, 2, ENGLISH);
  }

  @Nonnull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.KNOWLEDGE_BOOK, Message.forName("item-language-setting"));
  }

  @Nonnull
  @Override
  public ItemBuilder createSettingsItem() {
    String texture = getValue() == GERMAN ? GERMAN_SKULL : ENGLISH_SKULL;
    return new ItemBuilder.SkullBuilder(DefaultItem.getItemPrefix() + Message.forName(getSettingName())).setBase64Texture(texture).hideAttributes();
  }

  @Override
  public void playValueChangeTitle() {
    switch (getValue()) {
      case GERMAN:
        Objects.requireNonNull(Challenges.getInstance().getLoaderRegistry().getFirstLoaderByClass(LanguageLoader.class)).changeLanguage("de");
        ChallengeHelper.playChangeChallengeValueTitle(this, Message.forName(getSettingName()));
        break;
      case ENGLISH:
        Objects.requireNonNull(Challenges.getInstance().getLoaderRegistry().getFirstLoaderByClass(LanguageLoader.class)).changeLanguage("en");
        ChallengeHelper.playChangeChallengeValueTitle(this, Message.forName(getSettingName()));
        break;
      default:
        ChallengeHelper.playToggleChallengeTitle(this, false);
    }
  }

  private String getSettingName() {
    return getValue() == GERMAN ? "item-language-setting-german" : "item-language-setting-english";
  }

}
