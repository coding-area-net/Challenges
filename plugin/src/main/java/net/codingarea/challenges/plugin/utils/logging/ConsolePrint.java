package net.codingarea.challenges.plugin.utils.logging;

import net.anweisen.utilities.bukkit.utils.logging.Logger;
import org.bukkit.Bukkit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ConsolePrint {

  private ConsolePrint() {
  }

  public static void notSpigot() {
    log("");
    log("=============================================================================================");
    log("");
    log("Your server does NOT run an instance of spigot (Your server: " + Bukkit.getVersion() + ")");
    log("Please use an instance of spigot or paper to be able to use this plugin!");
    log("");
    log("Paper Download: https://papermc.io/downloads");
    log("");
    log("=============================================================================================");
    log("");
  }

  public static void unknownLanguage(@Nullable String language) {
    log("");
    log("Found unknown language '" + language + "'!");
    log("Defaulting to en (English)");
    log("");
  }

  public static void unableToGetLanguages() {
    log("");
    log("No languages found to load");
    log("Is the server / plugin set up correctly?");
    log("");
  }

  public static void alreadyExecutingContentLoader() {
    log("");
    log("Cannot load contents; Already loading contents?");
    log("Are you reloading too fast?");
    log("");
  }

  public static void accessBlocked() {
    log(" ");
    log(" ");
    log("██████╗░██╗░░░░░░█████╗░░█████╗░██╗░░██╗███████╗██████╗░");
    log("██╔══██╗██║░░░░░██╔══██╗██╔══██╗██║░██╔╝██╔════╝██╔══██╗");
    log("██████╦╝██║░░░░░██║░░██║██║░░╚═╝█████═╝░█████╗░░██║░░██║");
    log("██╔══██╗██║░░░░░██║░░██║██║░░██╗██╔═██╗░██╔══╝░░██║░░██║");
    log("██████╦╝███████╗╚█████╔╝╚█████╔╝██║░╚██╗███████╗██████╔╝");
    log("╚═════╝░╚══════╝░╚════╝░░╚════╝░╚═╝░░╚═╝╚══════╝╚═════╝░");
    log(" ");
    log("Your server's access is blocked.");
    log("For more information and support visit our discord server: https://discord.coding-area.net");
    log(" ");
    log(" ");
  }

  public static void noMongoDependencies() {
    log("");
    log("=============================================================================================");
    log("");
    log("Cannot use MongoDB as database without the Challenges-MongoConnector dependency plugin.");
    log("Please add this plugin in order to be able to access a mongodb database.");
    log("");
    log("=============================================================================================");
    log("");
  }

  private static void log(@Nonnull String message) {
    Logger.error(message);
  }

}
