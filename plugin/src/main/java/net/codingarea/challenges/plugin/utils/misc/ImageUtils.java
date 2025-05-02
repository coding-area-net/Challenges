package net.codingarea.challenges.plugin.utils.misc;

import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public final class ImageUtils {

  public static final char IMAGE_CHAR = '█';

  private ImageUtils() {
  }

  @Nullable
  public static BufferedImage getImage(@Nonnull String url) throws IOException {
    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
    return ImageIO.read(connection.getInputStream());
  }

  public static BufferedImage getPlayerHead(@Nonnull Player player, int size) throws IOException {
    String url = "https://crafatar.com/avatars/" + player.getUniqueId() + "?size=" + size + "&overlay";
    return getImage(url);
  }

  public static String[] convertImageToText(@Nonnull BufferedImage image) {
    String[] output = new String[image.getHeight()];
    for (int y = 0; y < output.length; y++) {
      StringBuilder text = new StringBuilder();
      for (int x = 0; x < image.getWidth(); x++) {
        text.append(ColorConversions.convertAwtColorToChatColor(new Color(image.getRGB(x, y))))
          .append(IMAGE_CHAR);
      }
      output[y] = text.toString();
    }
    return output;
  }

}
