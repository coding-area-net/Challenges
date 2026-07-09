package net.codingarea.challenges.plugin.challenges.custom.settings.sub.builder;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SubSettingsBuilder;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.management.menu.generator.impl.custom.IParentCustomGenerator;
import net.codingarea.challenges.plugin.spigot.listener.ChatInputListener;
import net.codingarea.challenges.plugin.utils.misc.MapUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class TextInputSubSettingsBuilder extends SubSettingsBuilder {

  private final Consumer<Player> onOpen;
  private final Predicate<AsyncPlayerChatEvent> isValid;

  public TextInputSubSettingsBuilder(String key) {
    this(key, null);
  }

  public TextInputSubSettingsBuilder(String key, SubSettingsBuilder parent) {
    this(key, parent, event -> {
    }, event -> true);
  }

  public TextInputSubSettingsBuilder(String key,
                                     Consumer<Player> onOpen,
                                     Predicate<AsyncPlayerChatEvent> isValid) {
    super(key);
    this.onOpen = onOpen;
    this.isValid = isValid;
  }

  public TextInputSubSettingsBuilder(String key,
                                     SubSettingsBuilder parent,
                                     Consumer<Player> onOpen,
                                     Predicate<AsyncPlayerChatEvent> isValid) {
    super(key, parent);
    this.onOpen = onOpen;
    this.isValid = isValid;
  }

  @Override
  public boolean open(Player player, IParentCustomGenerator parentGenerator, LocalizableMessage title) {
    player.closeInventory();
    onOpen.accept(player);

    ChatInputListener.setInputAction(player, event -> {
      if (!isValid.test(event)) {
        Bukkit.getScheduler().runTask(Challenges.getInstance(), () -> {
          parentGenerator.decline(player);
        });
        return;
      }
      Bukkit.getScheduler().runTask(Challenges.getInstance(), () -> {
        parentGenerator.accept(player, null, MapUtils.createStringArrayMap(getKey(), event.getMessage()));
      });
    });

    return true;
  }

  @NotNull
  @Override
  public Collection<SubSettingDisplay> getCurrentDisplayFor(@NotNull Map<String, String[]> activated) {
    // TODO ported logic from legacy code; needs optimization / complete impl overhaul
    String[] values = activated.get(this.getKey());
    if (values == null) return Collections.emptyList();

    List<SubSettingDisplay> display = new ArrayList<>(values.length);
    for (String value : values) {
      display.add(new SubSettingDisplay(getKeyTranslation(this.getKey()), LocalizableMessage.wrap(value)));
    }

    return display;
  }

  @Override
  public boolean hasSettings() {
    return true;
  }

}
