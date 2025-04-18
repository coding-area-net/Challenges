package net.codingarea.challenges.plugin.utils.bukkit.misc.version;

import org.bukkit.Bukkit;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

public enum MinecraftVersion implements Version {

    V1_0,       // 1.0
    V1_1,       // 1.1
    V1_2_1,     // 1.2.1
    V1_3_1,     // 1.3.1
    V1_4_2,     // 1.4.2
    V1_5,       // 1.5
    V1_6,       // 1.6
    V1_7,       // 1.7
    V1_7_2,     // 1.7.2
    V1_8,       // 1.8
    V1_9,       // 1.9
    V1_10,      // 1.10
    V1_11,      // 1.11
    V1_12,      // 1.12
    V1_13,      // 1.13
    V1_14,      // 1.14
    V1_15,      // 1.15
    V1_16,      // 1.16
    V1_16_5,    // 1.16.5
    V1_17,      // 1.17
    V1_18,		// 1.18
    V1_19,		// 1.19
    V1_20,    // 1.20
    V1_20_1, // 1.20.1
    V1_20_2, // 1.20.2
    V1_20_3, // 1.20.3
    V1_20_4, // 1.20.4
    V1_20_5, // 1.20.5
    V1_21,     // 1.21
    V1_21_1, // 1.21.1
    V1_21_2, // 1.21.2
    V1_21_3, // 1.21.3
    V1_21_4, // 1.21.4
    V1_21_5 // 1.21.5
    ;

    private final int major, minor, revision;

    MinecraftVersion() {

        String name = this.name().substring(1);
        String[] version = name.split("_");

        if (version.length != 2 && version.length != 3)
            throw new IllegalArgumentException("Name '" + name() + "' does not match pattern: V{major}_{minor}_[revision]");

        major    = Integer.parseInt(version[0]);
        minor    = Integer.parseInt(version[1]);
        revision = version.length > 2 ? Integer.parseInt(version[2]) : 0;

    }

    @Override
    public int getMajor() {
        return major;
    }

    @Override
    public int getMinor() {
        return minor;
    }

    @Override
    public int getRevision() {
        return revision;
    }

    @Override
    public String toString() {
        return this.format();
    }

    @Nonnull
    @CheckReturnValue
    public static Version parseExact(@Nonnull String bukkitVersion) {
        bukkitVersion = bukkitVersion.substring(0, bukkitVersion.indexOf("-"));
        return Version.parse(bukkitVersion);
    }

    @Nonnull
    @CheckReturnValue
    public static Version findNearest(@Nonnull Version realVersion) {
        return Version.findNearest(realVersion, values());
    }

    private static Version currentExact;
    private static Version current;

    @Nonnull
    @CheckReturnValue
    public static Version currentExact() {
        if (currentExact == null)
            currentExact = parseExact(Bukkit.getBukkitVersion());
        return currentExact;
    }

    @Nonnull
    @CheckReturnValue
    public static Version current() {
        if (current == null)
            current = findNearest(currentExact());
        return current;
    }

}
