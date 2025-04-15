package net.codingarea.challenges.plugin.challenges.custom.settings.sub.builder;

import com.google.common.collect.Lists;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SubSettingsBuilder;
import net.codingarea.challenges.plugin.management.menu.generator.implementation.custom.IParentCustomGenerator;
import net.codingarea.challenges.plugin.spigot.listener.ChatInputListener;
import net.codingarea.challenges.plugin.utils.item.DefaultItem;
import net.codingarea.challenges.plugin.utils.misc.MapUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.1.2
 */
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
	public boolean open(Player player, IParentCustomGenerator parentGenerator, String title) {
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

	@Override
	public List<String> getDisplay(Map<String, String[]> activated) {
		List<String> display = Lists.newLinkedList();

		for (Entry<String, String[]> entry : activated.entrySet()) {
			if (entry.getKey().equals(getKey())) {
				for (String value : entry.getValue()) {
					display.add("§7" + getKeyTranslation() + " " + DefaultItem.getItemPrefix() + value);
				}
			}
		}

		return display;
	}

	@Override
	public boolean hasSettings() {
		return true;
	}

}
