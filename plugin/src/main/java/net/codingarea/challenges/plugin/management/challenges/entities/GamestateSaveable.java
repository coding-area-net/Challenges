package net.codingarea.challenges.plugin.management.challenges.entities;

import net.anweisen.utilities.common.config.Document;

import javax.annotation.Nonnull;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.1.0
 */
public interface GamestateSaveable {

	String getUniqueGamestateName();

	void writeGameState(@Nonnull Document document);

	void loadGameState(@Nonnull Document document);

}
