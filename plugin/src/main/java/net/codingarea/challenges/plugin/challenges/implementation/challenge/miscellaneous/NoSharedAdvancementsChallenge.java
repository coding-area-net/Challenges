package net.codingarea.challenges.plugin.challenges.implementation.challenge.miscellaneous;

import net.anweisen.utilities.bukkit.utils.misc.BukkitReflectionUtils;
import net.anweisen.utilities.common.annotations.Since;
import net.anweisen.utilities.common.config.Document;
import net.codingarea.challenges.plugin.challenges.type.abstraction.Setting;
import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.advancement.Advancement;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Since("2.2.0")
public class NoSharedAdvancementsChallenge extends Setting {

  private final List<Advancement> advancementsDone = new LinkedList<>();

  public NoSharedAdvancementsChallenge() {
    super(MenuType.CHALLENGES);
  }

  @NotNull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.KNOWLEDGE_BOOK, Message.forName("item-no-shared-advancements-challenge"));
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onAdvancementDone(PlayerAdvancementDoneEvent event) {
    if (!shouldExecuteEffect()) return;
    if (event.getAdvancement().getKey().toString().contains(":recipes/")) return;
    if (advancementsDone.contains(event.getAdvancement())) {
      ChallengeHelper.kill(event.getPlayer());
    } else {
      advancementsDone.add(event.getAdvancement());
    }
  }

  @Override
  public void loadGameState(@NotNull Document document) {
    advancementsDone.clear();
    List<String> advancementKeys = document.getStringList("advancements");
    for (String advancementKey : advancementKeys) {
      try {
        advancementsDone.add(Bukkit.getAdvancement(Objects.requireNonNull(BukkitReflectionUtils.fromString(advancementKey))));
      } catch (Exception exception) {
        // DON'T EXIST
      }
    }
  }

  @Override
  public void writeGameState(@NotNull Document document) {
    List<String> foundItems = new LinkedList<>();
    for (Advancement advancement : advancementsDone) {
      foundItems.add(advancement.getKey().toString());
    }
    document.set("advancements", foundItems);
  }

}
