package net.codingarea.commons.common.misc;

import net.codingarea.commons.common.collection.IOUtils;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

public final class ImageUtils {

	private ImageUtils() {}

	/**
	 * @param height The y-position of the text
	 * @return The width of the text added in pixels
	 */
	public static int addCenteredText(@Nonnull Graphics2D graphics, @Nonnull String text, int height) {
		TextLayout layout = getTextLayout(graphics, text);
		int lineWidth = (int) layout.getBounds().getWidth();
		graphics.drawString(text, graphics.getDeviceConfiguration().getBounds().width / 2 - lineWidth / 2, height);
		return lineWidth;

	}

	/**
	 * @return The ending position of the text
	 */
	public static int addText(@Nonnull Graphics2D graphics, @Nonnull String text, int height, int x) {
		TextLayout layout = getTextLayout(graphics, text);
		graphics.drawString(text, x, height);
		return (int) (x + layout.getBounds().getWidth());
	}

	/**
	 * @return Returns where the text has started
	 */
	public static int addTextEndingAt(@Nonnull Graphics2D graphics, @Nonnull String text, int height, int endX) {
		TextLayout layout = getTextLayout(graphics, text);
		int position = (int) (endX - layout.getBounds().getWidth());
		graphics.drawString(text, position, height);
		return position;
	}

	/**
	 * @param height The y-position of the text
	 */
	public static void addTextEndingAtMid(@Nonnull Graphics2D graphics, @Nonnull String text, int height) {
		TextLayout layout = getTextLayout(graphics, text);
		int lineWidth = (int) layout.getBounds().getWidth();
		graphics.drawString(text, graphics.getClipBounds().width / 2 - lineWidth, height);
	}

	/**
	 * Reads a image from url using {@link ImageIO#read(InputStream)} and returns it
	 *
	 * @param url The URL the image is stored to
	 *
	 * @throws IOException
	 *         When something goes wrong while connecting or reading the image
	 */
	public static BufferedImage loadUrl(@Nonnull String url) throws IOException {
		URLConnection connection = IOUtils.createConnection(url);
		return ImageIO.read(connection.getInputStream());
	}

	public static BufferedImage loadResource(@Nonnull String path) throws IOException {
		InputStream stream = ImageUtils.class.getClassLoader().getResourceAsStream(path);
        assert stream != null;
        return ImageIO.read(stream);
	}

	public static BufferedImage loadFile(@Nonnull File file) throws IOException {
		return ImageIO.read(file);
	}

	@Nonnull
	public static TextLayout getTextLayout(@Nonnull Graphics2D graphics, @Nonnull String text) {
		return new TextLayout(text, graphics.getFont(), graphics.getFontRenderContext());
	}

	public static void darkenImage(@Nonnull BufferedImage image) {
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				image.setRGB(i, j, new Color(image.getRGB(i, j)).darker().getRGB());
			}
		}
	}

	@Nonnull
	@CheckReturnValue
	public static BufferedImage replaceTransparency(@Nonnull BufferedImage image, @Nullable Color replacementColor) {
		if (replacementColor == null) replacementColor = new Color(0, 0, 0, 0);
		BufferedImage created = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = created.createGraphics();
		graphics.setColor(replacementColor);
		graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
		graphics.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
		graphics.dispose();
		return created;
	}

}
