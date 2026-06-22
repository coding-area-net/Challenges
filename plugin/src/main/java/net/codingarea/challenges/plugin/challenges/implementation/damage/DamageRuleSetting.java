package net.codingarea.challenges.plugin.challenges.implementation.damage;

import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.item.LegacyItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class DamageRuleSetting extends Setting {

  private final List<DamageCause> causes;

  private final String name;

  public DamageRuleSetting(@NotNull LegacyItemBuilder preset, @NotNull String name, @NotNull DamageCause... causes) {
    super(MenuType.DAMAGE, null, true, preset.build(), "TITLE!");
    this.causes = Arrays.asList(causes);
    this.name = name;
  }

  @NotNull
  @Override
  public String getUniqueName() {
    return super.getUniqueName() + name;
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
