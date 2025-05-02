package net.codingarea.challenges.plugin.spigot.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class ChatInputListener implements Listener {

  private static Map<UUID, Consumer<AsyncPlayerChatEvent>> inputActions = new HashMap<>();

  public ChatInputListener() {
    inputActions = new HashMap<>();
  }

  public static void setInputAction(Player player, Consumer<AsyncPlayerChatEvent> event) {
    inputActions.put(player.getUniqueId(), event);
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onChat(AsyncPlayerChatEvent event) {

    Consumer<AsyncPlayerChatEvent> action = inputActions.remove(event.getPlayer().getUniqueId());
    if (action != null) {
      action.accept(event);
      event.setCancelled(true);
    }

  }

  @EventHandler(priority = EventPriority.LOW)
  public void onQuit(PlayerQuitEvent event) {
    inputActions.remove(event.getPlayer().getUniqueId());
  }

}
