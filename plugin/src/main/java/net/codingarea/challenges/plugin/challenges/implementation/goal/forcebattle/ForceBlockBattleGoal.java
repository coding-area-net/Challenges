package net.codingarea.challenges.plugin.challenges.implementation.goal.forcebattle;

import net.anweisen.utilities.common.annotations.Since;
import net.anweisen.utilities.common.config.Document;
import net.codingarea.challenges.plugin.challenges.implementation.goal.forcebattle.targets.BlockTarget;
import net.codingarea.challenges.plugin.challenges.type.abstraction.ForceBattleDisplayGoal;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.scheduler.policy.TimerPolicy;
import net.codingarea.challenges.plugin.management.scheduler.task.ScheduledTask;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.InventoryUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

@Since("2.2.0")
public class ForceBlockBattleGoal extends ForceBattleDisplayGoal<BlockTarget> {

  public ForceBlockBattleGoal() {
    super(Message.forName("menu-force-block-battle-goal-settings"));

    registerSetting("give-block", new BooleanSubSetting(
      () -> new ItemBuilder(Material.CHEST, Message.forName("item-force-block-battle-goal-give-block")),
      false
    ));
  }

  @Override
  protected BlockTarget[] getTargetsPossibleToFind() {
    List<Material> materials = BlockTarget.getPossibleBlocks();
    return materials.stream().map(BlockTarget::new).toArray(BlockTarget[]::new);
  }

  @Override
  public BlockTarget getTargetFromDocument(Document document, String path) {
    return new BlockTarget(document.getEnum(path, Material.class));
  }

  @Override
  public List<BlockTarget> getListFromDocument(Document document, String path) {
    return document.getEnumList(path, Material.class).stream().map(BlockTarget::new).collect(Collectors.toList());
  }

  @Override
  public Message getLeaderboardTitleMessage() {
    return Message.forName("force-block-battle-leaderboard");
  }

  @NotNull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.GRASS_BLOCK, Message.forName("item-force-block-battle-goal"));
  }

  @Override
  public void handleJokerUse(Player player) {
    super.handleJokerUse(player);
    if (giveBlockOnSkip()) {
      InventoryUtils.dropOrGiveItem(player.getInventory(), player.getLocation(), currentTarget.get(player.getUniqueId()).getTarget());
    }
  }

  @ScheduledTask(ticks = 5, async = false, timerPolicy = TimerPolicy.STARTED)
  public void checkBlocks() {
    if (!shouldExecuteEffect()) return;
    broadcastFiltered(player -> {
      BlockTarget target = currentTarget.get(player.getUniqueId());
      if (target != null && target.check(player)) {
        handleTargetFound(player);
      }
    });
  }

  private boolean giveBlockOnSkip() {
    return getSetting("give-block").getAsBoolean();
  }

}
