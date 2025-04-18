package net.codingarea.challenges.plugin.challenges.implementation.challenge.movement;

import net.anweisen.utilities.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifier;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.management.scheduler.policy.TimerPolicy;
import net.codingarea.challenges.plugin.management.scheduler.task.ScheduledTask;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.NameHelper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

@Since("2.0")
public class MoveMouseDamage extends SettingModifier {

  private final Map<UUID, Entry<Float, Float>> lastView = new HashMap<>();

  public MoveMouseDamage() {
    super(MenuType.CHALLENGES, 60);
    setCategory(SettingCategory.MOVEMENT);
  }

  @Nonnull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.COMPASS, Message.forName("item-no-mouse-move-challenge"));
  }

  @Nullable
  @Override
  protected String[] getSettingsDescription() {
    return Message.forName("item-heart-damage-description").asArray(getValue() / 2f);
  }

  @Override
  public void playValueChangeTitle() {
    ChallengeHelper.playChallengeHeartsValueChangeTitle(this, getValue() / 2);
  }

  @ScheduledTask(ticks = 1, timerPolicy = TimerPolicy.ALWAYS)
  public void onTick() {
    if (!shouldExecuteEffect()) {
      lastView.clear();
      return;
    }

    for (Player player : Bukkit.getOnlinePlayers()) {
      if (ignorePlayer(player)) {
        lastView.remove(player.getUniqueId());
        continue;
      }
      if (player.getNoDamageTicks() > 0) continue;

      float yaw = player.getLocation().getYaw();
      float pitch = player.getLocation().getPitch();
      Entry<Float, Float> pair = lastView.getOrDefault(player.getUniqueId(), new SimpleEntry<>(yaw, pitch));
      lastView.put(player.getUniqueId(), pair);

      if (yaw != pair.getKey() || pitch != pair.getValue()) {
        Bukkit.getScheduler().runTask(plugin, () -> {
          if (player.getNoDamageTicks() > 0) return;
          Message.forName("no-mouse-move-failed").broadcast(Prefix.CHALLENGES, NameHelper.getName(player));
          player.damage(getValue());
          player.setNoDamageTicks(5);
          Bukkit.getScheduler().runTaskLater(plugin, () -> lastView.remove(player.getUniqueId()), 3);
        });
      }
    }
  }

}
