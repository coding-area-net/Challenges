package net.codingarea.challenges.plugin.challenges.implementation.challenge.world;

import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifier;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.BlockUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FloorIsLavaChallenge extends SettingModifier {

	public FloorIsLavaChallenge() {
		super(MenuType.CHALLENGES, 1, 60, 30);
		setCategory(SettingCategory.WORLD);
	}

	@EventHandler
	public void onMove(@Nonnull PlayerMoveEvent event) {
		if (!shouldExecuteEffect()) return;
		if (event.getPlayer().getGameMode() == GameMode.CREATIVE || event.getPlayer().getGameMode() == GameMode.SPECTATOR)
			return;
		if (event.getTo() == null) return;
		if (!BlockUtils.isSameBlockLocationIgnoreHeight(event.getFrom(), event.getTo())) return;

		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			createMagmaFloor(event.getTo());
		}, getValue() * 20L);
	}

	private void createMagmaFloor(@Nonnull Location to) {
		BlockUtils.setBlockNatural(BlockUtils.getBlockBelow(to, 0.6), Material.MAGMA_BLOCK, true);
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			createLavaFloor(to);
		}, getValue() * 20L);
	}

	private void createLavaFloor(@Nonnull Location to) {
		BlockUtils.setBlockNatural(BlockUtils.getBlockBelow(to), Material.LAVA, true);
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.MAGMA_BLOCK, Message.forName("item-floor-lava-challenge"));
	}

	@Nullable
	@Override
	protected String[] getSettingsDescription() {
		return Message.forName("item-time-seconds-description").asArray(getValue());
	}

}
