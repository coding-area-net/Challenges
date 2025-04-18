package net.codingarea.challenges.plugin.content;

import net.anweisen.utilities.common.config.Document;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.utils.misc.ColorConversions;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public final class ItemDescription {

  private static final Document config = Challenges.getInstance().getConfigDocument().getDocument("design");

  private final String[] colors;
  private final String[] lore;
  private final String name;
  private final String originalName;

  public ItemDescription(@Nonnull String[] themeColors, @Nonnull String name, @Nonnull String[] formattedLore) {
    this.colors = themeColors;
    this.name = Message.forName("item-prefix") + name;
    this.originalName = name;
    this.lore = formattedLore;
  }

  public ItemDescription(@Nonnull String[] description) {
    if (description.length == 0)
      throw new IllegalArgumentException("Invalid item description: Cannot be empty");

    originalName = description[0];
    name = Message.forName("item-prefix") + originalName;
    colors = determineColors(originalName);

    List<String> loreOutput = new ArrayList<>();
    fillLore(description, loreOutput);
    lore = loreOutput.toArray(new String[0]);
  }

  public static ItemDescription empty() {
    return new ItemDescription(new String[]{"§e"}, Message.NULL, new String[0]);
  }

  @Nonnull
  public String getName() {
    return name;
  }

  @Nonnull
  public String getOriginalName() {
    return originalName;
  }

  @Nonnull
  public String[] getTheme() {
    return colors;
  }

  @Nonnull
  public String[] getLore() {
    return lore;
  }

  private void fillLore(@Nonnull String[] origin, @Nonnull List<String> output) {

    String colorBefore = "§7";
    boolean inColor = false;
    boolean nextIsColor = false;
    int themeIndex = 0;

    // Start at 1, first entry is not content of lore
    for (int i = 1; i < origin.length; i++) {
      StringBuilder line = new StringBuilder();
      line.append(colorBefore);
      for (char c : origin[i].toCharArray()) {
        if (c == '*') {
          if (inColor) {
            line.append(colorBefore);
            inColor = false;
          } else {
            line.append(colors[themeIndex]);
            themeIndex++;
            if (themeIndex >= colors.length)
              themeIndex = 0;
            inColor = true;
          }
        } else {
          line.append(c);
          if (c == '§') {
            nextIsColor = true;
          } else if (nextIsColor) {
            nextIsColor = false;
            if (ColorConversions.isValidColorCode(c))
              colorBefore = "";
            colorBefore += "§" + c;
          }
        }
      }
      output.add(line.toString());
    }

    if (output.isEmpty()) return;
    if (config.getBoolean("empty-line-above")) output.add(0, " ");
    if (config.getBoolean("empty-line-underneath")) output.add(" ");

  }

  private String[] determineColors(@Nonnull String input) {
    List<String> colors = new LinkedList<>();
    int colorIndex = 0;

    boolean nextIsCode = false;
    for (char c : input.toCharArray()) {
      if (c == ChatColor.COLOR_CHAR) {
        nextIsCode = true;
        continue;
      }
      if (!nextIsCode) continue;
      nextIsCode = false;

      String latestColors = "";
      if (colors.size() > colorIndex)
        latestColors = colors.remove(colorIndex);

      colors.add(latestColors + ChatColor.COLOR_CHAR + c);

      if (ColorConversions.isValidColorCode(c))
        colorIndex++;
    }

    if (colors.isEmpty())
      colors.add("§e");
    return colors.toArray(new String[0]);
  }

}
