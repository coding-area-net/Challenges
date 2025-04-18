package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.anweisen.utilities.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.type.abstraction.NetherPortalSpawnSetting;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.StructureType;

import javax.annotation.Nonnull;

@Since("2.0")
public class FortressSpawnSetting extends NetherPortalSpawnSetting {

  public FortressSpawnSetting() {
    super(MenuType.SETTINGS, StructureType.NETHER_FORTRESS, "unable-to-find-fortress", Material.NETHER_BRICKS);
  }

  @Nonnull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.NETHER_BRICK_STAIRS, Message.forName("item-fortress-spawn-setting"));
  }

}
