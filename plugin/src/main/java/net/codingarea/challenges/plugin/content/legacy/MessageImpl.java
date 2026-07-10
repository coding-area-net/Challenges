package net.codingarea.challenges.plugin.content.legacy;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.i18n.Prefix;
import net.codingarea.challenges.plugin.utils.bukkit.misc.BukkitStringUtils;
import net.codingarea.commons.common.collection.IRandom;
import net.codingarea.commons.common.misc.StringUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class MessageImpl implements Message {

  protected final String name;
  protected String[] value;

  public MessageImpl(@NotNull String name) {
    this.name = name;
  }

  @NotNull
  protected static IRandom defaultRandom() {
    return IRandom.threadLocal();
  }

  @NotNull
  @Override
  public String asString(@NotNull Object... args) {
    if (value == null) return name;
    return String.join("\n", asArray(args));
  }

  @NotNull
  @Override
  public BaseComponent asComponent(@NotNull Object... args) {
    if (value == null) return new TextComponent(name);
    BaseComponent[] components = asComponentArray(null, args);
    BaseComponent first = null;
    // TODO: This will bug with colors as they wont be added to the next line
    for (BaseComponent component : components) {
      if (first == null) first = component;
      else first.addExtra(component);
    }
    return first == null ? new TextComponent() : first;
  }

  @NotNull
  @Override
  public String asRandomString(@NotNull Object... args) {
    return asRandomString(defaultRandom(), args);
  }

  @NotNull
  @Override
  public String asRandomString(@NotNull IRandom random, @NotNull Object... args) {
    String[] array = asArray(args);
    if (array.length == 0) return Message.unknown(name);
    return random.choose(array);
  }

  @NotNull
  @Override
  public BaseComponent asRandomComponent(@NotNull IRandom random, @NotNull Prefix prefix, @NotNull Object... args) {
    BaseComponent[] array = asComponentArray(prefix, args);
    if (array.length == 0) return new TextComponent(Message.unknown(name));
    return random.choose(array);
  }

  @NotNull
  @Override
  public String[] asArray(@NotNull Object... args) {
    if (value == null) return new String[]{Message.unknown(name)};
    args = BukkitStringUtils.replaceArguments(args, true);
    return StringUtils.format(value, args);
  }

  @NotNull
  @Override
  public BaseComponent[] asComponentArray(@Nullable Prefix prefix, @NotNull Object... args) {
    if (value == null) return new TextComponent[]{new TextComponent(Message.unknown(name))};
    return BukkitStringUtils.format(prefix, value, args);
  }

  @NotNull
  @Override
  public ItemDescription asItemDescription(@NotNull Object... args) {
    if (value == null) {
      Message.unknown(name);
      return ItemDescription.empty();
    }
    return new ItemDescription(asArray(args));
  }

  @Override
  public void send(@NotNull CommandSender target, @NotNull Prefix prefix, @NotNull Object... args) {
    doSendLines(component -> target.spigot().sendMessage(component), prefix, asComponentArray(prefix, args));
  }

  @Override
  public void sendRandom(@NotNull CommandSender target, @NotNull Prefix prefix, @NotNull Object... args) {
    sendRandom(defaultRandom(), target, prefix, args);
  }

  @Override
  public void sendRandom(@NotNull IRandom random, @NotNull CommandSender target, @NotNull Prefix prefix, @NotNull Object... args) {
    doSendLine(components -> target.spigot().sendMessage(components), prefix, asRandomComponent(random, prefix, args));
  }

  @Override
  public void broadcast(@NotNull Prefix prefix, @NotNull Object... args) {
    doSendLines(components -> Bukkit.spigot().broadcast(components), prefix, asComponentArray(prefix, args));
  }

  @Override
  public void broadcastRandom(@NotNull Prefix prefix, @NotNull Object... args) {
    broadcastRandom(defaultRandom(), prefix, args);
  }

  @Override
  public void broadcastRandom(@NotNull IRandom random, @NotNull Prefix prefix, @NotNull Object... args) {
    doSendLine(component -> Bukkit.spigot().broadcast(component), prefix, asRandomComponent(random, prefix, args));
  }

  private void doSendLines(@NotNull Consumer<? super BaseComponent> sender, @NotNull Prefix prefix, @NotNull BaseComponent[] components) {
    for (BaseComponent line : components) {
      doSendLine(sender, prefix, line);
    }
  }

  private void doSendLine(@NotNull Consumer<? super BaseComponent> sender, @NotNull Prefix prefix, BaseComponent component) {
    BaseComponent component1 = component;

    // Weird bugs can cause this to be null if the line is empty and kicks the player in 1.19+
    if (component1 != null) {
      sender.accept(component1);
    }

  }

  @Override
  public void broadcastTitle(@NotNull Object... args) {
    String[] title = asArray(args);
    Bukkit.getOnlinePlayers().forEach(player -> doSendTitle(player, title));
  }

  @Override
  public void sendTitle(@NotNull Player player, @NotNull Object... args) {
    doSendTitle(player, asArray(args));
  }

  @Override
  public void sendTitleInstant(@NotNull Player player, @NotNull Object... args) {
    doSendTitleInstant(player, asArray(args));
  }

  protected void doSendTitle(@NotNull Player player, @NotNull String[] title) {
    sendTitle(title, (line1, line2) -> Challenges.getInstance().getTitleManager().sendTitle(player, line1, line2));
  }

  protected void doSendTitleInstant(@NotNull Player player, @NotNull String[] title) {
    sendTitle(title, (line1, line2) -> Challenges.getInstance().getTitleManager().sendTitleInstant(player, line1, line2));
  }

  protected void sendTitle(@NotNull String[] title, @NotNull BiConsumer<String, String> send) {
    if (title.length == 0) send.accept("", "");
    else if (title.length == 1) send.accept(title[0], "");
    else send.accept(title[0], title[1]);
  }

  @Override
  public void setValue(@NotNull String[] value) {
    this.value = value;
  }

  @NotNull
  @Override
  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return asString();
  }

}
