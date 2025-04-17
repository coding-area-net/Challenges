package net.codingarea.challenges.plugin.challenges.custom.settings.action.impl;

import net.codingarea.challenges.plugin.challenges.custom.settings.action.EntityTargetAction;
import net.codingarea.challenges.plugin.challenges.type.helper.SubSettingsHelper;
import net.codingarea.challenges.plugin.utils.bukkit.misc.version.MinecraftVersion;
import net.codingarea.challenges.plugin.utils.bukkit.misc.wrapper.AttributeWrapper;
import net.codingarea.challenges.plugin.utils.item.DefaultItem;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.MinecraftNameWrapper;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.Map;

/**
 * @author KxmischesDomi | https://github.com/kxmischesdomi
 * @since 2.1.0
 */
public class HealEntityAction extends EntityTargetAction {

	public HealEntityAction(String name) {
		super(name, SubSettingsHelper.createEntityTargetSettingsBuilder(true).createChooseItemChild("amount").fill(builder -> {
			String prefix = DefaultItem.getItemPrefix();
			for (int i = 1; i < 21; i++) {
				builder.addSetting(
						String.valueOf(i), new ItemBuilder(
						MinecraftNameWrapper.RED_DYE, prefix + "§7" + (i / 2f) + " §c❤").setAmount(i).build());
			}
		}));
	}

	@Override
	public void executeFor(Entity entity, Map<String, String[]> subActions) {
		int amount = Integer.parseInt(subActions.get("amount")[0]);
		if (entity instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity) entity;
			AttributeInstance attribute = livingEntity.getAttribute(AttributeWrapper.MAX_HEALTH);

			if (attribute == null) return;
			double newHealth = Math.min(livingEntity.getHealth() + amount,
					attribute.getBaseValue());
			livingEntity.setHealth(newHealth);
		}
	}

	@Override
	public Material getMaterial() {
		return Material.GOLDEN_APPLE;
	}

}
