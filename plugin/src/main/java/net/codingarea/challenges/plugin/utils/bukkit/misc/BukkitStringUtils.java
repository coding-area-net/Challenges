package net.codingarea.challenges.plugin.utils.bukkit.misc;

import net.codingarea.commons.bukkit.utils.misc.MinecraftVersion;
import net.codingarea.commons.common.collection.WrappedException;
import net.codingarea.commons.common.logging.ILogger;
import net.codingarea.challenges.plugin.content.Prefix;
import net.md_5.bungee.api.chat.*;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.loot.LootTable;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

public class BukkitStringUtils {

  @Nonnull
  public static BaseComponent[] format(@Nullable Prefix prefix, @Nonnull String[] array, @Nonnull Object... args) {
    List<BaseComponent> results = new ArrayList<>();
    for (String value : array) {
      String s = value;
      if (!s.trim().isEmpty() && prefix != null) {
        s = prefix + s;
      }
      BaseComponent comp = null;
      for (BaseComponent component : format(s, args)) {
        if (comp == null) {
          comp = component;
        } else {
          comp.addExtra(component);
        }
      }
      results.add(comp);
    }
    return results.toArray(new BaseComponent[0]);
  }

  @Nonnull
  public static List<BaseComponent> format(@Nonnull String sequence, @Nonnull Object... args) {

    args = replaceArguments(args, false);

    List<BaseComponent> results = new ArrayList<>();
    char start = '{', end = '}';
    boolean inArgument = false;

    boolean lastWasParagraph = false;
    ChatColor currentColor = null;
    List<ChatColor> currentFormatting = new LinkedList<>();

    StringBuilder argument = new StringBuilder();
    TextComponent currentText = new TextComponent();
    for (char c : sequence.toCharArray()) {

      if (c == '§') {
        lastWasParagraph = true;
      } else {
        if (lastWasParagraph) {
          ChatColor newColor = ChatColor.getByChar(c);
          assert newColor != null;
          if (!newColor.isColor()) {
            if (newColor == ChatColor.RESET) {
              currentFormatting.clear();
              currentColor = null;
            } else {
              currentFormatting.add(newColor);
            }
          } else {
            currentColor = newColor;
            currentFormatting.clear();
          }
        }
        lastWasParagraph = false;
      }

      if (c == end && inArgument) {
        inArgument = false;
        try {
          int arg = Integer.parseInt(argument.toString());
          Object current = args[arg];
          BaseComponent replacement =
            current instanceof BaseComponent ? (BaseComponent) current :
              current instanceof Supplier ? new TextComponent(String.valueOf(((Supplier<?>) current).get())) :
                current instanceof Callable ? new TextComponent(String.valueOf(((Callable<?>) current).call())) :
                  new TextComponent(String.valueOf(current));

          if (replacement instanceof TextComponent) {
            currentText.setText(currentText.getText() + ((TextComponent) replacement).getText());
          } else {
            results.add(currentText);
            currentText = new TextComponent();

            if (currentColor != null && replacement.getColor() == net.md_5.bungee.api.ChatColor.WHITE) {
              replacement.setColor(currentColor.asBungee());
            }
            for (ChatColor color : currentFormatting) {
              switch (color) {
                case BOLD:
                  replacement.setBold(true);
                  break;
                case MAGIC:
                  replacement.setObfuscated(true);
                  break;
                case ITALIC:
                  replacement.setItalic(true);
                  break;
                case STRIKETHROUGH:
                  replacement.setStrikethrough(true);
                  break;
                case UNDERLINE:
                  replacement.setUnderlined(true);
                  break;
              }
            }
            results.add(replacement);
          }

          currentColor = null;
        } catch (NumberFormatException | IndexOutOfBoundsException ex) {
          ILogger.forThisClass().warn("Invalid argument index '{}'", argument);
          results.add(new TextComponent(String.valueOf(start)));
          results.add(new TextComponent(String.valueOf(argument)));
          results.add(new TextComponent(String.valueOf(end)));
        } catch (Exception ex) {
          throw new WrappedException(ex);
        }
        argument = new StringBuilder();
        continue;
      }
      if (c == start && !inArgument) {
        inArgument = true;
        continue;
      }
      if (inArgument) {
        argument.append(c);
        continue;
      }
      currentText = new TextComponent(currentText.getText() + c);
    }
    if (!currentText.getText().isEmpty()) {
      results.add(currentText);
    }
    if (argument.length() > 0) {
      results.add(new TextComponent(String.valueOf(start)));
      results.add(new TextComponent(String.valueOf(argument)));
    }
    return results;
  }

  public static Object[] replaceArguments(Object[] args, boolean toStrings) {
    args = Arrays.copyOf(args, args.length);
    for (int i = 0; i < args.length; i++) {
      Object arg = args[i];

      arg = arg instanceof Material ? getItemComponent((Material) arg) :
        arg instanceof EntityType ? getEntityName((EntityType) arg) :
          arg instanceof PotionEffectType ? getPotionEffectName((PotionEffectType) arg) :
            arg instanceof Biome ? getBiomeName((Biome) arg) :
              arg instanceof GameMode ? getGameModeName((GameMode) arg) :
                arg instanceof Advancement ? getAdvancementComponent((Advancement) arg) :
                  arg instanceof LootTable ? getEntityName((LootTable) arg) :
                    arg instanceof Difficulty ? getDifficultyName((Difficulty) arg) :
                      arg;

      if (toStrings) {
        if (arg instanceof BaseComponent) {
          arg = ((BaseComponent) arg).toPlainText();
        }
      }
      args[i] = arg;

    }
    return args;
  }

  public static TranslatableComponent getItemName(@Nonnull Material material) {
    NamespacedKey key = material.getKey();
    return new TranslatableComponent((material.isBlock() ? "block" : "item") + "." + key.getNamespace() + "." + key.getKey());
  }

  public static @Nullable BaseComponent getMusicDiscName(@Nonnull Material material) {
    if (!material.name().startsWith("MUSIC_DISC")) return null;
    String key = "item.minecraft." + material.name().toLowerCase() + ".desc";
    return new TranslatableComponent(key);
  }

  public static BaseComponent getItemComponent(@Nonnull Material material) {
    BaseComponent component = getItemName(material);
    BaseComponent musicDiscName = getMusicDiscName(material);
    if (musicDiscName != null) {
      component.addExtra(" (");
      component.addExtra(musicDiscName);
      component.addExtra(")");
    }
    return component;
  }


  public static TranslatableComponent getEntityName(@Nonnull EntityType type) {

    String key;
    String namespace = "minecraft";

    if (MinecraftVersion.current().isNewerOrEqualThan(MinecraftVersion.V1_14)) {
      NamespacedKey namespacedKey = type.getKey();
      key = namespacedKey.getKey();
      namespace = namespacedKey.getNamespace();
    } else {
      key = type.getName();
    }

    return new TranslatableComponent("entity." + namespace + "." + key);
  }

  public static TranslatableComponent getEntityName(@Nonnull LootTable type) {
    NamespacedKey key = type.getKey();
    return new TranslatableComponent("entity." + key.getNamespace() + "." + key.getKey().replace("entities/", ""));
  }

  public static TranslatableComponent getPotionEffectName(@Nonnull PotionEffectType type) {

    String key;
    String namespace = "minecraft";

    if (MinecraftVersion.current().isNewerOrEqualThan(MinecraftVersion.V1_14)) {
      NamespacedKey namespacedKey = type.getKey();
      key = namespacedKey.getKey();
      namespace = namespacedKey.getNamespace();
    } else {
      key = type.getName().toLowerCase();
    }


    return new TranslatableComponent("effect." + namespace + "." + key);
  }

  public static TranslatableComponent getBiomeName(@Nonnull Biome biome) {
    String key;
    String namespace = "minecraft";

    if (MinecraftVersion.current().isNewerOrEqualThan(MinecraftVersion.V1_14)) {
      NamespacedKey namespacedKey = biome.getKey();
      key = namespacedKey.getKey();
      namespace = namespacedKey.getNamespace();
    } else {
      key = biome.name().toLowerCase();
    }

    return new TranslatableComponent("biome." + namespace + "." + key);
  }

  public static TranslatableComponent getGameModeName(@Nonnull GameMode gameMode) {
    return new TranslatableComponent("selectWorld.gameMode." + gameMode.name().toLowerCase());
  }

  public static BaseComponent getAdvancementTitle(@Nonnull Advancement advancement) {
    String replace = advancement.getKey().getKey().replace("/", ".");
    return new TranslatableComponent("advancements." + correctAdvancementKeys(replace) + ".title");
  }

  public static BaseComponent getAdvancementDescription(@Nonnull Advancement advancement) {
    String replace = advancement.getKey().getKey().replace("/", ".");
    return new TranslatableComponent("advancements." + correctAdvancementKeys(replace) + ".description");
  }

  public static BaseComponent getAdvancementComponent(@Nonnull Advancement advancement) {
    BaseComponent title = getAdvancementTitle(advancement);
    BaseComponent description = getAdvancementDescription(advancement);
    description.setColor(net.md_5.bungee.api.ChatColor.GREEN);
    title.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(description).create()));
    return title;
  }

  private static String correctAdvancementKeys(String s) {
    return s.replace("bred_all_animals", "breed_all_animals").replace("obtain_netherite_hoe", "netherite_hoe"); // mc sucks
  }

  public static TranslatableComponent getDifficultyName(@Nonnull Difficulty difficulty) {
    return new TranslatableComponent("options.difficulty." + difficulty.name().toLowerCase());
  }

}
