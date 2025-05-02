package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.codingarea.commons.bukkit.utils.misc.MinecraftVersion;
import net.codingarea.commons.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.type.abstraction.NetherPortalSpawnSetting;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.challenges.annotations.RequireVersion;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.ExperimentalUtils;
import org.bukkit.Material;
import org.bukkit.StructureType;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.stream.Collectors;

@Since("2.0")
@RequireVersion(MinecraftVersion.V1_16)
public class BastionSpawnSetting extends NetherPortalSpawnSetting {

  public BastionSpawnSetting() {
    super(MenuType.SETTINGS, StructureType.BASTION_REMNANT, "unable-to-find-bastion",
      Arrays.stream(ExperimentalUtils.getMaterials()).filter(material -> material.name().contains("BASALT")).collect(Collectors.toList()));
  }

  @Nonnull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.POLISHED_BLACKSTONE_BRICKS, Message.forName("item-bastion-spawn-setting"));
  }

}
