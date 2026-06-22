package net.codingarea.challenges.platform.message;

import com.google.errorprone.annotations.CheckReturnValue;
import net.codingarea.challenges.platform.message.wrapper.MessageBossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public interface MessagePlatform {

  Pattern POS_ARG_PATTERN = Pattern.compile("\\{(\\d+)\\}");

  char ESCAPE_CHAR = '\\';
  char START_ARG_CHAR = '{';
  char END_ARG_CHAR = '}';
  char REFERENCE_CHAR = '@';
  char START_REFERENCE_ARGS_CHAR = '(';
  char END_REFERENCE_ARGS_CHAR = ')';
  char REFERENCE_ARG_CHAR = '\'';

  void enable();

  void disable();

  void sendSenderMessage(@NotNull CommandSender sender, @Nullable String miniMessagePrefix,
                         @NotNull String[] miniMessageTextLines, @NotNull Object[] positionalArgs);

  void sendChatMessage(@NotNull Player player, @Nullable String miniMessagePrefix,
                       @NotNull String[] miniMessageTextLines, @NotNull Object[] positionalArgs);

  void sendActionBar(@NotNull Player player, @NotNull String miniMessageText, @NotNull Object[] positionalArgs);

  void sendTitle(@NotNull Player player, @NotNull String[] miniMessageTitleAndSubtitle, @NotNull Object[] positionalArgs);

  void sendTitle(@NotNull Player player, @NotNull String[] miniMessageTitleAndSubtitle, @NotNull Object[] positionalArgs,
                 int fadeInTicks, int stayTicks, int fadeOutTicks);

  @NotNull
  @CheckReturnValue
  Inventory createInventory(@NotNull InventoryHolder owner, int size,
                            @NotNull String miniMessageTitle, @NotNull Object[] positionalArgs);

  @NotNull
  @CheckReturnValue
  Inventory createInventory(@NotNull InventoryHolder owner, @NotNull InventoryType type,
                            @NotNull String miniMessageTitle, @NotNull Object[] positionalArgs);

  void applyItemName(@NotNull ItemMeta item, @NotNull String miniMessageDisplayName, @NotNull Object[] positionalArgs);

  void appendItemName(@NotNull ItemMeta item, @NotNull String miniMessageDisplayNameSuffix, @NotNull Object[] positionalArgs);

  void applyItemLore(@NotNull ItemMeta item, @NotNull String[] miniMessageLore, @NotNull Object[] positionalArgs);

  void appendItemLore(@NotNull ItemMeta item, @NotNull String[] miniMessageLore, @NotNull Object[] positionalArgs);

  @NotNull
  MessageBossBar createBossBar(@NotNull MessageHolder initialTitle);

}
