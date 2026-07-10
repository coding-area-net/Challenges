package net.codingarea.challenges.plugin.challenges.implementation.challenge.force;

import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.type.abstraction.EndingForceChallenge;
import net.codingarea.challenges.plugin.challenges.type.annotation.ExcludeFromRandomChallenges;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.content.i18n.Prefix;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.management.server.scoreboard.ChallengeBossBar.BossBarInstance;
import net.codingarea.commons.bukkit.utils.misc.BukkitReflectionUtils;
import net.codingarea.commons.common.config.Document;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

@ExcludeFromRandomChallenges
public class ForceHeightChallenge extends EndingForceChallenge {

  private int height;

  public ForceHeightChallenge() {
    super(MenuType.CHALLENGES, SettingCategory.FORCE, 2, 15, new ItemStack(Material.IRON_BOOTS), "force-height");
  }

  @Override
  public LocalizableMessage getSettingsDescription() {
    return ChallengeHelper.getSettingsDescriptionTimeSecondsRange(getValue() * 60, 30);
  }

  @NotNull
  @Override
  protected BiConsumer<BossBarInstance, Player> setupBossbar() {
    return (bossbar, player) -> {
      if (getState() == WAITING) {
        bossbar.setTitle(getChallengeMessageKey("bossbar-waiting"));
        return;
      }

      bossbar.setColor(BossBar.Color.GREEN);
      bossbar.setProgress(getProgress());
      bossbar.setTitle(getChallengeMessageKey("bossbar-instruction"), height, ChallengeAPI.formatTime(getSecondsLeftUntilNextActivation()));
    };
  }

  @Override
  protected boolean isFailing(@NotNull Player player) {
    return player.getLocation().getBlockY() != height;
  }

  @Override
  protected void broadcastFailedMessage(@NotNull Player player) {
    getChallengeMessageKey("fail").broadcast(Prefix.CHALLENGES, player, player.getLocation().getBlockY());
  }

  @Override
  protected void broadcastSuccessMessage() {
    getChallengeMessageKey("success").broadcast(Prefix.CHALLENGES);
  }

  @Override
  protected void chooseForcing() {
    World world = ChallengeAPI.getGameWorld(Environment.NORMAL);
    height = globalRandom.range(BukkitReflectionUtils.getMinHeight(world), world.getMaxHeight());
  }

  @Override
  protected int getSecondsUntilNextActivation() {
    return globalRandom.around(getValue() * 60, 30);
  }

  @Override
  protected int getForcingTime() {
    return globalRandom.range(3 * 60 + 30, 5 * 60);
  }

  @Override
  public void loadGameState(@NotNull Document document) {
    super.loadGameState(document);
    if (document.contains("target")) {
      height = document.getInt("target");
      setState(COUNTDOWN);
    }
  }

  @Override
  public void writeGameState(@NotNull Document document) {
    super.writeGameState(document);
    document.set("target", height);
  }

}
