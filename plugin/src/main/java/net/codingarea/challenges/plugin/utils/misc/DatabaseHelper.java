package net.codingarea.challenges.plugin.utils.misc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import net.codingarea.commons.bukkit.utils.logging.Logger;
import net.codingarea.commons.bukkit.utils.misc.GameProfileUtils;
import net.codingarea.challenges.plugin.Challenges;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public final class DatabaseHelper {

  private static final Map<UUID, String> cachedTextures = new HashMap<>();

  private DatabaseHelper() {
  }

  @Nullable
  public static String getPlayerTextures(@Nonnull Player player) {
    GameProfile profile = GameProfileUtils.getGameProfile(player);
    PropertyMap properties = profile.getProperties();
    List<Property> textures = new ArrayList<>(properties.get("textures"));
    return textures.isEmpty() ? null : textures.get(0).getValue();
  }

  public static void savePlayerData(@Nonnull Player player) {
    try {

      String textures = getPlayerTextures(player);
      if (textures != null) cachedTextures.put(player.getUniqueId(), textures);

      Challenges.getInstance().getDatabaseManager().getDatabase()
        .insertOrUpdate("challenges")
        .set("textures", textures)
        .set("name", player.getName())
        .where("uuid", player.getUniqueId())
        .execute();

    } catch (Exception ex) {
      Logger.error("Unable to update textures for {} | {}", player.getName(), player.getUniqueId(), ex);
    }
  }

  @Nullable
  public static String getTextures(@Nonnull UUID uuid) {
    String cached = cachedTextures.get(uuid);
    if (cached != null) return cached;

    Player player = Bukkit.getPlayer(uuid);
    if (player != null) {
      String textures = getPlayerTextures(player);
      if (textures != null) {
        cachedTextures.put(uuid, textures);
        return textures;
      }
    }

    if (!Challenges.getInstance().getDatabaseManager().isConnected())
      return null;

    try {
      String textures = Challenges.getInstance().getDatabaseManager().getDatabase()
        .query("challenges")
        .select("textures")
        .where("uuid", uuid)
        .execute().firstOrEmpty()
        .getString("textures");
      cachedTextures.put(uuid, textures);
      return textures;
    } catch (Exception ex) {
      Logger.error("Unable to get textures for {}", uuid, ex);
      return null;
    }
  }

  public static void clearCache(@Nonnull UUID uuid) {
    cachedTextures.remove(uuid);
  }

}
