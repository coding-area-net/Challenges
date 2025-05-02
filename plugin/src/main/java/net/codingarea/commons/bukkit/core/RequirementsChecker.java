package net.codingarea.commons.bukkit.core;

import net.codingarea.commons.bukkit.utils.misc.MinecraftVersion;
import net.codingarea.commons.common.config.Document;
import net.codingarea.commons.common.version.Version;
import org.bukkit.Bukkit;

import javax.annotation.Nonnull;

public final class RequirementsChecker {

	private final BukkitModule module;

	public RequirementsChecker(@Nonnull BukkitModule module) {
		this.module = module;
	}

	public void checkExceptionally(@Nonnull Document requirements) throws IllegalStateException {
		if (requirements.getBoolean("spigot")) requireSpigot();
		if (requirements.getBoolean("paper")) requirePaper();
		if (requirements.contains("version")) requireVersion(requirements.getVersion("version"));
	}

	public boolean checkBoolean(@Nonnull Document requirements) {
		try {
			checkExceptionally(requirements);
			return true;
		} catch (IllegalStateException ex) {
			return false;
		}
	}

	private void requireSpigot() {
		try {
			Bukkit.spigot();
		} catch (Throwable ex) {
			log("");
			log("============================== {} ==============================", module.getName());
			log("");
			log("Your server does not run an instance of Spigot (Your server: {}", Bukkit.getVersion());
			log("Please use an instance of Spigot or Paper to be able to use this plugin!");
			log("");
			log("Paper Download: https://papermc.io/downloads");
			log("Spigot Download: https://getbukkit.org/download/spigot");
			log("");
			log("============================== {} ==============================", module.getName());
			log("");

			throw new IllegalStateException();
		}
	}

	private void requirePaper() {
		try {
			Class.forName("com.destroystokyo.paper.VersionHistoryManager");
		} catch (Throwable ex) {
			log("");
			log("============================== {} ==============================", module.getName());
			log("");
			log("Your server does not run an instance of PaperMC (Your server: {}", Bukkit.getVersion());
			log("Please use an instance of Paper to be able to use this plugin!");
			log("");
			log("Paper Download: https://papermc.io/downloads");
			log("");
			log("============================== {} ==============================", module.getName());
			log("");

			throw new IllegalStateException();
		}
	}

	private void requireVersion(@Nonnull Version required) {
		if (MinecraftVersion.currentExact().isOlderThan(required)) {
			log("");
			log("============================== {} ==============================", module.getName());
			log("");
			log("This plugin requires the server version {} (You have: {})", required.format(), MinecraftVersion.currentExact().format());
			log("Please use this version (or an newer version) to be able to use this plugin!");
			log("");
			log("Paper Download: https://papermc.io/downloads");
			log("Spigot Download: https://getbukkit.org/download/spigot");
			log("");
			log("============================== {} ==============================", module.getName());
			log("");

			throw new IllegalStateException();
		}
	}

	private void log(@Nonnull String line, @Nonnull Object... args) {
		module.getILogger().error(line, args);
	}

}
