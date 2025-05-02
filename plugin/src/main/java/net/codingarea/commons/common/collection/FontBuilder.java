package net.codingarea.commons.common.collection;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class FontBuilder {

	private Font font;

	public FontBuilder(@Nonnull File file) throws IOException, FontFormatException {
		this(file, Font.TRUETYPE_FONT);
	}

	public FontBuilder(@Nonnull File file, int type) throws IOException, FontFormatException {
		this.font = Font.createFont(type, file);
	}

	public FontBuilder(@Nonnull String resource) throws IOException, FontFormatException {
		this(resource, Font.TRUETYPE_FONT);
	}

	public FontBuilder(@Nonnull String resource, int type) throws IOException, FontFormatException {
		this.font = Font.createFont(type, Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(resource)));
	}

	@Nonnull
	@CheckReturnValue
	public FontBuilder bold() {
		return style(Font.BOLD);
	}

	@Nonnull
	@CheckReturnValue
	public FontBuilder italic() {
		return style(Font.ITALIC);
	}

	@Nonnull
	@CheckReturnValue
	public FontBuilder style(int style) {
		font = font.deriveFont(style);
		return this;
	}

	@Nonnull
	@CheckReturnValue
	public FontBuilder size(float size) {
		font = font.deriveFont(size);
		return this;
	}

	@Nonnull
	@CheckReturnValue
	public FontBuilder derive(int style, float size) {
		font = font.deriveFont(style, size);
		return this;
	}

	@Nonnull
	public Font build() {
		registerFont(font);
		return font;
	}

	public static void registerFont(@Nonnull Font font) {
		GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
	}

	@Nonnull
	@CheckReturnValue
	public static FontBuilder fromFile(@Nonnull String filename) {
		try {
			return new FontBuilder(new File(filename));
		} catch (Exception ex) {
			throw new WrappedException(ex);
		}
	}

	@Nonnull
	@CheckReturnValue
	public static FontBuilder fromResource(@Nonnull String resource) {
		try {
			return new FontBuilder(resource);
		} catch (Exception ex) {
			throw new WrappedException(ex);
		}
	}

}
