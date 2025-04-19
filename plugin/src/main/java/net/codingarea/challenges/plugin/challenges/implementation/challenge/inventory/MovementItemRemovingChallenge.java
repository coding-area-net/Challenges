package net.codingarea.challenges.plugin.challenges.implementation.challenge.inventory;

import net.codingarea.commons.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifier;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.DefaultItem;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.BlockUtils;
import net.codingarea.challenges.plugin.utils.misc.InventoryUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

import javax.annotation.Nonnull;

@Since("2.0")
public class MovementItemRemovingChallenge extends SettingModifier {

  public static final int BLOCK = 1;

  public MovementItemRemovingChallenge() {
    super(MenuType.CHALLENGES, 1, 2, 2);
    setCategory(SettingCategory.INVENTORY);
  }

  @Nonnull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.DETECTOR_RAIL, Message.forName("item-block-chunk-item-remove-challenge"));
  }

  @Nonnull
  @Override
  public ItemBuilder createSettingsItem() {
    if (!isEnabled()) return DefaultItem.disabled();
    if (getValue() == BLOCK)
      return DefaultItem.create(Material.GRASS_BLOCK, Message.forName("item-block-chunk-item-remove-challenge-block"));
    return DefaultItem.create(Material.BOOK, Message.forName("item-block-chunk-item-remove-challenge-chunk"));
  }

  @Override
  public void playValueChangeTitle() {
    if (getValue() == BLOCK)
      ChallengeHelper.playChangeChallengeValueTitle(this, Message.forName("item-block-chunk-item-remove-challenge-block"));
    else
      ChallengeHelper.playChangeChallengeValueTitle(this, Message.forName("item-block-chunk-item-remove-challenge-chunk"));
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPlayerMove(@Nonnull PlayerMoveEvent event) {
    if (!shouldExecuteEffect()) return;
    if (ignorePlayer(event.getPlayer())) return;
    if (event.getTo() == null) return;

    if (getValue() == BLOCK) {
      if (BlockUtils.isSameBlockLocationIgnoreHeight(event.getFrom(), event.getTo())) return;
    } else {
      if (BlockUtils.isSameChunk(event.getFrom().getChunk(), event.getTo().getChunk()))
        return;
    }

    InventoryUtils.removeRandomItem(event.getPlayer().getInventory());
  }

}
