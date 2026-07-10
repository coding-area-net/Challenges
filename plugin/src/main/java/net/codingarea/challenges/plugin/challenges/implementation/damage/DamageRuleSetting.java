package net.codingarea.challenges.plugin.challenges.implementation.damage;

import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.content.i18n.CustomTranslatable;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class DamageRuleSetting extends Setting {

  private final List<DamageCause> causes;

  private final String name;

  public DamageRuleSetting(@NotNull ItemStack displayItemPreset, @NotNull String name, @NotNull DamageCause... causes) {
    super(MenuType.DAMAGE, null, true, displayItemPreset, "damage-rule." + name);
    this.causes = Arrays.asList(causes);
    this.name = name;
  }

  @NotNull
  @Override
  public String getUniqueName() {
    return super.getUniqueName() + name;
  }

  @NotNull
  @Override
  public LocalizableMessage getChallengeDescription() {
    return super.getChallengeDescription()
      .withArgs(LocalizableMessage.joinList(3, causes.stream().map(CustomTranslatable::of).toList()));
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onDamage(@NotNull EntityDamageEvent event) {
    if (ChallengeAPI.isWorldInUse()) return;
    if (isEnabled()) return;
    if (!(event.getEntity() instanceof Player)) return;
    if (event.getCause() == DamageCause.VOID || event.getCause() == DamageCause.CUSTOM)
      return; // Never ignore void or custom to prevent different issues
    if (!causes.contains(event.getCause())) return;
    event.setCancelled(true);
  }

}
