package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.codingarea.challenges.plugin.challenges.type.abstraction.Modifier;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.i18n.ArgumentFormat;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.misc.MinecraftNameWrapper;
import net.codingarea.commons.bukkit.utils.misc.MinecraftVersion;
import net.codingarea.commons.common.config.Document;
import net.codingarea.commons.common.config.document.GsonDocument;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MaxHealthSetting extends Modifier {

  /**
   * Offset that can be used to modify the max health for specific players with other challenges.
   * Is saved within the gamestate so it resets with a world reset.
   * Saves the data with the uuid as string as the key and with an integer which defines the offset
   * The health offset for every player is saved with the key "all"
   */
  private Document valueOffset = new GsonDocument();

  public MaxHealthSetting() {
    super(MenuType.SETTINGS, null, 1, 200 * 2, 20, new ItemStack(MinecraftNameWrapper.RED_DYE), "max-health");
  }

  @NotNull
  @Override
  public LocalizableMessage getSettingsName() {
    return ArgumentFormat.HP.apply(getValue());
  }

  @Override
  public void writeGameState(@NotNull Document document) {
    super.writeGameState(document);
    document.set("offset", valueOffset);
  }

  @Override
  public void loadGameState(@NotNull Document document) {
    super.loadGameState(document);
    valueOffset = document.contains("offset") ? document.getDocument("offset") : new GsonDocument();
    onValueChange();
  }

  @Override
  public void onValueChange() {
    broadcast(this::updateHealth);
  }

  @Override
  public void playValueChangeTitle() {
    ChallengeHelper.playChallengeHeartsValueChangeTitle(this);
  }

  @EventHandler
  public void onJoin(@NotNull PlayerJoinEvent event) {
    updateHealth(event.getPlayer());
  }

  private void updateHealth(Player player) {
    AttributeInstance attribute = player.getAttribute(MinecraftNameWrapper.MAX_HEALTH);
    if (attribute == null)
      return; // This should never happen because its a generic attribute, but just in case
    int newMaxHealth = getMaxHealth(player);
    double oldMaxHealth = attribute.getBaseValue();

    if (newMaxHealth <= 0) {
      ChallengeHelper.kill(player);
      valueOffset.remove(player.getUniqueId().toString());
      return;
    }

    if (oldMaxHealth != newMaxHealth) {
      attribute.setBaseValue(newMaxHealth);

      if (oldMaxHealth < newMaxHealth) {
        double oldHealth = player.getHealth();
        double newHealth = oldHealth + (newMaxHealth - oldMaxHealth);
        player.setHealth(Math.clamp(newHealth, 0, newMaxHealth));
      }
      if (MinecraftVersion.current().isNewerThan(MinecraftVersion.V1_19)) {
        player.sendHealthUpdate();
      }
      // TODO: Versions lower than 1.19 need to update health via nms
    }
  }

  public int getMaxHealth(Player player) {
    String key = player.getUniqueId().toString();
    return getValue() + valueOffset.getInt("all") + valueOffset.getInt(key);
  }

  public void addHealth(int health) {
    valueOffset.set("all", valueOffset.getInt("all") + health);
    onValueChange();
  }

  public void addHealth(Player player, int health) {
    String key = player.getUniqueId().toString();
    valueOffset.set(key, valueOffset.getInt(key) + health);
    updateHealth(player);
  }

}
