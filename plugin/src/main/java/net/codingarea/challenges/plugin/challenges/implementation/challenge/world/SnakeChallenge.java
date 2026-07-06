package net.codingarea.challenges.plugin.challenges.implementation.challenge.world;

import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.legacy.Message;
import net.codingarea.challenges.plugin.content.i18n.Prefix;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.utils.misc.BlockUtils;
import net.codingarea.challenges.plugin.utils.misc.NameHelper;
import net.codingarea.commons.common.config.Document;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SnakeChallenge extends Setting {

  private final ArrayList<Block> blocks = new ArrayList<>();

  public SnakeChallenge() {
    super(MenuType.CHALLENGES, SettingCategory.WORLD, new ItemStack(Material.BLUE_TERRACOTTA), "snake");
  }

  @Override
  protected void onDisable() {
    blocks.clear();
  }

  @Override
  public void writeGameState(@NotNull Document document) {
    super.writeGameState(document);

    List<Location> locations = blocks.stream().map(Block::getLocation).collect(Collectors.toList());
    document.set("blocks", locations);
  }

  @Override
  public void loadGameState(@NotNull Document document) {
    super.loadGameState(document);

    blocks.addAll(document.getSerializableList("blocks", Location.class).stream().map(Location::getBlock).collect(Collectors.toList()));
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onMove(@NotNull PlayerMoveEvent event) {
    if (!shouldExecuteEffect()) return;
    if (event.getTo() == null) return;
    if (event.getFrom().getBlock().equals(event.getTo().getBlock())) return;
    if (event.getPlayer().getGameMode() == GameMode.SPECTATOR || event.getPlayer().getGameMode() == GameMode.CREATIVE)
      return;

    Block from = event.getFrom().clone().subtract(0, 1, 0).getBlock();
    Block to = event.getTo().clone().subtract(0, 0.15, 0).getBlock();

    if (from.getType().isSolid()) {
      from.setType(BlockUtils.getTerracotta(getPlayersColor(event.getPlayer())), false);
      blocks.add(from);
    }

    if (blocks.contains(to)) {
      getChallengeMessageKey("failed").broadcast(Prefix.CHALLENGES, NameHelper.getName(event.getPlayer()));
      ChallengeHelper.kill(event.getPlayer());
      return;
    }

    if (to.getType().isSolid()) {
      to.setType(Material.BLACK_TERRACOTTA, false);

      Block block = event.getPlayer().getLocation().getBlock();
      if (!block.getType().isSolid()) {
        block.breakNaturally();
      }
    }

  }

  public int getPlayersColor(Player player) {
    int i = 0;
    for (Player currentPlayer : Bukkit.getOnlinePlayers()) {
      i++;
      if (i > 17) i = 0;
      if (currentPlayer == player) return i;
    }
    return 0;
  }

}
