package net.codingarea.challenges.plugin.content.loader;

import lombok.Getter;
import net.anweisen.utilities.bukkit.utils.logging.Logger;
import net.anweisen.utilities.common.collection.IOUtils;
import net.anweisen.utilities.common.version.Version;
import net.codingarea.challenges.plugin.Challenges;
import org.bukkit.configuration.file.YamlConfiguration;

import java.net.URL;

/**
 * @author anweisen | <a href="https://github.com/anweisen">...</a>
 * @since 2.0
 */
public final class UpdateLoader extends ContentLoader {

	public static final int RESOURCE_ID = 80548;

	@Getter
    private static boolean newestPluginVersion = true;
	@Getter
    private static boolean newestConfigVersion = true;
	@Getter
    private static Version defaultConfigVersion;
	@Getter
    private static Version currentConfigVersion;

    @Override
	protected void load() {
		try {
			URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + RESOURCE_ID);
			String response = IOUtils.toString(url);
			Version plugin = Challenges.getInstance().getVersion();
			YamlConfiguration defaultConfig = Challenges.getInstance().getConfigManager().getDefaultConfig();
			defaultConfigVersion = defaultConfig == null ? plugin : Version.parse(defaultConfig.getString("config-version"));
			currentConfigVersion = Version.parse(Challenges.getInstance().getConfigDocument().getString("config-version"));
			Version latestVersion = Version.parse(response);

			if (latestVersion.isNewerThan(plugin)) {
				Logger.info("A new version of Challenges is available: {}, you have {}", latestVersion, plugin);
				newestPluginVersion = false;
			}
			if (defaultConfigVersion.isNewerThan(currentConfigVersion)) {
				Logger.info("A new version of the config (plugins/Challenges/config.yml) is available");
				newestConfigVersion = false;
			}

		} catch (Exception ex) {
			Logger.error("Could not check for update: {}", ex.getMessage());
		}
	}

}
