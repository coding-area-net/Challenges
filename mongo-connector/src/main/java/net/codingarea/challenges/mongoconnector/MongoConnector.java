package net.codingarea.challenges.mongoconnector;

import net.codingarea.commons.database.mongodb.MongoDBDatabase;
import net.codingarea.challenges.plugin.Challenges;
import org.bukkit.plugin.java.JavaPlugin;


public final class MongoConnector extends JavaPlugin {

  @Override
  public void onLoad() {
    Challenges.getInstance().getDatabaseManager().registerDatabase("mongodb", MongoDBDatabase.class, this);
  }

}
