package net.codingarea.challenges.plugin.content.i18n.impl.format;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ComponentFormatter {

  public static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
  public static final LegacyComponentSerializer LEGACY_SECTION = LegacyComponentSerializer.legacySection(); // temp migration impl

  private static final Map<String, Component> prefixCache = new ConcurrentHashMap<>();

  private ComponentFormatter() {
  }

  @NotNull
  public static Component deserializeLinesWithArgs(@Nullable String prefix, @NotNull String[] textLines, @NotNull Object[] positionalArgs) {
    return replacePositionalArgs(deserializeLines(prefix, textLines), positionalArgs);
  }

  @NotNull
  public static Component deserializeLineWithArgs(@NotNull String line, @NotNull Object[] positionalArgs) {
    return replacePositionalArgs(deserializeText(line), positionalArgs);
  }

  @NotNull
  public static Component deserializeLines(@Nullable String prefix, @NotNull String[] textLines) {
    if (textLines.length == 0) return Component.empty();

    // Component.text() (Builder) incompatible in version change 26.1.2 -> 26.2
    Component rootComponent = Component.empty();

    Component prefixComponent = (prefix != null && !prefix.isEmpty()) ?
      prefixCache.computeIfAbsent(prefix, ComponentFormatter::deserializeText) : null;

    // DISCUSSION: performance trade off for caching frequently used components vs. parsing on the fly
    for (int i = 0; i < textLines.length; i++) {
      if (textLines[i].isBlank()) { // no prefix for empty lines
        rootComponent = rootComponent.append(Component.newline());
        continue;
      }

      Component lineComponent = deserializeText(textLines[i]);

      if (prefixComponent != null) {
        rootComponent = rootComponent.append(prefixComponent).append(lineComponent);
      } else {
        rootComponent = rootComponent.append(lineComponent);
      }

      if (i < textLines.length - 1) {
        rootComponent = rootComponent.append(Component.newline());
      }
    }

    return rootComponent;
  }

  @NotNull
  public static List<Component> deserializeLinesAsListWithArgs(@NotNull String[] textLines, @NotNull Object[] args) {
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
  public static Component replacePositionalArgs(@NotNull Component component, @Nullable Object[] positionalArgs) {
    if (positionalArgs == null || positionalArgs.length == 0) {
      return component;
    }

    return component.replaceText(TextReplacementConfig.builder()
      .match(MessageFormatter.POS_ARG_PATTERN)
      .replacement((matchResult, builder) -> {
        int index = Integer.parseInt(matchResult.group(1));
        if (index >= 0 && index < positionalArgs.length) {
          return ComponentArguments.convertToComponent(positionalArgs[index]);
        }
        return builder; // or else leave as is, no runtime exceptions
      }).build());
  }

  @NotNull
  public static List<Component> replacePositionalArgsAsList(@NotNull Component component, @Nullable Object[] positionalArgs) {
    if (positionalArgs == null || positionalArgs.length == 0) {
      return List.of(component);
    }

    Component replaced = replacePositionalArgs(component, positionalArgs);
    return ComponentSplitter.split(replaced);
  }

  @NotNull
  static Component deserializeText(@NotNull String textLine) {
    return applyDecorationIfAbsent(deserializeText0(textLine), TextDecoration.ITALIC, TextDecoration.State.FALSE);
  }

  @NotNull
  private static Component applyDecorationIfAbsent(@NotNull Component component, @NotNull TextDecoration decoration, @NotNull TextDecoration.State value) {
    // Component#decorationIfAbsent introduced in 1.19.3
    if (component.decoration(decoration) == TextDecoration.State.NOT_SET) {
      component = component.decoration(decoration, value);
    }
    return component;
  }

  @NotNull
  private static Component deserializeText0(@NotNull String textLine) {
    // TODO legacy text should only be supported during migration; impl will be removed in the future
    if (isProbablyLegacyText(textLine)) {
      return LEGACY_SECTION.deserialize(textLine);
    }
    try {
      return MINI_MESSAGE.deserialize(textLine);
    } catch (Exception ex) { // maybe heuristic failed, try legacy again
      return LEGACY_SECTION.deserialize(textLine);
    }
  }

  private static boolean isProbablyLegacyText(@NotNull String text) {
    // legacy texts often start/end with a '§' color; heuristic so we don't have to check the full string
    if (text.length() < 2) return false;
    if (text.charAt(0) == '§') return true;
    return text.charAt(text.length() - 2) == '§';
  }

}
