package net.codingarea.challenges.plugin.management.stats;

import net.codingarea.commons.common.collection.NumberFormatter;

import javax.annotation.Nonnull;
import java.util.function.Function;

public enum Statistic {

  DRAGON_KILLED(Display.INTEGER),
  DEATHS(Display.INTEGER),
  DAMAGE_TAKEN(Display.HEARTS),
  ENTITY_KILLS(Display.INTEGER),
  DAMAGE_DEALT(Display.HEARTS),
  BLOCKS_MINED(Display.INTEGER),
  BLOCKS_PLACED(Display.INTEGER),
  BLOCKS_TRAVELED(Display.INTEGER),
  CHALLENGES_PLAYED(Display.INTEGER),
  JUMPS(Display.INTEGER);

  private final Display display;

  Statistic(@Nonnull Display display) {
    this.display = display;
  }

  @Nonnull
  public String formatChat(double value) {
    return display.formatChat(value);
  }

  public enum Display {

    HEARTS(value -> NumberFormatter.BIG_NUMBER.format(value / 2) + " §c❤"),
    INTEGER(NumberFormatter.BIG_NUMBER::format);

    private final Function<Double, String> chatFormat;

    Display(@Nonnull Function<Double, String> chatFormat) {
      this.chatFormat = chatFormat;
    }

    @Nonnull
    public String formatChat(double value) {
      return chatFormat.apply(value);
    }

  }

}
