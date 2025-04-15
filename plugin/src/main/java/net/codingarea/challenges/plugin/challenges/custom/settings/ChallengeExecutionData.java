package net.codingarea.challenges.plugin.challenges.custom.settings;

import lombok.Getter;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.custom.settings.action.impl.CancelEventAction;
import net.codingarea.challenges.plugin.challenges.custom.settings.trigger.IChallengeTrigger;
import net.codingarea.challenges.plugin.challenges.type.abstraction.AbstractChallenge;
import net.codingarea.challenges.plugin.challenges.type.helper.SubSettingsHelper;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import java.util.*;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.1.0
 */
public class ChallengeExecutionData {

	@Getter
	private final IChallengeTrigger trigger;
	@Getter
	private final Map<String, List<String>> triggerData;
	@Getter
	private Entity entity;
	private Runnable cancelAction;
	@Getter
	private int timesExecuting;

	public ChallengeExecutionData(
			IChallengeTrigger trigger) {
		this.trigger = trigger;
		this.triggerData = new HashMap<>();
	}

	public ChallengeExecutionData data(String key, String data) {
		triggerData.put(key, Collections.singletonList(data));
		return this;
	}

	public ChallengeExecutionData data(String key, List<String> data) {
		triggerData.put(key, data);
		return this;
	}

	public ChallengeExecutionData data(String key, String... data) {
		triggerData.put(key, Arrays.asList(data));
		return this;
	}

	public ChallengeExecutionData block(Material material) {
		return data(SubSettingsHelper.BLOCK, SubSettingsHelper.ANY, material.name());
	}

	public ChallengeExecutionData entityType(EntityType type) {
		return data(SubSettingsHelper.ENTITY_TYPE, SubSettingsHelper.ANY, type.name());
	}

	public ChallengeExecutionData entity(Entity entity) {
		this.entity = entity;
		return this;
	}

	public ChallengeExecutionData cancelAction(Runnable cancelAction) {
		this.cancelAction = cancelAction;
		return this;
	}

	public ChallengeExecutionData event(Cancellable event) {
		this.cancelAction = () -> event.setCancelled(true);
		return this;
	}

	public ChallengeExecutionData times(int timesExecuting) {
		this.timesExecuting = timesExecuting;
		return this;
	}

	public void execute() {
		if (ChallengeAPI.isStarted() && !ChallengeAPI.isWorldInUse()) {
			if (entity instanceof Player && AbstractChallenge.ignorePlayer(((Player) entity))) {
				return;
			}
			if (cancelAction != null) {
				CancelEventAction.onPreTrigger();
			}
			Challenges.getInstance().getCustomChallengesLoader().executeTrigger(this);
			if (cancelAction != null && CancelEventAction.shouldCancel()) {
				cancelAction.run();
			}
		}
	}

}
