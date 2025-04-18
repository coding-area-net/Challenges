package net.codingarea.challenges.plugin.challenges.implementation.goal.forcebattle;

import net.anweisen.utilities.common.config.Document;
import net.codingarea.challenges.plugin.challenges.implementation.goal.forcebattle.targets.BiomeTarget;
import net.codingarea.challenges.plugin.challenges.type.abstraction.ForceBattleGoal;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.scheduler.policy.TimerPolicy;
import net.codingarea.challenges.plugin.management.scheduler.task.ScheduledTask;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ForceBiomeBattleGoal extends ForceBattleGoal<BiomeTarget> {

  public ForceBiomeBattleGoal() {
    super(Message.forName("menu-force-biome-battle-goal-settings"));
  }

  @NotNull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.FILLED_MAP, Message.forName("item-force-biome-battle-goal"));
  }

  @Override
  protected BiomeTarget[] getTargetsPossibleToFind() {
    List<Biome> biomes = BiomeTarget.getPossibleBiomes();
    return biomes.stream().map(BiomeTarget::new).toArray(BiomeTarget[]::new);
  }

  @Override
  public BiomeTarget getTargetFromDocument(Document document, String path) {
    return new BiomeTarget(Registry.BIOME.get(NamespacedKey.minecraft(Objects.requireNonNull(document.getString(path)).toLowerCase())));
  }

  @Override
  public List<BiomeTarget> getListFromDocument(Document document, String path) {
    return document.getStringList(path).stream()
      .map(biomeName -> new BiomeTarget(
        Registry.BIOME.get(NamespacedKey.minecraft(biomeName.toLowerCase()))
      ))
      .collect(Collectors.toList());
  }


  @Override
  protected Message getLeaderboardTitleMessage() {
    return Message.forName("force-biome-battle-leaderboard");
  }

  @ScheduledTask(ticks = 5, async = false, timerPolicy = TimerPolicy.STARTED)
  public void checkBiomes() {
    if (!shouldExecuteEffect()) return;
    broadcastFiltered(player -> {
      BiomeTarget target = currentTarget.get(player.getUniqueId());
      if (target != null && target.check(player)) {
        handleTargetFound(player);
      }
    });
  }

}
