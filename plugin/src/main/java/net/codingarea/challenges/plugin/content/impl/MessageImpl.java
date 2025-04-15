package net.codingarea.challenges.plugin.content.impl;

import net.anweisen.utilities.common.collection.IRandom;
import net.anweisen.utilities.common.misc.StringUtils;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.ItemDescription;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.content.loader.LanguageLoader;
import net.codingarea.challenges.plugin.utils.bukkit.misc.BukkitStringUtils;
import net.codingarea.challenges.plugin.utils.misc.FontUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author anweisen | <a href="https://github.com/anweisen">...</a>
 * @since 2.0
 */
public class MessageImpl implements Message {

	protected final String name;
	protected String[] value;

	public MessageImpl(@Nonnull String name) {
		this.name = name;
	}

	@Nonnull
	protected static IRandom defaultRandom() {
		return IRandom.threadLocal();
	}

	@Nonnull
	@Override
	public String asString(@Nonnull Object... args) {
		if (value == null) return name;
		return String.join("\n", asArray(args));
	}

	@Nonnull
	@Override
	public BaseComponent asComponent(@Nonnull Object... args) {
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

	@Nonnull
	@Override
	public String asRandomString(@Nonnull Object... args) {
		return asRandomString(defaultRandom(), args);
	}

	@Nonnull
	@Override
	public String asRandomString(@Nonnull IRandom random, @Nonnull Object... args) {
		String[] array = asArray(args);
		if (array.length == 0) return Message.unknown(name);
		return random.choose(array);
	}

	@Nonnull
	@Override
	public BaseComponent asRandomComponent(@Nonnull IRandom random, @Nonnull Prefix prefix, @Nonnull Object... args) {
		BaseComponent[] array = asComponentArray(prefix, args);
		if (array.length == 0) return new TextComponent(Message.unknown(name));
		return random.choose(array);
	}

	@Nonnull
	@Override
	public String[] asArray(@Nonnull Object... args) {
		if (value == null) return new String[]{Message.unknown(name)};
		args = BukkitStringUtils.replaceArguments(args, true);
		LanguageLoader loader = Challenges.getInstance().getLoaderRegistry().getFirstLoaderByClass(LanguageLoader.class);
		boolean capsFont = false;
		if (loader != null) capsFont = loader.isSmallCapsFont();
		return capsFont ? FontUtils.toSmallCaps(StringUtils.format(value, args)) : StringUtils.format(value, args);
	}

	@Nonnull
	@Override
	public BaseComponent[] asComponentArray(@Nullable Prefix prefix, @Nonnull Object... args) {
		if (value == null) return new TextComponent[] { new TextComponent(Message.unknown(name)) };
		return BukkitStringUtils.format(prefix, value, args);
	}

	@Nonnull
	@Override
	public ItemDescription asItemDescription(@Nonnull Object... args) {
		if (value == null) {
			Message.unknown(name);
			return ItemDescription.empty();
		}
		return new ItemDescription(asArray(args));
	}

	@Override
	public void send(@Nonnull CommandSender target, @Nonnull Prefix prefix, @Nonnull Object... args) {
		doSendLines(component -> target.spigot().sendMessage(component), prefix, asComponentArray(prefix, args));
	}

	@Override
	public void sendRandom(@Nonnull CommandSender target, @Nonnull Prefix prefix, @Nonnull Object... args) {
		sendRandom(defaultRandom(), target, prefix, args);
	}

	@Override
	public void sendRandom(@Nonnull IRandom random, @Nonnull CommandSender target, @Nonnull Prefix prefix, @Nonnull Object... args) {
		doSendLine(components -> target.spigot().sendMessage(components), prefix, asRandomComponent(random, prefix, args));
	}

	@Override
	public void broadcast(@Nonnull Prefix prefix, @Nonnull Object... args) {
		doSendLines(components -> Bukkit.spigot().broadcast(components), prefix, asComponentArray(prefix, args));
	}

	@Override
	public void broadcastRandom(@Nonnull Prefix prefix, @Nonnull Object... args) {
		broadcastRandom(defaultRandom(), prefix, args);
	}

	@Override
	public void broadcastRandom(@Nonnull IRandom random, @Nonnull Prefix prefix, @Nonnull Object... args) {
		doSendLine(component -> Bukkit.spigot().broadcast(component), prefix, asRandomComponent(random, prefix, args));
	}

	private void doSendLines(@Nonnull Consumer<? super BaseComponent> sender, @Nonnull Prefix prefix, @Nonnull BaseComponent[] components) {
		for (BaseComponent line : components) {
			doSendLine(sender, prefix, line);
		}
	}

	private void doSendLine(@Nonnull Consumer<? super BaseComponent> sender, @Nonnull Prefix prefix, BaseComponent component) {
		LanguageLoader loader = Challenges.getInstance().getLoaderRegistry().getFirstLoaderByClass(LanguageLoader.class);
		boolean capsFont = false;
		if (loader != null) capsFont = loader.isSmallCapsFont();

		BaseComponent component1 = component;
		if (capsFont) {
			if (component1 instanceof TextComponent) {
				component1 = new TextComponent(FontUtils.toSmallCaps(((TextComponent) component1).getText()));
			}
			if (component != null) {
				List<BaseComponent> extra = component.getExtra();
				if (extra != null) {
					component.setExtra(new LinkedList<>());
					for (BaseComponent baseComponent : extra) {
						if (baseComponent instanceof TextComponent) {
							String text = ((TextComponent) baseComponent).getText();
							component1.addExtra(new TextComponent(text));
						} else {
							component1.addExtra(baseComponent);
						}
					}
				}
			}
		}

		// Weird bugs can cause this to be null if the line is empty and kicks the player in 1.19+
		if (component1 != null) {
			sender.accept(component1);
		}

	}

	@Override
	public void broadcastTitle(@Nonnull Object... args) {
		String[] title = asArray(args);
		Bukkit.getOnlinePlayers().forEach(player -> doSendTitle(player, title));
	}

	@Override
	public void sendTitle(@Nonnull Player player, @Nonnull Object... args) {
		doSendTitle(player, asArray(args));
	}

	@Override
	public void sendTitleInstant(@Nonnull Player player, @Nonnull Object... args) {
		doSendTitleInstant(player, asArray(args));
	}

	protected void doSendTitle(@Nonnull Player player, @Nonnull String[] title) {
		sendTitle(title, (line1, line2) -> Challenges.getInstance().getTitleManager().sendTitle(player, line1, line2));
	}

	protected void doSendTitleInstant(@Nonnull Player player, @Nonnull String[] title) {
		sendTitle(title, (line1, line2) -> Challenges.getInstance().getTitleManager().sendTitleInstant(player, line1, line2));
	}

	protected void sendTitle(@Nonnull String[] title, @Nonnull BiConsumer<String, String> send) {
		if (title.length == 0) send.accept("", "");
		else if (title.length == 1) send.accept(title[0], "");
		else send.accept(title[0], title[1]);
	}

	@Override
	public void setValue(@Nonnull String[] value) {
		this.value = value;
	}

	@Nonnull
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return asString();
	}

}
