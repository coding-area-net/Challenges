package net.codingarea.challenges.plugin.content.loader;

import net.anweisen.utilities.bukkit.utils.logging.Logger;
import net.anweisen.utilities.common.config.FileDocument;
import net.anweisen.utilities.common.misc.FileUtils;
import net.codingarea.challenges.plugin.content.Prefix;

import java.io.File;

/**
 * @author anweisen | <a href="https://github.com/anweisen">...</a>
 * @since 2.0
 */
public final class PrefixLoader extends ContentLoader {

	@Override
	protected void load() {
		try {

			File file = getMessageFile("prefix", "properties");
			FileUtils.createFilesIfNecessary(file);

			FileDocument document = FileDocument.readPropertiesFile(file);
			boolean changed = false;

			for (Prefix prefix : Prefix.values()) {
				if (!document.contains(prefix.getName())) {
					document.set(prefix.getName(), prefix.toString());
					changed = true;
					continue;
				}

				prefix.setValue(document.getString(prefix.getName()));
			}

			if (changed) document.save();
			Logger.info("Successfully loaded {} prefixes from config file", Prefix.values().size());

		} catch (Exception ex) {
			Logger.error("Could not load prefixes", ex);
		}
	}

}
