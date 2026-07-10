package net.codingarea.challenges.plugin.spigot.command;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.i18n.MessageKey;
import net.codingarea.challenges.plugin.content.i18n.Prefix;
import net.codingarea.challenges.plugin.management.blocks.BlockDropManager.RegisteredDrops;
import net.codingarea.challenges.plugin.utils.bukkit.command.Completer;
import net.codingarea.challenges.plugin.utils.bukkit.command.SenderCommand;
import net.codingarea.challenges.plugin.utils.misc.ExperimentalUtils;
import net.codingarea.challenges.plugin.utils.misc.Utils;
import net.codingarea.commons.bukkit.utils.item.ItemUtils;
import net.codingarea.commons.common.misc.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class SearchCommand implements SenderCommand, Completer {

  @Override
  public void onCommand(@NotNull CommandSender sender, @NotNull String[] args) throws Exception {

    if (args.length == 0) {
      MessageKey.of("syntax").send(sender, Prefix.CHALLENGES, "search <item>");
      return;
    }

    String input = String.join("_", args).toUpperCase();
    Material material = Utils.getMaterial(input);

    if (material == null) {
      MessageKey.of("no-such-material").send(sender, Prefix.CHALLENGES);
      return;
    }
    if (!material.isItem()) {
      MessageKey.of("not-an-item").send(sender, Prefix.CHALLENGES, material);
      return;
    }

    Map<Material, RegisteredDrops> allDrops = Challenges.getInstance().getBlockDropManager().getRegisteredDrops();

    List<Material> blocks = new ArrayList<>(1);
    for (Entry<Material, RegisteredDrops> entry : allDrops.entrySet()) {
      List<Material> drops = entry.getValue().getFirst().orElse(new ArrayList<>());
      if (drops.contains(material))
        blocks.add(entry.getKey());
    }

    if (blocks.isEmpty()) {
      MessageKey.of("command-search-nothing").send(sender, Prefix.CHALLENGES, material);
    } else {
      MessageKey.of("command-search-result").send(sender, Prefix.CHALLENGES, material, StringUtils.getIterableAsString(blocks, ", ", StringUtils::getEnumName));
    }
  }

  @Nullable
  @Override
  public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
    return args.length != 1 ? null :
      Arrays.stream(ExperimentalUtils.getMaterials())
        .filter(ItemUtils::isObtainableInSurvival)
        .filter(Material::isItem)
        .map(material -> material.name().toLowerCase())
        .collect(Collectors.toList());
  }

}
