package net.codingarea.challenges.plugin.management.challenges.entities;

import net.anweisen.utilities.common.config.Document;

import javax.annotation.Nonnull;

public interface GamestateSaveable {

  String getUniqueGamestateName();

  void writeGameState(@Nonnull Document document);

  void loadGameState(@Nonnull Document document);

}
