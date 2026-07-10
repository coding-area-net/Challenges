package net.codingarea.challenges.plugin.content.i18n.impl.format;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
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

  /**
   * MiniMessage tag name prefix used to insert positional arguments ({@code {0}}, {@code {1}}, ...)
   * at <b>parse time</b> instead of substituting them into the already parsed component.
   * <p>
   * Post-parse {@link Component#replaceText} cannot reach a placeholder that sits inside a
   * {@code <gradient>} or {@code <rainbow>} tag: those tags split their content into one component
   * <i>per character</i>, so a multi-character match such as {@code "{0}"} is scattered across
   * several text nodes ({@code "{"}, {@code "0"}, {@code "}"}) and is silently left untouched.
   * Resolving the argument as a MiniMessage {@link Tag#inserting(Component) inserting} tag while
   * parsing sidesteps the problem entirely and lets the gradient colorize the inserted content.
   *
   * @see <a href="https://github.com/PaperMC/adventure/issues/1133">PaperMC/adventure#1133</a>
   */
  static final String ARG_TAG_PREFIX = "_carg_"; // e.g. {0} -> <_carg_0>

  private static final Map<String, Component> prefixCache = new ConcurrentHashMap<>();

  private ComponentFormatter() {
  }

  @NotNull
  public static Component deserializeLinesWithArgs(@Nullable String prefix, @NotNull String[] textLines, @NotNull Object[] positionalArgs) {
    if (positionalArgs.length == 0) {
      return deserializeLines(prefix, textLines);
    }
    return deserializeLines(prefix, textLines, new PositionalArgResolver(positionalArgs));
  }

  @NotNull
  public static Component deserializeLines(@Nullable String prefix, @NotNull String[] textLines) {
    return deserializeLines(prefix, textLines, null);
  }

  @NotNull
  private static Component deserializeLines(@Nullable String prefix, @NotNull String[] textLines, @Nullable PositionalArgResolver args) {
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

      // args (if any) are inserted at parse time so they render correctly inside <gradient>/<rainbow>
      Component lineComponent = args == null ? deserializeText(textLines[i]) : deserializeText(textLines[i], args);

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

    if (args.length == 0) { // don't waste time on processing otherwise
      List<Component> components = new ArrayList<>(textLines.length);
      for (String textLine : textLines) {
        components.add(deserializeText(textLine));
      }
      return components;
    }

    PositionalArgResolver resolver = new PositionalArgResolver(args);
    List<Component> components = new ArrayList<>(textLines.length + args.length); // might grow with args; extending is more expensive
    for (String textLine : textLines) {
      // an inserted arg (e.g. a multi-line MessageHolder) can introduce newlines, so we still
      // split the parsed component into separate lines afterwards
      components.addAll(ComponentSplitter.split(deserializeText(textLine, resolver)));
    }
    return components;
  }

  /**
   * Replaces {@code {n}} placeholders in an <b>already parsed</b> component.
   * <p>
   * Kept for the legacy ({@code §}) deserialization path only. It cannot reach placeholders inside
   * {@code <gradient>}/{@code <rainbow>} tags (see {@link #ARG_TAG_PREFIX}); the MiniMessage path
   * inserts arguments at parse time instead via {@link PositionalArgResolver}.
   */
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
  static Component deserializeText(@NotNull String textLine) {
    return applyDecorationIfAbsent(deserializeText0(textLine), TextDecoration.ITALIC, TextDecoration.State.FALSE);
  }

  @NotNull
  static Component deserializeText(@NotNull String textLine, @NotNull PositionalArgResolver args) {
    return applyDecorationIfAbsent(deserializeText0(textLine, args), TextDecoration.ITALIC, TextDecoration.State.FALSE);
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

  @NotNull
  private static Component deserializeText0(@NotNull String textLine, @NotNull PositionalArgResolver args) {
    // legacy text cannot carry MiniMessage placeholders (and never contains gradients),
    // so fall back to post-parse replacement for it
    if (isProbablyLegacyText(textLine)) {
      return replacePositionalArgs(LEGACY_SECTION.deserialize(textLine), args.args);
    }
    try {
      // rewrite {n} -> <_carg_n> so args are inserted while parsing (works inside gradients/rainbows)
      return MINI_MESSAGE.deserialize(rewritePositionalPlaceholders(textLine, args.args.length), args);
    } catch (Exception ex) { // maybe heuristic failed, try legacy on the original (un-rewritten) line
      return replacePositionalArgs(LEGACY_SECTION.deserialize(textLine), args.args);
    }
  }

  /**
   * Rewrites every in-bounds positional placeholder {@code {n}} into its MiniMessage tag form
   * {@code <_carg_n>} so it can be resolved at parse time by {@link PositionalArgResolver}.
   * Out-of-bounds indices, empty braces and non-numeric braces (e.g. {@code {mm}}) are left
   * untouched. Returns the original instance unchanged when there is nothing to rewrite.
   */
  @NotNull
  static String rewritePositionalPlaceholders(@NotNull String line, int argCount) {
    int len = line.length();
    StringBuilder builder = null; // lazily allocated: zero-copy when the line has no placeholder
    int copiedUpTo = 0;
    int i = 0;
    while (i < len) {
      if (line.charAt(i) == MessageFormatter.START_ARG_CHAR) {
        int j = i + 1;
        int index = 0;
        boolean hasDigit = false;
        while (j < len) {
          char digit = line.charAt(j);
          if (digit < '0' || digit > '9') break;
          index = index * 10 + (digit - '0');
          hasDigit = true;
          j++;
        }
        if (hasDigit && j < len && line.charAt(j) == MessageFormatter.END_ARG_CHAR && index < argCount) {
          if (builder == null) builder = new StringBuilder(len + 8);
          builder.append(line, copiedUpTo, i).append('<').append(ARG_TAG_PREFIX).append(index).append('>');
          i = j + 1;
          copiedUpTo = i;
          continue;
        }
      }
      i++;
    }
    if (builder == null) return line;
    return builder.append(line, copiedUpTo, len).toString();
  }

  private static boolean isProbablyLegacyText(@NotNull String text) {
    // legacy texts often start/end with a '§' color; heuristic so we don't have to check the full string
    if (text.length() < 2) return false;
    if (text.charAt(0) == '§') return true;
    return text.charAt(text.length() - 2) == '§';
  }

  /**
   * Resolves the {@code <_carg_n>} tags produced by {@link #rewritePositionalPlaceholders} into the
   * matching positional argument, converted via {@link ComponentArguments#convertToComponent(Object)}.
   * Converted components are cached per index, so a placeholder used multiple times is only converted
   * once. A single instance is reused across all lines of one message.
   */
  static final class PositionalArgResolver implements TagResolver {

    private final Object[] args;
    private final Component[] cache;

    PositionalArgResolver(@NotNull Object[] args) {
      this.args = args;
      this.cache = new Component[args.length];
    }

    @Override
    public boolean has(@NotNull String name) {
      return argIndex(name) >= 0;
    }

    @Nullable
    @Override
    public Tag resolve(@NotNull String name, @NotNull ArgumentQueue arguments, @NotNull Context ctx) {
      int index = argIndex(name);
      if (index < 0) return null; // not ours -> let MiniMessage try its standard tags
      Component component = cache[index];
      if (component == null) {
        component = ComponentArguments.convertToComponent(args[index]);
        cache[index] = component;
      }
      return Tag.inserting(component);
    }

    private int argIndex(@NotNull String name) {
      int prefixLength = ARG_TAG_PREFIX.length();
      if (name.length() <= prefixLength || !name.startsWith(ARG_TAG_PREFIX)) return -1;
      int index = 0;
      for (int i = prefixLength; i < name.length(); i++) {
        char digit = name.charAt(i);
        if (digit < '0' || digit > '9') return -1;
        index = index * 10 + (digit - '0');
      }
      return index < args.length ? index : -1;
    }
  }
}
