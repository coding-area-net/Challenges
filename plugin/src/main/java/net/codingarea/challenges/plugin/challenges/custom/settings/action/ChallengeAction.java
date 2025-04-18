package net.codingarea.challenges.plugin.challenges.custom.settings.action;

import net.anweisen.utilities.common.collection.IRandom;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.custom.settings.ChallengeSetting;
import net.codingarea.challenges.plugin.challenges.custom.settings.sub.SubSettingsBuilder;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.function.Supplier;

public abstract class ChallengeAction extends ChallengeSetting implements
  IChallengeAction {

  protected static final IRandom random = IRandom.create();

  public ChallengeAction(String name,
                         SubSettingsBuilder subSettingsBuilder) {
    super(name, subSettingsBuilder);
  }

  public ChallengeAction(String name) {
    super(name);
  }

  public ChallengeAction(String name, Supplier<SubSettingsBuilder> builderSupplier) {
    super(name, builderSupplier);
  }

  public static LinkedHashMap<String, ItemStack> getMenuItems() {
    LinkedHashMap<String, ItemStack> map = new LinkedHashMap<>();

    for (ChallengeAction value : Challenges.getInstance().getCustomSettingsLoader().getActions().values()) {
      map.put(value.getName(), new ItemBuilder(value.getMaterial(), Message.forName(value.getMessage())).hideAttributes().build());
    }

    return map;
  }

  @Override
  public final String getMessage() {
    return "item-custom-action-" + getMessageSuffix();
  }

}
