package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.codingarea.challenges.plugin.challenges.type.abstraction.KillMobsGoal;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Since("2.1.3")
public class KillAllMobsGoal extends KillMobsGoal {

  public KillAllMobsGoal() {
    super(SettingCategory.KILL_ENTITY, getAllMobsToKill(), new ItemStack(Material.BOW), "all-mobs-goal");
  }

  static List<EntityType> getAllMobsToKill() {
    LinkedList<EntityType> list = new LinkedList<>(Arrays.asList(EntityType.values()));
    list.removeIf(type -> !type.isAlive());
    list.remove(EntityType.GIANT);
    list.remove(EntityType.ILLUSIONER);
    list.remove(EntityType.PLAYER);
    list.remove(EntityType.ARMOR_STAND);
    return list;
  }

  @Override
  public Message getBossbarMessage() {
    return Message.forName("bossbar-kill-all-mobs");
  }

}
