package net.codingarea.challenges.plugin.management.menu.generator.implementation.custom;

import net.anweisen.utilities.bukkit.utils.animation.SoundSample;
import net.anweisen.utilities.bukkit.utils.menu.MenuClickInfo;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.custom.CustomChallenge;
import net.codingarea.challenges.plugin.challenges.type.IChallenge;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.management.menu.InventoryTitleManager;
import net.codingarea.challenges.plugin.management.menu.generator.ChallengeMenuGenerator;
import net.codingarea.challenges.plugin.management.menu.info.ChallengeMenuClickInfo;
import net.codingarea.challenges.plugin.utils.item.DefaultItem;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnull;

public class MainCustomMenuGenerator extends ChallengeMenuGenerator {

	public static final int[] SLOTS = {10, 11, 12, 13, 14, 15, 16};
	public static final int[] NAVIGATION_SLOTS = {36, 44};
	public static final int SIZE = 5 * 9;
	private static final int VIEW_SLOT = 21;
	private static final int CREATE_SLOT = 23;

	private static final int maxCustomChallenges;

	static {
		maxCustomChallenges = Challenges.getInstance().getConfigDocument().getInt("custom-challenge-settings.max-challenges");
	}

	public MainCustomMenuGenerator() {
		super(1);
	}

	@Override
	protected String getTitle(int page) {
		return page != 0 ? InventoryTitleManager.getTitle(getMenuType(),
				Message.forName("custom-title-view").asString(), String.valueOf(page)) :
				InventoryTitleManager.getTitle(getMenuType(), "Menu");
	}

	@Override
	public void executeClickAction(@Nonnull IChallenge challenge, @Nonnull MenuClickInfo info, int itemIndex) {
		if (itemIndex == 0 || itemIndex == 2) {
			challenge.handleClick(new ChallengeMenuClickInfo(info, itemIndex == 0));
		} else if (challenge instanceof CustomChallenge) {
			InfoMenuGenerator infoMenuGenerator = new InfoMenuGenerator((CustomChallenge) challenge);
			infoMenuGenerator.open(info.getPlayer(), 0);
			SoundSample.CLICK.play(info.getPlayer());
		}
	}

	@Override
	public void generatePage(@Nonnull Inventory inventory, int page) {
		if (page == 0) {
			inventory.setItem(VIEW_SLOT, new ItemBuilder(Material.BOOK, Message.forName("custom-main-view-challenges")).build());
			inventory.setItem(CREATE_SLOT, new ItemBuilder(Material.WRITABLE_BOOK, Message.forName("custom-main-create-challenge")).build());
		}
	}

	@Override
	public void onPreChallengePageClicking(@Nonnull MenuClickInfo info, int page) {
		if (info.getSlot() == VIEW_SLOT) {
			if (Challenges.getInstance().getCustomChallengesLoader().getCustomChallenges().isEmpty()) {
				Message.forName("custom-not-loaded").send(info.getPlayer(), Prefix.CUSTOM);
				return;
			}
			open(info.getPlayer(), 1);
			SoundSample.PLOP.play(info.getPlayer());
		} else if (info.getSlot() == CREATE_SLOT) {
			if (ChallengeMenuGenerator.playNoPermissionsEffect(info.getPlayer())) {
				return;
			}
			if (Challenges.getInstance().getCustomChallengesLoader().getCustomChallenges().size() > maxCustomChallenges) {
				Message.forName("custom-limit").send(info.getPlayer(), Prefix.CUSTOM, maxCustomChallenges);
				SoundSample.BASS_OFF.play(info.getPlayer());
				return;
			}
			new InfoMenuGenerator().open(info.getPlayer(), 0);
			SoundSample.PLING.play(info.getPlayer());
		}
	}

	@Override
	public void setSettingsItems(@Nonnull Inventory inventory, @Nonnull IChallenge challenge, int topSlot) {
		inventory.setItem(getSlots()[topSlot], getDisplayItemBuilder(challenge).build());
		inventory.setItem(getSlots()[topSlot] + 9, DefaultItem.customize().build());
		inventory.setItem(getSlots()[topSlot] + 18, getSettingsItem(challenge));
	}

	@Override
	public int[] getSlots() {
		return SLOTS;
	}

	@Override
	public int getSize() {
		return SIZE;
	}

	@Override
	public int[] getNavigationSlots(int page) {
		return page == 0 ? new int[]{NAVIGATION_SLOTS[0]} : NAVIGATION_SLOTS;
	}

}
