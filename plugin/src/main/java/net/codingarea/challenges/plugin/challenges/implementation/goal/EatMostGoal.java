package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.anweisen.utilities.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.type.abstraction.PointsGoal;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.jetbrains.annotations.NotNull;

@Since("2.1.2")
public class EatMostGoal extends PointsGoal {

  public EatMostGoal() {
    super();
    setCategory(SettingCategory.SCORE_POINTS);
  }

  @NotNull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.COOKIE, Message.forName("item-eat-most-goal"));
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onItemConsume(FoodLevelChangeEvent event) {
    if (!shouldExecuteEffect()) return;
    if (!(event.getEntity() instanceof Player)) return;
    if (ignorePlayer(((Player) event.getEntity()))) return;
    int changedFoodLevel = event.getFoodLevel() - event.getEntity().getFoodLevel();
    if (changedFoodLevel > 0) {
      addPoints(event.getEntity().getUniqueId(), changedFoodLevel);
      Message.forName("points-change").send(event.getEntity(), Prefix.CHALLENGES, "+" + changedFoodLevel);
    }
  }

}
