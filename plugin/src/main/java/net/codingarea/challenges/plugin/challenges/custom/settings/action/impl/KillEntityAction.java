package net.codingarea.challenges.plugin.challenges.custom.settings.action.impl;

import net.codingarea.challenges.plugin.challenges.custom.settings.action.EntityTargetAction;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.challenges.type.helper.SubSettingsHelper;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.1.0
 */
public class KillEntityAction extends EntityTargetAction {

	public KillEntityAction(String name) {
		super(name, SubSettingsHelper.createEntityTargetSettingsBuilder(true));
	}

	@Override
	public void executeFor(Entity entity, Map<String, String[]> subActions) {
		if (entity instanceof Player) {
			ChallengeHelper.kill(((Player) entity));
		} else if (entity instanceof LivingEntity) {
			((LivingEntity) entity).damage(((LivingEntity) entity).getHealth());
		}
	}

	@Override
	public Material getMaterial() {
		return Material.DIAMOND_SWORD;
	}

}
