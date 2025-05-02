package net.codingarea.challenges.plugin.spigot.command;

import net.codingarea.commons.bukkit.utils.item.ItemUtils;
import net.codingarea.commons.common.misc.StringUtils;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.management.blocks.BlockDropManager.RegisteredDrops;
import net.codingarea.challenges.plugin.utils.bukkit.command.Completer;
import net.codingarea.challenges.plugin.utils.bukkit.command.SenderCommand;
import net.codingarea.challenges.plugin.utils.misc.ExperimentalUtils;
import net.codingarea.challenges.plugin.utils.misc.Utils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class SearchCommand implements SenderCommand, Completer {

  @Override
  public void onCommand(@Nonnull CommandSender sender, @Nonnull String[] args) throws Exception {

    if (args.length == 0) {
      Message.forName("syntax").send(sender, Prefix.CHALLENGES, "search <item>");
      return;
    }

    String input = String.join("_", args).toUpperCase();
    Material material = Utils.getMaterial(input);

    if (material == null) {
      Message.forName("no-such-material").send(sender, Prefix.CHALLENGES);
      return;
    }
    if (!material.isItem()) {
      Message.forName("not-an-item").send(sender, Prefix.CHALLENGES, material);
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
      Message.forName("command-search-nothing").send(sender, Prefix.CHALLENGES, material);
    } else {
      Message.forName("command-search-result").send(sender, Prefix.CHALLENGES, material, StringUtils.getIterableAsString(blocks, ", ", StringUtils::getEnumName));
    }
  }

  @Nullable
  @Override
  public List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull String[] args) {
    return args.length != 1 ? null :
      Arrays.stream(ExperimentalUtils.getMaterials())
        .filter(ItemUtils::isObtainableInSurvival)
        .filter(Material::isItem)
        .map(material -> material.name().toLowerCase())
        .collect(Collectors.toList());
  }

}
