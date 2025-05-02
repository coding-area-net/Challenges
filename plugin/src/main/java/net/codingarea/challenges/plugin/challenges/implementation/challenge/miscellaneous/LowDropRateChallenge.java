package net.codingarea.challenges.plugin.challenges.implementation.challenge.miscellaneous;

import net.codingarea.commons.common.annotations.Since;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifier;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.blocks.BlockDropManager.DropPriority;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.ExperimentalUtils;
import org.bukkit.Material;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Random;

@Since("2.0")
public class LowDropRateChallenge extends SettingModifier {

  private final Random random = new Random();

  public LowDropRateChallenge() {
    super(MenuType.CHALLENGES, 9);
  }

  @Nonnull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.WOODEN_AXE, Message.forName("item-low-drop-rate-challenge"));
  }

  @Nullable
  @Override
  protected String[] getSettingsDescription() {
    return Message.forName("item-chance-description").asArray(getValue() * 10);
  }

  @Override
  public void playValueChangeTitle() {
    ChallengeHelper.playChangeChallengeValueTitle(this, getValue() * 10 + "%");
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
