package net.codingarea.challenges.plugin.management.challenges;

import net.anweisen.utilities.bukkit.core.BukkitModule;
import net.anweisen.utilities.bukkit.utils.logging.Logger;
import net.anweisen.utilities.bukkit.utils.misc.MinecraftVersion;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.implementation.damage.DamageRuleSetting;
import net.codingarea.challenges.plugin.challenges.implementation.material.BlockMaterialSetting;
import net.codingarea.challenges.plugin.challenges.type.IChallenge;
import net.codingarea.challenges.plugin.management.challenges.annotations.RequireVersion;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.util.Optional;

public class ModuleChallengeLoader {

	protected final BukkitModule plugin;

	public ModuleChallengeLoader(@Nonnull BukkitModule plugin) {
		this.plugin = plugin;
	}

	public final void registerWithCommand(@Nonnull IChallenge challenge, @Nonnull String... commandNames) {
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

	public final void register(@Nonnull IChallenge challenge) {
		registerWithCommand(challenge);
	}

	public final void registerWithCommand(@Nonnull Class<? extends IChallenge> classOfChallenge, @Nonnull String[] commandNames, @Nonnull Class<?>[] parameterClasses, @Nonnull Object... parameters) {
		try {

			if (classOfChallenge.isAnnotationPresent(RequireVersion.class)) {
				RequireVersion annotation = classOfChallenge.getAnnotation(RequireVersion.class);
				MinecraftVersion minVersion = annotation.value();

				if (!MinecraftVersion.current().isNewerOrEqualThan(minVersion)) {
					Logger.debug("Did not register challenge {}, requires version {}, server running on {}", classOfChallenge.getSimpleName(), minVersion, MinecraftVersion.current());
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

	public final void register(@Nonnull Class<? extends IChallenge> classOfChallenge, @Nonnull Class<?>[] parameterClasses, @Nonnull Object... parameters) {
		registerWithCommand(classOfChallenge, new String[0], parameterClasses, parameters);
	}

	public final void register(@Nonnull Class<? extends IChallenge> classOfChallenge, @Nonnull Object... parameters) {

		Class<?>[] parameterClasses = new Class[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			parameterClasses[i] = Optional.ofNullable(parameters[i]).<Class<?>>map(Object::getClass).orElse(Object.class);
		}

		register(classOfChallenge, parameterClasses, parameters);

	}

	public final void registerWithCommand(@Nonnull Class<? extends IChallenge> classOfChallenge, @Nonnull String... commandNames) {
		registerWithCommand(classOfChallenge, commandNames, new Class[0]);
	}

	public final void registerDamageRule(@Nonnull String name, @Nonnull Material material, @Nonnull DamageCause... causes) {
		registerDamageRule(name, new ItemBuilder(material), causes);
	}

	public final void registerDamageRule(@Nonnull String name, @Nonnull ItemBuilder preset, @Nonnull DamageCause... causes) {
		register(DamageRuleSetting.class, new Class[]{ItemBuilder.class, String.class, DamageCause[].class}, preset, name, causes);
	}

	public final void registerMaterialRule(@Nonnull String title, @Nonnull String replacement, @Nonnull Material... materials) {
		registerMaterialRule("item-block-material", new Object[]{title, replacement}, materials);
	}

	public final void registerMaterialRule(@Nonnull String name, Object[] replacements, @Nonnull Material... materials) {
		registerMaterialRule(name, new ItemBuilder(materials[0]), replacements, materials);
	}

	public final void registerMaterialRule(@Nonnull String name, @Nonnull ItemBuilder preset, Object[] replacements, @Nonnull Material... materials) {
		register(BlockMaterialSetting.class, new Class[]{String.class, ItemBuilder.class, Object[].class, Material[].class}, name, preset, replacements, materials);
	}


	/**
	 * Unregisters an existing challenge and deletes its settings.
	 * It does not unregister commands!
	 */
	public final void unregister(@Nonnull IChallenge challenge) {
		Challenges.getInstance().getChallengeManager().unregister(challenge);
		Challenges.getInstance().getScheduler().unregister(challenge);
		Challenges.getInstance().getConfigManager().getSettingsConfig().remove(challenge.getUniqueName());
		Challenges.getInstance().getConfigManager().getGamestateConfig().remove(challenge.getUniqueGamestateName());

		if (challenge instanceof Listener) {
			HandlerList.unregisterAll((Listener) challenge);
		}
	}

}
