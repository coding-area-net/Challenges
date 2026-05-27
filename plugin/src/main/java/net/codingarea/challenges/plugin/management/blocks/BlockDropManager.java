package net.codingarea.challenges.plugin.management.blocks;

import net.codingarea.challenges.plugin.challenges.implementation.setting.CutCleanSetting;
import net.codingarea.challenges.plugin.challenges.type.abstraction.AbstractChallenge;
import net.codingarea.challenges.plugin.challenges.type.abstraction.MenuSetting.SubSetting;
import net.codingarea.commons.bukkit.utils.logging.Logger;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

public final class BlockDropManager {

  private final Map<Material, RegisteredDrops> drops = new HashMap<>();
  private final Map<Material, RegisteredChance> chance = new HashMap<>();
  private SubSetting directInventorySetting;

  @NotNull
  public Collection<ItemStack> getDrops(@NotNull Block block) {
    if (!getDropChance(block.getType()).getAsBoolean()) return new ArrayList<>();
    List<Material> customDrops = getCustomDrops(block.getType());
    if (!customDrops.isEmpty())
      return customDrops.stream().map(ItemStack::new).collect(Collectors.toList());
    return block.getDrops();
  }

  @NotNull
  public Collection<ItemStack> getDrops(@NotNull Block block, @Nullable ItemStack tool) {
    if (!getDropChance(block.getType()).getAsBoolean()) return new ArrayList<>();
    List<Material> customDrops = getCustomDrops(block.getType());
    if (!customDrops.isEmpty())
      return customDrops.stream().map(ItemStack::new).collect(Collectors.toList());
    return block.getDrops(tool);
  }

  @NotNull
  public List<Material> getCustomDrops(@NotNull Material block) {
    RegisteredDrops option = drops.get(block);
    if (option == null) return new ArrayList<>();
    return option.getFirst().orElse(new ArrayList<>());
  }

  public void setCustomDrops(@NotNull Material block, @NotNull Material item, byte priority) {
    setCustomDrops(block, Collections.singletonList(item), priority);
  }

  public void setCustomDrops(@NotNull Material block, @NotNull List<Material> items, byte priority) {
    Logger.debug("Setting block drop for {} to {} at priority {}", block, items, priority);

    RegisteredDrops option = this.drops.computeIfAbsent(block, key -> new RegisteredDrops());
    option.setOption(priority, items);
  }

  public void resetCustomDrop(@NotNull Material block, byte priority) {
    Logger.debug("Resetting block drop for {} at priority {}", block, priority);

    RegisteredDrops option = drops.get(block);
    if (option == null) return;

    option.resetOption(priority);
    if (option.isEmpty()) drops.remove(block);
  }

  public void resetCustomDrops(byte priority) {
    Logger.debug("Resetting block drops at priority {}", priority);

    List<Material> remove = new ArrayList<>();
    for (Entry<Material, RegisteredDrops> entry : drops.entrySet()) {

      RegisteredDrops option = entry.getValue();
      option.resetOption(priority);
      if (option.isEmpty()) remove.add(entry.getKey());
    }

    remove.forEach(drops::remove);
  }

  @NotNull
  public BooleanSupplier getDropChance(@NotNull Material block) {
    RegisteredChance option = chance.get(block);
    if (option == null) return () -> true;
    return option.getFirst().orElse(() -> true);
  }

  public void setDropChance(@NotNull Material block, byte priority, @NotNull BooleanSupplier chance) {
    Logger.debug("Setting block drop chance for {} at priority {}", block, priority);

    RegisteredChance option = this.chance.computeIfAbsent(block, key -> new RegisteredChance());
    option.setOption(priority, chance);
  }

  public void resetDropChance(byte priority) {
    Logger.debug("Resetting block drop chance at priority " + priority);

    List<Material> remove = new ArrayList<>();
    for (Entry<Material, RegisteredChance> entry : chance.entrySet()) {

      RegisteredChance option = entry.getValue();
      option.resetOption(priority);
      if (option.isEmpty()) remove.add(entry.getKey());
    }

    remove.forEach(drops::remove);
  }

  @NotNull
  public Map<Material, RegisteredDrops> getRegisteredDrops() {
    return Collections.unmodifiableMap(drops);
  }

  public boolean isItemsDirectIntoInventory() {
    if (directInventorySetting == null)
      directInventorySetting = AbstractChallenge.getFirstInstance(CutCleanSetting.class).getSetting("items->inventory");
    return directInventorySetting.isEnabled();
  }

  public static final class DropPriority {

    public static final byte
      CUT_CLEAN = 10,
      RANDOMIZER = 5,
      CHANCE = -128;

    private DropPriority() {
    }
  }

  private static abstract class RegisteredOptions<T> {

    private final SortedMap<Byte, T> optionByPriority = new TreeMap<>(Collections.reverseOrder());

    public void setOption(byte priority, @NotNull T option) {
      optionByPriority.put(priority, option);
    }

    public void resetOption(byte priority) {
      optionByPriority.remove(priority);
    }

    @NotNull
    public Optional<T> getFirst() {
      return optionByPriority.values().stream().findFirst();
    }

    public boolean isEmpty() {
      return optionByPriority.isEmpty();
    }

  }

  public static class RegisteredDrops extends RegisteredOptions<List<Material>> {
    private RegisteredDrops() {
    }
  }

  public static class RegisteredChance extends RegisteredOptions<BooleanSupplier> {
    private RegisteredChance() {
    }
  }

}
