package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.codingarea.challenges.plugin.challenges.type.abstraction.PointsGoal;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.content.i18n.Prefix;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.ItemStack;

@Since("2.1.2")
public class EatMostGoal extends PointsGoal {

  public EatMostGoal() {
    super(SettingCategory.SCORE_POINTS, new ItemStack(Material.COOKIE), "eat-most");
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onItemConsume(FoodLevelChangeEvent event) {
    if (!shouldExecuteEffect()) return;
    if (!(event.getEntity() instanceof Player)) return;
    if (ignorePlayer(((Player) event.getEntity()))) return;
    int changedFoodLevel = event.getFoodLevel() - event.getEntity().getFoodLevel();
    if (changedFoodLevel > 0) {
      addPoints(event.getEntity().getUniqueId(), changedFoodLevel);
      MessageKey.of("points-change").send(event.getEntity(), Prefix.CHALLENGES, "+" + changedFoodLevel);
    }
  }

}
