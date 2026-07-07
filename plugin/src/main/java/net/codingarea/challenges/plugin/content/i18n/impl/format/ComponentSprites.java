package net.codingarea.challenges.plugin.content.i18n.impl.format;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.object.ObjectContents;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class ComponentSprites {

  private ComponentSprites() {
  }

  @NotNull
  static Component createSpriteComponent(@Nullable Material material) {
    if (material == null || material.isAir() || !material.isItem()) {
      return Component.empty();
    }

    // 2. Get the clean, lowercase vanilla key string (e.g., "diamond_sword" or "stone")
    NamespacedKey namespaced = material.getKey();
    String materialKey = namespaced.getKey().toLowerCase();

    // 3. Determine if the path should be prefixed with 'item/' or 'block/'
    // Note: Some blocks (like STONE) use 'block/stone', while items use 'item/diamond_sword'
    String prefix = material.isBlock() ? "block/" : "item/";

    // 4. Create the resource path key inside the default atlas (minecraft:blocks)
    Key spritePath = Key.key("minecraft", prefix + materialKey);
    System.err.println("Creating sprite component for material: " + material + " with path: " + spritePath + " key: " + material.key() + " translatebleKey: " + material.translationKey());

    // 5. Build and return the object component with an empty fallback
    return Component.object(builder -> builder
      .contents(ObjectContents.sprite(material.key())) // Defaults to the minecraft:blocks atlas
    );
  }

}
