package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.codingarea.challenges.plugin.challenges.type.abstraction.NetherPortalSpawnSetting;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.item.LegacyItemBuilder;
import org.bukkit.Material;
import org.bukkit.StructureType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Since("2.0")
public class FortressSpawnSetting extends NetherPortalSpawnSetting {

  public FortressSpawnSetting() {
    super(MenuType.SETTINGS, null, StructureType.NETHER_FORTRESS, new ItemStack(Material.NETHER_BRICK_STAIRS),
      "fortress-spawn", "unable-to-find-fortress", Material.NETHER_BRICKS);
  }

}
