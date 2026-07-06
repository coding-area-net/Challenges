package net.codingarea.challenges.plugin.challenges.implementation.challenge.damage;

import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifier;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class AdvancementDamageChallenge extends SettingModifier {

  public AdvancementDamageChallenge() {
    super(MenuType.CHALLENGES, SettingCategory.DAMAGE, 1, 40, new ItemStack(Material.BOOK), "advancement-damage");
  }

//  @Nullable
//  @Override
//  protected String[] getSettingsDescription() {
//    return Message.forName("item-heart-damage-description").asArray(getValue() / 2f);
//  }

  @Override
  public void playValueChangeTitle() {
    ChallengeHelper.playChallengeHeartsValueChangeTitle(this);
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPlayerAdvancementDone(@NotNull PlayerAdvancementDoneEvent event) {
    if (!shouldExecuteEffect()) return;
    if (ignorePlayer(event.getPlayer())) return;
    if (event.getAdvancement().getKey().toString().contains(":recipes/")) return;

    event.getPlayer().setNoDamageTicks(0);
    event.getPlayer().damage(getValue());
  }

}
