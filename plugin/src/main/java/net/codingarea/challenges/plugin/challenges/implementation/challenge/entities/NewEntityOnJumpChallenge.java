package net.codingarea.challenges.plugin.challenges.implementation.challenge.entities;

import net.codingarea.challenges.plugin.challenges.custom.settings.action.impl.RandomMobAction;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.spigot.events.PlayerJumpEvent;
import net.codingarea.commons.bukkit.utils.item.StandardItemBuilder;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

@Since("2.0")
public class NewEntityOnJumpChallenge extends Setting {

  public NewEntityOnJumpChallenge() {
    super(MenuType.CHALLENGES, SettingCategory.ENTITIES, new StandardItemBuilder.LeatherArmorBuilder(Material.LEATHER_BOOTS).setColor(Color.GREEN).build(),
      "jump-entity");
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onJump(@NotNull PlayerJumpEvent event) {
    if (ignorePlayer(event.getPlayer())) return;
    if (!shouldExecuteEffect()) return;
    spawnRandomEntity(event.getPlayer().getLocation());
  }

  private void spawnRandomEntity(@NotNull Location location) {
    if (location.getWorld() == null) return;
    EntityType type = globalRandom.choose(RandomMobAction.getSpawnableMobs());
    location.getWorld().spawnEntity(location, type);
  }

}
