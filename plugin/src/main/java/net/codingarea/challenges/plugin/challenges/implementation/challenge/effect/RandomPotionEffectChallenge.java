package net.codingarea.challenges.plugin.challenges.implementation.challenge.effect;

import net.codingarea.challenges.plugin.challenges.type.abstraction.menu.MenuSetting;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.SettingCategory;
import net.codingarea.challenges.plugin.management.scheduler.task.ScheduledTask;
import net.codingarea.challenges.plugin.utils.misc.MinecraftNameWrapper;
import net.codingarea.commons.common.collection.IRandom;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Since("2.0")
public class RandomPotionEffectChallenge extends MenuSetting {

  private int currentTime = 0;

  public RandomPotionEffectChallenge() {
//    super(Message.forName("menu-random-effect-challenge-settings"));
    super(MenuType.CHALLENGES, SettingCategory.EFFECT, new ItemStack(Material.BREWING_STAND), "random-effect-challenge");
//    registerSetting("time", new NumberSubSetting(
//        () -> new LegacyItemBuilder(Material.CLOCK, Message.forName("item-random-effect-time-challenge")),
//        value -> null,
//        value -> "§e" + value + " §7" + Message.forName(value == 1 ? "second" : "seconds").asString(),
//        1,
//        60,
//        30
//      )
//    );
//    registerSetting("length", new NumberSubSetting(
//        () -> new LegacyItemBuilder(Material.ARROW, Message.forName("item-random-effect-length-challenge")),
//        value -> null,
//        value -> "§e" + value + " §7" + Message.forName(value == 1 ? "second" : "seconds").asString(),
//        1,
//        20,
//        10
//      )
//    );
//    registerSetting("amplifier", new NumberSubSetting(
//        () -> new LegacyItemBuilder(Material.STONE_SWORD, Message.forName("item-random-effect-amplifier-challenge")),
//        value -> null,
//        value -> "§7" + Message.forName("amplifier") + " §e" + value,
//        1,
//        8,
//        3
//      )
//    );

  }

  @Nullable
  public static PotionEffectType getNewRandomEffect(@NotNull LivingEntity entity) {
    List<PotionEffectType> activeEffects = entity.getActivePotionEffects().stream().map(PotionEffect::getType).collect(Collectors.toList());

    ArrayList<PotionEffectType> possibleEffects = new ArrayList<>(Arrays.asList(PotionEffectType.values()));
    possibleEffects.removeAll(activeEffects);
    possibleEffects.remove(MinecraftNameWrapper.INSTANT_HEALTH);
    possibleEffects.remove(MinecraftNameWrapper.INSTANT_DAMAGE);
    return possibleEffects.get(IRandom.threadLocal().nextInt(possibleEffects.size()));
  }

  @ScheduledTask(ticks = 20, async = false)
  public void onSecond() {
    currentTime++;

    if (currentTime > getSetting("time").getAsInt()) {
      currentTime = 0;
      applyRandomEffect();
    }

  }

  private void applyRandomEffect() {
    Bukkit.getOnlinePlayers().forEach(this::applyRandomEffect);
  }

  private void applyRandomEffect(@NotNull Player entity) {
    PotionEffectType effect = getNewRandomEffect(entity);
    if (effect == null) return;
    applyEffect(entity, effect);
  }

  private void applyEffect(@NotNull Player player, @NotNull PotionEffectType effectType) {
    PotionEffect potionEffect = new PotionEffect(effectType, (getSetting("length").getAsInt() + 1) * 20, getSetting("amplifier").getAsInt() - 1);
    player.addPotionEffect(potionEffect);
  }

}
