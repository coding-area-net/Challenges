package net.codingarea.challenges.plugin.utils.bukkit.misc.Version;

import java.util.Comparator;
import javax.annotation.Nonnull;

public class VersionComparator implements Comparator<Version> {
    public VersionComparator() {
    }

    public int compare(@Nonnull Version v1, @Nonnull Version v2) {
        return v1.equals(v2) ? 0 : (v1.isNewerThan(v2) ? 1 : -1);
    }
}
