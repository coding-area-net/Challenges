package net.codingarea.challenges.plugin.challenges.custom.settings.trigger.impl;

import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SubSettingsBuilder;
import net.codingarea.challenges.plugin.challenges.custom.settings.trigger.ChallengeTrigger;
import net.codingarea.challenges.plugin.challenges.type.helper.SubSettingsHelper;
import net.codingarea.challenges.plugin.utils.bukkit.misc.BukkitStringUtils;
import net.codingarea.challenges.plugin.utils.item.DefaultItem;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.ExperimentalUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerItemConsumeEvent;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.1.0
 */
public class ConsumeItemTrigger extends ChallengeTrigger {

	public ConsumeItemTrigger(String name) {
		super(name, SubSettingsBuilder.createChooseMultipleItem(SubSettingsHelper.ITEM).fill(builder -> {
			for (Material material : ExperimentalUtils.getMaterials()) {
				if (material.isEdible()) {
					builder.addSetting(material.name(), new ItemBuilder(material, DefaultItem.getItemPrefix() + BukkitStringUtils.getItemName(material).toPlainText()).build());
				}
			}
		}));
	}

	@Override
	public Material getMaterial() {
		return Material.COOKED_BEEF;
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onConsumeItem(PlayerItemConsumeEvent event) {
		createData()
				.entity(event.getPlayer())
				.event(event)
				.data(SubSettingsHelper.ITEM, SubSettingsHelper.ANY, event.getItem().getType().name())
				.execute();
	}

}
