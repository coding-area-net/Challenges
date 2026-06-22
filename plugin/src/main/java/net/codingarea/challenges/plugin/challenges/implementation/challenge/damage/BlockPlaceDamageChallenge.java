package net.codingarea.challenges.plugin.challenges.implementation.challenge.damage;

import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifier;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

@Since("2.0")
public class BlockPlaceDamageChallenge extends SettingModifier {

  public BlockPlaceDamageChallenge() {
    super(MenuType.CHALLENGES, SettingCategory.DAMAGE, 1, 60, new ItemStack(Material.GOLD_BLOCK), "block-place-damange-challenge");
  }

//  @Nullable
//  @Override
//  protected String[] getSettingsDescription() {
//    return Message.forName("item-heart-damage-description").asArray(getValue() / 2f);
//  }


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

}
