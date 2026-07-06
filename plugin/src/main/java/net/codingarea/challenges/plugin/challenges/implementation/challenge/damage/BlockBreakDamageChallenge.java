package net.codingarea.challenges.plugin.challenges.implementation.challenge.damage;

import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifier;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BlockBreakDamageChallenge extends SettingModifier {

  public BlockBreakDamageChallenge() {
    super(MenuType.CHALLENGES, SettingCategory.DAMAGE, 1, 60, new ItemStack(Material.GOLDEN_PICKAXE), "block-break-damage");
  }

//  @Nullable
//  @Override
//  protected String[] getSettingsDescription() {
//    return Message.forName("item-heart-damage-description").asArray(getValue() / 2f);
//  }

  @EventHandler
  public void onBreak(BlockBreakEvent event) {
    if (!shouldExecuteEffect()) return;
    event.getPlayer().setNoDamageTicks(0);
    event.getPlayer().damage(getValue());
  }

  @Override
  public void playValueChangeTitle() {
    ChallengeHelper.playChallengeHeartsValueChangeTitle(this);
  }

}
