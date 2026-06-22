package net.codingarea.challenges.platform.message.common;

import lombok.AllArgsConstructor;
import net.codingarea.challenges.platform.message.MessageHolder;
import net.codingarea.challenges.platform.message.MessagePlatform;
import net.codingarea.challenges.platform.message.common.wrapper.AdventureMessageBossBar;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.translation.Translatable;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Common base for implementations of MiniMessage preventing duplicate code.
 * This class will be duplicated at compile time and relocated inside the legacy module for compatibility with
 * relocated adventure lib. The original version (not relocated) will be used by the native impl.
 * <p>
 * Due to version/classpath incompatibility we have to abstract all calls to the adventure api.
 *
 * @see #convertToComponent(Object) for compatible argument types
 */
@AllArgsConstructor
public abstract class AbstractMiniMessagePlatform implements MessagePlatform {

  protected final Map<String, Component> prefixCache = new ConcurrentHashMap<>(); // messages may be sent concurrently
  protected final MiniMessage miniMessage;
  protected final LegacyComponentSerializer legacySerializer;

  public abstract void sendSenderComponent(@NotNull CommandSender sender, @NotNull Component component);

  public abstract void sendChatComponent(@NotNull Player player, @NotNull Component component);

  public abstract void sendActionBarComponent(@NotNull Player player, @NotNull Component component);

  public abstract void sendAdventureTitle(@NotNull Player player, @NotNull Title title);

  @NotNull
  public abstract Inventory createInventoryTitled(@NotNull InventoryHolder owner, int size, @NotNull Component titleComponent);

  @NotNull
  public abstract Inventory createInventoryTitled(@NotNull InventoryHolder owner, @NotNull InventoryType type,
                                                  @NotNull Component titleComponent);

  @NotNull
  public abstract Component getPlayerNameComponent(@NotNull Player player);

  public abstract void applyItemNameComponent(@NotNull ItemMeta meta, @NotNull Component nameComponent);

  public abstract void appendItemNameComponent(@NotNull ItemMeta meta, @NotNull Component nameComponent);

  public abstract void applyItemLoreComponent(@NotNull ItemMeta meta, @NotNull List<Component> loreComponentLines);

  public abstract void appendItemLoreComponent(@NotNull ItemMeta meta, @NotNull List<Component> loreComponentLines);

  public abstract void showAdventureBossBar(@NotNull Player player, @NotNull BossBar bar);

  public abstract void hideAdventureBossBar(@NotNull Player player, @NotNull BossBar bar);

  @Override
  public void sendSenderMessage(@NotNull CommandSender sender, @Nullable String miniMessagePrefix,
                                @NotNull String[] miniMessageTextLines, @NotNull Object[] positionalArgs) {
    if (miniMessageTextLines.length == 0) return;
    sendSenderComponent(sender, deserializeLinesWithArgs(miniMessagePrefix, miniMessageTextLines, positionalArgs));
  }

  @Override
  public void sendChatMessage(@NotNull Player player, @Nullable String miniMessagePrefix,
                              @NotNull String[] miniMessageTextLines, @NotNull Object[] positionalArgs) {
    if (miniMessageTextLines.length == 0) return;
    sendChatComponent(player, deserializeLinesWithArgs(miniMessagePrefix, miniMessageTextLines, positionalArgs));
  }

  @Override
  public void sendActionBar(@NotNull Player player, @NotNull String miniMessageText, @NotNull Object[] positionalArgs) {
    sendActionBarComponent(player, deserializeLineWithArgs(miniMessageText, positionalArgs));
  }

  @Override
  public void sendTitle(@NotNull Player player, @NotNull String[] miniMessageTitleAndSubtitle, @NotNull Object[] positionalArgs) {
    sendAdventureTitle(player, Title.title(deserializeTitleWithArgs(miniMessageTitleAndSubtitle, positionalArgs),
      deserializeSubtitle(miniMessageTitleAndSubtitle, positionalArgs)));
  }

  @Override
  public void sendTitle(@NotNull Player player, @NotNull String[] miniMessageTitleAndSubtitle, @NotNull Object[] positionalArgs,
                        int fadeInTicks, int stayTicks, int fadeOutTicks) {
    sendAdventureTitle(player, Title.title(deserializeTitleWithArgs(miniMessageTitleAndSubtitle, positionalArgs),
      deserializeSubtitle(miniMessageTitleAndSubtitle, positionalArgs), fadeInTicks, stayTicks, fadeOutTicks));
  }

  @NotNull
  @Override
  public Inventory createInventory(@NotNull InventoryHolder owner, int size, @NotNull String miniMessageTitle, @NotNull Object[] positionalArgs) {
    return createInventoryTitled(owner, size, deserializeLineWithArgs(miniMessageTitle, positionalArgs));
  }

  @NotNull
  @Override
  public Inventory createInventory(@NotNull InventoryHolder owner, @NotNull InventoryType type,
                                   @NotNull String miniMessageTitle, @NotNull Object[] positionalArgs) {
    return createInventoryTitled(owner, type, deserializeLineWithArgs(miniMessageTitle, positionalArgs));
  }

  @Override
  public void applyItemName(@NotNull ItemMeta item, @NotNull String miniMessageDisplayName, @NotNull Object[] positionalArgs) {
    applyItemNameComponent(item, deserializeLineWithArgs(miniMessageDisplayName, positionalArgs));
  }

  @Override
  public void appendItemName(@NotNull ItemMeta item, @NotNull String miniMessageDisplayNameSuffix, @NotNull Object[] positionalArgs) {
    appendItemNameComponent(item, deserializeLineWithArgs(miniMessageDisplayNameSuffix, positionalArgs));
  }

  @Override
  public void applyItemLore(@NotNull ItemMeta item, @NotNull String[] miniMessageLore, @NotNull Object[] positionalArgs) {
    applyItemLoreComponent(item, deserializeLinesAsListWithArgs(miniMessageLore, positionalArgs));
  }

  @Override
  public void appendItemLore(@NotNull ItemMeta item, @NotNull String[] miniMessageLore, @NotNull Object[] positionalArgs) {
    appendItemLoreComponent(item, deserializeLinesAsListWithArgs(miniMessageLore, positionalArgs));
  }

  @NotNull
  @Override
  public AdventureMessageBossBar createBossBar(@NonNull MessageHolder initialTitle) {
    Component initialTitleComponent = convertMessageHolderToComponent(initialTitle);
    return new AdventureMessageBossBar(this, createDefaultAdventureBossBar(initialTitleComponent));
  }

  @NotNull
  protected Component deserializeTitleWithArgs(@NotNull String[] titleAndSubtitle, @NotNull Object[] positionalArgs) {
    if (titleAndSubtitle.length == 0) return Component.empty();
    return deserializeLineWithArgs(titleAndSubtitle[0], positionalArgs);
  }

  @NotNull
  protected Component deserializeSubtitle(@NotNull String[] titleAndSubtitle, @NotNull Object[] positionalArgs) {
    if (titleAndSubtitle.length < 1) return Component.empty();
    return deserializeLineWithArgs(titleAndSubtitle[1], positionalArgs);
  }

  @NotNull
  protected Component deserializeLinesWithArgs(@Nullable String prefix, @NotNull String[] textLines, @NotNull Object[] positionalArgs) {
    return replacePositionalArgs(deserializeLines(prefix, textLines), positionalArgs);
  }

  @NotNull
  protected Component deserializeLineWithArgs(@NotNull String line, @NotNull Object[] positionalArgs) {
    return replacePositionalArgs(deserializeText(line), positionalArgs);
  }

  @NotNull
  protected Component deserializeLines(@Nullable String prefix, @NotNull String[] textLines) {
    if (textLines.length == 0) return Component.empty();

    TextComponent.Builder rootBuilder = Component.text();
    Component prefixComponent = (prefix != null && !prefix.isEmpty()) ?
      prefixCache.computeIfAbsent(prefix, this::deserializeText) : null;

    // DISCUSSION: performance trade off for caching frequently used components vs. parsing on the fly
    for (int i = 0; i < textLines.length; i++) {
      if (textLines[i].isEmpty() || textLines[i].isBlank()) { // no prefix for empty lines
        rootBuilder.append(Component.newline());
        continue;
      }

      Component lineComponent = deserializeText(textLines[i]);

      if (prefixComponent != null) {
        rootBuilder.append(prefixComponent).append(lineComponent);
      } else {
        rootBuilder.append(lineComponent);
      }

      if (i < textLines.length - 1) {
        rootBuilder.append(Component.newline());
      }
    }

    return rootBuilder.build();
  }

  @NotNull
  protected List<Component> deserializeLinesAsListWithArgs(@NotNull String[] textLines, @NotNull Object[] args) {
    if (textLines.length == 0) return Collections.emptyList();

    boolean hasArgs = args.length > 0; // don't waste time on processing otherwise
    List<Component> components = new ArrayList<>(textLines.length + args.length); // might grow with args; extending is more expensive
    for (String textLine : textLines) {
      Component line = deserializeText(textLine);
      if (hasArgs) {
        List<Component> replaced = replacePositionalArgsAsList(line, args);
        components.addAll(replaced);
      } else {
        components.add(line);
      }
    }
    return components;
  }

  @NotNull
  protected Component replacePositionalArgs(@NotNull Component component, @Nullable Object[] positionalArgs) {
    if (positionalArgs == null || positionalArgs.length == 0) {
      return component;
    }

    return component.replaceText(TextReplacementConfig.builder()
      .match(POS_ARG_PATTERN)
      .replacement((matchResult, builder) -> {
        int index = Integer.parseInt(matchResult.group(1));
        if (index >= 0 && index < positionalArgs.length) {
          return convertToComponent(positionalArgs[index]);
        }
        return builder; // or else leave as is, no runtime exceptions
      }).build());
  }

  @NotNull
  protected List<Component> replacePositionalArgsAsList(@NotNull Component component, @Nullable Object[] positionalArgs) {
    if (positionalArgs == null || positionalArgs.length == 0) {
      return List.of(component);
    }

    Component replaced = replacePositionalArgs(component, positionalArgs);
    return ComponentSplitter.split(replaced, "\\n");
  }

  protected Component convertToComponent(@Nullable Object obj) {
    return switch (obj) {
      case null -> Component.empty();
      case Component component -> component;
      case ComponentLike like -> like.asComponent();
      case MessageHolder holder -> convertMessageHolderToComponent(holder);
      case Translatable translatable -> // net.kyori.adventure.translation.Translatable
        Component.translatable(translatable);
      case String string -> Component.text(string); // literal escaped text; not deserializeText
      case Material material -> TranslatableComponents.fromMaterial(material);
      case EntityType entityType -> TranslatableComponents.fromEntityType(entityType);
      case GameMode gameMode -> TranslatableComponents.fromGameMode(gameMode);
      case Player player -> getPlayerNameComponent(player);
      case Object _ when TranslatableComponents.isBukkitTranslatable(obj) -> // org.bukkit.Translatable
        TranslatableComponents.fromBukkitTranslatable(obj);
      case Enum<?> enumObj -> Component.text(stringifyFallbackEnumName(enumObj.name()));
      default -> Component.text(obj.toString());
    };
  }

  @NotNull
  public Component convertMessageHolderToComponent(@NotNull MessageHolder holder) {
    return deserializeLinesWithArgs(null, holder.miniMessageRaw(), holder.positionalArgs());
  }

  @NotNull
  protected Component deserializeText(@NotNull String textLine) {
    return deserializeText0(textLine)
      .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
  }

  @NotNull
  protected Component deserializeText0(@NotNull String textLine) {
    if (isProbablyLegacyText(textLine)) {
      return legacySerializer.deserialize(textLine);
    }
    try {
      return miniMessage.deserialize(textLine);
    } catch (Exception ex) { // maybe heuristic failed, try legacy again
      return legacySerializer.deserialize(textLine);
    }
  }

  @NotNull
  protected BossBar createDefaultAdventureBossBar(@NotNull Component initialText) {
    return BossBar.bossBar(initialText, 1f, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS);
  }

  protected boolean isProbablyLegacyText(@NotNull String text) {
    // legacy texts often start/end with a '§' color; heuristic so we don't have to check the full string
    if (text.length() < 2) return false;
    if (text.charAt(0) == '§') return true;
    return text.charAt(text.length() - 2) == '§';
  }

  protected String stringifyFallbackEnumName(@NotNull String enumName) {
    // EXAMPLE_ENUM -> "Example Enum"
    int len = enumName.length();
    if (len == 0) return "";

    char[] result = new char[len];
    boolean nextUpperCase = true;
    for (int i = 0; i < len; i++) {
      char letter = enumName.charAt(i);
      if (letter == '_') {
        result[i] = ' ';
        nextUpperCase = true;
      } else {
        if (nextUpperCase) {
          result[i] = (letter >= 'a' && letter <= 'z') ? (char) (letter - 32) : letter; // to uppercase
        } else {
          result[i] = (letter >= 'A' && letter <= 'Z') ? (char) (letter + 32) : letter; // to lowercase
        }
        nextUpperCase = false;
      }
    }
    return new String(result);
  }

}
