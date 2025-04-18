package net.codingarea.challenges.plugin.utils.bukkit.misc.Version;

import java.util.Objects;
import javax.annotation.Nullable;

import lombok.Getter;
import net.anweisen.utilities.common.logging.ILogger;

@Getter
public class VersionInfo implements Version {
    protected static final ILogger logger = ILogger.forThisClass();
    private final int major;
    private final int minor;
    private final int revision;

    public VersionInfo() {
        this(1, 0, 0);
    }

    public VersionInfo(int major, int minor, int revision) {
        this.major = major;
        this.minor = minor;
        this.revision = revision;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else {
            return other instanceof Version && this.equals(other);
        }
    }

    public int hashCode() {
        return Objects.hash(this.major, this.minor, this.revision);
    }

    public String toString() {
        return this.format();
    }

    public static Version parseExceptionally(@Nullable String input) {
        if (input == null) {
            throw new IllegalArgumentException("Version cannot be null");
        } else {
            String[] array = input.split("\\.");
            if (array.length == 0) {
                throw new IllegalArgumentException("Version cannot be empty");
            } else {
                try {
                    int major = Integer.parseInt(array[0]);
                    int minor = array.length >= 2 ? Integer.parseInt(array[1]) : 0;
                    int revision = array.length >= 3 ? Integer.parseInt(array[2]) : 0;
                    return new VersionInfo(major, minor, revision);
                } catch (Exception ex) {
                    throw new IllegalArgumentException("Cannot parse Version: " + input + " (" + ex.getMessage() + ")");
                }
            }
        }
    }

    public static Version parse(@Nullable String input, Version def) {
        try {
            return parseExceptionally(input);
        } catch (Exception ex) {
            logger.error("Could not parse version for input {}", ex.getMessage());
            return def;
        }
    }
}
