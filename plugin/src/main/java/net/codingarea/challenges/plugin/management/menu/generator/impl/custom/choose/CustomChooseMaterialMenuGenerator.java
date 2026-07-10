package net.codingarea.challenges.plugin.management.menu.generator.impl.custom.choose;

import net.codingarea.challenges.plugin.challenges.custom.settings.SettingType;
import net.codingarea.challenges.plugin.content.i18n.LocalizableMessage;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.UncachedMultiPageSelectMenuGenerator;
import net.codingarea.challenges.plugin.management.menu.generator.impl.custom.IParentCustomGenerator;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.ExperimentalUtils;
import net.codingarea.challenges.plugin.utils.misc.MapUtils;
import net.codingarea.commons.bukkit.utils.menu.MenuClickInfo;
import net.codingarea.commons.bukkit.utils.misc.BukkitReflectionUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Locale;

public class CustomChooseMaterialMenuGenerator extends UncachedMultiPageSelectMenuGenerator<Material> {

  public static final int SIZE = CustomChooseOptionMenuGenerator.SIZE;
  public static final int[] SLOTS = CustomChooseOptionMenuGenerator.SLOTS;
  public static final Material[] MATERIALS;

  static {
    ArrayList<Material> materials = new ArrayList<>();
    for (Material material : ExperimentalUtils.getMaterials()) {
      if (BukkitReflectionUtils.isAir(material)) continue;
      if (!material.isItem()) continue;
      materials.add(material);
    }
    MATERIALS = materials.toArray(new Material[0]);
  }

  private final IParentCustomGenerator parent;

  public CustomChooseMaterialMenuGenerator(@NotNull IParentCustomGenerator parent) {
    this(parent, MATERIALS);
  }

  protected CustomChooseMaterialMenuGenerator(@NotNull IParentCustomGenerator parent, @NotNull Material[] elements) {
    super(elements);
    this.menuType = MenuType.CUSTOM;
    this.parent = parent;
  }

  @NotNull
  @Override
  public LocalizableMessage getMenuName() {
    return createSubMenuTitle(MessageKey.of("menu.custom.sub-name"), MessageKey.of("menu.custom.material.name"));
  }

  @Override
  public void handleElementClick(@NotNull Material element, @NotNull MenuClickInfo info) {
    parent.accept(info.getPlayer(), SettingType.MATERIAL, MapUtils.createStringArrayMap("material", element.name()));
  }

  @NotNull
  @Override
  public ItemStack createDisplayItem(@NotNull Material element, @NotNull Locale locale) {
    return new ItemBuilder(locale, element, MessageKey.of("menu.custom.material.format"),
      element).build();
  }

  @Override
  protected void handleNavigateOutOfMenu(@NotNull Player player) {
    parent.decline(player);
  }

  @Override
  public int[] getSlots() {
    return SLOTS;
  }


  @Override
  public int getInventorySize() {
    return SIZE;
  }
}
