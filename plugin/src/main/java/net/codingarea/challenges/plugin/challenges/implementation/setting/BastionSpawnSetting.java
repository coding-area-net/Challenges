package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.codingarea.challenges.plugin.challenges.type.abstraction.NetherPortalSpawnSetting;
import net.codingarea.challenges.plugin.challenges.type.annotation.RequireVersion;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.misc.ExperimentalUtils;
import net.codingarea.commons.bukkit.utils.misc.MinecraftVersion;
import org.bukkit.Material;
import org.bukkit.StructureType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.stream.Collectors;

@Since("2.0")
@RequireVersion(MinecraftVersion.V1_16)
public class BastionSpawnSetting extends NetherPortalSpawnSetting {

  public BastionSpawnSetting() {
    super(MenuType.SETTINGS, null, StructureType.BASTION_REMNANT, new ItemStack(Material.POLISHED_BLACKSTONE_BRICKS),
      "bastion-spawn", "unable-to-find-bastion",
      Arrays.stream(ExperimentalUtils.getMaterials()).filter(material -> material.name().contains("BASALT")).collect(Collectors.toList()));
  }

}
