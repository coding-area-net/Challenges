package net.codingarea.commons.common.version;

import net.codingarea.commons.common.annotations.Since;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public interface Version {

	@Nonnegative
	int getMajor();

	@Nonnegative
	int getMinor();

	@Nonnegative
	int getRevision();

	default boolean isNewerThan(@Nonnull Version other) {
		return this.intValue() > other.intValue();
	}

	default boolean isNewerOrEqualThan(@Nonnull Version other) {
		return this.intValue() >= other.intValue();
	}

	default boolean isOlderThan(@Nonnull Version other) {
		return this.intValue() < other.intValue();
	}

	default boolean isOlderOrEqualThan(@Nonnull Version other) {
		return this.intValue() <= other.intValue();
	}

	default boolean equals(@Nonnull Version other) {
		return this.intValue() == other.intValue();
	}

	@Nonnull
	default String format() {
		int revision = getRevision();
		return revision > 0 ? String.format("%s.%s.%s", getMajor(), getMinor(), revision)
							: String.format("%s.%s",    getMajor(), getMinor());
	}

	default int intValue() {
		int major = getMajor();
		int minor = getMinor();
		int revision = getRevision();

		if (major > 99)     throw new IllegalStateException("Malformed version: major is greater than 99");
		if (minor > 99)     throw new IllegalStateException("Malformed version: minor is greater than 99");
		if (revision > 99)  throw new IllegalStateException("Malformed version: revision is greater than 99");

		return revision
			 + minor * 100
			 + major * 10000;
	}

	@Nonnull
	@CheckReturnValue
	static Version parse(@Nullable String input) {
		return parse(input, new VersionInfo(1, 0, 0));
	}

	@CheckReturnValue
	static Version parse(@Nullable String input, Version def) {
		return VersionInfo.parse(input, def);
	}

	@Nonnull
	@CheckReturnValue
	static Version parseExceptionally(@Nullable String input) {
		return VersionInfo.parseExceptionally(input);
	}

	@Nonnull
	@CheckReturnValue
	static Version getAnnotatedSince(@Nonnull Object object) {
		if (!object.getClass().isAnnotationPresent(Since.class)) return new VersionInfo(1, 0, 0);
		return parse(object.getClass().getAnnotation(Since.class).value());
	}

	@Nonnull
	@CheckReturnValue
	static <V extends Version> V findNearest(@Nonnull Version target, @Nonnull V[] sortedVersionsArray) {
		List<V> versions = new ArrayList<>(Arrays.asList(sortedVersionsArray));
		Collections.reverse(versions);
		for (V version : versions) {
			if (version.isNewerThan(target)) continue;
			return version;
		}
		throw new IllegalArgumentException("No version found for '" + target + "'");
	}

	@Nonnull
	@CheckReturnValue
	static Comparator<Version> comparator() {
		return new VersionComparator();
	}

}
