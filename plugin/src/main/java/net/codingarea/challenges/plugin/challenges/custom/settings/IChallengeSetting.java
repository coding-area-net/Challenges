package net.codingarea.challenges.plugin.challenges.custom.settings;

import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SubSettingsBuilder;
import org.bukkit.Material;

public interface IChallengeSetting {

  SubSettingsBuilder getSubSettingsBuilder();

  String getName();

  Material getMaterial();

  String getMessage();

}
