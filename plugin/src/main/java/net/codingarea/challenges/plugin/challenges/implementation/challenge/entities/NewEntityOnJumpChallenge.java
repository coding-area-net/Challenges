package net.codingarea.challenges.plugin.challenges.implementation.challenge.entities;

import net.anweisen.utilities.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.custom.settings.action.impl.RandomMobAction;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.spigot.events.PlayerJumpEvent;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder.LeatherArmorBuilder;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import javax.annotation.Nonnull;

@Since("2.0")
public class NewEntityOnJumpChallenge extends Setting {

  public NewEntityOnJumpChallenge() {
    super(MenuType.CHALLENGES);
    setCategory(SettingCategory.ENTITIES);
  }

  @Nonnull
  @Override
  public ItemBuilder createDisplayItem() {
    return new LeatherArmorBuilder(Material.LEATHER_BOOTS, Message.forName("item-jump-entity-challenge")).setColor(Color.GREEN);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onJump(@Nonnull PlayerJumpEvent event) {
    if (ignorePlayer(event.getPlayer())) return;
    if (!shouldExecuteEffect()) return;
    spawnRandomEntity(event.getPlayer().getLocation());
  }

  private void spawnRandomEntity(@Nonnull Location location) {
    if (location.getWorld() == null) return;
    EntityType type = globalRandom.choose(RandomMobAction.getSpawnableMobs());
    location.getWorld().spawnEntity(location, type);
  }

}
