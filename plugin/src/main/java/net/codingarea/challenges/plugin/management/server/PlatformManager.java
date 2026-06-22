package net.codingarea.challenges.plugin.management.server;

import net.codingarea.challenges.platform.message.MessagePlatform;
import net.codingarea.challenges.platform.message.legacy.LegacyMiniMessagePlatform;
import net.codingarea.challenges.platform.message.paper.NativePaperMessagePlatform;
import net.codingarea.challenges.platform.message.paper.NativePaperMessagePlatformChecker;
import net.codingarea.challenges.plugin.Challenges;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class PlatformManager {

  private MessagePlatform messagePlatform;

  public void createPlatforms() {
    messagePlatform = createMessagePlatform();
  }

  public void enablePlatforms() {
    messagePlatform.enable();
  }

  public void disablePlatforms() {
    if (messagePlatform != null) messagePlatform.disable();
  }

  @NotNull
  public MessagePlatform getMessagePlatform() {
    if (messagePlatform == null)
      throw new IllegalStateException("PlatformManager not loaded yet (MessagePlatform uninitialized)");
    return messagePlatform;
  }

  public void setMessagePlatform(@NotNull MessagePlatform messagePlatform) {
    Objects.requireNonNull(messagePlatform, "messagePlatform cannot be null");
    this.messagePlatform = messagePlatform;
  }

  private static MessagePlatform createMessagePlatform() {
    if (NativePaperMessagePlatformChecker.isAvailable()) {
      Challenges.getInstance().getILogger().debug("Found and using native Paper message API");
      return new NativePaperMessagePlatform();
    }
    Challenges.getInstance().getILogger().debug("Using legacy fallback message API");
    return new LegacyMiniMessagePlatform(Challenges.getInstance());
  }

}
