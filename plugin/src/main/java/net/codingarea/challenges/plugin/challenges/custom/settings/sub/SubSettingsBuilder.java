package net.codingarea.challenges.plugin.challenges.custom.settings.sub;

import net.anweisen.utilities.common.misc.StringUtils;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.builder.*;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.impl.MessageManager;
import net.codingarea.challenges.plugin.management.menu.generator.implementation.custom.IParentCustomGenerator;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class SubSettingsBuilder {

	private String key;
	private SubSettingsBuilder parent;
	private SubSettingsBuilder child;

	protected SubSettingsBuilder(String key) {
		this.key = key;
		parent = null;
	}

	protected SubSettingsBuilder(String key, SubSettingsBuilder parent) {
		this.key = key;
		this.parent = parent;
	}

	public static ChooseItemSubSettingsBuilder createChooseItem(String key) {
		return new ChooseItemSubSettingsBuilder(key);
	}

	public static ValueSubSettingsBuilder createValueItem() {
		return new ValueSubSettingsBuilder();
	}

	public static ChooseMultipleItemSubSettingBuilder createChooseMultipleItem(String key) {
		return new ChooseMultipleItemSubSettingBuilder(key);
	}

	public static TextInputSubSettingsBuilder createTextInput(String key,
															  Consumer<Player> onOpen,
															  Predicate<AsyncPlayerChatEvent> isValid) {
		return new TextInputSubSettingsBuilder(key, onOpen, isValid);
	}

	public static EmptySubSettingsBuilder createEmpty() {
		return new EmptySubSettingsBuilder();
	}

	public abstract boolean open(Player player, IParentCustomGenerator parentGenerator, String title);

	public abstract List<String> getDisplay(Map<String, String[]> activated);

	public abstract boolean hasSettings();

	public SubSettingsBuilder getParent() {
		return parent;
	}

	public SubSettingsBuilder setParent(SubSettingsBuilder parent) {

		SubSettingsBuilder parentBuilder = getParent();
		if (parentBuilder != null) {
			return parentBuilder.setParent(parent);
		} else {
			this.parent = parent;
			return this;
		}

	}

	public SubSettingsBuilder getChild() {
		return child;
	}

	public String getKey() {
		return key;
	}

	public SubSettingsBuilder setKey(String key) {
		this.key = key;
		return this;
	}

	public String getKeyTranslation() {
		String messageName = "custom-subsetting-" + key;
		return MessageManager.hasMessageInCache(messageName)
				? Message.forName(messageName).asString() : StringUtils.getEnumName(key);
	}

	public List<SubSettingsBuilder> getAllChildren() {
		LinkedList<SubSettingsBuilder> children = new LinkedList<>(Collections.singleton(this));

		SubSettingsBuilder last = this;

		while (last != null) {
			if (last.getChild() != null) {
				children.add(last.getChild());
			}
			last = last.getChild();
		}

		return children;
	}

	/**
	 * @return the first parent that was created.
	 * Only required if first builder has one ore more children.
	 */
	public SubSettingsBuilder build() {
		return parent == null ? this : parent.build();
	}

	/**
	 * Sets the highest parent of a child as the child of this builder.
	 *
	 * @param child one of the child builders that are added.
	 * @return the highest parent of that child
	 */
	public SubSettingsBuilder addChild(SubSettingsBuilder child) {
		this.child = child.setParent(this);
		return child;
	}

	public ChooseItemSubSettingsBuilder createChooseItemChild(String key) {
		ChooseItemSubSettingsBuilder builder = new ChooseItemSubSettingsBuilder(key, this);
		this.child = builder;
		return builder;
	}

	public ValueSubSettingsBuilder createValueChild() {
		ValueSubSettingsBuilder builder = new ValueSubSettingsBuilder(this);
		this.child = builder;
		return builder;
	}

	public ChooseMultipleItemSubSettingBuilder createChooseMultipleChild(String key) {
		ChooseMultipleItemSubSettingBuilder builder = new ChooseMultipleItemSubSettingBuilder(key, this);
		this.child = builder;
		return builder;
	}

	public TextInputSubSettingsBuilder createTextInputChild(String key,
															Consumer<Player> onOpen,
															Predicate<AsyncPlayerChatEvent> isValid) {
		TextInputSubSettingsBuilder builder = new TextInputSubSettingsBuilder(key,
				this, onOpen, isValid);
		this.child = builder;
		return builder;
	}

}
