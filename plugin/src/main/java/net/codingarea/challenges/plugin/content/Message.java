package net.codingarea.challenges.plugin.content;

import net.anweisen.utilities.bukkit.utils.logging.Logger;
import net.anweisen.utilities.common.collection.IRandom;
import net.codingarea.challenges.plugin.content.impl.MessageManager;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author anweisen | <a href="https://github.com/anweisen">...</a>
 * @since 2.0
 */
public interface Message {

	String NULL = "§r§fN/A";
	Collection<String> UNKNOWN_MESSAGES = new ArrayList<>();

	static String unknown(@Nonnull String name) {
		if (!UNKNOWN_MESSAGES.contains(name)) {
			UNKNOWN_MESSAGES.add(name);
			Logger.warn("Tried accessing unknown messages '{}'", name);
		}

		return name;
	}

	@Nonnull
	@CheckReturnValue
	static Message forName(@Nonnull String name) {
		return MessageManager.getOrCreateMessage(name);
	}

	@Nonnull
	String asString(@Nonnull Object... args);

	@Nonnull
	BaseComponent asComponent(@Nonnull Object... args);

	@Nonnull
	String asRandomString(@Nonnull IRandom random, @Nonnull Object... args);

	@Nonnull
	BaseComponent asRandomComponent(@Nonnull IRandom random, @Nonnull Prefix prefix, @Nonnull Object... args);

	@Nonnull
	String asRandomString(@Nonnull Object... args);

	@Nonnull
	String[] asArray(@Nonnull Object... args);

	@Nonnull
	BaseComponent[] asComponentArray(@Nullable Prefix prefix, @Nonnull Object... args);

	@Nonnull
	ItemDescription asItemDescription(@Nonnull Object... args);

	void send(@Nonnull CommandSender target, @Nonnull Prefix prefix, @Nonnull Object... args);

	void sendRandom(@Nonnull CommandSender target, @Nonnull Prefix prefix, @Nonnull Object... args);

	void sendRandom(@Nonnull IRandom random, @Nonnull CommandSender target, @Nonnull Prefix prefix, @Nonnull Object... args);

	void broadcast(@Nonnull Prefix prefix, @Nonnull Object... args);

	void broadcastRandom(@Nonnull Prefix prefix, @Nonnull Object... args);

	void broadcastRandom(@Nonnull IRandom random, @Nonnull Prefix prefix, @Nonnull Object... args);

	void broadcastTitle(@Nonnull Object... args);

	void sendTitle(@Nonnull Player player, @Nonnull Object... args);

	void sendTitleInstant(@Nonnull Player player, @Nonnull Object... args);

	void setValue(@Nonnull String[] value);

	@Nonnull
	String getName();

}
