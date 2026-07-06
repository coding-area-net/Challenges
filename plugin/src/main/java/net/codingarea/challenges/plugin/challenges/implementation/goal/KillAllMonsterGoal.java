package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.codingarea.challenges.plugin.challenges.type.abstraction.KillMobsGoal;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.content.legacy.Message;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.commons.bukkit.utils.misc.MinecraftVersion;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Since("2.1.3")
public class KillAllMonsterGoal extends KillMobsGoal {

  public KillAllMonsterGoal() {
    super(SettingCategory.KILL_ENTITY, getAllMobsToKill(), new ItemStack(Material.ARROW), "all-monster");
  }

  static List<EntityType> getAllMobsToKill() {
    LinkedList<EntityType> list = new LinkedList<>(Arrays.asList(EntityType.values()));
    list.removeIf(type -> !type.isAlive());
    list.removeIf(type -> {
      assert type.getEntityClass() != null;
      return !Monster.class.isAssignableFrom(type.getEntityClass());
    });
    list.add(EntityType.PHANTOM);
    list.add(EntityType.ENDER_DRAGON);
    list.add(EntityType.SHULKER);
    list.add(EntityType.GHAST);
    list.add(EntityType.MAGMA_CUBE);
    list.add(EntityType.SLIME);
    list.remove(EntityType.GIANT);
    list.remove(EntityType.ILLUSIONER);
    if (MinecraftVersion.current().isNewerOrEqualThan(MinecraftVersion.V1_16)) {
      list.add(EntityType.HOGLIN);
    }
    return list;
  }

  @Override
  public Message getBossbarMessage() {
    return Message.forName("bossbar-kill-all-monster");
  }

}
