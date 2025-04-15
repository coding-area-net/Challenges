package net.codingarea.challenges.plugin.challenges.implementation.challenge.world;

import net.anweisen.utilities.bukkit.utils.misc.BukkitReflectionUtils;
import net.anweisen.utilities.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.0
 */
@Since("2.0")
public class IceFloorChallenge extends Setting {

	private final List<Player> ignoredPlayers = new ArrayList<>();

	public IceFloorChallenge() {
		super(MenuType.CHALLENGES);
		setCategory(SettingCategory.WORLD);
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.PACKED_ICE, Message.forName("item-ice-floor-challenge"));
	}

	@Override
	protected void onEnable() {
		bossbar.setContent((bossbar, player) -> {
			bossbar.setTitle(Message.forName("bossbar-ice-floor").asString(ignoreIce(player) ? Message.forName("disabled") : Message.forName("enabled")));
			bossbar.setColor(ignoreIce(player) ? BarColor.RED : BarColor.GREEN);
		});
		bossbar.show();
	}

	@Override
	protected void onDisable() {
		bossbar.hide();
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerMove(@Nonnull PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (!shouldExecuteEffect()) return;
		if (ignorePlayer(player)) return;
		if (ignoredPlayers.contains(player)) return;
		createIceFloorForPlayer(player);
	}

	private void createIceFloorForPlayer(@Nonnull Player player) {
		Block middleBlock = player.getLocation().clone().subtract(0, 1, 0).getBlock();
		createIceFloor(middleBlock);
	}

	private void createIceFloor(@Nonnull Block middleBlock) {

		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				Location iceLocation = middleBlock.getLocation().add(x, 0, z);
				Block iceBlock = iceLocation.getBlock();
				if (iceBlock.getType().isSolid()) continue;
				if (!BukkitReflectionUtils.isAir(iceBlock.getType())) {
					ChallengeHelper.breakBlock(iceBlock, new ItemStack(Material.AIR));
				}
				iceBlock.setType(Material.PACKED_ICE);
			}
		}

	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSneak(@Nonnull PlayerToggleSneakEvent event) {
		if (!shouldExecuteEffect()) return;
		if (!event.isSneaking()) return;
		if (ignorePlayer(event.getPlayer())) return;
		if (ignoredPlayers.contains(event.getPlayer())) {
			ignoredPlayers.remove(event.getPlayer());
			event.getPlayer().setVelocity(new Vector(0, 0, 0));
			createIceFloorForPlayer(event.getPlayer());
		} else {
			ignoredPlayers.add(event.getPlayer());
		}
		bossbar.update(event.getPlayer());
	}

	private boolean ignoreIce(@Nonnull Player player) {
		return ignoredPlayers.contains(player);
	}

}