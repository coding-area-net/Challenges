package net.codingarea.challenges.plugin.management.menu.generator.implementation.custom;

import net.codingarea.challenges.plugin.management.menu.generator.ChooseMultipleItemGenerator;
import net.codingarea.challenges.plugin.utils.misc.MapUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;

/**
 * @author KxmischesDomi | <a href="https://github.com/kxmischesdomi">...</a>
 * @since 2.1.0
 */
public class SubSettingChooseMultipleMenuGenerator extends ChooseMultipleItemGenerator {

	private final IParentCustomGenerator parent;
	private final String key;
	private final String title;

	public SubSettingChooseMultipleMenuGenerator(String key, IParentCustomGenerator parent, LinkedHashMap<String, ItemStack> map, String title) {
		super(map);
		this.key = key;
		this.title = title;
		this.parent = parent;
	}

	@Override
	public String[] getSubTitles(int page) {
		return new String[]{title};
	}

	@Override
	public void onItemClick(Player player, String[] itemKeys) {
		parent.accept(player, null, MapUtils.createStringArrayMap(key, itemKeys));
	}

	@Override
	public void onBackToMenuItemClick(Player player) {
		parent.decline(player);
	}

}
