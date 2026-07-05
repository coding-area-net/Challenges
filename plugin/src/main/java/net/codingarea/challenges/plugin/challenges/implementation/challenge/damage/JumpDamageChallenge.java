package net.codingarea.challenges.plugin.challenges.implementation.challenge.damage;

import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifier;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.content.legacy.Message;
import net.codingarea.challenges.plugin.content.i18n.Prefix;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.spigot.events.PlayerJumpEvent;
import net.codingarea.challenges.plugin.utils.misc.NameHelper;
import net.codingarea.commons.bukkit.utils.item.StandardItemBuilder;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

@Since("2.0")
public class JumpDamageChallenge extends SettingModifier {

  public JumpDamageChallenge() {
    super(MenuType.CHALLENGES, SettingCategory.DAMAGE, 1, 60, new StandardItemBuilder.LeatherArmorBuilder(Material.LEATHER_BOOTS).setColor(Color.ORANGE).build(),
      "jump-damage-challenge");
  }

//  @Nullable
//  @Override
//  protected String[] getSettingsDescription() {
//    return Message.forName("item-heart-damage-description").asArray(getValue() / 2f);
//  }

  @Override
  public void playValueChangeTitle() {
    ChallengeHelper.playChallengeHeartsValueChangeTitle(this, getValue() / 2);
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onSneak(@NotNull PlayerJumpEvent event) {
    if (!shouldExecuteEffect()) return;
    if (ignorePlayer(event.getPlayer())) return;
    MessageKey.of("jump-damage-failed").broadcast(Prefix.CHALLENGES, event.getPlayer());
    event.getPlayer().setNoDamageTicks(0);
    event.getPlayer().damage(getValue());
    event.getPlayer().setNoDamageTicks(0);
  }

}
