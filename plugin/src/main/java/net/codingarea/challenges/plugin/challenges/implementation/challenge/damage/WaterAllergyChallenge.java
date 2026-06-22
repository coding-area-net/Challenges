package net.codingarea.challenges.plugin.challenges.implementation.challenge.damage;

import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifier;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.management.scheduler.task.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Since("2.0")
public class WaterAllergyChallenge extends SettingModifier {

  public WaterAllergyChallenge() {
    super(MenuType.CHALLENGES, SettingCategory.DAMAGE, 1, 40, new ItemStack(Material.CYAN_GLAZED_TERRACOTTA), "water-allergy-challenge");
  }

//  @Nullable
//  @Override
//  protected String[] getSettingsDescription() {
//    return Message.forName("item-heart-damage-description").asArray(getValue() / 2f);
//  }

  @ScheduledTask(ticks = 5, async = false)
  public void onFifthTick() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (ignorePlayer(player)) return;
      if (player.getLocation().getBlock().getType() == Material.WATER) {
        player.damage(getValue());
      }
    }
  }

  @Override
  public void playValueChangeTitle() {
    ChallengeHelper.playChallengeHeartsValueChangeTitle(this);
  }

}
