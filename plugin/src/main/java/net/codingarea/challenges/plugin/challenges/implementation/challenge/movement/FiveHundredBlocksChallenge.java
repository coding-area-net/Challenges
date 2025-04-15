package net.codingarea.challenges.plugin.challenges.implementation.challenge.movement;

import net.anweisen.utilities.common.annotations.Since;
import net.anweisen.utilities.common.config.Document;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifier;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.BlockUtils;
import net.codingarea.challenges.plugin.utils.misc.InventoryUtils;
import net.codingarea.challenges.plugin.utils.misc.MinecraftNameWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.1.0
 */
@Since("2.1.0")
public class FiveHundredBlocksChallenge extends SettingModifier {

	private final Map<UUID, Integer> blocksWalked = new HashMap<>();

	public FiveHundredBlocksChallenge() {
		super(MenuType.CHALLENGES, 1, 5, 5);
		setCategory(SettingCategory.MOVEMENT);

		// Loot Generate Event was added in 1.15
		try {
			Listener listener = new Listener() {

				@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
				public void onLootGenerate(LootGenerateEvent event) {
					if (!shouldExecuteEffect())
						return;
					event.setLoot(new LinkedList<>());
				}
			};

			Challenges.getInstance().registerListener(listener);
		} catch (NoClassDefFoundError noClassDefFoundError) {
			Challenges.getInstance().getLogger().warning("Loot Generation couldn't be blocked in FiveHundredBlocks Challenge: Please Use 1.15 or higher");
		}


	}

	@Override
	protected void onEnable() {
		bossbar.setContent((bar, player) -> {
			int walked = blocksWalked.getOrDefault(player.getUniqueId(), 0);
			bar.setTitle(Message.forName("bossbar-five-hundred-blocks").asString(walked, getBlocksToWalk()));
		});
		bossbar.show();
	}

	@Override
	protected void onDisable() {
		bossbar.hide();
	}

	@NotNull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(MinecraftNameWrapper.SIGN, Message.forName("item-five-hundred-blocks-challenges"));
	}

	@Override
	public void playValueChangeTitle() {
		ChallengeHelper.playChangeChallengeValueTitle(this, Message.forName("subtitle-blocks").asString(getBlocksToWalk()));
	}

	@Nullable
	@Override
	protected String[] getSettingsDescription() {
		return Message.forName("item-blocks-description").asArray(getBlocksToWalk());
	}

	private int getBlocksToWalk() {
		return getValue() * 100;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMove(PlayerMoveEvent event) {
		if (!shouldExecuteEffect()) return;
		Player player = event.getPlayer();
		if (ignorePlayer(player)) return;
		if (BlockUtils.isSameBlockLocationIgnoreHeight(event.getFrom(), event.getTo())) return;

		if (updateOrReset(player)) {
			InventoryUtils.giveItem(player.getInventory(), player.getLocation(), InventoryUtils.getRandomItem(false, false));
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		if (!shouldExecuteEffect()) return;
		if (ignorePlayer(event.getPlayer())) return;
		event.setDropItems(false);
		event.setExpToDrop(0);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityDeath(EntityDeathEvent event) {
		if (!shouldExecuteEffect()) return;
		event.getDrops().clear();
		event.setDroppedExp(0);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityExplosion(EntityExplodeEvent event) {
		if (!shouldExecuteEffect()) return;
		for (Block block : event.blockList()) {
			block.setType(Material.AIR);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockExplosion(BlockExplodeEvent event) {
		if (!shouldExecuteEffect()) return;
		for (Block block : event.blockList()) {
			block.setType(Material.AIR);
		}
	}

	/**
	 * @return if 500 blocks were reached
	 */
	private boolean updateOrReset(@Nonnull Player player) {
		UUID uuid = player.getUniqueId();

		int blocksWalked = this.blocksWalked.getOrDefault(uuid, 0);

		blocksWalked++;

		boolean reached = false;
		if (blocksWalked >= getBlocksToWalk()) {
			blocksWalked = 0;
			reached = true;
		}

		this.blocksWalked.put(uuid, blocksWalked);

		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			bossbar.update(player);
		});
		return reached;
	}

	@Override
	public void loadGameState(@NotNull Document document) {
		blocksWalked.clear();
		for (String key : document.keys()) {
			try {
				UUID uuid = UUID.fromString(key);
				int blocks = document.getInt(key);
				blocksWalked.put(uuid, blocks);
			} catch (IllegalArgumentException exception) {
				plugin.getLogger().severe("Error while loading 500 Blocks Challenge, "
						+ "key '" + key + "' is not a valid uuid");
				Challenges.getInstance().getLogger().error("", exception);
			}
		}
	}

	@Override
	public void writeGameState(@NotNull Document document) {
		blocksWalked.forEach((uuid, integer) -> {
			document.set(uuid.toString(), integer);
		});
	}

}
