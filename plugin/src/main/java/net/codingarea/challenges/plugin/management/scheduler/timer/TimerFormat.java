package net.codingarea.challenges.plugin.management.scheduler.timer;

import net.codingarea.commons.common.config.Document;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

public final class TimerFormat {

  private final String seconds, minutes, hours, day, days;

  public TimerFormat(@Nonnull Document document) {
    seconds = document.getString("seconds", "");
    minutes = document.getString("minutes", "");
    hours = document.getString("hours", "");
    day = document.getString("day", "");
    days = document.getString("days", "");
  }

  public TimerFormat() {
    seconds = "{mm}:{ss}";
    minutes = "{mm}:{ss}";
    hours = "{hh}:{mm}:{ss}";
    day = "{d}:{hh}:{mm}:{ss}";
    days = "{d}:{hh}:{mm}:{ss}";
  }

  @Nonnull
  public String format(@Nonnegative long time) {

    long seconds = time;
    long minutes = seconds / 60;
    long hours = minutes / 60;
    long days = hours / 24;

    seconds %= 60;
    minutes %= 60;
    hours %= 24;

    return getFormat(minutes, hours, days)
      .replace("{d}", String.valueOf(days))
      .replace("{dd}", digit2(days))
      .replace("{h}", String.valueOf(hours))
      .replace("{hh}", digit2(hours))
      .replace("{m}", String.valueOf(minutes))
      .replace("{mm}", digit2(minutes))
      .replace("{s}", String.valueOf(seconds))
      .replace("{ss}", digit2(seconds));
  }

  private String getFormat(long minutes, long hours, long days) {
    String format;
    if (days > 1) {
      format = this.days;
    } else if (days == 1) {
      format = this.day;
    } else if (hours >= 1) {
      format = this.hours;
    } else if (minutes >= 1) {
      format = this.minutes;
    } else {
      format = this.seconds;
    }
    return format;
  }

  private String digit2(@Nonnegative long number) {
    return number > 9 ? String.valueOf(number) : "0" + number;
  }

}
