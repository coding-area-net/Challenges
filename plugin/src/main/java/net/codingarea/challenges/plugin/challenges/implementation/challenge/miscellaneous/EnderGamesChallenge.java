package net.codingarea.challenges.plugin.challenges.implementation.challenge.miscellaneous;

import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import net.codingarea.commons.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.type.abstraction.TimedChallenge;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

@Since("2.0")
public class EnderGamesChallenge extends TimedChallenge {

  public EnderGamesChallenge() {
    super(MenuType.CHALLENGES, 1, 10, 5, false);
  }

  @Nonnull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.ENDER_PEARL, Message.forName("item-ender-games-challenge"));
  }

  @Nullable
  @Override
  protected String[] getSettingsDescription() {
    return Message.forName("item-time-seconds-range-description").asArray(getValue() * 60 - 20, getValue() * 60 + 20);
  }

  @Override
  public void playValueChangeTitle() {
    ChallengeHelper.playChallengeSecondsRangeValueChangeTitle(this, getValue() * 60 - 20, getValue() * 60 + 20);
  }

  @Override
  protected int getSecondsUntilNextActivation() {
    return globalRandom.around(getValue() * 60, 20);
  }

  @Override
  protected void onTimeActivation() {
    restartTimer();
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (ignorePlayer(player)) continue;
      teleportRandom(player);
    }
    SoundSample.TELEPORT.broadcast();
  }

  private void teleportRandom(@Nonnull Player player) {

    List<Entity> list = player.getWorld().getNearbyEntities(player.getLocation(), 200, 200, 200).stream()
      .filter(entity -> !(entity instanceof Player))
      .filter(entity -> entity instanceof LivingEntity)
      .toList();

    Entity targetEntity = list.get(globalRandom.nextInt(list.size()));

    Location playerLocation = player.getLocation().clone();
    player.teleport(targetEntity.getLocation());
    Message.forName("endergames-teleport").send(player, Prefix.CHALLENGES, targetEntity.getType());
    targetEntity.teleport(playerLocation);

  }

}
