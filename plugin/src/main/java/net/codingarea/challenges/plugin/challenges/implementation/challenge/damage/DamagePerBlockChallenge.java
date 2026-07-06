package net.codingarea.challenges.plugin.challenges.implementation.challenge.damage;

import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifier;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.utils.misc.BlockUtils;
import net.codingarea.commons.bukkit.utils.item.StandardItemBuilder;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

public class DamagePerBlockChallenge extends SettingModifier {

  public DamagePerBlockChallenge() {
    super(MenuType.CHALLENGES, SettingCategory.DAMAGE, 1, 40, new StandardItemBuilder.LeatherArmorBuilder(Material.LEATHER_BOOTS).setColor(Color.RED).build(),
      "damage-per-block");
  }

//  @Nullable
//  @Override
//  protected String[] getSettingsDescription() {
//    return Message.forName("item-heart-damage-description").asArray(getValue() / 2f);
//  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onMove(@NotNull PlayerMoveEvent event) {
    if (!shouldExecuteEffect()) return;
    if (ignorePlayer(event.getPlayer())) return;
    if (BlockUtils.isSameBlockLocationIgnoreHeight(event.getTo(), event.getFrom())) return;

    event.getPlayer().setNoDamageTicks(0);
    event.getPlayer().damage(getValue());
  }

  @Override
  public void playValueChangeTitle() {
    ChallengeHelper.playChallengeHeartsValueChangeTitle(this);
  }

}
