package net.codingarea.challenges.plugin.challenges.implementation.goal.forcebattle.targets;

import net.codingarea.challenges.plugin.challenges.implementation.goal.forcebattle.ExtremeForceBattleGoal;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.utils.bukkit.misc.BukkitStringUtils;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BiomeTarget extends ForceTarget<Biome> {

  public BiomeTarget(Biome target) {
    super(target);
  }

  @Override
  public boolean check(Player player) {
    return player.getLocation().getBlock().getBiome() == target;
  }

  public static List<Biome> getPossibleBiomes() {
    return Arrays.stream(Biome.values())
      .filter(biome -> !biome.name().contains("VOID"))
      .filter(biome -> !biome.name().equals("CUSTOM"))
      .collect(Collectors.toList());
  }

  @Override
  public Object toMessage() {
    return target;
  }

  @Override
  public String getName() {
    return BukkitStringUtils.getBiomeName(target).toPlainText();
  }

  @Override
  public Message getNewTargetMessage() {
    return Message.forName("extreme-force-battle-new-biome");
  }

  @Override
  public Message getCompletedMessage() {
    return Message.forName("extreme-force-battle-found-biome");
  }

  @Override
  public ExtremeForceBattleGoal.TargetType getType() {
    return ExtremeForceBattleGoal.TargetType.BIOME;
  }

  @Override
  public Message getScoreboardDisplayMessage() {
    return Message.forName("force-battle-biome-target-display");
  }

}
