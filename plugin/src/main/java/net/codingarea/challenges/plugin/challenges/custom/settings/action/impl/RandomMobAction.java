package net.codingarea.challenges.plugin.challenges.custom.settings.action.impl;

import lombok.Getter;
import net.codingarea.challenges.plugin.challenges.custom.settings.action.ChallengeAction;
import net.codingarea.challenges.plugin.challenges.custom.settings.action.EntityTargetAction;
import net.codingarea.challenges.plugin.challenges.type.helper.SubSettingsHelper;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.1.0
 */
public class RandomMobAction extends EntityTargetAction {

	@Getter
	private static final EntityType[] spawnableMobs;
	@Getter
	private static final EntityType[] livingMobs;

	static {
		List<EntityType> list = new LinkedList<>(Arrays.asList(EntityType.values()));
		list = list.stream().filter(EntityType::isSpawnable).collect(Collectors.toList());
		spawnableMobs = list.toArray(new EntityType[0]);
		list = list.stream().filter(EntityType::isAlive).collect(Collectors.toList());
		livingMobs = list.toArray(new EntityType[0]);
	}

	public RandomMobAction(String name) {
		super(name, SubSettingsHelper.createEntityTargetSettingsBuilder(false));
	}

	@Override
	public void executeFor(Entity entity, Map<String, String[]> subActions) {
		if (entity.getLocation().getWorld() == null) return;
		EntityType value = ChallengeAction.random.choose(spawnableMobs);
		entity.getLocation().getWorld().spawnEntity(entity.getLocation(), value);
	}

	@Override
	public Material getMaterial() {
		return Material.BLAZE_SPAWN_EGG;
	}

}
