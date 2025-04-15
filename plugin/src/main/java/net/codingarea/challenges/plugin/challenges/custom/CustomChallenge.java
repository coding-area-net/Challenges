package net.codingarea.challenges.plugin.challenges.custom;

import lombok.Getter;
import lombok.ToString;
import net.anweisen.utilities.common.config.Document;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.custom.settings.ChallengeExecutionData;
import net.codingarea.challenges.plugin.challenges.custom.settings.action.ChallengeAction;
import net.codingarea.challenges.plugin.challenges.custom.settings.trigger.ChallengeTrigger;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.implementation.custom.InfoMenuGenerator;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.1.0
 */
@Getter
@ToString
public class CustomChallenge extends Setting {

	private final UUID uuid;
	private Material material;
	private String name;
	private ChallengeTrigger trigger;
	private Map<String, String[]> subTriggers;
	private ChallengeAction action;
	private Map<String, String[]> subActions;

	public CustomChallenge(MenuType menuType, UUID uuid, Material displayItem, String displayName, ChallengeTrigger trigger,
						   Map<String, String[]> subTriggers, ChallengeAction action, Map<String, String[]> subActions) {
		super(menuType);
		this.uuid = uuid;
		this.material = displayItem;
		this.name = displayName;
		this.trigger = trigger;
		this.subTriggers = subTriggers;
		this.action = action;
		this.subActions = subActions;
	}

	@NotNull
	@Override
	public ItemStack getDisplayItem() {
		String name = this.name;
		if (name == null) {
			name = "NULL";

		}
		Material material = this.material;
		if (material == null) {
			material = Material.BARRIER;
		}

		ItemBuilder builder = new ItemBuilder(material, Message.forName("item-prefix").asString() + "§7" + name);

		// ADDING CONDITION INFO
		if (getTrigger() != null) {
			builder.appendLore(" ");
			List<String> triggerDisplay = InfoMenuGenerator
					.getSubSettingsDisplay(getTrigger().getSubSettingsBuilder(), getSubTriggers());

			String triggerName = Message.forName(getTrigger().getMessage()).asItemDescription().getName();
			builder.appendLore(Message.forName("custom-info-trigger").asString() + " " + triggerName);
			builder.appendLore(triggerDisplay);
		}

		// ADDING ACTION INFO
		if (getAction() != null) {
			builder.appendLore(" ");
			List<String> actionDisplay = InfoMenuGenerator
					.getSubSettingsDisplay(getAction().getSubSettingsBuilder(), getSubActions());

			String actionName = Message.forName(getAction().getMessage()).asItemDescription().getName();
			builder.appendLore(Message.forName("custom-info-action").asString() + " " + actionName);
			builder.appendLore(actionDisplay);
		}

		return builder.build();
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.BARRIER);
	}

	@Override
	public void playStatusUpdateTitle() {
		Challenges.getInstance().getTitleManager().sendChallengeStatusTitle(enabled ? Message.forName("title-challenge-enabled") : Message.forName("title-challenge-disabled"), getDisplayName());
	}

	@Override
	public void writeSettings(@Nonnull Document document) {
		super.writeSettings(document);

		document.set("material", material == null ? null : material.name());
		document.set("name", name);
		document.set("trigger", trigger == null ? null : trigger.getName());
		document.set("subTrigger", subTriggers);
		document.set("action", action == null ? null : action.getName());
		document.set("subActions", subActions);
	}

	@Nullable
	@Override
	protected String[] getSettingsDescription() {
		return super.getSettingsDescription();
	}

	public final void onTriggerFulfilled(ChallengeExecutionData challengeExecutionData) {
		if (isEnabled()) {

			boolean triggerMet = isTriggerMet(challengeExecutionData.getTriggerData());
			if (triggerMet) {
				executeAction(challengeExecutionData);
			}

		}
	}

	/**
	 * @return if the trigger is met.
	 * That is when every key in the subTriggers is contained by the data map and one or more value
	 * of the lists are equal to one another.
	 */
	public boolean isTriggerMet(Map<String, List<String>> data) {
		if (!subTriggers.isEmpty()) {
			for (Entry<String, String[]> entry : subTriggers.entrySet()) {
				if (!data.containsKey(entry.getKey())) {
					return false;
				}
				List<String> list = data.get(entry.getKey());

				boolean match = false;
				for (String value : entry.getValue()) {
					if (list.contains(value)) {
						match = true;
						break;
					}
				}

				if (!match) {
					return false;
				}
			}
		}
		return true;
	}

	public void executeAction(ChallengeExecutionData challengeExecutionData) {
		if (!Bukkit.isPrimaryThread()) {
			Bukkit.getScheduler().runTask(Challenges.getInstance(), () -> {
				action.execute(challengeExecutionData, subActions);
			});
			return;
		}
		action.execute(challengeExecutionData, subActions);
	}

	public void applySettings(@Nonnull Material material, @Nonnull String name, @Nonnull ChallengeTrigger trigger,
							  Map<String, String[]> subTriggers, ChallengeAction action, Map<String, String[]> subActions) {
		this.material = material;
		this.name = name;
		this.trigger = trigger;
		this.subTriggers = subTriggers;
		this.action = action;
		this.subActions = subActions;
	}

	public UUID getUniqueId() {
		return uuid;
	}

	@Nonnull
	public String getDisplayName() {
		return name;
	}

	@NotNull
	@Override
	public String getUniqueName() {
		return uuid.toString();
	}

}
