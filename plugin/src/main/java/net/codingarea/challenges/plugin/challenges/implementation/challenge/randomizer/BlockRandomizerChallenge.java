package net.codingarea.challenges.plugin.challenges.implementation.challenge.randomizer;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.abstraction.RandomizerSetting;
import net.codingarea.challenges.plugin.management.blocks.BlockDropManager;
import net.codingarea.challenges.plugin.management.blocks.BlockDropManager.DropPriority;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.misc.ExperimentalUtils;
import net.codingarea.commons.bukkit.utils.item.ItemUtils;
import net.codingarea.commons.bukkit.utils.misc.BukkitReflectionUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockRandomizerChallenge extends RandomizerSetting {

  public BlockRandomizerChallenge() {
    super(MenuType.CHALLENGES, new ItemStack(Material.MINECART), "block-randomizer-challenge");
  }

  @Override
  protected void reloadRandomization() {
    BlockDropManager manager = Challenges.getInstance().getBlockDropManager();

    List<Material> blocks = new ArrayList<>(Arrays.asList(ExperimentalUtils.getMaterials()));
    blocks.removeIf(material -> !ItemUtils.isObtainableInSurvival(material) || !material.isBlock() || BukkitReflectionUtils.isAir(material));
    random.shuffle(blocks);

    List<Material> drops = new ArrayList<>(Arrays.asList(ExperimentalUtils.getMaterials()));
    drops.removeIf(material -> !material.isItem() || !ItemUtils.isObtainableInSurvival(material));
    random.shuffle(drops);

    while (!blocks.isEmpty()) {
      Material block = blocks.remove(0);
      List<Material> items = new ArrayList<>();

      int addDrops = getMatches(blocks.size(), drops.size());
      for (int i = 0; i < addDrops && !drops.isEmpty(); i++) {
        items.add(drops.remove(0));
      }
      manager.setCustomDrops(block, items, DropPriority.RANDOMIZER);
    }

  }

  @Override
  protected void onDisable() {
    BlockDropManager manager = Challenges.getInstance().getBlockDropManager();
    manager.resetCustomDrops(DropPriority.RANDOMIZER);
  }

}
