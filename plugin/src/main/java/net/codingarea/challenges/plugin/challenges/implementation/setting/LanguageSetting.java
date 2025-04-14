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
import java.net.MalformedURLException;
import java.net.URL;

public class LanguageSetting extends Modifier {

    public static final int ENGLISH = 1;
    public static final int GERMAN = 2;

    public static final URL GERMAN_SKULL;
    public static final URL ENGLISH_SKULL;

    static {
        try {
            GERMAN_SKULL = new URL("http://textures.minecraft.net/texture/5e7899b4806858697e283f084d9173fe487886453774626b24bd8cfecc77b3f");
            ENGLISH_SKULL = new URL("http://textures.minecraft.net/texture/46c9923bebd9ad90a80a0731c3f3b9db729b0785015e18e3ec07e4e91099be06");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

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
        ItemBuilder.SkullBuilder GermanSkull = new ItemBuilder.SkullBuilder(GERMAN_SKULL);
        ItemBuilder.SkullBuilder EnglishSkull = new ItemBuilder.SkullBuilder(ENGLISH_SKULL);
        if (getValue() == GERMAN)
            return GermanSkull.setName(DefaultItem.getItemPrefix() + Message.forName("item-language-setting-german")).hideAttributes();
        return EnglishSkull.setName(DefaultItem.getItemPrefix() + Message.forName("item-language-setting-english")).hideAttributes();
    }

    @Override
    public void playValueChangeTitle() {
        switch (getValue()) {
            case GERMAN:
                ChallengeHelper.playChangeChallengeValueTitle(this, Message.forName("item-language-setting-german"));
                Challenges.getInstance().getLoaderRegistry().getFirstLoaderByClass(LanguageLoader.class).reload("de");
                break;
            case ENGLISH:
                ChallengeHelper.playChangeChallengeValueTitle(this, Message.forName("item-language-setting-english"));
                Challenges.getInstance().getLoaderRegistry().getFirstLoaderByClass(LanguageLoader.class).reload("en");
                break;
            default:
                ChallengeHelper.playToggleChallengeTitle(this, false);
        }
    }

}