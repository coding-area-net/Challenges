package net.codingarea.challenges.plugin.management.menu.info;

import lombok.ToString;
import net.codingarea.commons.bukkit.utils.menu.MenuClickInfo;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

@ToString
public class ChallengeMenuClickInfo extends MenuClickInfo {

  protected final boolean upperItem;

  public ChallengeMenuClickInfo(@Nonnull MenuClickInfo parent, boolean upperItem) {
    this(parent.getPlayer(), parent.getInventory(), parent.isShiftClick(), parent.isRightClick(), parent.getSlot(), upperItem);
  }

  public ChallengeMenuClickInfo(@Nonnull Player player, @Nonnull Inventory inventory, boolean shiftClick, boolean rightClick, @Nonnegative int slot, boolean upperItem) {
    super(player, inventory, shiftClick, rightClick, slot);
    this.upperItem = upperItem;
  }

  public boolean isUpperItemClick() {
    return upperItem;
  }

  public boolean isLowerItemClick() {
    return !upperItem;
  }

}
