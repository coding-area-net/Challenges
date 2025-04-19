package net.codingarea.commons.bukkit.utils.misc;

import net.codingarea.commons.common.logging.ILogger;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/*
 * In API version 1.20.6 and earlier, InventoryView is an abstract class
 * In API version 1.21, InventoryView is an interface
 */
public class CompatibilityUtils {

  protected static final ILogger logger = ILogger.forThisClass();
  private static final Logger log = LoggerFactory.getLogger(CompatibilityUtils.class);

  private CompatibilityUtils() {
  }

  public static Inventory getTopInventory(@Nonnull Player player) {
    InventoryView view = player.getOpenInventory();

    try {
      Method getTopInventory = InventoryView.class.getMethod("getTopInventory");
      return (Inventory) getTopInventory.invoke(view);
    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
      logger.error("Failed to get top inventory", ex);
      return null;
    }
  }

  public static Inventory getTopInventory(@Nonnull InventoryClickEvent event) {
    InventoryView view = event.getView();

    try {
      Method getTopInventory = InventoryView.class.getMethod("getTopInventory");
      return (Inventory) getTopInventory.invoke(view);
    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
      logger.error("Failed to get top inventory", ex);
      return null;
    }
  }
}
