package net.codingarea.challenges.plugin.challenges.implementation.challenge.miscellaneous;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifier;
import net.codingarea.challenges.plugin.challenges.type.annotation.Since;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.management.blocks.BlockDropManager.DropPriority;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.misc.ExperimentalUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Random;

@Since("2.0")
public class LowDropRateChallenge extends SettingModifier {

  private final Random random = new Random();

  public LowDropRateChallenge() {
    super(MenuType.CHALLENGES, null, 9, new ItemStack(Material.WOODEN_AXE), "low-drop-rate");
  }

//  @Nullable
//  @Override
//  protected String[] getSettingsDescription() {
//    return Message.forName("item-chance-description").asArray(getValue() * 10);
//  }

  @Override
  public void playValueChangeTitle() {
    ChallengeHelper.playChallengeValueTitle(this, getValue() * 10 + "%");
  }

  @Override
  protected void onValueChange() {
    reloadChances();
  }

  @Override
  protected void onEnable() {
    reloadChances();
  }

  @Override
  protected void onDisable() {
    Challenges.getInstance().getBlockDropManager().resetDropChance(DropPriority.CHANCE);
  }

  protected void reloadChances() {
    Arrays.stream(ExperimentalUtils.getMaterials()).filter(Material::isBlock).forEach(block -> {
      Challenges.getInstance().getBlockDropManager().setDropChance(block, DropPriority.CHANCE, () -> random.nextInt(10) < getValue());
    });
  }

}
