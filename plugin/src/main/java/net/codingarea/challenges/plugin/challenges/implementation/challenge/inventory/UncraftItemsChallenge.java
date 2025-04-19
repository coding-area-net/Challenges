package net.codingarea.challenges.plugin.challenges.implementation.challenge.inventory;

import net.codingarea.commons.bukkit.utils.misc.MinecraftVersion;
import net.codingarea.commons.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.type.abstraction.TimedChallenge;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.generator.categorised.SettingCategory;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Since("2.0.2")
public class UncraftItemsChallenge extends TimedChallenge {

  public UncraftItemsChallenge() {
    super(MenuType.CHALLENGES, 5, 60, 20);
    setCategory(SettingCategory.INVENTORY);
  }

  public static void uncraftInventory(@Nonnull Player player) {

    PlayerInventory inventory = player.getInventory();

    List<ItemStack> itemsToAdd = new ArrayList<>();

    for (int slot = 0; slot < inventory.getContents().length; slot++) {
      ItemStack item = inventory.getItem(slot);
      if (item == null) continue;
      List<Recipe> recipes = Bukkit.getRecipesFor(new ItemStack(item.getType()));
      if (recipes.isEmpty()) continue;

      Recipe recipe = null;
      for (Recipe currentRecipe : recipes) {

        if (canCraft(recipes, item.getType())) {
          continue;
        }

        recipe = currentRecipe;
      }

      if (recipe == null) continue;

      ItemStack[] ingredients = getIngredientsOfRecipe(recipe).toArray(new ItemStack[0]);

      for (int i = 0; i < item.getAmount() / recipe.getResult().getAmount(); i++) {
        for (ItemStack ingredient : ingredients) {
          if (ingredient == null) {
            continue;
          }
          itemsToAdd.add(ingredient);
        }
      }
      inventory.setItem(slot, null);
    }

    itemsToAdd.forEach(itemStack -> InventoryUtils.giveItem(player, itemStack));

  }

  private static boolean canCraft(List<Recipe> recipes, Material material) {

    for (Recipe recipe : recipes) {
      for (ItemStack ingredient : getIngredientsOfRecipe(recipe)) {
        if (ingredient == null) continue;

        List<Recipe> ingredientRecipes = Bukkit.getRecipesFor(ingredient);

        for (Recipe ingredientRecipe : ingredientRecipes) {
          for (ItemStack itemStack : getIngredientsOfRecipe(ingredientRecipe)) {
            if (itemStack == null) continue;
            if (itemStack.getType() == material) {
              return true;
            }

          }

        }


      }
    }
    return false;
  }

  private static List<ItemStack> getIngredientsOfRecipe(@Nonnull Recipe recipe) {
    List<ItemStack> ingredients = new ArrayList<>();
    if (recipe instanceof ShapedRecipe) {
      ShapedRecipe shaped = (ShapedRecipe) recipe;
      ingredients.addAll(shaped.getIngredientMap().values());
    } else if (recipe instanceof ShapelessRecipe) {
      ShapelessRecipe shapeless = (ShapelessRecipe) recipe;
      ingredients.addAll(shapeless.getIngredientList());
    } else if (recipe instanceof FurnaceRecipe) {
      FurnaceRecipe furnace = (FurnaceRecipe) recipe;
      ingredients.add(furnace.getInput());
    } else if (MinecraftVersion.current().isNewerOrEqualThan(MinecraftVersion.V1_14) && recipe instanceof SmithingRecipe) {
      SmithingRecipe smithing = (SmithingRecipe) recipe;
      ingredients.add(Objects.requireNonNull(smithing.getBase()).getItemStack());
      ingredients.add(Objects.requireNonNull(smithing.getAddition()).getItemStack());
    }

    return ingredients;
  }

  @Nonnull
  @Override
  public ItemBuilder createDisplayItem() {
    return new ItemBuilder(Material.CRAFTING_TABLE, Message.forName("item-uncraft-challenge"));
  }

  @Nullable
  @Override
  protected String[] getSettingsDescription() {
    return Message.forName("item-time-seconds-description").asArray(getValue());
  }

  @Override
  protected int getSecondsUntilNextActivation() {
    return getValue();
  }

  @Override
  protected void onTimeActivation() {
    restartTimer();

    broadcastFiltered(UncraftItemsChallenge::uncraftInventory);

  }

}
