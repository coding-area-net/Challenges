package net.codingarea.challenges.plugin.challenges.implementation.challenge.world;

import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.utils.misc.BlockUtils;
import net.codingarea.commons.bukkit.utils.item.StandardItemBuilder;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

public class BedrockPathChallenge extends Setting {

  public BedrockPathChallenge() {
    super(MenuType.CHALLENGES, SettingCategory.WORLD, new StandardItemBuilder.LeatherArmorBuilder(Material.LEATHER_BOOTS).setColor(Color.GRAY).build(),
      "bedrock-path-challenge");
  }

  @EventHandler
  public void onMove(@NotNull PlayerMoveEvent event) {
    if (!shouldExecuteEffect()) return;
    if (event.getPlayer().getGameMode() == GameMode.CREATIVE || event.getPlayer().getGameMode() == GameMode.SPECTATOR)
      return;

    BlockUtils.createBlockPath(event.getFrom(), event.getTo(), Material.BEDROCK);
  }

}
