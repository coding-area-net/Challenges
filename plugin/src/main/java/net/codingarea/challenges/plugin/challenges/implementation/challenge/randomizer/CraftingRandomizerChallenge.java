package net.codingarea.challenges.plugin.challenges.implementation.challenge.randomizer;

import net.codingarea.challenges.plugin.challenges.type.abstraction.RandomizerSetting;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.ExperimentalUtils;
import net.codingarea.commons.bukkit.utils.item.ItemUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.*;

public class CraftingRandomizerChallenge extends RandomizerSetting {

  protected final Map<Material, Material> randomization = new HashMap<>();

  public CraftingRandomizerChallenge() {
    super(MenuType.CHALLENGES);
  }

  @Nonnull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.CHEST_MINECART, Message.forName("item-crafting-randomizer-challenge"));
  }

  @Override
  protected void reloadRandomization() {

    List<Material> from = new ArrayList<>(Arrays.asList(ExperimentalUtils.getMaterials()));
    from.removeIf(material -> !material.isItem() || !ItemUtils.isObtainableInSurvival(material));
    random.shuffle(from);

    List<Material> to = new ArrayList<>(Arrays.asList(ExperimentalUtils.getMaterials()));
    to.removeIf(material -> !material.isItem() || !ItemUtils.isObtainableInSurvival(material));
    random.shuffle(to);

    while (!from.isEmpty()) {
      Material item = from.remove(0);
      Material result = to.remove(0);

      randomization.put(item, result);
    }

  }

  @Override
  protected void onDisable() {
    randomization.clear();
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onCraftItem(@Nonnull CraftItemEvent event) {
    if (!isEnabled()) return;
    ItemStack item = event.getCurrentItem();
    if (item == null) return;
    Material result = randomization.get(item.getType());
    if (result == null) return;
    event.setCurrentItem(new ItemBuilder(result).amount(item.getAmount()).build());
  }

}
