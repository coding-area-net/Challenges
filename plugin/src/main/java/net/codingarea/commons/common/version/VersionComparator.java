package net.codingarea.commons.common.version;

import javax.annotation.Nonnull;
import java.util.Comparator;

public class VersionComparator implements Comparator<Version> {

	@Override
	public int compare(@Nonnull Version v1, @Nonnull Version v2) {
		return v1.equals(v2) ? 0 : v1.isNewerThan(v2) ? 1 : -1;
	}

}
