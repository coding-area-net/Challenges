package net.codingarea.challenges.plugin.challenges.custom.settings.action.impl;

import net.codingarea.challenges.plugin.challenges.custom.settings.action.EntityTargetAction;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.damage.FreezeChallenge;
import net.codingarea.challenges.plugin.challenges.type.abstraction.AbstractChallenge;
import net.codingarea.challenges.plugin.challenges.type.helper.SubSettingsHelper;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.Map;

public class FreezeAction extends EntityTargetAction {

	FreezeChallenge instance = AbstractChallenge
			.getFirstInstance(FreezeChallenge.class);

	public FreezeAction(String name) {
		super(name, SubSettingsHelper.createEntityTargetSettingsBuilder(true));
	}

	@Override
	public Material getMaterial() {
		return Material.ICE;
	}

	@Override
	public void executeFor(Entity entity, Map<String, String[]> subActions) {

		if (entity instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity) entity;
			instance.setFreeze(livingEntity, 2);
		}


	}

}
