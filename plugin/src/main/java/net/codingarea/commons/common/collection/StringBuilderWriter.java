package net.codingarea.commons.common.collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Writer;

/**
 * @author org.apache.commons.io
 */
public class StringBuilderWriter extends Writer {

	private final StringBuilder builder;

	public StringBuilderWriter() {
		this.builder = new StringBuilder();
	}

	public StringBuilderWriter(int capacity) {
		this.builder = new StringBuilder(capacity);
	}

	public StringBuilderWriter(@Nullable StringBuilder builder) {
		this.builder = builder != null ? builder : new StringBuilder();
	}

	public Writer append(char value) {
		builder.append(value);
		return this;
	}

	public Writer append(@Nullable CharSequence value) {
		builder.append(value);
		return this;
	}

	public Writer append(@Nullable CharSequence value, int start, int end) {
		builder.append(value, start, end);
		return this;
	}

	public void close() {
	}

	public void flush() {
	}

	public void write(@Nonnull String value) {
		builder.append(value);
	}

	public void write(@Nullable char[] value, int offset, int length) {
		if (value != null) {
			builder.append(value, offset, length);
		}
	}

	@Nonnull
	public StringBuilder getBuilder() {
		return this.builder;
	}

	@Override
	public String toString() {
		return this.builder.toString();
	}

}

