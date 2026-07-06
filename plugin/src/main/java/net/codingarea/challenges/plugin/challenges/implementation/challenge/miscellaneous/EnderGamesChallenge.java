package net.codingarea.challenges.plugin.challenges.implementation.challenge.miscellaneous;

import net.codingarea.challenges.plugin.challenges.type.abstraction.TimedChallenge;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.i18n.Prefix;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.commons.bukkit.utils.animation.SoundSample;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

@Since("2.0")
public class EnderGamesChallenge extends TimedChallenge {

  public EnderGamesChallenge() {
    super(MenuType.CHALLENGES, null, 1, 10, 5, false, new ItemStack(Material.ENDER_PEARL), "ender-games");
  }

//  @Nullable
//  @Override
//  protected String[] getSettingsDescription() {
//    return Message.forName("item-time-seconds-range-description").asArray(getValue() * 60 - 20, getValue() * 60 + 20);
//  }

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

  private void teleportRandom(@NotNull Player player) {

    List<Entity> list = player.getWorld().getNearbyEntities(player.getLocation(), 200, 200, 200).stream()
      .filter(entity -> !(entity instanceof Player))
      .filter(entity -> entity instanceof LivingEntity)
      .collect(Collectors.toList());

    Entity targetEntity = list.get(globalRandom.nextInt(list.size()));

    Location playerLocation = player.getLocation().clone();
    player.teleport(targetEntity.getLocation());
    getChallengeMessageKey("teleport").send(player, Prefix.CHALLENGES, targetEntity.getType());
    targetEntity.teleport(playerLocation);

  }

}
