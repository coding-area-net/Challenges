package net.codingarea.challenges.plugin.management.menu.generator.categorised;

public class SmallCategorisedMenuGenerator extends CategorisedMenuGenerator {

	@Override
	public int getEntriesPerPage() {
		return 7;
	}

	@Override
	public int getSize() {
		return 9 * 3;
	}

	@Override
	public int[] getNavigationSlots(int page) {
		return new int[]{18, 26};
	}
}
