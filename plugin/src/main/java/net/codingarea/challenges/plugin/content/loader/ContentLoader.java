package net.codingarea.challenges.plugin.content.loader;

import net.codingarea.challenges.plugin.Challenges;

import javax.annotation.Nonnull;
import java.io.File;

public abstract class ContentLoader {

	@Nonnull
	protected final File getMessagesFolder() {
		return Challenges.getInstance().getDataFile("messages");
	}

	@Nonnull
	protected final File getMessageFile(@Nonnull String name, @Nonnull String extension) {
		return new File(getMessagesFolder(), name + "." + extension);
	}

	@Nonnull
	protected final String getGitHubUrl(@Nonnull String path) {
		return "https://raw.githubusercontent.com/anweisen/Challenges/" + (Challenges.getInstance().isDevMode() ? "development" : "master") + "/" + path;
	}

	protected abstract void load();

}
