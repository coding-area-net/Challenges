package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Since("2.0")
public class TotemSaveDeathSetting extends Setting {

  public TotemSaveDeathSetting() {
    super(MenuType.SETTINGS, null, new ItemStack(Material.TOTEM_OF_UNDYING), "totem-save-death");
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onEntityResurrect(@NotNull EntityResurrectEvent event) {
    if (ChallengeHelper.isInInstantKill() && !isEnabled()) {
      event.setCancelled(true);
    }
  }

}
