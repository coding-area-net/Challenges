package net.codingarea.challenges.plugin.content.loader;

import net.codingarea.challenges.plugin.Challenges;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public abstract class ContentLoader {

  @NotNull
  protected final File getMessagesFolder() {
    return Challenges.getInstance().getDataFile("messages");
  }

  @NotNull
  protected final File getMessageFile(@NotNull String name, @NotNull String extension) {
    return new File(getMessagesFolder(), name + "." + extension);
  }

  @NotNull
  protected final String getGitHubUrl(@NotNull String path) {
    return "https://raw.githubusercontent.com/anweisen/Challenges/" + (Challenges.getInstance().isDevMode() ? "development" : "master") + "/" + path;
  }

  protected abstract void load();

}
