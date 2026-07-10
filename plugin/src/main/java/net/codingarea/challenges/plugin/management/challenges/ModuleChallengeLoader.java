package net.codingarea.challenges.plugin.management.challenges;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.implementation.damage.DamageRuleSetting;
import net.codingarea.challenges.plugin.challenges.implementation.material.BlockMaterialSetting;
import net.codingarea.challenges.plugin.challenges.type.IChallenge;
import net.codingarea.challenges.plugin.challenges.type.annotation.RequireDepend;
import net.codingarea.challenges.plugin.challenges.type.annotation.RequireVersion;
import net.codingarea.commons.bukkit.core.BukkitModule;
import net.codingarea.commons.bukkit.utils.logging.Logger;
import net.codingarea.commons.bukkit.utils.misc.MinecraftVersion;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.util.Optional;

public class ModuleChallengeLoader {

  protected final BukkitModule plugin;

  public ModuleChallengeLoader(@NotNull BukkitModule plugin) {
    this.plugin = plugin;
  }

  public final void registerWithCommand(@NotNull IChallenge challenge, @NotNull String... commandNames) {
    try {

      Challenges.getInstance().getChallengeManager().register(challenge);
      Challenges.getInstance().getScheduler().register(challenge);

      if (challenge instanceof CommandExecutor) {
        plugin.registerCommand((CommandExecutor) challenge, commandNames);
      }
      if (challenge instanceof Listener) {
        plugin.registerListener((Listener) challenge);
      }

    } catch (Throwable ex) {
      Logger.error("Could not register challenge {}", challenge.getClass().getSimpleName(), ex);
    }
  }

  public final void register(@NotNull IChallenge challenge) {
    registerWithCommand(challenge);
  }

  public final void registerWithCommand(@NotNull Class<? extends IChallenge> classOfChallenge, @NotNull String[] commandNames, @NotNull Class<?>[] parameterClasses, @NotNull Object... parameters) {
    try {

      if (classOfChallenge.isAnnotationPresent(RequireVersion.class)) {
        RequireVersion annotation = classOfChallenge.getAnnotation(RequireVersion.class);
        MinecraftVersion minVersion = annotation.value();

        if (MinecraftVersion.current().isOlderThan(minVersion)) {
          Logger.debug("Did not register challenge {}, requires version {}, server running on {}", classOfChallenge.getSimpleName(), minVersion, MinecraftVersion.current());
          return;
        }
      }

      if (classOfChallenge.isAnnotationPresent(RequireDepend.class)) {
        RequireDepend annotation = classOfChallenge.getAnnotation(RequireDepend.class);
        String depend = annotation.plugin();

        if (!plugin.getServer().getPluginManager().isPluginEnabled(depend)) {
          Logger.debug("Did not register challenge {}, requires plugin {}", classOfChallenge.getSimpleName(), depend);
          return;
        }
      }

      Constructor<? extends IChallenge> constructor = classOfChallenge.getDeclaredConstructor(parameterClasses);
      IChallenge challenge = constructor.newInstance(parameters);

      registerWithCommand(challenge, commandNames);

    } catch (Throwable ex) {
      Logger.error("Could not create challenge {}", classOfChallenge.getSimpleName(), ex);
    }
  }

  public final void register(@NotNull Class<? extends IChallenge> classOfChallenge, @NotNull Class<?>[] parameterClasses, @NotNull Object... parameters) {
    registerWithCommand(classOfChallenge, new String[0], parameterClasses, parameters);
  }

  public final void register(@NotNull Class<? extends IChallenge> classOfChallenge, @NotNull Object... parameters) {

    Class<?>[] parameterClasses = new Class[parameters.length];
    for (int i = 0; i < parameters.length; i++) {
      parameterClasses[i] = Optional.ofNullable(parameters[i]).<Class<?>>map(Object::getClass).orElse(Object.class);
    }

    register(classOfChallenge, parameterClasses, parameters);

  }

  public final void registerWithCommand(@NotNull Class<? extends IChallenge> classOfChallenge, @NotNull String... commandNames) {
    registerWithCommand(classOfChallenge, commandNames, new Class[0]);
  }

  public final void registerDamageRule(@NotNull String name, @NotNull Material displayItemMaterial, @NotNull DamageCause... causes) {
    registerDamageRule(name, new ItemStack(displayItemMaterial), causes);
  }

  public final void registerDamageRule(@NotNull String name, @NotNull ItemStack displayItemPreset, @NotNull DamageCause... causes) {
    register(DamageRuleSetting.class, new Class[]{ItemStack.class, String.class, DamageCause[].class}, displayItemPreset, name, causes);
  }

  public final void registerMaterialRule(@NotNull String name, @NotNull Material... materials) {
    registerMaterialRule(name, new ItemStack(materials[0]), materials);
  }

  public final void registerMaterialRule(@NotNull String name, @NotNull ItemStack preset, @NotNull Material... materials) {
    register(BlockMaterialSetting.class, new Class[]{String.class, ItemStack.class, Material[].class}, name, preset, materials);
  }

  /**
   * Unregisters an existing challenge and deletes its settings.
   * It does not unregister commands!
   */
  public final void unregister(@NotNull IChallenge challenge) {
    Challenges.getInstance().getChallengeManager().unregister(challenge);
    Challenges.getInstance().getScheduler().unregister(challenge);
    Challenges.getInstance().getConfigManager().getSettingsConfig().remove(challenge.getUniqueName());
    Challenges.getInstance().getConfigManager().getGamestateConfig().remove(challenge.getUniqueGamestateName());

    if (challenge instanceof Listener) {
      HandlerList.unregisterAll((Listener) challenge);
    }
  }

}
