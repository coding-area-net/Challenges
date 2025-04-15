package net.codingarea.challenges.plugin.management.menu.position;

import lombok.Getter;
import net.anweisen.utilities.bukkit.utils.menu.MenuPosition;
import net.codingarea.challenges.plugin.management.menu.generator.MenuGenerator;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.1.0
 */
@Getter
public abstract class GeneratorMenuPosition implements MenuPosition {

	protected final MenuGenerator generator;
	protected final int page;

	public GeneratorMenuPosition(MenuGenerator generator, int page) {
		this.generator = generator;
		this.page = page;
	}

}
