package net.codingarea.challenges.plugin.challenges.implementation.challenge.damage;

import net.anweisen.utilities.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifier;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author anweisen | <a href="https://github.com/anweisen">...</a>
 * @autho<a href="r">KxmischesDomi | https://github</a>.com/kxmischesdomi
 * @since 2.0
 */
@Since("2.0")
public class BlockPlaceDamageChallenge extends SettingModifier {

	public BlockPlaceDamageChallenge() {
		super(MenuType.CHALLENGES, 1, 60);
		setCategory(SettingCategory.DAMAGE);
	}

	@EventHandler
	public void onBreak(BlockPlaceEvent event) {
		if (!shouldExecuteEffect()) return;
		event.getPlayer().setNoDamageTicks(0);
		event.getPlayer().damage(getValue());
	}

	@Override
	public void playValueChangeTitle() {
		ChallengeHelper.playChallengeHeartsValueChangeTitle(this);
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.GOLD_BLOCK, Message.forName("item-block-place-damage-challenge"));
	}

	@Nullable
	@Override
	protected String[] getSettingsDescription() {
		return Message.forName("item-heart-damage-description").asArray(getValue() / 2f);
	}

}