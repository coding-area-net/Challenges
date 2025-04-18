package net.codingarea.challenges.plugin.challenges.implementation.challenge.entities;

import net.anweisen.utilities.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.spigot.events.EntityDamageByPlayerEvent;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Since("2.0")
public class MobTransformationChallenge extends Setting {

  public MobTransformationChallenge() {
    super(MenuType.CHALLENGES);
    setCategory(SettingCategory.ENTITIES);
  }

  @Override
  protected void onEnable() {
    bossbar.setContent((bossbar, player) -> {
      bossbar.setColor(BarColor.GREEN);
      EntityType type = getPlayerData(player).getEnum("type", EntityType.class);
      Object typeName = type == null ? "None" : type;
      bossbar.setTitle(Message.forName("bossbar-mob-transformation").asString(typeName));
    });
    bossbar.show();
  }

  @Override
  protected void onDisable() {
    bossbar.hide();
  }

  @Nonnull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.STONE_SWORD, Message.forName("item-mob-transformation-challenge"));
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onEntityDamageByPlayer(@Nonnull EntityDamageByPlayerEvent event) {
    if (!shouldExecuteEffect()) return;
    if (event.getEntity() instanceof Player || !(event.getEntity() instanceof LivingEntity) || event.getEntity() instanceof EnderDragon)
      return;

    Player player = event.getDamager();
    if (ignorePlayer(player)) return;

    EntityType type = getPlayerData(player).getEnum("type", event.getEntityType());

    if (type != event.getEntityType()) {
      event.getEntity().remove();
      event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation(), type);
    }
    getPlayerData(player).set("type", event.getEntityType());
    bossbar.update(player);
  }

  private EntityType getType(@Nonnull Player player, @Nullable EntityType defaultType) {
    EntityType type = getPlayerData(player).getEnum("type", EntityType.class);
    if (type == null) return defaultType;
    return type;
  }

}
