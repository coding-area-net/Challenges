package net.codingarea.challenges.plugin.challenges.implementation.challenge.randomizer;

import net.codingarea.commons.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.type.abstraction.AbstractChallenge;
import net.codingarea.challenges.plugin.challenges.type.abstraction.RandomizerSetting;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.utils.bukkit.command.Completer;
import net.codingarea.challenges.plugin.utils.bukkit.command.SenderCommand;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.ListBuilder;
import net.codingarea.challenges.plugin.utils.misc.Utils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.LootTables;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

@Since("2.2.0")
public class EntityLootRandomizerChallenge extends RandomizerSetting implements SenderCommand, Completer {

  protected Map<EntityType, LootTable> randomization;

  public EntityLootRandomizerChallenge() {
    super(MenuType.CHALLENGES);
    setCategory(SettingCategory.RANDOMIZER);
  }

  @NotNull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.FURNACE_MINECART, Message.forName("item-entity-loot-randomizer-challenge"));
  }

  @Override
  protected void reloadRandomization() {
    randomization = new HashMap<>();

    List<EntityType> from = new ArrayList<>(Arrays.asList(EntityType.values()));
    from.removeIf(entityType -> !getLootableEntities().contains(entityType));
    random.shuffle(from);
    from.removeIf(Objects::isNull);

    List<EntityType> entityTypesToRemove = new ArrayList<>();
    List<LootTable> to = from.stream().map(entityType -> {
      if (entityType == null) return null;
      try {
        return LootTables.valueOf(entityType.name()).getLootTable();
      } catch (IllegalArgumentException exception) {
        entityTypesToRemove.add(entityType);
        return null;
      }
    }).filter(Objects::nonNull).collect(Collectors.toList());
    from.removeAll(entityTypesToRemove);
    random.shuffle(to);

    while (!from.isEmpty()) {
      EntityType entityType = from.remove(0);
      LootTable lootTable = to.remove(0);

      randomization.put(entityType, lootTable);
    }
  }

  public List<EntityType> getLootableEntities() {
    return new ListBuilder<>(EntityType.values())
      .removeIf(type -> !type.isSpawnable())
      .removeIf(type -> !type.isAlive())
      .remove(EntityType.ENDER_DRAGON)
      .remove(EntityType.GIANT)
      .remove(EntityType.ILLUSIONER)
      .remove(EntityType.ZOMBIE_HORSE)
      .build();
  }

  @Override
  protected void onDisable() {
    randomization.clear();
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onEntityDeath(EntityDeathEvent event) {
    if (!isEnabled()) return;
    LivingEntity entity = event.getEntity();
    if (!getLootableEntities().contains(entity.getType())) return;
    event.getDrops().clear();
    if (!randomization.containsKey(entity.getType())) return;

    LootTable lootTable = randomization.get(entity.getType());
    LootContext.Builder builder = new LootContext.Builder(entity.getLocation())
      .lootedEntity(entity);

    if (entity.getKiller() == null) {
      builder.lootingModifier(0);
    } else {
      builder.killer(entity.getKiller());
    }

    Collection<ItemStack> newDrops = lootTable.populateLoot(random.asRandom(), builder.build());
    event.getDrops().addAll(newDrops);
  }

  public LootTable getLootTableForEntity(EntityType entityType) {
    return randomization.get(entityType);
  }

  public Optional<EntityType> getEntityForLootTable(LootTable lootTable) {
    return randomization.entrySet().stream()
      .filter(entry -> entry.getValue().equals(lootTable))
      .map(Map.Entry::getKey)
      .findFirst();
  }

  @Override
  public void onCommand(@Nonnull CommandSender sender, @Nonnull String[] args) throws Exception {

    if (!isEnabled()) {
      Message.forName("command-searchloot-disabled").send(sender, Prefix.CHALLENGES);
      return;
    }

    if (args.length == 0) {
      Message.forName("syntax").send(sender, Prefix.CHALLENGES, "searchloot <entity>");
      return;
    }

    String input = String.join("_", args).toUpperCase();
    EntityType entityType = Utils.getEntityType(input);

    if (entityType == null) {
      Message.forName("no-such-entity").send(sender, Prefix.CHALLENGES);
      return;
    }
    if (!entityType.isAlive()) {
      Message.forName("not-alive").send(sender, Prefix.CHALLENGES, entityType);
      return;
    }

    LootTable givenLootTable;
    try {
      givenLootTable = LootTables.valueOf(entityType.name()).getLootTable();
    } catch (IllegalArgumentException exception) {
      Message.forName("no-loot").send(sender, Prefix.CHALLENGES, entityType);
      return;
    }

    Optional<EntityType> optionalEntity = getEntityForLootTable(givenLootTable);
    LootTable droppedLootTable = getLootTableForEntity(entityType);

    if (optionalEntity.isPresent()) {
      Message.forName("command-searchloot-result").send(sender, Prefix.CHALLENGES, entityType, droppedLootTable, optionalEntity.get());
    } else {
      Message.forName("command-searchloot-nothing").send(sender, Prefix.CHALLENGES, entityType);
    }

  }

  @Nullable
  @Override
  public List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull String[] args) {
    EntityLootRandomizerChallenge instance = AbstractChallenge.getFirstInstance(EntityLootRandomizerChallenge.class);
    return args.length != 1 ? null :
      instance.getLootableEntities().stream()
        .map(material -> material.name().toLowerCase())
        .collect(Collectors.toList()
        );
  }

}
